package com.ipcamerasen5.record.ui.presenter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ipcamerasen5.main.MainApplication;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.callback.DialogCallback;
import com.ipcamerasen5.record.common.CommonTools;
import com.ipcamerasen5.record.common.NumberPicker;
import com.ipcamerasen5.record.db.Clock;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.entity.ClockTempObject;
import com.ipcamerasen5.record.service.RecordService;
import com.ipcamerasen5.record.ui.view.CustomDialog;
import com.ipcamerasen5.record.ui.view.IRecordSettingActivity;
import com.ipcamerasen5.record.ui.view.RecordSettingActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import nes.ltlib.utils.LogUtils;

/**
 * Created by chenqianghua on 2017/4/18.
 */

public class ActivityRecordSettingPresent implements NumberPicker.ChooseCallback,DialogCallback {
    //popupwindow
    private PopupWindow mPopupWindow;
    private ListView pop_listView;
    private View mView;
    private String[] datas;
    //构造
    private Context mContext;
    private Activity mActivity;
    //UI逻辑
    private IRecordSettingActivity iRecordSettingView;
    //当前操作的摄像头：
    private String currentDid;
    public int currentPosition;//当前摄像头位置
    public IpCamDevice currentDevice;
    private int currentType = 100;

    //闹钟设置标记
    private int currentTimeType;//:具体为startTime还是EndTime
    private int[] currentRepeats = new int[]{0,0,0,0,0,0,0,8};//当前设置闹钟的循环周期:默认为每天都进行录制
    private String startTime = "";
    private String endTime = "";

    //该设备所有闹钟
    private List<Clock> mClocks = new ArrayList<>();
    private Clock lastClock = new Clock();//距离此时最近的一个闹钟
    private String repeat;

    public ActivityRecordSettingPresent(Context context, IRecordSettingActivity iVIew) {
        mContext = context;
        iRecordSettingView = iVIew;
    }
    //初始化数据
    public void initData(){
        mActivity = (Activity) mContext;
       currentDid = mActivity.getIntent().getStringExtra("did");
        RecordSettingActivity.did = currentDid;
        //当前编辑的摄像头的位置
        currentPosition = mActivity.getIntent().getIntExtra("position",0);

        currentDevice = DataSupport.where("did = ?",currentDid).find(IpCamDevice.class).get(0);
        if (currentDevice != null){
            LogUtils.e("initData","currentDevice is "+currentDevice.getRecordType());
        }
        String type = currentDevice.getRecordType();
        if (type != null && !type.equals("")){
            currentType = Integer.parseInt(type);
            iRecordSettingView.changeRecordingType(currentType);
        }
        else {
            //未设置录制类型的设备首次进入默认显示闹钟设置的选项
            iRecordSettingView.changeRecordingType(0);
        }
//        //最后录制的视频文件地址
//        String mLastRecordPath = currentDevice.getLastRecordPath();
//            LogUtils.e("initData","lastVideoPath is "+mLastRecordPath);
//            Bitmap mBitmap = CommonTools.getVideoThumbnail(mLastRecordPath);
        String jpgPath = currentDevice.getLastJPGPath();
        LogUtils.e("initData","jpgPath is "+jpgPath);
          iRecordSettingView.setDeviceImage(jpgPath);
        String aliseName = currentDevice.getAliasName();
        if (aliseName != null){
            iRecordSettingView.setDeviceName(aliseName);
        }
        String deviceStatus = currentDevice.getStatus();
        if (deviceStatus != null){
            iRecordSettingView.setDeviceStatus(deviceStatus);
        }

        mClocks = DataSupport.where("did = ?",currentDid).find(Clock.class);
        if (mClocks.size() != 0){
             lastClock = mClocks.get(mClocks.size()-1);
            repeat = lastClock.getTimes();
            String content  = CommonTools.timesToWeeks(repeat,mContext);
            LogUtils.e("initData","lastRepeat is "+content);
            iRecordSettingView.setLastRepeatInfo(content);
            String[] mStrings = new String[]{lastClock.getStartTime(),lastClock.getEndTime()};
            iRecordSettingView.setinClude_clockView(0,mStrings);
        }
        else {
            iRecordSettingView.setinClude_clockView(3);
        }
    }

    //判断是否需要进行保存，之前可能已经保存过该闹钟
    public boolean judgeShouldSave(String startTime,String endTime){
        boolean returnValue = true;
        List<Clock> mClocks = DataSupport.where("did = ?",currentDid).find(Clock.class);
        for (Clock mClock : mClocks){
            if (mClock.getStartTime().equals(startTime) && mClock.getEndTime().equals(endTime)){
                returnValue = false;
            }
        }
        int startTime_int = Integer.parseInt(startTime.replace(":",""));
        int endTime_int = Integer.parseInt(endTime.replace(":",""));
        LogUtils.e("judgeShouldSave","startTime is "+startTime+"endTime is "+endTime+"startInt ="+startTime_int+" endInt ="+endTime_int);
        if (endTime_int < startTime_int){
            returnValue = false;
        }
        return returnValue;
    }
    //保存用户选择的当前闹钟并删除之前的闹钟
    private List<Clock> mClocks1 = new ArrayList<>();
    private List<Clock> mClocks2 = new ArrayList<>();
    public void saveClockTime(String sT,String eT){

        mClocks1 = DataSupport.findAll(Clock.class);
        for (Clock mClock1 : mClocks1){
            if (mClock1.getDid().equals(currentDid)){
                mClocks2.add(mClock1);
                //删除闹钟
                mClock1.delete();
            }
        }
            Clock mClock = new Clock();
            mClock.setDid(currentDid);
            if (currentRepeats != null) {
                mClock.setTimes(CommonTools.intArrayToString(currentRepeats));
            } else {
                mClock.setTimes("00000008");//默认为每天都进行录制
            }
            if (startTime != "" && startTime != null) {
                mClock.setStartTime(startTime);
            } else {
                mClock.setStartTime(sT);
                startTime = sT;
            }
            if (endTime != "" && endTime != null) {
                mClock.setEndTime(endTime);
            } else {
                mClock.setEndTime(eT);
                endTime = eT;
            }
            int duration = CommonTools.computeDuration(startTime, endTime);
            if (duration != 0) {
                mClock.setDuration(duration);
                mClock.save();

                //更新录制service需要监听的录制闹钟
                List<ClockTempObject> mClockTempObjects = new ArrayList<>();
                for (int i = 0; i < currentRepeats.length; i++) {
                    if (currentRepeats[i] > 0) {
                        ClockTempObject mClockTempObject = new ClockTempObject();
                        mClockTempObject.setDid(currentDid);
                        String startTime_string = currentRepeats[i] + startTime.replaceAll(":", "");
                        mClockTempObject.setStartTime(startTime_string);
                        mClockTempObject.setDuration(duration);
                        mClockTempObject.setHasStartRecord(false);
                        mClockTempObjects.add(mClockTempObject);
                        LogUtils.e("saveClockTime","recordService will add one clock,and the time is "+startTime_string);
                    }
                }
//                RecordService.mTempObjects.removeAll(CommonTools.getAllClock(mClocks2));
                for (int i=0; i<RecordService.mTempObjects.size(); i++){
                    if (RecordService.mTempObjects.get(i).getDid().equals(currentDid)){
                        RecordService.mTempObjects.remove(RecordService.mTempObjects.get(i));
                    }
                }
                LogUtils.e("saveClockTime","add some clock size is "+mClockTempObjects.size());
                RecordService.mTempObjects.addAll(mClockTempObjects);
            }
    }

    /**
     * 保存ipcamera录制类型
     * @param position
     */
    public void saveIpCameraType(int position){
            currentDevice.setRecordType(position + "");
            currentDevice.save();
    }


    /**
     * 保存用户更改
     * @param currentID
     * @param lastID
     */
    public void saveChange(int currentID,int lastID,String... times){

    }



    //numberDialog显示
    public void showDialog(int type){
        if (type == 1 || type == 2){
            final CustomDialog   mCustomDialog = new CustomDialog(mContext,CustomDialog.type_time_picker, (NumberPicker.ChooseCallback) this,type);
            mCustomDialog.show();
            mCustomDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK){
                        if (mCustomDialog.isShowing()){
                        }
                    }
                    return false;
                }
            });
        }
        else if (type == 3){
            CustomDialog mCustomDialog = new CustomDialog(mContext,CustomDialog.type_clock_cycle, (DialogCallback) this,type);
            mCustomDialog.show();
        }
    }

    //popWindow操作
    public void initPop(){
         mView = LayoutInflater.from(mContext).inflate(R.layout.poplayout,null);
        DisplayMetrics dm;
        dm = mContext.getResources().getDisplayMetrics();
        float denisty = dm.density;
        float xdpi =  dm.widthPixels;
        float ydpi = dm.heightPixels;
        float width = MainApplication.getWholeContext().getResources().getDimension(R.dimen.FF);
        mPopupWindow = new PopupWindow(mView, (int)(width), WindowManager.LayoutParams.WRAP_CONTENT);
        // TODO: 2017/6/27 添加一个背景，popwidow响应返回事件自动消失
        ColorDrawable dw = new ColorDrawable(0x00000000);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setFocusable(true);
        pop_listView = (ListView) mView.findViewById(R.id.pop_lv);
        MyAdapter mMyAdapter = new MyAdapter();
        pop_listView.setAdapter(mMyAdapter);
        pop_listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtils.e("onItemSelected","onNothing selected");
            }
        });
    }

    /**
     * 显示pop
     * @param view:某个view下方显示
     */
    public void popShowOrDismiss(final View view){
        final TextView mTextView = (TextView) view.findViewById(R.id.record_setting_record_type_tv);
        mPopupWindow.showAsDropDown(view);
        ObjectAnimator anim = ObjectAnimator.ofFloat(mView, "translationY", -300,0);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mView,"alpha",0,1);
        AnimatorSet animaSet = new AnimatorSet();
        animaSet.setDuration(500);
        animaSet.play(anim).with(anim2);
        animaSet.start();
        pop_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(mView, "translationY", 0,-300);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(mView,"alpha",1,0);
                AnimatorSet animaSet = new AnimatorSet();
                animaSet.setDuration(500);
                animaSet.play(anim).with(anim2);
                animaSet.start();
                animaSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mPopupWindow.dismiss();
                        mTextView.setText(datas[position]);
                        if (position == 0){
                            iRecordSettingView.timedIsSelected(position);
                        }
                        else {
                            iRecordSettingView.timedIsnotSelected(position);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });
    }

    //用户设置好时间
    @Override
    public void chooseCallback(int value1, int value2,int type) {
        if (value2<10){
            iRecordSettingView.setinClude_clockView(type,value1+":"+"0"+value2);
            if (type == 1){
                startTime = value1+":"+"0"+value2;
            }
            else {
                endTime = value1+":"+"0"+value2;
            }
        }
        else {
            iRecordSettingView.setinClude_clockView(type,value1+":"+value2);
            if (type == 1){
                startTime = value1+":"+value2;
            }
            else {
                endTime = value1+":"+value2;
            }
        }
    }

    /**
     * popupwindow adapter
     */
    class  MyAdapter extends BaseAdapter{

        public MyAdapter() {
            Resources res = mContext.getResources () ;
            datas = res.getStringArray(R.array.record_setting_pop);
        }

        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pop,null);
            TextView tv_content = (TextView) convertView.findViewById(R.id.item_pop_tv);
            tv_content.setText(datas[position]);
            return convertView;
        }
    }

    /**
     * dialog行为回调
     * @param repeats
     */
    @Override
    public void repeatCallback(int[] repeats) {
        currentRepeats = repeats;
        //更新重复闹钟的UI
        String content = CommonTools.timesToWeeks(CommonTools.intArrayToString(repeats),mContext);
        iRecordSettingView.setLastRepeatInfo(content);
    }

    @Override
    public void chooseStorageCallback(String paths) {

    }

    @Override
    public void makeSureCallback(String path) {

    }
}
