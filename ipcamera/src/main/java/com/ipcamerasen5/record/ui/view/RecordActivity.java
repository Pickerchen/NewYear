package com.ipcamerasen5.record.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipcamerasen5.main.MainApplication;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.common.CommonTools;
import com.ipcamerasen5.record.common.Constant;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.event.IpCamStopRecord;
import com.ipcamerasen5.record.event.IpcameraExit;
import com.ipcamerasen5.record.event.IpcameraHomePress;
import com.ipcamerasen5.record.ui.presenter.ActivityRecordPresenter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import nes.ltlib.LTSDKManager;
import nes.ltlib.utils.LogUtils;

public class RecordActivity extends Activity implements IRecordActivity{

    private static RelativeLayout rl_LTCamera;

    //chat
    private LinearLayout ll_chat;
    //currentUserId
    public static long currentUserId;
    public static int position;
    //TAG
    private String TAG = RecordActivity.class.getSimpleName();
    int hour;
    int minutes;
    //falg:标记是否已经进入过
    public static boolean hasStartRecord;
    //服务是否还活着
    public static boolean isWork;
    //view
    private LinearLayout ll_content;
    private RecyclerViewTV mRecyclerView;
    private SwitchCompat mSwitchCompat;
    private TextView tv_status;
    private CustomGlsurfaceView mGLSurfaceView;
    private RelativeLayout mRelativeLayout;
    private LinearLayout record_ll_switch;

    //presenter
    private ActivityRecordPresenter activityRecord;

    //handler
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    initView();
                    break;
                case 500:
                    activityRecord.registerReceiver();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.e("RecordActivityLife","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //获取当前用户点击的摄像头进入的userID
        Intent intent = getIntent();
        currentUserId = intent.getLongExtra("userID",0);
        position = intent.getIntExtra("position",0);
        LogUtils.e("currentUserId","currentUserId is "+currentUserId);
         activityRecord = new ActivityRecordPresenter(this,mHandler,this);
        initEventBus();
        mHandler.sendEmptyMessageDelayed(100,400);
        //检测录制服务是否在后台运行中
        isWork = CommonTools.isServiceWork(this,"com.ipcamerasen5.record.service.RecordService", Constant.PACKGENAME);
        LogUtils.e("onCreate","isServiceWork is "+isWork);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.e("RecordActivityLife","onSaveInstanceState");
        Intent intent = getIntent();
        currentUserId = intent.getLongExtra("userID",0);
        LogUtils.e("currentUserId","currentUserId is "+currentUserId);
    }

    @Override
    protected void onResume() {
        Logger.e("RecordActivityLife","onResume");
        MainApplication.activityName = RecordActivity.class.getSimpleName();
        MainApplication.isFront = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Logger.e("RecordActivityLife","onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainApplication.isFront = false;
        Logger.e("RecordActivityLife","onStop");
    }

    @Override
    protected void onDestroy() {
        LogUtils.e("RecordActivityLife","onDestroy");
        //按下home键时全部停止
//        IpcamStopAllRecord stopAllRecord = new IpcamStopAllRecord();
//        EventBus.getDefault().post(stopAllRecord);
        EventBus.getDefault().unregister(this);
        activityRecord.unRegisterReceiver();
        activityRecord.removeResource();
        if (rl_LTCamera.getChildCount() > 0){
            rl_LTCamera.removeViewAt(0);
        }
        super.onDestroy();
    }

    public static void removeLTCamera(){
        if (rl_LTCamera.getChildCount() > 0){
            rl_LTCamera.removeViewAt(0);
        }
    }

    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    private void initView() {
        rl_LTCamera = (RelativeLayout) findViewById(R.id.rl_LTCamera);
        if (position<LTSDKManager.getInstance().mLTDevices.size() && null != LTSDKManager.getInstance().mLTDevices.get(position).getAView()){
            rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(position).getAView());
        }

        ll_content = (LinearLayout) findViewById(R.id.ll_record);
        record_ll_switch = (LinearLayout) findViewById(R.id.record_ll_switch);
        record_ll_switch.setSelected(true);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.record_content_rl);
        mRecyclerView = (RecyclerViewTV) findViewById(R.id.rv_devices);
        mSwitchCompat = (SwitchCompat) findViewById(R.id.switch_sensor_record);
        tv_status = (TextView) findViewById(R.id.record_activity_switch_status);
        mSwitchCompat.setThumbDrawable(getDrawable(R.drawable.switch_btn));
        mGLSurfaceView = new CustomGlsurfaceView(RecordActivity.this);
        mGLSurfaceView.setUserId(currentUserId);
        activityRecord.setSwitchListener(mSwitchCompat);
        // TODO: 2017/9/4 旧版摄像头播放
//        activityRecord.initGLsurfaceView(mGLSurfaceView,mRelativeLayout);
        //注册广播接收者
        mHandler.sendEmptyMessageDelayed(500,100);
        List<String> paths = CommonTools.getAllStorages(this);
        LogUtils.e("RecordActivity_onCreate","paths.size is "+paths.size());
        // TODO: 2017/9/4 创建对应gid文件 
        activityRecord.createFile(paths);
    }

    /**
     * 更新recyclerView
     */
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void dealwithUpdateUI(IpCamDevice ipCamDevice){
        LogUtils.e("dealwithUpdateUI",ipCamDevice.getStatus());
        activityRecord.updateAdapter_StartRecord(ipCamDevice);
    }

    /**
     * 由recordPresenter发出:录制完成时发出。
     * @param event
     */
    private long lastReceiverStopEvent = 0;//eventbus的bug，发出一次，但是此处接收两次
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithRecordStopEvent(IpCamStopRecord event){
        Log.e("dealwithrecordstopevent","recordActivity_收到录制停止+did is "+event.getDid());
            String did = event.getDid();
        if (event.getJpgPath().equals("") || event.getJpgPath() == null){
            activityRecord.updateAdapter_StopRecord_IsStop(did);
        }
        else {
            activityRecord.updateAdapter_StopRecord(did);
        }
            lastReceiverStopEvent = System.currentTimeMillis();
    }

    //UI逻辑
    @Override
    public void addGl() {

    }

    @Override
    public void setSwitchListener() {

    }

    @Override
    public void setLayoutSelected(boolean flag) {
            if (flag){
                record_ll_switch.setSelected(true);
            }
        else {
                record_ll_switch.setSelected(false);
            }
    }

    @Override
    public void initRecyclerView() {
        activityRecord.initRecyclerView(mRecyclerView);
    }

    @Override
    public void setSwitchStatus(int status) {
        switch (status){
            case 0:
                tv_status.setText(getString(R.string.switch_status_on));
            break;
            case 1:
                tv_status.setText(getString(R.string.switch_status_off));
                break;
        }
    }

    @Override
    public void removeAview() {
        if (null != rl_LTCamera && rl_LTCamera.getChildCount() > 0){
            rl_LTCamera.removeViewAt(0);
        }
    }

    static int constNum = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (rl_LTCamera.getChildCount() == 0 && null != LTSDKManager.getInstance().mLTDevices.get(position).getAView()){
            rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(position).getAView());
        }
        boolean changeConfig = false;
        LogUtils.e("onActivityResult_recordActivity","resultCode is "+resultCode+"requestCode is "+requestCode);
        if (data != null){
             changeConfig = data.getBooleanExtra("dataChange",false);
            int position = data.getIntExtra("position",0);
            int type = data.getIntExtra("type",0);
            LogUtils.e("onActivityResult_recordActivity","resultCode is "+resultCode+"requestCode is "+requestCode+"changeConfig is "+changeConfig+"position is "+position+"type is "+type);
            if (changeConfig){
                activityRecord.updateAdapter_ChangeType(changeConfig,position,type);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (rl_LTCamera.getChildCount() > 0){
                rl_LTCamera.removeViewAt(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * home键按下时收到该事件，从MainApplication中发出
     * @param ipcameraHomePress
     */
    @Subscribe
    public void getHomePressEvent(IpcameraHomePress ipcameraHomePress){
        removeCameraView();
    }
    public void removeCameraView(){
        if (rl_LTCamera.getChildCount() > 0){
            rl_LTCamera.removeViewAt(0);
        }
    }


    @Subscribe (threadMode =  ThreadMode.MAIN)
    public void dealwithExit(IpcameraExit event){
        LogUtils.e("dealwithExit","exit");
        finish();
    }

}
