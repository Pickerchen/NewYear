package com.ipcamerasen5.record.ui.presenter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.adapter.RecordDeviceAdapter;
import com.ipcamerasen5.record.callback.DialogCallback;
import com.ipcamerasen5.record.common.CommonTools;
import com.ipcamerasen5.record.common.Constant;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.entity.IpcameraSecondBroadcast;
import com.ipcamerasen5.record.event.IpcamStopAllRecord;
import com.ipcamerasen5.record.ui.view.CustomDialog;
import com.ipcamerasen5.record.ui.view.CustomGlsurfaceView;
import com.ipcamerasen5.record.ui.view.IRecordActivity;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nes.ltlib.utils.AppLog;
import nes.ltlib.utils.LogUtils;

import static com.ipcamerasen5.record.ui.view.RecordSettingActivity.did;
import static com.tencent.bugly.crashreport.crash.c.j;

/**
 * RecordActivity业务逻辑
 * Created by chenqianghua on 2017/4/7.
 */

public class ActivityRecordPresenter implements DialogCallback {
    private Context mContext;
    private Activity mActivity;
    private IRecordActivity mIRecordActivity;
    private List<String> userIds = new ArrayList<>();//userID,userid不能存放到数据库中，每次重新打开应用userID就会发生变化
    private List<String> names = new ArrayList<>();
    private  List<String> userIds2 = new ArrayList<>();
    private List<String> dids_cameraStatus = new ArrayList<>();
    private List<String> dids = new ArrayList<>();//从共享的配置文件中读取到的did数量
    private List<String> dids_db = new ArrayList<>();//已经保存到数据中的did数量
    private List<CustomGlsurfaceView> surfaceViews = new ArrayList<>();
    private boolean hasStorage = false;

    //插入移动存储设备提示
    private CustomDialog mCustomDialog;//未插入移动存储设备时的弹出提示
    private CustomDialog mChooseStorageDialog;//选择存放路径弹出框
    private CustomDialog mStorageNewRemind;//新设备插入询问是否更改存储路径
    private CustomDialog mCustomDialog_storage_tip;//显示提示内存不足的对话框

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 插入U盘广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.e("onReceive","action is "+action);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){
                hasStorage = true;
                if (mCustomDialog.isShowing()){
                    mCustomDialog.dismiss();
                }
                String path = intent.getData().getPath();
                List<String> paths = CommonTools.getAllStorages(mContext);
                createFile(paths);
            }
            else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){//U盘被拔出
                String path = intent.getData().getPath();
                LogUtils.e("onReceive","path is "+path);
                List<String> paths =  CommonTools.getAllStorages(mContext);
                //拔出后无外接存储设备了，弹出提示框
                if (paths.size() == 0){
                    hasStorage = false;
                    //停止所有的录制
                    EventBus.getDefault().post(new IpcamStopAllRecord());
                    if (mCustomDialog == null){
                        mCustomDialog = new CustomDialog(mContext,CustomDialog.type_record_storage_remind);
                    }
                    mCustomDialog.show();
                    if (null != mCustomDialog_storage_tip){
                        mCustomDialog_storage_tip.dismiss();
                    }
                    mCustomDialog.show();
                }
                //拔出u盘后还有其他设备，判断拔出的是当前存储盘还是其他
                if (paths.size() != 0){
                    String lastStorage = CommonTools.getPreference(Constant.lastStorage);
                    //还剩一个u盘，且不是上次选中录制的那个
                    if ( !paths.contains(lastStorage) && paths.size() == 1){
                        //停止所有的录制
                        EventBus.getDefault().post(new IpcamStopAllRecord());
                        //直接对该U盘地址进行数据库初始化
                        createFile(paths);
                    }
                    //还剩多个U盘，且不包括上次选中那个
                    else  if (!paths.contains(lastStorage) && paths.size() > 1){
                        EventBus.getDefault().post(new IpcamStopAllRecord());
                        if (mStorageNewRemind == null){
                            mStorageNewRemind = new CustomDialog(mContext,CustomDialog.type_record_newStorage_remind,ActivityRecordPresenter.this,path);
                        }
                        mStorageNewRemind.show();
                    }
                    //剩的u盘中包括之前选中的
                    else if (paths.contains(lastStorage)){

                    }
                }
            }
        }
    };


    public ActivityRecordPresenter(Context context, Handler handler,IRecordActivity recordActivity) {
        mContext = context;
        this.mIRecordActivity = recordActivity;
        mActivity = (Activity) mContext;
        EventBus.getDefault().register(this);
        initDialog();
    }

    //初始化部分对话框
    private void initDialog() {
        mCustomDialog = new CustomDialog(mContext,CustomDialog.type_record_storage_remind);
        mCustomDialog_storage_tip = new CustomDialog(mContext,CustomDialog.type_record_storage_tip);
        mCustomDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    LogUtils.e("onkey","onkey back");
                    mIRecordActivity.removeAview();
                    Activity mActivity = (Activity)mContext;
                    mActivity.finish();
                }
                return false;
            }
        });
    }

    private boolean storageCanUse = true;
    //为每个摄像头创建视频文件夹
    public void createFile(List<String> paths) {
        dids = CommonTools.initStartCameraDevice();
        //比对摄像头：数据库中的摄像头多余配置文件中的则直接删除
        deleteAbandonDevice();
        if (paths.size() == 0){
            mCustomDialog.show();
            hasStorage = false;
//            getAllCameraData();
            return;
        }
        //如果只存在一个移动磁盘
        if (paths.size() == 1) {
            storageCanUse = true;
            if (storageCanUse) {
                String lastPath = CommonTools.getPreference(Constant.lastStorage);
                CommonTools.savePreference(Constant.lastStorage, paths.get(0));
                Constant.commonPath = paths.get(0) + File.separator + "IPCamera";
                Constant.recordPath = Constant.commonPath + File.separator + "record";//record视频文件
                Constant.thumpPath = Constant.commonPath + File.separator + "thump";//视频缩略图文件
                CommonTools.createCommonFolder(Constant.recordPath);
                CommonTools.createCommonFolder(Constant.thumpPath);
                names = CommonTools.getDeviceName(mContext);
                devices = DataSupport.findAll(IpCamDevice.class);
                for (IpCamDevice mIpCamDevice : devices) {
                    dids_db.add(mIpCamDevice.getDid());
                    LogUtils.e("createFile","record_type is "+mIpCamDevice.getRecordType());
                    //如果U盘路径发生改变
                    if (null != lastPath && (!lastPath.equals("")) && !lastPath.equals(paths.get(0))){
                        initFile(paths.get(0));
                    }
                }
                if (dids.size() != 0) {
                    for (String did : dids) {
                        if (!dids_db.contains(did)) {//如果数据库中未保存该did
                            LogUtils.e("createFile", did);
                            IpCamDevice device = new IpCamDevice();
                            device.setDid(did);
                            device.setIsStop("2");//默认录制按钮打开
                            device.setSecondIsOpen("2");//默认sensor触发十秒钟打开
                            int position = dids.indexOf(did);
                            device.setAliasName(names.get(position));
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
                                boolean isok7 = CommonTools.createFolder(file_did,"dynamic");
                                if (isok7){
                                    device.setDynamicPath(file_did + File.separator + "dynamic");
                                }
                            }
                            updateIpCamDevices(device);
                        }
                    }
                }
                CommonTools.savePreference( Constant.isFirst, Constant.isFirst_false);
                CommonTools.savePreference(Constant.lastStorage, paths.get(0));
            }
            //创建好数据库之后加载服务和初始化recyclerView
            mIRecordActivity.initRecyclerView();
        }
        else {
            storageCanUse = false;
            String lastPath = CommonTools.getPreference(Constant.lastStorage);
            if (null == lastPath || lastPath.equals("")){
                //存在多个移动存储盘
                if (mChooseStorageDialog == null){
                    mChooseStorageDialog = new CustomDialog(mContext,CustomDialog.type_record_choose_storage,this,paths);
                }
                mChooseStorageDialog.show();
            }
            else {
                mIRecordActivity.initRecyclerView();
            }
        }
    }

    /**
     * U盘路径改变时
     * @param filePath
     */
    private void initFile(String filePath) {
        Constant.commonPath = filePath + File.separator + "IPCamera";
        Constant.recordPath = Constant.commonPath + File.separator + "record";//record视频文件
        Constant.thumpPath = Constant.commonPath + File.separator + "thump";//视频缩略图文件
        CommonTools.createCommonFolder(Constant.recordPath);
        CommonTools.createCommonFolder(Constant.thumpPath);
        names = CommonTools.getDeviceName(mContext);
        devices = DataSupport.findAll(IpCamDevice.class);
        if (devices.size() != 0) {
            for (IpCamDevice device : devices) {
                LogUtils.e("createFile", did);
                File file = new File(Constant.recordPath);
                String did = device.getDid();
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
                }
                device.save();
            }
        }
    }

    /**
     * 是否有摄像头被删除了，如果删除了则将数据库中的也删除
     */
    private void deleteAbandonDevice() {
        try{
            List<IpCamDevice> ipCamDevices = DataSupport.findAll(IpCamDevice.class);
            if (ipCamDevices.size() > dids.size()){
                for (IpCamDevice device : ipCamDevices){
                    if (!dids.contains(device.getDid())){
                        device.delete();
                    }
                }
            }
        }
        catch (Exception e){
            LogUtils.e("deleteAbandonDevice","exception is "+e.getMessage());
        }
    }

    /**
     * 处理sensor触发录制
     * @param position
     */
    public void dealwithSensorRecord(int position){
        IpCamDevice mIpCamDevice = devices.get(position);
        mIpCamDevice.setStatus("2");
        mAdapter.notifyItemChanged(position);
    }

    //更新数据库
    private void updateIpCamDevices(IpCamDevice device) {
        device.setStatus("0");
        device.setIsStop("2");
        device.setSecondIsOpen("2");
        device.save();//存储(新增一行)
    }


    private RecordDeviceAdapter mAdapter;
    private  List<IpCamDevice> devices = new ArrayList<>();
    //初始化广播接收者
    public void registerReceiver(){
        LogUtils.e("registerReceiver","registerReceiver");
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//存在还未被挂载
        mIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);//存在且已经被挂载
        mIntentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);//sd卡拔出，但是挂载还没移除
        mIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);//sd移除，挂载点也解除
        mIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);//开始进行扫描
        mIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);//扫描结束
        mIntentFilter.addDataScheme("file");
        mContext.registerReceiver(mBroadcastReceiver,mIntentFilter);
    }
    //注销
    public void unRegisterReceiver(){
        LogUtils.e("registerReceiver","unregister");
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
    //清空资源
    public void removeResource(){
        if (null != mCustomDialog){
            mCustomDialog.dismiss();
            mCustomDialog = null;
        }
        if (null != mCustomDialog_storage_tip){
            mCustomDialog_storage_tip.dismiss();
            mCustomDialog_storage_tip = null;
        }
    }


    //初始化recyclerView
    private RecyclerViewTV mRecyclerViewTV;
    public void initRecyclerView(final RecyclerViewTV recyclerView) {
        mRecyclerViewTV = recyclerView;
//        mRecyclerViewTV.setPagingableListener(new RecyclerViewTV.PagingableListener() {
//            @Override
//            public void onLoadMoreItems() {
//                AppLog.e("onLoadMoreIntems is coming");
//                mRecyclerViewTV.scrollBy(0,300);
//            }
//        });
        devices = DataSupport.findAll(IpCamDevice.class);
        for (IpCamDevice device : devices){
            LogUtils.e("initRecyclerView","type is "+device.getRecordType());
        }
        mAdapter = new RecordDeviceAdapter(devices, mContext);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mAdapter);
    }

    private SwitchCompat mSwitchCompat;

    //设置switchView监听
    private boolean flag = false;
    public void setSwitchListener(final SwitchCompat switchBtn1) {
        mSwitchCompat = switchBtn1;
        mSwitchCompat.setThumbDrawable(mContext.getDrawable(R.drawable.switch_btn_nomal));
        mSwitchCompat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.e("onFocusChange", "hasFocus is " + hasFocus);
                if (hasFocus) {
                    mSwitchCompat.setThumbDrawable(mContext.getDrawable(R.drawable.switch_btn));
                    mIRecordActivity.setLayoutSelected(true);
                    ObjectAnimator mObjectAnimatorY = ObjectAnimator.ofFloat(v, View.SCALE_Y, 1f, 1.3f);
                    ObjectAnimator mObjectAnimatorX = ObjectAnimator.ofFloat(v, View.SCALE_X, 1f, 1.3f);
                    AnimatorSet set = new AnimatorSet();
                    set.play(mObjectAnimatorX).with(mObjectAnimatorY);
                    set.setDuration(500);
                    set.start();
                } else {
                    mSwitchCompat.setThumbDrawable(mContext.getDrawable(R.drawable.switch_btn_nomal));
                    mIRecordActivity.setLayoutSelected(false);
                    if (flag){
                        ObjectAnimator mObjectAnimatorY = ObjectAnimator.ofFloat(v, View.SCALE_Y, 1.3f, 1f);
                        ObjectAnimator mObjectAnimatorX = ObjectAnimator.ofFloat(v, View.SCALE_X, 1.3f, 1f);
                        AnimatorSet set = new AnimatorSet();
                        set.play(mObjectAnimatorX).with(mObjectAnimatorY);
                        set.setDuration(500);
                        set.start();
                    }
                    flag = true;
                }
            }
        });

        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mIRecordActivity.setSwitchStatus(0);
                    for (IpCamDevice mIpCamDevice : devices){
                        mIpCamDevice = CommonTools.findDeviceFromDB(mIpCamDevice.getDid());
                        mIpCamDevice.setSecondIsOpen("2");
                        mIpCamDevice.save();
                    }
                }
                else {
                    mIRecordActivity.setSwitchStatus(1);
                    if (devices.size() != 0){
                        for (IpCamDevice mIpCamDevice : devices){
                            mIpCamDevice = CommonTools.findDeviceFromDB(mIpCamDevice.getDid());
                            mIpCamDevice.setSecondIsOpen("1");
                            mIpCamDevice.save();
                        }
                    }
                }
            }
        });
    }



    //录制方式发生改变
    public void updateAdapter_ChangeType(boolean flag, int position, int type) {
        LogUtils.e("updateAdapter2", "flag is " + flag + " position is " + position + " type is " + type);
        IpCamDevice mIpCamDevice = devices.get(position);
        mIpCamDevice = CommonTools.findDeviceFromDB(mIpCamDevice.getDid());
        mIpCamDevice.setRecordType(type + "");
        mIpCamDevice.save();
        devices.remove(position);
        devices.add(position,mIpCamDevice);

        LogUtils.e("updateAdapter2","flag is "+flag);
        if (flag) {
            mSwitchCompat.setFocusable(false);
            mAdapter.notifyItemChanged(position);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwitchCompat.setFocusable(true);
                }
            }, 100);
        }
    }

    //某个摄像头录制完成了，更新UI
    public void updateAdapter_StopRecord(String did) {
        int position = dids.indexOf(did);
        if (devices.get(position).getStatus().equals("0")){//离线了，状态不用从录制更改到在线

        }
        else {//正常录制结束：由录制状态更改到在线状态
            IpCamDevice mIpCamDevice = CommonTools.findDeviceFromDB(did);
            Log.d("updateAdapter_停止录制","did is "+did+" isStop is "+mIpCamDevice.getIsStop());
            String lastJpgPath = mIpCamDevice.getLastJPGPath();
        mIpCamDevice.setStatus("1");
            devices.remove(position);
            devices.add(position,mIpCamDevice);
        Log.d("updateAdapter", "did is " + did + "position is " + position + " " + "lastPath is " + lastJpgPath);
        mAdapter.notifyItemChanged(position);
        }
    }

    //更新UI,仅更改recording的显示
    public void updateAdapter_StopRecord_IsStop(String did) {
        int position = dids.indexOf(did);
        if (devices.get(position).getStatus().equals("0")){//离线了，状态不用从录制更改到在线

        }
        else {//正常录制结束：由录制状态更改到在线状态
            devices.get(position).setIsStop("1");
            mAdapter.notifyItemChanged(position);
        }
    }


    /**
     * 某个设备开始录制
     * @param camera_update
     */
    public void updateAdapter_StartRecord(IpCamDevice camera_update) {
        LogUtils.e("updateAdapRter_状态","devices.size is "+devices.size()+" dids.size is "+dids_db.size());
        if (dids_db.size() != devices.size()){
            for (IpCamDevice mIpCamDevice : devices){
                dids_db.add(mIpCamDevice.getDid());
            }
        }
        String did = camera_update.getDid();
        int position = dids_db.indexOf(did);
        LogUtils.e("updateAdapter_状态","状态值为："+camera_update.getStatus()+"did is "+camera_update.getDid()+"dids1 is "+dids_db.get(0));
        devices.remove(position);
        devices.add(position,camera_update);
        mSwitchCompat.setFocusable(false);
        mAdapter.notifyItemChanged(position);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwitchCompat.setFocusable(true);
            }
        }, 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithEvent(Object object){

    }


    /**
     * dialog显示回调
     * @param repeats
     */
    @Override
    public void repeatCallback(int[] repeats) {

    }

    @Override
    public void chooseStorageCallback(String path) {
        mChooseStorageDialog.dismiss();
        List<String> paths = new ArrayList<>();
        paths.add(path);
        createFile(paths);
        CommonTools.savePreference(Constant.lastStorage,path);
        LogUtils.e("chooseStorageCallback","String path is ");
    }

    @Override
    public void makeSureCallback(String path) {
        //更改有关path的数据库
//        initFile(path);
        //todo 发送订阅事件，暂停所有的录制
    }
}
