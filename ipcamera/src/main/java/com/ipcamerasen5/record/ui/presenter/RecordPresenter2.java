package com.ipcamerasen5.record.ui.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ipcamerasen5.record.common.CommonTools;
import com.ipcamerasen5.record.common.Constant;
import com.ipcamerasen5.record.common.DateUtil;
import com.ipcamerasen5.record.db.ClockPath;
import com.ipcamerasen5.record.db.DynamicPath;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.db.RoundPath;
import com.ipcamerasen5.record.db.SecondPath;
import com.ipcamerasen5.record.db.SensorPath;
import com.ipcamerasen5.record.db.VideoForMemory;
import com.ipcamerasen5.record.entity.IpcameraSecondBroadcast;
import com.ipcamerasen5.record.event.IpCamRecordTypeChange;
import com.ipcamerasen5.record.event.IpCamStopRecord;
import com.ipcamerasen5.record.event.IpcamStopAllRecord;
import com.ipcamerasen5.record.event.IpcamStopStatusChanges;
import com.ipcamerasen5.record.service.RecordService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import nes.ltlib.LTSDKManager;

/**
 * GlsurfaceView业务逻辑:主要为录制和播放
 * Created by chenqianghua on 2017/4/5.
 */

public class RecordPresenter2{
    public int recordStatus = Constant.status_onLine;
    //当前的设备
    private IpCamDevice currentIpCamDevice;
    //    ----------------------------------------------------------------
    //construct
    public long userID;
    public String device_did;
    private Context mContext;
    //flag第一帧图片渲染完成
    private boolean isFirst = true;
    //当前正在执行录制的文件名称
    private String currentFileName;//当前录制文件的文件名
    private String currentImageFileName;//当前录制文件的视频缩略图文件名
    private String currentFile;//当前录制文件的绝对地址
    private int currentRecordType;//当前录制的类型
    public long currentDuration;//当前录制的时长
    public long currentDuration_original;//本来需要录制的时长
    public long hasRecordDuration;//已经录制了的时长
    public long currentClockDuration;//记录当前闹钟录制的时长()
    public long startTime;
    //数据库中所有的设备
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 构造
     * @param did：当前设备did
     * @param userID：当前设备实例userID
     * @param context：上下文
     */
    public RecordPresenter2(String did, long userID, Context context,int recordType) {
        this.device_did = did;
        this.userID = userID;
        this.mContext = context;
        this.currentRecordType = recordType;
        currentIpCamDevice = CommonTools.findDeviceFromDB(device_did);
        EventBus.getDefault().register(this);
        Log.d("recordPresnter2","did is"+did+"userID is"+userID);
    }


    /**
     * 离线时设置录制的duration，等到恢复在线状态判断是否还需要进行录制
     * @param duration
     * @return
     */
    public void setDurationAndRecordType(long duration,int type){
        currentRecordType = type;
        currentDuration = duration;
        currentDuration_original = duration;
        startTime = System.currentTimeMillis();
    }

    /**
     * 开始录制
     * @param recordType
     * @param duration
     * @return
     */
    public boolean startRecord(String gid,int recordType,long duration){
        Log.d("startRecord","recordType is "+recordType+"startRecord"+" hasStorage is "+RecordService.hasStorage+" userId is "+gid+" recordstatus is "+recordStatus+"deviceId is "+device_did);
        //如果没有磁盘直接退出。
        if (!RecordService.hasStorage) {
            return false;
        }
        currentIpCamDevice = CommonTools.findDeviceFromDB(device_did);
        Log.d("startRecord","status is "+currentIpCamDevice.getStatus());
        if (null == currentIpCamDevice) {
            return false;
        }
        currentRecordType = recordType;
        if (currentIpCamDevice.getIsStop().equals("1")) {
            return false;
        }
        if (currentIpCamDevice.getStatus().equals("0")) {
            recordStatus = Constant.status_offLine;
        } else if (currentIpCamDevice.getStatus().equals("1")) {
            recordStatus = Constant.status_onLine;
        }
        boolean flag = false;
        Log.d("startRecord","gid ? deviceID "+(gid != device_did)+" gid is " + gid+" ");
        //判断是否同一个摄像头
        if (!gid.equals(device_did)) {
            return flag;
        }
        Log.d("startRecord","recordStatus is "+recordStatus);
        //判断该摄像头的状态
        switch (recordStatus) {
            case Constant.status_offLine:
                Log.d("startRecord","recordStatus is offLine");
                flag = false;
                break;
            case Constant.status_onLine:
                Log.d("startRecord","recordStatus is onLine");
                flag = true;
                recordOpeRate(duration);
                break;
            case Constant.status_recording:
                Log.d("startRecord","recordStatus is recording");
                flag = false;
                break;
        }
        return flag;
    }


    public void recordOpeRate(long duration){
        currentIpCamDevice = CommonTools.findDeviceFromDB(device_did);
        currentIpCamDevice.setStatus("2");
        currentIpCamDevice.save();
        EventBus.getDefault().post(currentIpCamDevice);
        currentDuration = duration;
        currentDuration_original = duration;
        Log.d("recordOpeRate","duration is "+currentDuration+" currentRecordType is " + currentRecordType);
        switch (currentRecordType){
            case Constant.type_clock:
                //记录开始录制的时间
                startTime = System.currentTimeMillis();
                //获取当前录制文件名
                currentFileName = DateUtil.formatDateTime1(System.currentTimeMillis()) + ".mp4";
                currentImageFileName = DateUtil.formatDateTime1(System.currentTimeMillis()) + ".jpg";
                currentFile = currentIpCamDevice.getClockPath() + File.separator + currentFileName;
                Log.d("recordOpeRate","currentFie is " + currentFile);
                //开始进行录制操作
                LTSDKManager.getInstance().startRecord(device_did,currentFile);
                //设备状态改为录制状态
                recordStatus = Constant.status_recording;
                //判断硬盘并删除文件
                CommonTools.makeSureRecordNomal(mContext,duration/1000);
                break;
            case Constant.type_move:
                //记录开始录制的时间
                startTime = System.currentTimeMillis();
                //获取当前录制文件名
                currentFileName = DateUtil.formatDateTime1(System.currentTimeMillis()) + ".mp4";
                currentImageFileName = DateUtil.formatDateTime1(System.currentTimeMillis()) + ".jpg";
                currentFile = currentIpCamDevice.getDynamicPath() + File.separator + currentFileName;
                //开始进行录制操作
                LTSDKManager.getInstance().startRecord(device_did,currentFile);
                //设备状态改为录制状态
                recordStatus = Constant.status_recording;
                //判断硬盘并删除文件
                CommonTools.makeSureRecordNomal(mContext,duration/1000);
                break;
            case Constant.type_round:
                //记录开始录制的时间
                startTime = System.currentTimeMillis();
                //获取当前录制文件名
                currentFileName = DateUtil.formatDateTime1(System.currentTimeMillis()) + ".mp4";
                currentImageFileName = DateUtil.formatDateTime1(System.currentTimeMillis()) + ".jpg";
                currentFile = currentIpCamDevice.getRoundPath() + File.separator + currentFileName;
                Log.d("recordOpeRate_round","currentFile is "+currentFile);
                //开始进行录制操作
                LTSDKManager.getInstance().startRecord(device_did,currentFile);
                //设备状态改为录制状态
                recordStatus = Constant.status_recording;
                //判断硬盘并删除文件
                CommonTools.makeSureRecordNomal(mContext,duration/1000);
                break;
            case Constant.type_sensor:
                //记录开始录制的时间
                startTime = System.currentTimeMillis();
                currentFileName = DateUtil.formatDateTime1(System.currentTimeMillis()) + ".mp4";
                currentImageFileName = DateUtil.formatDateTime1(System.currentTimeMillis()) + ".jpg";
                //获取当前录制文件名
                if (currentDuration == 13000){
                    currentFile = currentIpCamDevice.getSecondPath() + File.separator + currentFileName;
                }
                else {
                    currentFile = currentIpCamDevice.getSensorPath() + File.separator + currentFileName;
                }

                //开始进行录制操作
                LTSDKManager.getInstance().startRecord(device_did,currentFile);
                //设备状态改为录制状态
                recordStatus = Constant.status_recording;
                //判断硬盘并删除文件
                CommonTools.makeSureRecordNomal(mContext,duration/1000);
                break;
        }
    }
    public void stopRecord(int type){
        Log.d("stopRecord","type is "+type+" currentRecordType is "+currentRecordType);
        //录制方式不同直接退出，防止已经切换录制方式，之前的录制停止命令发过来。
        if (currentRecordType != type){
            return;
        }
        currentIpCamDevice = CommonTools.findDeviceFromDB(device_did);
        //如果设备在录制，则进行停止录制操作。
        if (recordStatus == Constant.status_recording){
            hasRecordDuration = System.currentTimeMillis() - startTime;
            switch (currentRecordType){
                case Constant.type_clock:
                    String clockPath = currentIpCamDevice.getClockPath();
                    final ClockPath mClockPath = new ClockPath();
                    mClockPath.setClockPath(clockPath);
                    mClockPath.setFileName(currentFileName);
                    mClockPath.setDuration(hasRecordDuration + "");
                    mClockPath.setTime(DateUtil.fileNameToDate(currentFileName));
                    mClockPath.setLastThumpPath(currentImageFileName);
                    mClockPath.setDid(device_did);
                    LTSDKManager.getInstance().stopRecord(device_did);
                    currentIpCamDevice.setLastRecordPath(currentFile);
                    currentIpCamDevice.setStatus("1");
                    //超过三秒的视频才更换视频缩略
                    Log.d("stopRecord","hasRecordDuration is "+hasRecordDuration);
                    if (hasRecordDuration/3000 >= 1) {
                        currentIpCamDevice.setLastJPGPath(currentIpCamDevice.getThumpPath() + File.separator + currentImageFileName);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap mBitmap_clock = CommonTools.getVideoThumbnail(currentIpCamDevice.getLastRecordPath());
//                                Log.d("stopRecord","mBitmap_clock.size is "+mBitmap_clock.getByteCount());
                                CommonTools.saveFile(mBitmap_clock, currentIpCamDevice.getLastJPGPath());
                                mClockPath.save();
                            }
                        }, 100);
                    }
                    Log.d("stopRecord", "thumppath is " + currentIpCamDevice.getLastJPGPath() + "lastRecordPath is " + currentIpCamDevice.getLastRecordPath());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recordStatus = Constant.status_onLine;
                            currentIpCamDevice.save();
                            Log.d("stopRecord", "ipcamstop will post");
                            IpCamStopRecord mIpCamStopRecord = new IpCamStopRecord();
                            mIpCamStopRecord.setJpgPath(currentIpCamDevice.getLastJPGPath());
                            mIpCamStopRecord.setDid(device_did);
                            EventBus.getDefault().post(mIpCamStopRecord);
                        }
                    },200);
                    break;
                case Constant.type_move:
                    String dynamicPath = currentIpCamDevice.getDynamicPath();
                    final DynamicPath mDynamicPath = new DynamicPath();
                    mDynamicPath.setClockPath(dynamicPath);
                    mDynamicPath.setFileName(currentFileName);
                    mDynamicPath.setDuration(hasRecordDuration + "");
                    mDynamicPath.setTime(DateUtil.fileNameToDate(currentFileName));
                    mDynamicPath.setLastThumpPath(currentImageFileName);
                    mDynamicPath.setDid(device_did);
                    LTSDKManager.getInstance().stopRecord(device_did);
                    currentIpCamDevice.setLastRecordPath(currentFile);
                    currentIpCamDevice.setStatus("1");
                    //超过三秒的视频才更换视频缩略
                    if (hasRecordDuration/3000 >= 1) {
                        currentIpCamDevice.setLastJPGPath(currentIpCamDevice.getThumpPath() + File.separator + currentImageFileName);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap mBitmap_clock = CommonTools.getVideoThumbnail(currentIpCamDevice.getLastRecordPath());
                                CommonTools.saveFile(mBitmap_clock, currentIpCamDevice.getLastJPGPath());
                                mDynamicPath.save();
                            }
                        }, 100);
                    }
                    Log.d("stopRecord", "thumppath is " + currentIpCamDevice.getLastJPGPath() + "lastRecordPath is " + currentIpCamDevice.getLastRecordPath());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recordStatus = Constant.status_onLine;
                            currentIpCamDevice.save();
                            Log.d("stopRecord", "ipcamstop will post");
                            IpCamStopRecord mIpCamStopRecord = new IpCamStopRecord();
                            mIpCamStopRecord.setJpgPath(currentIpCamDevice.getLastJPGPath());
                            mIpCamStopRecord.setDid(device_did);
                            EventBus.getDefault().post(mIpCamStopRecord);
                        }
                    },200);
                    break;
                case Constant.type_round:
                    String roundPath = currentIpCamDevice.getRoundPath();
                    final RoundPath mRoundPath = new RoundPath();
                    if (hasRecordDuration/3000 >= 1){
                        mRoundPath.setRoundPath(roundPath);
                        mRoundPath.setDid(device_did);
                        mRoundPath.setFileName(currentFileName);
                        mRoundPath.setDuration(hasRecordDuration + "");
                        mRoundPath.setTime(DateUtil.fileNameToDate(currentFileName));
                        mRoundPath.setLastThumpPath(currentImageFileName);
                    }
                    //停止录制
                    LTSDKManager.getInstance().stopRecord(device_did);
                    currentIpCamDevice.setLastRecordPath(currentFile);
                    currentIpCamDevice.setStatus("1");
                    Log.d("stopRecord", "thumppath is " + currentIpCamDevice.getLastJPGPath() + "lastRecordPath is " + currentIpCamDevice.getLastRecordPath()+"hasDuration is " + hasRecordDuration);
                    //超过三秒的视频才更换视频缩略
                    if (hasRecordDuration/3000 >= 1) {
                        currentIpCamDevice.setLastJPGPath(currentIpCamDevice.getThumpPath() + File.separator + currentImageFileName);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap mBitmap_clock = CommonTools.getVideoThumbnail(currentIpCamDevice.getLastRecordPath());
                                CommonTools.saveFile(mBitmap_clock, currentIpCamDevice.getLastJPGPath());
                                mRoundPath.save();
                            }
                        }, 100);
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recordStatus = Constant.status_onLine;
                            currentIpCamDevice.save();
                            Log.d("stopRecord_round", "ipcamstop will post"+currentIpCamDevice.getLastJPGPath());
                            IpCamStopRecord mIpCamStopRecord = new IpCamStopRecord();
                            mIpCamStopRecord.setJpgPath(currentIpCamDevice.getLastJPGPath());
                            mIpCamStopRecord.setDid(device_did);
                            EventBus.getDefault().post(mIpCamStopRecord);
                        }
                    },600);
                    break;
                case Constant.type_sensor:
                    String secondPath = currentIpCamDevice.getSecondPath();
                    final SecondPath mSecondPath = new SecondPath();
                    final SensorPath mSensorPath = new SensorPath();
                    //如果是十秒小视频则保存十秒视频
                    if (currentDuration == 13000){
                        mSecondPath.setSecondPath(secondPath);
                        mSecondPath.setFileName(currentFileName);
                        mSecondPath.setTime(DateUtil.fileNameToDate(currentFileName));
                        mSecondPath.setLastThumpPath(currentImageFileName);
                        mSecondPath.setDuration(10000+"");
                        mSecondPath.setDid(device_did);
                        mSecondPath.save();
                    }
                    else {
                        String sensorPath = currentIpCamDevice.getSensorPath();
                        mSensorPath.setSensorPath(sensorPath);
                        mSensorPath.setFileName(currentFileName);
                        mSensorPath.setTime(DateUtil.fileNameToDate(currentFileName));
                        mSensorPath.setLastThumpPath(currentImageFileName);
                        mSensorPath.setDuration(hasRecordDuration + "");
                        mSecondPath.setDid(device_did);
                        mSensorPath.save();
                    }
                    LTSDKManager.getInstance().stopRecord(device_did);
                    currentIpCamDevice.setLastRecordPath(currentFile);
                    //更新数据库设备为在线状态
                    currentIpCamDevice.setStatus("1");
                    //超过三秒的视频才更换视频缩略
                    if (hasRecordDuration/3000 >= 1) {
                        currentIpCamDevice.setLastJPGPath(currentIpCamDevice.getThumpPath() + File.separator + currentImageFileName);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap mBitmap_clock = CommonTools.getVideoThumbnail(currentIpCamDevice.getLastRecordPath());
                                CommonTools.saveFile(mBitmap_clock, currentIpCamDevice.getLastJPGPath());
                                if (currentDuration == 13000){
                                    mSecondPath.save();
                                }
                                else {
                                    mSensorPath.save();
                                }
                            }
                        }, 100);
                    }
                    currentIpCamDevice.save();
                    Log.d("stopRecord", "thumppath is " + currentIpCamDevice.getLastJPGPath() + "lastRecordPath is " + currentIpCamDevice.getLastRecordPath());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recordStatus = Constant.status_onLine;
                            currentIpCamDevice.save();
                            Log.d("stopRecord", "ipcamstop will post");
                            IpCamStopRecord mIpCamStopRecord = new IpCamStopRecord();
                            mIpCamStopRecord.setJpgPath(currentIpCamDevice.getLastJPGPath());
                            mIpCamStopRecord.setDid(device_did);
                            EventBus.getDefault().post(mIpCamStopRecord);
                            if (currentDuration == 13000){
                                Log.d("stopRecord_sensor","发送十秒小视频广播通知："+currentFile);
                                //通知发送广播
                                IpcameraSecondBroadcast event = new IpcameraSecondBroadcast();
                                event.setPath(currentFile);
                                EventBus.getDefault().post(event);
                            }
                        }
                    },600);
                    break;
            }
            //保存每个视频文件的占空间大小用作循环录制使用
            VideoForMemory mVideoForMemory = new VideoForMemory();
            mVideoForMemory.setFileName(currentFileName);
            Log.d("currentFileName","currentFileName is "+currentFileName);
            mVideoForMemory.setRecord_type(currentRecordType);
            mVideoForMemory.setFilePath(currentIpCamDevice.getLastRecordPath());

            float filesizeDuration = currentDuration;
            float fileSize = (float) ((filesizeDuration/36000)*1.4);
            mVideoForMemory.setFileSize(fileSize);
            mVideoForMemory.save();
            Log.d("stopRecord","lastJpgPath is " + currentIpCamDevice.getLastJPGPath()+" lastvedioPath is " + currentIpCamDevice.getLastRecordPath());
        }
        else {
            return;
        }
    }

    /**
     * 设备掉线停止录制
     */
    private  long lastOffLineTime;//收到的掉线信息可能会多次发送，五秒内的掉线只执行一次就行。
    public void stopForOffline() {
        if ((System.currentTimeMillis() - lastOffLineTime) > 5000) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("stopForOffline", "设备掉线暂停录制" + "duration is " + currentDuration);
                    stopRecord(currentRecordType);
                }
            }, 100);
        } else {
            return;
        }
        lastOffLineTime = System.currentTimeMillis();
    }

    /**
     * 处理设备录制开关
     */
    private void dealRecordClose() {
        Log.d("busEvent2_1","录制开关关闭");
        stopRecord(currentRecordType);
        //将显示的recording去掉。
//        Log.d("busEvent2_1", "ipcamstop will post");
//        IpCamStopRecord mIpCamStopRecord = new IpCamStopRecord();
//        mIpCamStopRecord.setDid(device_did);
//        EventBus.getDefault().post(mIpCamStopRecord);
    }

    /**
     * 摄像头重新在线或者录制开关打开
     */
    private void dealRecordOpen() {
        Log.d("busEvent2_2", "currentType is " + currentRecordType + " isStop is " + CommonTools.findDeviceFromDB(device_did).getIsStop());
        //重新打开录制开关
        Log.d("busEvent2_2", "currentDuration is " + currentDuration + " startTime is " + startTime);
        if (currentRecordType == Constant.type_sensor || currentRecordType == Constant.type_move) {
            //sensor触发录制时用户手动停止时是不要将剩余时间录完的,而移动侦测录制则是由视频内容自动触发，所以也不需要接着录制
            return;
        }
        if (currentDuration_original > (System.currentTimeMillis() - startTime)) {
            currentDuration_original = currentDuration_original - (System.currentTimeMillis() - startTime);
            startRecord(device_did, currentRecordType, currentDuration_original);
        }
    }

    /**
     * u盘重新插上
     */
    public void dealRecord_StorageOk(){
        Log.d("dealRecord_storageOk","dealRecord_StorageOk");
        if (currentDuration == Constant.type_sensor || currentRecordType == Constant.type_move){
            return;
        }
        if (currentDuration_original > (System.currentTimeMillis() - startTime)) {
            currentDuration_original = currentDuration_original - (System.currentTimeMillis() - startTime);
            startRecord(device_did, currentRecordType, currentDuration_original);
        }
    }


    //释放资源
    public void release(){
        currentIpCamDevice = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void busEvent(final IpCamRecordTypeChange changeEvent){
        Log.d("busEvent1","changeEvent is "+changeEvent.getDid()+" type is"+changeEvent.getType()+changeEvent.getDid().equals(device_did));
        if (changeEvent.getDid().equals(device_did)) {
            Log.d("busEvent1","changeEvent");
            //设备录制方式发生改变，停止录制
            Log.d("busEvent1", "busEvent is coming" + "currentRecordType is " + currentRecordType);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (changeEvent.getDid().equals(device_did) && recordStatus == Constant.status_recording) {
                        stopRecord(currentRecordType);
                        currentRecordType = changeEvent.getType();
                        //切换录制方式时将duration置零
                        currentDuration = 0;
                        currentDuration_original = 0;
                        Log.d("busEvent1", "stopRecord 执行" + "currentDuration is " + currentDuration + " startTime is " + startTime);
                    }
                    else {
                        if (changeEvent.getDid().equals(device_did)){
                            currentRecordType = changeEvent.getType();
                        }
                    }
                }
            }, 100);
        }
    }

    /**
     * 录制开关状态发生改变
     * @param event
     */
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void busEvent2(final IpcamStopStatusChanges event) {
        if (event.getDid().equals(device_did)) {

            if (event.getIsStop().equals("1") && recordStatus == Constant.status_recording){
                long hasRecordTime = (System.currentTimeMillis() - startTime);
                //currentDuration大于零且录制时间不到一个小时
                if (currentDuration > 0 && (hasRecordTime < 3600000) && currentDuration != 13000){
                    currentDuration = (System.currentTimeMillis() - startTime);
                }
                //关闭录制开关
                dealRecordClose();
            }
            else {
                dealRecordOpen();
            }
        }
    }

    //U盘被拔出时强制停止所有录制操作
    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void dealWithStopRecord(IpcamStopAllRecord ipcamStopAllRecord){
        Log.d("dealwithStopRecord","停止当前所有录制操作");
        stopRecord(currentRecordType);
    }
}
