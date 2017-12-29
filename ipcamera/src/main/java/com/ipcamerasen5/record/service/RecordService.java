package com.ipcamerasen5.record.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ipcamerasen5.main.MainApplication;
import com.ipcamerasen5.main.nobuffer.CameraProviderFControl;
import com.ipcamerasen5.main.nobuffer.EntityCameraStatus;
import com.ipcamerasen5.record.common.CommonTools;
import com.ipcamerasen5.record.common.Constant;
import com.ipcamerasen5.record.common.DateUtil;
import com.ipcamerasen5.record.db.Clock;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.entity.ClockTempObject;
import com.ipcamerasen5.record.entity.IpcameraSecondBroadcast;
import com.ipcamerasen5.record.entity.ThreadWithDid;
import com.ipcamerasen5.record.event.AddIpcameraEvent;
import com.ipcamerasen5.record.event.DeleteIpcameraEvent;
import com.ipcamerasen5.record.event.IpCamAlarmEvent;
import com.ipcamerasen5.record.event.IpCamAlarmEvent_Switch_close;
import com.ipcamerasen5.record.event.IpCamMoveEvent;
import com.ipcamerasen5.record.event.IpCamStopRecord;
import com.ipcamerasen5.record.event.IpcamStopAllRecord;
import com.ipcamerasen5.record.event.IpcamStopStatusChanges;
import com.ipcamerasen5.record.event.IpcameraExit;
import com.ipcamerasen5.record.ui.presenter.RecordPresenter2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import nes.ltlib.LTSDKManager;
import nes.ltlib.event.DataSourceListenerEvent;
import nes.ltlib.event.LTSDKGlobalStatusEvent;
import nes.ltlib.utils.AppLog;
import nes.ltlib.utils.LogUtils;

import static com.ipcamerasen5.record.common.CommonTools.findDeviceFromDB;
import static com.ipcamerasen5.record.common.CommonTools.mContext;
import static com.ipcamerasen5.record.common.CommonTools.makeSureFile;
import static com.ipcamerasen5.record.ui.view.RecordActivity.position;

/**
 * 处理后台录制的服务
 * Created by chenqianghua on 2017/4/27.
 */

public class RecordService extends Service{
    private List<EntityCameraStatus> mAllCameraStatus;
    private CameraProviderFControl mCameraProviderFControl;
    private static List<RecordPresenter2> backgroundPresenters;//
    private List<Clock> mClocks;
    public  static CopyOnWriteArrayList<IpCamDevice> mIpCamDevices;//本应用数据库中的摄像头数据
    public static CopyOnWriteArrayList<String> dids_DB;//数据库中的摄像头did
    private List<String> dids_clock;//闹钟录制的摄像头
    private static List<String> dids_round;//24小时录制的摄像头
    private static List<ThreadWithDid> threads_clock;//闹钟录制线程
    private static List<ThreadWithDid> threads_round;//24小时录制线程
    public static CopyOnWriteArrayList<ClockTempObject> mTempObjects;//存放需要录制的闹钟结构体
    public static boolean isAlive;
    private List<String> gids_statu = new ArrayList<>();//用来标记哪些状态未回来
    private List<String> paths = null;

    //当前设备存在硬盘
    public static boolean hasStorage = false;
    private boolean hasInit;
    //硬盘拔插监听
    private BroadcastReceiver storage_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("onReceive_u盘","action is "+action+"hasInt is"+hasInit+" hasStorage is "+hasStorage);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){
                String path = intent.getData().getPath();
                if (!hasStorage){
                    //不存在磁盘
                    CommonTools.savePreference(Constant.lastStorage,path);
                    hasStorage = true;
                    //确保各个录制文件夹都被创建出来
                    makeSureFile();
                    if (CommonTools.getPreference(Constant.shouldOpenRecordService).equals("true")){
                        //已经进过recordSetting页面
                        if (!hasInit){
                            initRecord();
                        }
                        else {
                            //重新开启所有的录制
                            if (null != backgroundPresenters && backgroundPresenters.size() > 0){
                                for (int i=0; i<backgroundPresenters.size(); i++){
                                    backgroundPresenters.get(i).dealRecord_StorageOk();
                                }
                            }
                        }
                    }
                }
                else {
                    // TODO: 2017/8/25 存在磁盘此时需要让用户去选择
                }
            }
            else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){
                String path = intent.getData().getPath();
                if (!CommonTools.getPreference(Constant.shouldOpenRecordService).equals("true")){
                    return;
                }
                //如果已经开启录制的线程了
                if (hasInit){
                    if (path.equals(CommonTools.getPreference(Constant.lastStorage)) || CommonTools.getAllStorages(MainApplication.getWholeContext()).size() == 0){
                        //停止所有的录制
                        EventBus.getDefault().post(new IpcamStopAllRecord());
                        if (CommonTools.getAllStorages(MainApplication.getWholeContext()).size() > 1){
                            String path_temp = CommonTools.getAllStorages(MainApplication.getWholeContext()).get(0);
                            CommonTools.savePreference(Constant.lastStorage,path_temp);
                            //更改设备录制的所有地址
                            CommonTools.makeSureFile();
                            //重新开启所有的录制
                            if (null != backgroundPresenters && backgroundPresenters.size() > 0){
                                for (int i=0; i<backgroundPresenters.size(); i++){
                                    backgroundPresenters.get(i).dealRecord_StorageOk();
                                }
                            }
                        }
                        else {
                            hasStorage = false;
                        }
                    }
                }
            }
        }
    };


    public static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("Record_onCreate"," com.ipcamerasen5.main1 has started");
        Log.e("Record_onCreate"," iswork is "+CommonTools.getPreference(Constant.shouldOpenRecordService).equals("true"));
        paths = CommonTools.getAllStorages(MainApplication.getWholeContext());
        //全局工具mContext赋值
        mContext = MainApplication.getWholeContext();
        initPreData();
        super.onCreate();
    }
    private void initData() {
        mClocks = new ArrayList<>();
        mIpCamDevices = new CopyOnWriteArrayList<>();
        dids_DB = new CopyOnWriteArrayList<>();
        List<IpCamDevice> mIpCamDevices = IpCamDevice.findAll(IpCamDevice.class);
        if (null != mIpCamDevices && mIpCamDevices.size() > 0){
            for (int i=0; i<mIpCamDevices.size(); i++){
                //防止添加了did为null的数据
                if (null != mIpCamDevices.get(i).getDid()){
                    dids_DB.add(mIpCamDevices.get(i).getDid());
                }
            }
        }
        dids_clock = new ArrayList<>();
        dids_round = new ArrayList<>();
        threads_clock = new ArrayList<>();
        threads_round = new ArrayList<>();
        mTempObjects = new CopyOnWriteArrayList<>();
        backgroundPresenters = new ArrayList<>();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Record_startComma","startId is "+startId+CommonTools.getPreference(Constant.shouldOpenRecordService));
        Log.d("Record_startCommand","paths.size is "+paths.size());
        if (paths.size() >0){
            hasStorage = true;
        }
        Log.d("Record_startCommand","startId is "+startId+CommonTools.getPreference(Constant.shouldOpenRecordService));
// 如果用户还没配置过record，在launcher开启服务时直接退出
        if (!hasStorage){
            Log.d("Record_startCommand","hasStorage is "+hasStorage);
            return super.onStartCommand(intent, flags, startId);
        }
        Log.d("Record_startCommand"," init is "+hasInit);
        initRecord();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initRecord() {
        hasInit = true;
        Log.e("RecordService_life","record_onStartCommand");
        //设置服务已经启动的标记
        isAlive = true;
        //将录制实例添加到service中
        addView();
        //启动线程读取数据库并找出所有闹钟和24小时录制的摄像头
        new Thread(new ReadDBRunnable()).start();
        if (!CommonTools.isServiceWork(MainApplication.getWholeContext(),"com.ipcamerasen5.record.service.LocalUDPService",Constant.PACKGENAME)){
            Log.d("RecordService_onCreate","iswork is "+false);
            Intent intent = new Intent(this,LocalUDPService.class);
            startService(intent);
        }
    }

    private void initPreData(){
        initData();
        //注册事件bus
        EventBus.getDefault().register(this);
        Log.e("initPreadta","initpredata");
        registerReceivers();
        //没有外置存储时只注册u盘监听，等插上u盘再去做初始化
        registerStorageReceiver();
        //确保数据库正常。
        CommonTools.createFile();
    }

    @Override
    public void onDestroy() {

        IpcameraExit mIpcameraExit = new IpcameraExit();
        EventBus.getDefault().post(mIpcameraExit);

        Log.d("RecordService","recordService_ondestroy");
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(storage_Receiver);
        EventBus.getDefault().unregister(this);
        isAlive = false;
        //释放所有的资源
        backgroundPresenters.clear();
        backgroundPresenters = null;
        mClocks.clear();
        mClocks = null;
        mIpCamDevices.clear();
        mIpCamDevices = null;
        dids_DB.clear();
        dids_DB = null;
        dids_clock.clear();
        dids_clock = null;
        threads_round.clear();
        threads_round = null;
        mTempObjects.clear();
        mTempObjects = null;
        dids_round.clear();
        dids_round = null;
        isAlive = false;
        hasInit = false;
        hasStorage = false;
    }

    //处理录制开关状态的事件
    public static void dealwithRecordIsStop(String did,String isStop){
        if (isAlive){
            int position = dids_DB.indexOf(did);
            if (position != -1){
                IpCamDevice mIpCamDevice = mIpCamDevices.get(position);
                mIpCamDevice.setIsStop(isStop);
            }
        }
    }
    //添加一个摄像头
    public static void addIpcameraDevice(final String did){
        //使用延时添加
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<IpCamDevice>  mIpCamDevices_temp =  IpCamDevice.findAll(IpCamDevice.class);
                if (null != mIpCamDevices_temp && mIpCamDevices_temp.size() != 0){
                    for (IpCamDevice mDevice : mIpCamDevices_temp){
                        if (mDevice.getDid().equals(did)){
                            mIpCamDevices.add(mDevice);
                            dids_DB.add(did);
                        }
                    }
                }
            }
        },500);
    }

    //当录制方式改为24小时录制时
    public static void dealwithRoundRecord(final String roundDid){//新增24小时录制
        Log.e("dealWithRoundRecord","新增24小时录制");
        if (isAlive) {
            IpCamDevice mIpCamDevice = null;
            List<IpCamDevice> devices = DataSupport.findAll(IpCamDevice.class);
            for (IpCamDevice mDevice : devices){
                if (mDevice.getDid().equals(roundDid)){
                    mIpCamDevice = mDevice;
                    Log.e("dealWithRoundRecord","did is "+mDevice.getDid());
                }
            }
            if (null != mIpCamDevice) {
                Log.e("dealWithRoundRecord","isStop is "+mIpCamDevice.getIsStop());
                if (mIpCamDevice.getIsStop().equals("2")) {
                    RoundRunnable mRoundRunnable = new RoundRunnable(roundDid);
                    Thread mThread = new Thread(mRoundRunnable);
                    mThread.start();
                    dids_round.add(roundDid);
                    ThreadWithDid mThreadWithDid = new ThreadWithDid(roundDid,mThread,mRoundRunnable);
                    threads_round.add(mThreadWithDid);
                } else {
                    if (!dids_round.contains(roundDid)){
                        dids_round.add(roundDid);
                    }
                }
            }
        }
    }

    /**
     * 处理24小时转变成其他录制方式，将24小时的闹钟线程关闭
     * @param changeDid
     */
    public static void dealWithRoundChange(String changeDid){
        int position = dids_round.indexOf(changeDid);
        ThreadWithDid current_thread = null;
        Log.e("dealwithRoundChange","threads_round.size is "+threads_round.size()+"position is "+position);
        if (position != -1){
            //移除
            dids_round.remove(changeDid);
        }
        //停止当前的24小时录制线程
        for (ThreadWithDid mThreadWithDid : threads_round){
            if (mThreadWithDid.getDid().equals(changeDid)){
                RoundRunnable mRoundRunnable = (RoundRunnable) mThreadWithDid.mRunnable;
                mRoundRunnable.flag = false;
                mThreadWithDid.stopThread();
                current_thread = mThreadWithDid;
            }
        }
        if (null != current_thread){
            threads_round.remove(current_thread);
            Log.e("dealwithRoundChange","threads_round.size is "+threads_round.size());
        }
    }

    /**
     * 处理闹钟录制变成其他录制方式
     * @param changeDid
     */
    public static void dealWithClockChange(String changeDid){
        if (null != threads_clock && threads_clock.size() == 0){
            return;
        }
        ThreadWithDid current_thread = null;
        //停止当前的闹钟录制线程
        if (null != threads_clock) {
            for (ThreadWithDid mThreadWithDid : threads_clock) {
                if (mThreadWithDid.getDid().equals(changeDid)) {
                    ClockForHours mRunnable = (ClockForHours) mThreadWithDid.mRunnable;
                    mRunnable.flag = false;
                    mThreadWithDid.stopThread();
                    current_thread = mThreadWithDid;
                }
            }
            if (null != current_thread) {
                threads_clock.remove(current_thread);
                Log.e("dealWithClockChange", "threads_round.size is " + threads_round.size());
            }
        }
    }

    /**
     * 开启24小时录制线程
     */
    private void startRoundThread() {
        for (final String did : dids_round) {
            IpCamDevice mIpCamDevice = mIpCamDevices.get(dids_DB.indexOf(did));
            //录制开关打开了
            if (mIpCamDevice.getIsStop().equals("2")) {
                RoundRunnable mRoundRunnable = new RoundRunnable(did);
                Thread mThread = new Thread(mRoundRunnable);
                mThread.start();
                ThreadWithDid thread_did = new ThreadWithDid(did,mThread,mRoundRunnable);
                threads_round.add(thread_did);
            }
        }
    }
    /**
     * 闹钟录制线程
     */
    class recordServiceRunnable implements Runnable{
        @Override
        public void run() {
            while (true) {
                if (null != mTempObjects && mTempObjects.size() != 0) {
                    String currentStructure = DateUtil.currentTimeToClock();//当前时钟结构体
                    Log.e("clock_thread", "tempSize is " + mTempObjects.size());
                    for (final ClockTempObject structure : mTempObjects) {
                        final String did = structure.getDid();
                        long duration = structure.getDuration();
                        long leftDuration;
                        Log.e("clock_thread", "duration is " + duration + " startTime is " + structure.getStartTime());
                        IpCamDevice mIpCamDevice = findDeviceFromDB(did);
                        final int position = dids_DB.indexOf(did);
                        //除去星期外的时间值80928---》》0928
                        String noRoundTime = structure.getStartTime().substring(1, structure.getStartTime().length());
                        String currentStructureTime = currentStructure.substring(1, currentStructure.length());
                        int noRoundTime_int = Integer.parseInt(noRoundTime);
                        int currentStructureTime_int = Integer.parseInt(currentStructureTime);
                        //是否还在录制区间
                        boolean flag_duration = false;
                        if (structure.getStartTime().startsWith("8") || (structure.getStartTime().substring(0, 1).equals(currentStructure.substring(0, 1)))) {
                            if (currentStructureTime_int > noRoundTime_int) {
                                Log.e("clock_thread_int", "int1 = " + currentStructureTime_int + " int2 is " + noRoundTime_int);
                                leftDuration = duration - CommonTools.computerDuration(currentStructureTime_int, noRoundTime_int);
                                Log.e("clock_thread_int", "duration is " + duration + "leftDuration is " + leftDuration);
                                if (leftDuration > 0) {
                                    flag_duration = true;
                                    duration = leftDuration;
                                    Log.e("clock_thread_duration", "duration is " + duration);
                                }
                            }
                        }
                        Log.e("clock_thread", "did is " + did + " dids.size is " + dids_DB.size() + " noRoundTime is " + noRoundTime + "currentStructure is" + currentStructure);
                        if (structure.getStartTime().equals(currentStructure) || (structure.getStartTime().startsWith("8") && currentStructure.endsWith(noRoundTime)) || flag_duration) {
                            if (mIpCamDevice.getIsStop().equals("2") && !mIpCamDevice.getStatus().equals("2")) {
                                //开始录制，通过eventBus发送通知
                                if (null != backgroundPresenters && backgroundPresenters.size() >= position) {
                                    //当前设备不在录制时
                                    if (position != -1 && position < backgroundPresenters.size()) {
                                        if (backgroundPresenters.get(position).recordStatus != Constant.status_recording) {
                                            structure.setHasStartRecord(true);
                                            ClockForHours mClockForHours = new ClockForHours((int) duration, position);
                                            Thread mThread = new Thread(mClockForHours);
                                            mThread.start();
                                            ThreadWithDid mThreadWithDid = new ThreadWithDid(structure.getDid(), mThread, mClockForHours);
                                            threads_clock.add(mThreadWithDid);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 24小时录制线程
     */
    static class RoundRunnable implements Runnable{
        private String roundDid;
        public boolean flag = true;
        int position_presenter;
        public RoundRunnable(String roundDid) {
            this.roundDid = roundDid;
            position_presenter = dids_DB.indexOf(roundDid);
        }

        @Override
        public void run() {
            int times = 0;
            Log.e("roundThread","times1 is"+times);
            while (flag) {
                try {
                    times++;
                    Log.e("roundThread", "times2 is " + times);
                    Thread.sleep(2000);
                    IpCamAlarmEvent mEvent = new IpCamAlarmEvent(roundDid, 3600000);
                    mEvent.setType(Constant.type_round);
                    EventBus.getDefault().post(mEvent);
                    Thread.sleep(3606000);//3606000
                    backgroundPresenters.get(position_presenter).stopRecord(Constant.type_round);
                    times ++;
                    Log.e("roundThread","times3 is "+times);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 大于一个小时的闹钟录制处理线程
     */
    static class ClockForHours implements Runnable {
        private int mDuration = 0;
        private int position_launcher = 0;
        private int num;
        public boolean flag = true;

        public ClockForHours(int duration, int position) {
            this.mDuration = duration;
            this.position_launcher = position;
            num = mDuration / 3600000;
        }

        @Override
        public void run() {
            try {
                if (num > 0) {
                    //按小时录制完
                    for (int i = 0; i < num; i++) {
                        if (flag) {
                            Thread.sleep(1000);
                            IpCamAlarmEvent event = new IpCamAlarmEvent(dids_DB.get(position_launcher), 3600000);
                            event.setType(0);
                            EventBus.getDefault().post(event);
                            Thread.sleep(3601000);
                            if (null != backgroundPresenters && backgroundPresenters.size() >= position_launcher) {
                                backgroundPresenters.get(position_launcher).stopRecord(Constant.type_clock);
                            }
                        }
                    }
                    //开始录制余数
                    Thread.sleep(1000);
                    IpCamAlarmEvent event = new IpCamAlarmEvent(dids_DB.get(position_launcher), mDuration % 3600000);
                    event.setType(0);
                    EventBus.getDefault().post(event);
                    Thread.sleep(mDuration % 3600000 + 1000);
                    Log.e("ClockForHours","position is "+position_launcher);
                    if (null != backgroundPresenters && backgroundPresenters.size() >= position_launcher) {
                        backgroundPresenters.get(position_launcher).stopRecord(Constant.type_clock);
                    }
                } else if (num == 0) {
                    //不足一个小时
                    Thread.sleep(1000);
                    IpCamAlarmEvent event = new IpCamAlarmEvent(dids_DB.get(position_launcher), mDuration);
                    event.setType(0);
                    EventBus.getDefault().post(event);
                    Thread.sleep(mDuration + 1000);
                    Log.e("ClockForHours","position is "+position_launcher+" duration is "+mDuration);
                    if (null != backgroundPresenters && backgroundPresenters.size() >= position_launcher) {
                        backgroundPresenters.get(position_launcher).stopRecord(Constant.type_clock);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化数据线程
     */
    class ReadDBRunnable implements Runnable{
        @Override
        public void run() {
            List<IpCamDevice> mIpCamDevices_temp = IpCamDevice.findAll(IpCamDevice.class);
            if (null != mIpCamDevices_temp && mIpCamDevices_temp.size() != 0) {
                for (IpCamDevice mDevice : mIpCamDevices_temp) {
                    mIpCamDevices.add(mDevice);
                }
                //将录制分类
                for (IpCamDevice mIpCamDevice : mIpCamDevices) {
                    if (null != mIpCamDevice.getRecordType()) {
                        if (mIpCamDevice.getRecordType().equals("0")) {
                            dids_clock.add(mIpCamDevice.getDid());
                        } else if (mIpCamDevice.getRecordType().equals("1")) {
                            dids_round.add(mIpCamDevice.getDid());
                        }
                    }
                }
                //找出所有的符合条件的闹钟录制
                for (String did : dids_clock) {
                    mClocks.addAll(Clock.where("did = ?", did).find(Clock.class));
                }
                Log.e("RecordService", "mClocks size is " + mClocks.size());
                for (Clock mClock : mClocks) {
                    if (mClock.getDuration() <= 0) {
                        continue;
                    }
                    String time = mClock.getStartTime();
                    time = time.replace(":", "");
                    String time2 = mClock.getTimes();//02045678:大于零表示当天有该闹钟:周一到周日中哪些需要录制
                    List<String> cycle = CommonTools.formatTimes(time2);
                    if (cycle.contains("8")) {
                        String roundTime = 8 + time;//81703:代表每天下午5点零三开始录制。八代表everyDay
                        int duration = mClock.getDuration();
                        String did = mClock.getDid();
                        ClockTempObject mClockTempObject = new ClockTempObject();
                        mClockTempObject.setDuration(duration);
                        mClockTempObject.setDid(did);
                        mClockTempObject.setHasStartRecord(false);
                        mClockTempObject.setStartTime(roundTime);
                        mTempObjects.add(mClockTempObject);
                    } else {
                        for (int i = 0; i < cycle.size(); i++) {
                            String roundTime = cycle.get(i) + time;
                            int duration = mClock.getDuration();
                            String did = mClock.getDid();
                            ClockTempObject mClockTempObject = new ClockTempObject();
                            mClockTempObject.setDuration(duration);
                            mClockTempObject.setDid(did);
                            mClockTempObject.setHasStartRecord(false);
                            mClockTempObject.setStartTime(roundTime);
                            mTempObjects.add(mClockTempObject);
                        }
                    }
                }
                //开始定时录制闹钟
                new Thread(new recordServiceRunnable()).start();
                //开始24小时录制
                startRoundThread();
            }
        }
    }


    //-------------------------------------------------------------------------------------------------//
    /**
     * 处理sensor触发广播和增删摄像头广播
     */
    private int receiver_times = 0;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Constant.sensorBroadCast_change:
                    if (hasStorage) {
                        //存在u盘再进行处理
                        String did = intent.getStringExtra("receiver_mac");
                        int position = dids_DB.indexOf(did);
                        Log.e("receiver_onReceive", "did is " + did + " position is " + position + "收到sensor触发广播次数为：" + (++receiver_times));
                        if (backgroundPresenters.size() != 0 && position != -1) {
                            dealWithSensorRecord(did);
                        }
                    }
                    break;
                case Constant.cameraChange:
                    int type = intent.getIntExtra("type",0);
                    String did_change = intent.getStringExtra("CameraDID");
                    Log.e("receiver_onReceive","type is"+type+" changeDid is "+did_change);
                    //删除摄像头
                    if (type == 1){
                        deleteOneDevice(did_change);
                        EventBus.getDefault().post(new DeleteIpcameraEvent(did_change));
                        //释放LT资源
                        LTSDKManager.getInstance().deleteOneDevice(did_change);
                        //删除线程
                        int position = dids_round.indexOf(did_change);
                        ThreadWithDid current_thread = null;
                        Log.e("dealwithRoundChange","threads_round.size is "+threads_round.size()+"position is "+position);
                        if (position != -1){
                            //移除
                            dids_round.remove(did_change);
                        }
                        //停止当前的24小时录制线程
                        for (ThreadWithDid mThreadWithDid : threads_round){
                            if (mThreadWithDid.getDid().equals(did_change)){
                                RoundRunnable mRoundRunnable = (RoundRunnable) mThreadWithDid.mRunnable;
                                mRoundRunnable.flag = false;
                                mThreadWithDid.stopThread();
                                current_thread = mThreadWithDid;
                            }
                        }
                        if (null != current_thread){
                            threads_round.remove(current_thread);
                            Log.e("dealwithRoundChange","threads_round.size is "+threads_round.size());
                        }

                        List<IpCamDevice> mIpCamDevices = DataSupport.findAll(IpCamDevice.class);
                        int size = CommonTools.initStartCameraDevice().size();
                        AppLog.e("mIpcameraDevices.size is " + mIpCamDevices.size()+" size is " + size);
                        if (mIpCamDevices.size() == 0 && size == 0){
                            stopSelf();
                        }
                    }
                    else if (type == 0){
                        //增加摄像头
                        addOneDevice(did_change);
                        EventBus.getDefault().post(new AddIpcameraEvent(did_change));
                        // TODO: 2017/10/19 改由launcher去执行该方法。
//                        LTSDKManager.getInstance().addOneDevice(did_change);
                    }
                    break;
                case Intent.ACTION_SHUTDOWN:
                    Log.e("receiver_onReceive","action is "+Intent.ACTION_SHUTDOWN);
                    //停止所有的录制
                    EventBus.getDefault().post(new IpcamStopAllRecord());
                    break;
            }
        }
    };

    /**
     * 添加一个摄像头
     * @param did
     */
    private void addOneDevice(String did){
        Log.e("addoneDevice","did is " + did);
        //防止出现上次未删除相同的摄像头又添加了
        IpCamDevice device_already = CommonTools.findDeviceFromDB(did);
        if (null != device_already){
            device_already.delete();
        }
        int deletePosition = -1;
        for (int i=0; i<mIpCamDevices.size(); i++){
            if (Objects.equals(mIpCamDevices.get(i).getDid(),did)){
                deletePosition = i;
            }
        }
        if (deletePosition != -1){
            mIpCamDevices.remove(deletePosition);
        }
        //------
        IpCamDevice device = new IpCamDevice();
        List<String> names = CommonTools.getDeviceName(MainApplication.getWholeContext());
        File file = new File(Constant.recordPath);
        boolean isOk = CommonTools.createFolder(file, did);
        if (isOk) {
            File file_did = new File(Constant.recordPath + File.separator + did);
            boolean isOk2 = CommonTools.createFolder(file_did, "sensor");
            if (isOk2) {
                device.setSensorPath(file_did.getAbsolutePath() + File.separator + "sensor");
            }
            boolean isOk3 = CommonTools.createFolder(file_did, "second");
            if (isOk3) {
                device.setSecondPath(file_did.getAbsolutePath() + File.separator + "second");
            }
            boolean isOk4 = CommonTools.createFolder(file_did, "clock");
            if (isOk4) {
                device.setClockPath(file_did + File.separator + "clock");
            }
            boolean isok5 = CommonTools.createFolder(file_did, "round");
            if (isok5) {
                device.setRoundPath(file_did + File.separator + "round");
            }
            boolean isok6 = CommonTools.createFolder(new File(Constant.thumpPath), did);
            if (isok6) {
                device.setThumpPath(Constant.thumpPath + File.separator + did);
            }
            boolean isok7 = CommonTools.createFolder(file_did, "dynamic");
            if (isok7) {
                device.setDynamicPath(file_did + File.separator + "dynamic");
            }
        }
        if (names.size() != 0 && null != names){
            device.setAliasName(names.get(names.size()-1));
        }
        device.setStatus("1");
        device.setIsStop("2");
        device.setSecondIsOpen("2");

        //did为null校验
        if (did == null || "".equals(did)){
            List<String> dids = new ArrayList();
            dids = CommonTools.initStartCameraDevice();
            int size = dids.size();
            did = dids.get(size-1);
            AppLog.e("addOneDevice"+"did is " + did);
        }
        Log.e("addoneDevice"," did1 is " + did);

        device.setDid(did);
        device.save();//存储(新增一行)
        Log.e("addoneDevice"," did2 is " + did);
        //更新数据
        dids_DB.add(did);
        Log.e("addoneDevice"," did3 is " + did);
        mIpCamDevices.add(device);

        RecordPresenter2 presenter = new RecordPresenter2(did, 1, MainApplication.getWholeContext(),0);
        backgroundPresenters.add(presenter);
    }

    /**
     * 删除一个摄像头
     * @param did
     */
    private void deleteOneDevice(String did){
        int position = -1;
        int position_DB = -1;
        List<Clock> mClocks = DataSupport.findAll(Clock.class);
        Clock mClock = null;
        if (null != mClocks){
            for (int i = 0; i<mClocks.size(); i++){
                if (mClocks.get(i).getDid().equals(did)){
                    mClock = mClocks.get(i);
                }
            }
        }
        if (null != mClock){
            mClock.delete();
        }
        for (int i=0; i<mIpCamDevices.size(); i++){
            if (mIpCamDevices.get(i).getDid().equals(did)){
                position = i;
                mIpCamDevices.get(i).delete();
            }
        }
        for (int i=0; i<dids_DB.size(); i++){
            if (dids_DB.get(i).equals(did)){
                position_DB = i;
            }
        }
        if (position != -1 && mIpCamDevices.size() > position){
            mIpCamDevices.remove(position);
        }
        else if (position == -1){
            CommonTools.deleteOneDeviceFromDB(did);
        }
        AppLog.e("deleteOneDevice position is " + position+" positionDb is " + position_DB);
        if (position_DB != -1 && backgroundPresenters.size() > position_DB){
            dids_DB.remove(position_DB);
            backgroundPresenters.get(position_DB).release();
            RecordPresenter2 mRecordPresenter2 = backgroundPresenters.get(position_DB);
            mRecordPresenter2 = null;
            backgroundPresenters.remove(position_DB);
        }
    }

    /**
     * 添加在后台用作录制的录制实例
     * 该操作随service的生命周期只操作一遍
     */
    public void addView() {
        Log.e("addView", "did.size is" + dids_DB.size() + "userId2 size is " + dids_DB.size() + "backpresentet size is " + backgroundPresenters.size());
        for (String did : dids_DB){
            int position = dids_DB.indexOf(did);
            int recordType = 0;
            if(null != findDeviceFromDB(did)){
                if (null != findDeviceFromDB(did).getRecordType() && !findDeviceFromDB(did).getRecordType().equals("")){
                    recordType = Integer.parseInt(findDeviceFromDB(did).getRecordType());
                }
            }
            RecordPresenter2 presenter = new RecordPresenter2(did, 1, MainApplication.getWholeContext(),recordType);
            boolean shouldAddOnePresenter = true;
            for (int i =0; i<backgroundPresenters.size(); i++){
                if (backgroundPresenters.get(i).device_did.equals(did)){
                    shouldAddOnePresenter = false;
                }
            }
            if (shouldAddOnePresenter){
                backgroundPresenters.add(presenter);
            }
        }
        Log.e("addView","background.size is "+backgroundPresenters.size());
    }

    /**
     * 注册sensor和摄像头相关广播
     */
    private void registerReceivers() {
        //后台录制的内容添加好了之后注册监听sensor触发的广播
        IntentFilter mIntentFilter1 = new IntentFilter();
        mIntentFilter1.addAction(Constant.sensorBroadCast_change);//sensor触发开始录制
        mIntentFilter1.addAction(Constant.cameraChange);//摄像头更改时发出的广播
        mIntentFilter1.addAction(Intent.ACTION_SHUTDOWN);
        registerReceiver(mReceiver,mIntentFilter1);
    }

    /**
     * 注册磁盘更改广播
     */
    private boolean isStorageRegister = false;
    private void registerStorageReceiver(){
        if (! isStorageRegister){
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//存在还未被挂载
            mIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);//存在且已经被挂载
            mIntentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);//sd卡拔出，但是挂载还没移除
            mIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);//sd移除，挂载点也解除
            mIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);//开始进行扫描
            mIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);//扫描结束
            mIntentFilter.addDataScheme("file");
            registerReceiver(storage_Receiver,mIntentFilter);
            isStorageRegister = true;
        }
    }


    /**
     * 用来给recordpresent中的duration赋值
     * @param did
     * @param duration
     */
    public void initRecordPresentsDuration(String did,int duration){
        int position = dids_DB.indexOf(did);
        Log.e("initRecordPresents","duration is"+duration+" position is"+position);
        backgroundPresenters.get(position).currentDuration = duration;
        backgroundPresenters.get(position).startTime = System.currentTimeMillis();
    }

    /**
     * 录制完成更新数据库
     * @param did
     */
    public void updateDB(String did) {
        int position = dids_DB.indexOf(did);
        IpCamDevice current_device = findDeviceFromDB(mIpCamDevices.get(position).getDid());
        Log.e("updateDB","isStop is "+current_device.getIsStop());
        if (mIpCamDevices.get(position).getStatus().equals("0")){//离线了，状态不用从录制更改到在线

        }
        else {//正常录制结束：由录制状态更改到在线状态
            current_device.setStatus("1");//保存的操作在recordpresent中
        }
        current_device.save();
    }
    /**
     * 处理闹钟响应事件
     */
    public void dealWithClockRecord(IpCamAlarmEvent event) {
        IpCamDevice currentIpcameraDevice = null;
        long duration = event.getDuration();
        String did = event.getDid();
        int record_type = event.getType();

        Log.e("dealwithClockRecord", "type is " + record_type+event.getDid() + "duration is " + duration+"dids_db.size is " + dids_DB.size());
        int position_db = dids_DB.indexOf(did);
        if (position_db != -1) {
            currentIpcameraDevice = findDeviceFromDB(mIpCamDevices.get(position_db).getDid());
            Log.e("dealwithClockRecord", "statue is " + currentIpcameraDevice.getStatus() + "position is" + position_db + "presenters.size is " + backgroundPresenters.size() + "dids.size is " + dids_DB.size());
        }
        else {
            Log.e("dealwithClockRecord","position is -1, return");
            return;
        }
        //录制开关是打开的再去处理
        if (currentIpcameraDevice.getIsStop().equals("2")) {
            Log.e("dealwithClockRecord","isStop is 2");
            if (currentIpcameraDevice.getStatus().equals("0")) {//设备掉线了
                //掉线了只设置设备的录制时长和录制类型
                backgroundPresenters.get(position_db).setDurationAndRecordType((int) duration,record_type);
                Log.e("dealwithClockRecord","status is 0");
            }
            else if (currentIpcameraDevice.getStatus().equals("1") || currentIpcameraDevice.getStatus().equals("2")) {
                Log.e("dealwithClockRecord", "status is 1 coming in" + " ");
                for (RecordPresenter2 mPresenter : backgroundPresenters) {
                    //startplay中进行userId判断
                    boolean flag = mPresenter.startRecord(did, record_type, duration);
                    Log.e("dealwithClockRecord","flag is "+flag);
                    //某个设备开始录制，更改UI
                    if (flag) {
                        currentIpcameraDevice.setStatus("2");
                        currentIpcameraDevice.save();
                        EventBus.getDefault().post(currentIpcameraDevice);
                    }
                }
            }
        }
        else {//设备录制开关未打开
            for (RecordPresenter2 mPresenter : backgroundPresenters) {
                //录制开关未打开
                if (mPresenter.device_did.equals(did)){
                    backgroundPresenters.get(position_db).setDurationAndRecordType((int) duration,record_type);
                }
            }
        }
    }

    /**
     * 处理sensor触发录制
     * @param did
     */
    public void dealWithSensorRecord(final String did) {
        IpCamDevice currentIpcameraDevice = null;
        int position_db = dids_DB.indexOf(did);
        currentIpcameraDevice = findDeviceFromDB(mIpCamDevices.get(position_db).getDid());
        Log.e("onReceive", "backgroundPresentes.size is " + backgroundPresenters.size());
        Log.e("onReceive", "devices.size is " + mIpCamDevices.size());
        if (null != currentIpcameraDevice.getIsStop() && null != currentIpcameraDevice.getRecordType()){
            if (currentIpcameraDevice.getIsStop().equals("2")) {
                if (currentIpcameraDevice.getRecordType().equals("2")) {//如果是sensor触发录制
                    String sensor_path = currentIpcameraDevice.getSensorPath();
                    String file_sensor_name = System.currentTimeMillis() + ".mp4";
                    Log.e("onReceive", "did is " + did + " position is " +position_db + "file_sensor_name is " + sensor_path + File.separator + file_sensor_name);
                    final RecordPresenter2 current_sensor_record = backgroundPresenters.get(position_db);
                    //todo 设置UI为正在录制
//                            dealwithSensorRecord(position);
                    if (currentIpcameraDevice.getSecondIsOpen().equals("2")) {
                        //10秒小视频打开
                        Log.e("onReceive", "十秒小视频开关打开，开始录制十秒");
                        current_sensor_record.startRecord(currentIpcameraDevice.getDid(), Constant.type_sensor, 13000);
                        currentIpcameraDevice.setStatus("2");
                        currentIpcameraDevice.save();
                        EventBus.getDefault().post(currentIpcameraDevice);
                        //停止十秒钟的录制
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                current_sensor_record.stopRecord(Constant.type_sensor);
                            }
                        }, 13500);
                        //开启2分50秒的录制
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                current_sensor_record.startRecord(did, Constant.type_sensor, 170000);
                            }
                        }, 18000);
                        //停止2分50秒的录制
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                current_sensor_record.stopRecord(Constant.type_sensor);
                            }
                        }, 180000);
                    } else {
                        //开始录制三分钟
                        current_sensor_record.startRecord(did, Constant.type_sensor, 180000);
                        //停止三分钟的录制
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                current_sensor_record.stopRecord(Constant.type_sensor);
                            }
                        }, 180000);
                    }
                }
            }
        }
    }

    //--------------------------------------------------事件总线--------------------------------------------------//
    /**
     * 处理录制响应事件，该event由RecordService或打开录制开关时由recordpresenter发出
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithEvent(final IpCamAlarmEvent event){
        int delayed =(int) (1 + Math.random()*(5))*100+1000;
        Log.e("IpCamAlarm","currentDid is "+event.getDid()+"duration is "+event.getDuration());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dealWithClockRecord(event);
            }
        },delayed);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void dealWithMoveRecord(final IpCamAlarmEvent event){
//        int delayed =(int) (1 + Math.random()*(5))*100+1000;
//        Log.e("IpCamAlarm","currentDid is "+event.getDid()+"duration is "+event.getDuration()+"where is "+event.getWhere());
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dealWithClockRecord(event);
//            }
//        },delayed);
//    }

    /**
     * 由recordservice发出
     * 当遍历到该闹钟时，需要录制闹钟开关却被关闭了，先记录下duration。
     */
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void dealwithEvent_witch_close(IpCamAlarmEvent_Switch_close event){
        Log.e("dealwithEvent","did is "+event.getDid());
        initRecordPresentsDuration(event.getDid(),event.getDuration());
    }

    /**
     * 由recordPresenter发出:录制完成时发出。
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithRecordStopEvent(IpCamStopRecord event){
        Log.e("dealwithrecordstopevent","did is "+event.getDid());
        String did = event.getDid();
        updateDB(did);
    }

    /**
     * 处理移动侦测录制
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithDynamicRecord(IpCamMoveEvent event){
        String did = event.did;
        int position = dids_DB.indexOf(did);
        IpCamDevice currentIpcameraDevice = findDeviceFromDB(event.did);
        //如果用户打开了录制开关则进行录制
        if (null != currentIpcameraDevice && currentIpcameraDevice.getIsStop().equals("2") && currentIpcameraDevice.getRecordType().equals("3") ) {
            if (event.getStatus() == 0) {
                if (null != backgroundPresenters && backgroundPresenters.size() > 0){
                    Log.e("dealwithDynamicRecord","发送即将进行移动侦测录制的event");
                    currentIpcameraDevice.setStatus("2");
                    currentIpcameraDevice.save();
                    EventBus.getDefault().post(currentIpcameraDevice);
                    //duration由recordpresent2去计算
                    backgroundPresenters.get(position).startRecord(did, Constant.type_move, 36000);
                }
            } else if (event.getStatus() == 1) {
                Log.e("dealwithDynamicRecord","即将暂停移动侦测录制");
                if (null != backgroundPresenters && backgroundPresenters.size() > 0){
                    backgroundPresenters.get(position).stopRecord(Constant.type_move);
                }
            }
        }
    }

    /**
     * 接收单个摄像头状态
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithGidStaus(DataSourceListenerEvent event){
        Log.e("dealwithGidStaus","gid is "+event.getDid()+" eventStatus is "+event.getStatus());
        if (event.getStatus() == 1){
            IpCamDevice mIpCamDevice = findDeviceFromDB(event.getDid());
            //设置离线
            if (null != mIpCamDevice){
                mIpCamDevice.setStatus("0");
                mIpCamDevice.save();
                EventBus.getDefault().post(mIpCamDevice);
                if (null != backgroundPresenters && backgroundPresenters.size() > 0){
                    backgroundPresenters.get(position).stopForOffline();
                }
            }
            //设备在在线的情况下有时也会回调onDisconnected()所有此处重新创建一次
            LTSDKManager.getInstance().createDevice_one(MainApplication.getWholeContext(),event.getDid());
        }
        else if (event.getStatus() == 0){
            //收到连接成功回调
            String gid = event.getDid();
            IpCamDevice mIpCamDevice = CommonTools.findDeviceFromDB(gid);
            if (null != mIpCamDevice && mIpCamDevice.getStatus().equals("0")){
                AppLog.e("dealwithGidStaus"+"status is " + mIpCamDevice.getStatus());
                mIpCamDevice.setStatus("1");
                mIpCamDevice.save();
                EventBus.getDefault().post(mIpCamDevice);
//                //继续接着录制
                IpcamStopStatusChanges event_2 = new IpcamStopStatusChanges(mIpCamDevice.getDid(), "2");
                event_2.setIsStop("2");
                EventBus.getDefault().post(event_2);
            }
        }
    }

    /**
     * 接收SDK全局状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithLTGlobalStatus(LTSDKGlobalStatusEvent globalStatusEvent){
        String s = globalStatusEvent.getGid();
        int i = globalStatusEvent.getStatus();
        //此处未连上网的设备状态不回来，增加计时更改离线状态功能
        Log.e("dealwithGidStaus","s is "+s+" i is "+i);
        IpCamDevice mIpCamDevice = findDeviceFromDB(s);
        int position = LTSDKManager.getInstance().gids.indexOf(s);
        if (gids_statu.contains(s)){
            gids_statu.remove(s);
        }
        if (i == 1 || i == 3){
//            LTSDKManager.getInstance().createDevice_one(MainApplication.getWholeContext(),s);
//            LTSDKManager.getInstance().startPlay(s);
            if (null != mIpCamDevice && mIpCamDevice.getStatus().equals("0")){
                AppLog.e("dealwithGidStaus"+"status is " + mIpCamDevice.getStatus());
                mIpCamDevice.setStatus("1");
                mIpCamDevice.save();
                EventBus.getDefault().post(mIpCamDevice);
//                //继续接着录制
                IpcamStopStatusChanges event = new IpcamStopStatusChanges(mIpCamDevice.getDid(), "2");
                event.setIsStop("2");
                EventBus.getDefault().post(event);
            }
        }
        //在线,需要post
//        EventBus.getDefault().post(mIpCamDevice);
//        if (i > 0) {
//            if (!mIpCamDevice.getStatus().equals("2")) {
//                mIpCamDevice.setStatus("1");
//                mIpCamDevice.save();
//                EventBus.getDefault().post(mIpCamDevice);
//                //继续接着录制
//                IpcamStopStatusChanges event = new IpcamStopStatusChanges(mIpCamDevice.getDid(), "2");
//                event.setIsStop("2");
//                EventBus.getDefault().post(event);
//            }
//        }
        if (i < 0){
            //设置离线
            if (null != mIpCamDevice){
                mIpCamDevice.setStatus("0");
                mIpCamDevice.save();
                EventBus.getDefault().post(mIpCamDevice);
                if (null != backgroundPresenters && backgroundPresenters.size() > 0){
                    backgroundPresenters.get(position).stopForOffline();
                }
            }
        }
    }

    // TODO: 2017/10/19 eventbus问题发一遍收到三遍
    /**
     * 发送视频文件广播
     */
    long sendTime = 0;
    @Subscribe (threadMode =  ThreadMode.MAIN)
    public void dealVideoBroadCast(IpcameraSecondBroadcast event){
        LogUtils.e("sendVideoPathCast","send");
        if ((System.currentTimeMillis() - sendTime) > 10000){
            String path = event.getPath();
            sendVideoPathCast(path);
            sendTime = System.currentTimeMillis();
        }
    }
    private String videoPath = "video_path";
    private long lastSensorCastTime = 0;//eventbus的bug，此处被调了两次
    private void sendVideoPathCast(String path) {
        if ((System.currentTimeMillis() - lastSensorCastTime) > 5000) {
            lastSensorCastTime = System.currentTimeMillis();
            LogUtils.e("sendVideoPathCast", "path will send");
            Intent intent = new Intent(Constant.tenSecondVideoBroadCast);
            intent.putExtra(videoPath, path);
            mContext.sendBroadcast(intent);
        }
    }

}
