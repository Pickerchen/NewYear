package com.ipcamerasen5.record.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ipcamerasen5.main.MainApplication;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.callback.DialogClockCallback;
import com.ipcamerasen5.record.callback.SettingRemindCallback;
import com.ipcamerasen5.record.common.CommonTools;
import com.ipcamerasen5.record.common.Constant;
import com.ipcamerasen5.record.common.DateUtil;
import com.ipcamerasen5.record.event.IpCamRecordTypeChange;
import com.ipcamerasen5.record.event.IpcameraExit;
import com.ipcamerasen5.record.event.IpcameraHomePress;
import com.ipcamerasen5.record.service.RecordService;
import com.ipcamerasen5.record.ui.presenter.ActivityRecordSettingPresent;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import nes.ltlib.LTSDKManager;
import nes.ltlib.utils.LogUtils;


public class RecordSettingActivity extends Activity implements View.OnClickListener,IRecordSettingActivity,DialogClockCallback,SettingRemindCallback{

    private RelativeLayout record_setting_record_type_rl;
    private RelativeLayout record_setting_save_rl;
    private RelativeLayout record_setting_start_time_rl;
    private RelativeLayout record_setting_end_time_rl;
    private RelativeLayout record_setting_repeat_rl;
    private RelativeLayout ll_record;
    private RelativeLayout lt_record;
    private ActivityRecordSettingPresent activityPresetnt;
    public static String did;
    private int intent_position;//摄像头序号
    private int intent_play_position;//播放的序号
    private LinearLayout include_clock;
    private TextView tv_record_type_title;
    private TextView record_setting_tv_name;
    private TextView record_setting_start_time_tv;
    private TextView record_setting_end_time_tv;
    private TextView record_setting_repeat_tv;
    private TextView record_type_description;
    private TextView record_type_description_hint;
    private ImageView iv_device_image;
    private ImageView iv_record_setting_status;
    private TextView tv_record_setting_status;
    private Drawable errDrawable;
    private CustomDialog mDialog_clock_tip;
    private CustomDialog mDialog_save_remind;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_setting);

        //用来判断用户是否已经配置过设备的录制
        Logger.e("settingOnCreate","flag is "+CommonTools.getPreference(Constant.shouldOpenRecordService).equals("true"));
        if (!CommonTools.getPreference(Constant.shouldOpenRecordService).equals("true")){
            // TODO: 2017/9/8 调试时注释的，这句必须加上。
            CommonTools.mContext = MainApplication.getWholeContext();
            CommonTools.savePreference(Constant.shouldOpenRecordService,"true");
            Intent intent = new Intent(this,RecordService.class);
            startService(intent);
        }

        activityPresetnt = new ActivityRecordSettingPresent(this,this);
        EventBus.getDefault().register(this);
        intent_position  = getIntent().getIntExtra("position",0);
        intent_play_position = getIntent().getIntExtra("playPosition",0);
        initView();

        errDrawable = RecordSettingActivity.this.getDrawable(R.mipmap.bg_camera_default_allscreen);
        activityPresetnt.initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.activityName = RecordSettingActivity.class.getSimpleName();
        MainApplication.isFront = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainApplication.isFront = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        //初始化pop
        activityPresetnt.initPop();
        ll_record = (RelativeLayout) findViewById(R.id.rl_record);
        lt_record = (RelativeLayout) findViewById(R.id.rl_LTCamera);
        if (LTSDKManager.getInstance().mLTDevices.size() > intent_play_position && null != LTSDKManager.getInstance().mLTDevices.get(intent_play_position).getAView()){
            lt_record.addView(LTSDKManager.getInstance().mLTDevices.get(intent_play_position).getAView());
        }
        record_setting_record_type_rl = (RelativeLayout) findViewById(R.id.record_setting_record_type_rl);
        record_setting_save_rl = (RelativeLayout) findViewById(R.id.record_setting_save_rl);
        record_setting_start_time_rl = (RelativeLayout) findViewById(R.id.record_setting_start_time_rl);
        record_setting_start_time_rl.setOnClickListener(this);
        record_setting_end_time_rl = (RelativeLayout) findViewById(R.id.record_setting_end_time_rl);
        record_setting_end_time_rl.setOnClickListener(this);
        record_setting_save_rl.setOnClickListener(this);
        record_setting_record_type_rl.setOnClickListener(this);
        record_setting_repeat_rl = (RelativeLayout) findViewById(R.id.record_setting_repeat_rl);
        record_setting_repeat_rl.setOnClickListener(this);
        include_clock = (LinearLayout) findViewById(R.id.include_clock_setting);
        tv_record_type_title = (TextView) findViewById(R.id.record_setting_record_type_tv);
        record_setting_tv_name = (TextView) findViewById(R.id.record_setting_tv_name);
        record_setting_start_time_tv = (TextView) findViewById(R.id.record_setting_start_time_tv);
        record_setting_end_time_tv = (TextView) findViewById(R.id.record_setting_end_time_tv);
        iv_device_image = (ImageView) findViewById(R.id.record_setting_iv);
        iv_record_setting_status = (ImageView) findViewById(R.id.record_setting_iv_status);
        tv_record_setting_status = (TextView) findViewById(R.id.record_setting_tv_status);
        record_setting_repeat_tv = (TextView) findViewById(R.id.record_setting_repeat_tv);
        record_type_description = (TextView) findViewById(R.id.record_type_description);
        String language = getString(R.string.language);
        if (language.equals("zh-cn")){//
            float density = getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) record_type_description.getLayoutParams();
            mLayoutParams.leftMargin = (int) (145*density);
            Log.e("zh-cn","density is "+density);
            record_type_description.setLayoutParams(mLayoutParams);
        }
        record_type_description_hint = (TextView) findViewById(R.id.record_type_description_hint);

        Intent intent = getIntent();
        long userID = intent.getLongExtra("userID",0);
    }

    //起始时间
    private String startTime = "";
    private String endTime = "";
    private boolean shouldFinish = true;//是否需要finish();
    @Override
    public void onClick(View v) {
        Log.e("onRecordSettingActivity","is service alive = "+CommonTools.isServiceWork(RecordSettingActivity.this,"com.ipcamerasen5.record.service.RecordService",Constant.PACKGENAME));
        int i = v.getId();
        if (i == R.id.record_setting_save_rl) {
            saveRecordType();

        } else if (i == R.id.record_setting_record_type_rl) {
            activityPresetnt.popShowOrDismiss(record_setting_record_type_rl);
        } else if (i == R.id.record_setting_start_time_rl) {
            activityPresetnt.showDialog(1);

        } else if (i == R.id.record_setting_end_time_rl) {
            activityPresetnt.showDialog(2);

        } else if (i == R.id.record_setting_repeat_rl) {
            activityPresetnt.showDialog(3);

        }
    }

    private void saveRecordType() {
        startTime = record_setting_start_time_tv.getText().toString();
        endTime = record_setting_end_time_tv.getText().toString();
        Intent intent = new Intent();
        IpCamRecordTypeChange changeEvent = new IpCamRecordTypeChange();
        Log.e("onClick", "clockTime is" + startTime + "到" + endTime);
        Log.e("onClick", "currItem is " + currentSelectedItem + " lastItem is " + lastSelectedItem);
        switch (currentSelectedItem) {
            case 0://当期选中为闹钟录制
                if (CommonTools.computeDuration(startTime, endTime) <= 0) {
                    //当用户选择的闹钟不符合规定是弹出提示
                    if (null == mDialog_clock_tip) {
                        mDialog_clock_tip = new CustomDialog(RecordSettingActivity.this, CustomDialog.type_record_clock_tip, (SettingRemindCallback) RecordSettingActivity.this);
                    }
                    mDialog_clock_tip.show();
                    return;
                }
                switch (lastSelectedItem) {
                    case 0:
                        // TODO: 2017/6/28 避免首次进入应用时选择闹钟录制record界面未更新
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        break;
                    case 1:
                        Log.e("switchcase", "0-1");
                        RecordService.dealWithRoundChange(did);
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(0);
                        EventBus.getDefault().post(changeEvent);
                        break;
                    case 2:
                        Log.e("switchcase", "0-2");
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(0);
                        EventBus.getDefault().post(changeEvent);
                        break;
                    case 3:
                        Log.e("switchcase", "0-3");
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(0);
                        EventBus.getDefault().post(changeEvent);
                        break;
                }
                if (startTime.equals(endTime)) {
                    // TODO: 2017/6/12  提示用户闹钟一致
                    shouldFinish = false;
                } else {
                    activityPresetnt.saveClockTime(startTime, endTime);
                }
                break;
            case 1://当前选中为24小时录制
                switch (lastSelectedItem) {
                    case 0:
                        Log.e("switchcase", "1-0");
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        RecordService.dealWithClockChange(did);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(1);
                        EventBus.getDefault().post(changeEvent);
                        RecordService.dealwithRoundRecord(did);
                        break;
                    case 1:
                        break;
                    case 2:
                        Log.e("switchcase", "1-2");
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(1);
                        EventBus.getDefault().post(changeEvent);
                        RecordService.dealwithRoundRecord(did);
                        break;
                    case 3:
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(1);
                        EventBus.getDefault().post(changeEvent);
                        RecordService.dealwithRoundRecord(did);
                        break;
                }
                break;
            case 2://当前选中为智能录制
                switch (lastSelectedItem) {
                    case 0:
                        Log.e("switchcase", "2-0");
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        RecordService.dealWithClockChange(did);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(2);
                        EventBus.getDefault().post(changeEvent);
                        break;
                    case 1:
                        Log.e("switchcase", "2-1");
                        RecordService.dealWithRoundChange(did);
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(2);
                        EventBus.getDefault().post(changeEvent);
                        break;
                    case 2:
                        break;
                    case 3:
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(2);
                        EventBus.getDefault().post(changeEvent);
                        break;
                }
                break;
            case 3://动态移动侦测录制
                switch (lastSelectedItem) {
                    case 0:
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        RecordService.dealWithClockChange(did);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(3);
                        EventBus.getDefault().post(changeEvent);
                        break;
                    case 1:
                        RecordService.dealWithRoundChange(did);
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(3);
                        EventBus.getDefault().post(changeEvent);
                        break;
                    case 2:
                        activityPresetnt.saveIpCameraType(currentSelectedItem);
                        //setResult只是用来更新adapter的数据的
                        intent.putExtra("type", currentSelectedItem);
                        intent.putExtra("dataChange", true);
                        intent.putExtra("position", intent_position);
                        setResult(Activity.RESULT_OK, intent);//更改recordActivityUI显示
                        changeEvent.setDid(did);
                        changeEvent.setType(3);
                        EventBus.getDefault().post(changeEvent);
                        break;
                    case 3:
                        break;
                }
                break;
        }
        // TODO: 2017/6/19 判断是提示还是直接finish()
        if (lt_record.getChildCount() > 0) {
            lt_record.removeViewAt(0);
        }
        finish();
    }

    private int currentSelectedItem = -1;//默认为-1，用来标记用户是否更改
    private int lastSelectedItem = -1;
    @Override
    public void timedIsSelected(int position) {
        record_type_description.setText("");
        record_type_description_hint.setText(getString(R.string.record_setting_Detail_Setting));
        currentSelectedItem = position;
        ObjectAnimator anim = ObjectAnimator.ofFloat(include_clock, "translationY", -200,0);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(include_clock,"alpha",0,1);
        AnimatorSet animaSet = new AnimatorSet();
        animaSet.play(anim).with(anim2);
        animaSet.start();
        animaSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                setinClude_clockView(0);
                include_clock.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        include_clock.setVisibility(View.VISIBLE);
    }

    @Override
    public void timedIsnotSelected(int position) {
        switch (position){
            case 1:
                record_type_description.setText(getString(R.string.record_setting_Detail_description_24h));
                break;
            case 3:
                record_type_description.setText(getString(R.string.record_setting_Detail_description_dymanic));
                break;
//            case 3:
//                record_type_description.setText(getString(R.string.record_setting_Detail_description_smart));
//                break;
            case 2:
                record_type_description.setText(getString(R.string.record_setting_Detail_description_smart));
                break;
        }
        record_type_description_hint.setText(getString(R.string.record_setting_Detail_description));
        currentSelectedItem = position;
        ObjectAnimator anim = ObjectAnimator.ofFloat(include_clock, "translationY", 0,-200);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(include_clock,"alpha",1,0);
        AnimatorSet animaSet = new AnimatorSet();
        animaSet.play(anim).with(anim2);
        animaSet.start();
        animaSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                setinClude_clockView(0);
                include_clock.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void changeRecordingType(int type) {
        Log.e("changeRecordingType","changeRecordingType is "+type);
        String[] mStrings = getResources().getStringArray(R.array.record_setting_pop);
        currentSelectedItem = type;
        lastSelectedItem = type;
        switch (type){
            case 0:
                record_type_description.setText("");
                record_type_description_hint.setText(getString(R.string.record_setting_Detail_Setting));
                tv_record_type_title.setText(mStrings[0]);
                include_clock.setVisibility(View.VISIBLE);
                setinClude_clockView(1);
                break;
            case 1:
                record_type_description_hint.setText(getString(R.string.record_setting_Detail_description));
                record_type_description.setText(getString(R.string.record_setting_Detail_description_24h));
                tv_record_type_title.setText(mStrings[1]);
                break;
            case 2:
                record_type_description_hint.setText(getString(R.string.record_setting_Detail_description));
                record_type_description.setText(getString(R.string.record_setting_Detail_description_smart));
                tv_record_type_title.setText(mStrings[2]);
                break;
            case 3:
                record_type_description_hint.setText(getString(R.string.record_setting_Detail_description));
                record_type_description.setText(getString(R.string.record_setting_Detail_description_dymanic));
                tv_record_type_title.setText(mStrings[3]);
                break;
            default:
                record_type_description_hint.setText(getString(R.string.record_setting_Detail_Setting));
                tv_record_type_title.setText(mStrings[0]);
                break;
        }
    }

    @Override
    public void setDeviceImage(String jpgPath) {
        Glide.with(RecordSettingActivity.this)
                .load(jpgPath)
                .error(errDrawable)
                .into(iv_device_image);
    }

    @Override
    public void setinClude_clockView(int type,String... times) {
        if (times != null && times.length >0){
            if (type == 1){
                record_setting_start_time_tv.setText(times[0]);
            }
            else if (type == 2){
                record_setting_end_time_tv.setText(times[0]);
            }
        }
         if ((type == 0)&&(times.length == 2)){
            record_setting_start_time_tv.setText(times[0]);
            record_setting_end_time_tv.setText(times[1]);
        }
         if (type == 3){
             record_setting_end_time_tv.setText(DateUtil.getHourAndMinuteString());
             record_setting_start_time_tv.setText(DateUtil.getHourAndMinuteString());
        }
    }

    @Override
    public void setDeviceName(String name) {
        record_setting_tv_name.setText(name);
    }

    @Override
    public void setDeviceStatus(String type){
        switch (type){
            case "0":
                tv_record_setting_status.setTextColor(getResources().getColor(R.color.record_content));
            iv_record_setting_status.setImageResource(R.drawable.record_ic_offline);
                tv_record_setting_status.setText(R.string.status_off_line);
                break;
            case "1":
                tv_record_setting_status.setTextColor(getResources().getColor(R.color.record_setting_status));
                tv_record_setting_status.setText(R.string.status_online);
                break;
            case "2":
                tv_record_setting_status.setTextColor(getResources().getColor(R.color.record_item_recording));
                iv_record_setting_status.setImageResource(R.drawable.record_ic_recording);
                tv_record_setting_status.setText(R.string.record_item_recording);
                break;
        }
    }

    @Override
    public void setLastRepeatInfo(String content) {
        record_setting_repeat_tv.setText(content);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithEvent(Object event){

    }

    @Override
    public void callbackSure() {

    }

    @Override
    public void callbackCancel() {

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
        if (lt_record.getChildCount() > 0){
            lt_record.removeViewAt(0);
        }
    }

    @Override
    public void saveCurrentChoose() {
        if (lt_record.getChildCount() > 0){
            lt_record.removeViewAt(0);
        }
        saveRecordType();
    }

    @Override
    public void cancelChoose() {
        if (lt_record.getChildCount() > 0){
            lt_record.removeViewAt(0);
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (currentSelectedItem != lastSelectedItem || currentSelectedItem == 0){
                    if (null == mDialog_save_remind){
                        mDialog_save_remind = new CustomDialog(RecordSettingActivity.this,CustomDialog.type_record_setting_save_remind, (SettingRemindCallback) RecordSettingActivity.this);
                        mDialog_save_remind.show();
                    }
                    else {
                        mDialog_save_remind.show();
                    }
                    return true;
                }
                else {
                    if (lt_record.getChildCount() > 0){
                        lt_record.removeViewAt(0);
                    }
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe (threadMode =  ThreadMode.MAIN)
    public void dealwithExit(IpcameraExit event){
        LogUtils.e("dealwithExit","exit");
        finish();
    }

}
