package com.ipcamerasen5.record.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.adapter.DialogChooseStorageAdapter;
import com.ipcamerasen5.record.callback.DialogCallback;
import com.ipcamerasen5.record.callback.DialogClockCallback;
import com.ipcamerasen5.record.callback.SettingRemindCallback;
import com.ipcamerasen5.record.common.DateUtil;
import com.ipcamerasen5.record.common.NumberPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqianghua on 2017/4/11.
 */

public class CustomDialog extends Dialog implements View.OnClickListener
{
    public final static int type_tips = 0;
    public final static int type_time_picker = 1;
    public final static int type_clock_cycle = 2;//闹钟循环周期
    public final static int type_record_storage_remind = 3;//多个外接设备选择
    public final static int type_record_choose_storage = 4;//选择视频存放路径
    public final static int type_record_newStorage_remind = 5;//插入新设备时的提示
    public final static int type_record_clock_tip = 6;//设置闹钟提示
    public final static int type_record_storage_tip = 7;//提示磁盘不足1G的提示
    public final static int type_record_setting_save_remind = 8;//提示选择保存录制。
    private NumberPicker.ChooseCallback mChooseCallback;
    private DialogCallback mRepeatDialogCallback;
    private SettingRemindCallback dialog_setting_remind_callback;
    private DialogClockCallback clockCallback;
    private NumberPicker hour_picker;
    private NumberPicker minute_picker;
    private LinearLayout ll_monday;
    private LinearLayout ll_tuesday;
    private LinearLayout ll_wednesday;
    private LinearLayout ll_thursday;
    private LinearLayout ll_friday;
    private LinearLayout ll_saturday;
    private LinearLayout ll_sunday;
    private LinearLayout ll_everyday;
    private CheckBox cb_monday;
    private CheckBox cb_tuesday;
    private CheckBox cb_wednesday;
    private CheckBox cb_thursday;
    private CheckBox cb_friday;
    private CheckBox cb_saturday;
    private CheckBox cb_sunday;
    private CheckBox cb_everyday;
    private Button btn_verify;
    private int[] checks = new int[8];
    private boolean haschooseSomeDay = false;//用户是否有选择任何一天
    public int currentClockTime;//startTime还是endTime :0,1
    private List<String> mPaths = new ArrayList<>();
    private String mPath;


    private Context mContext;
    private int currentType;
    public CustomDialog(Context context, int type, NumberPicker.ChooseCallback callback,int time) {
        super(context);
        mChooseCallback = callback;
        mContext = context;
        currentType = type;
        currentClockTime = time;
        initDialog();
    }
    public CustomDialog(Context context,int type){
        super(context,R.style.custom_dialog);
        mContext = context;
        currentType = type;
        initDialog();
    }

    public CustomDialog(Context context, int type, DialogCallback callback, int time) {
        super(context);
        mRepeatDialogCallback = callback;
        mContext = context;
        currentType = type;
        currentClockTime = time;
        initDialog();
    }
    public  CustomDialog(Context context,int type,DialogCallback callback,List<String> paths){
        super(context);
        mRepeatDialogCallback = callback;
        mPaths = paths;
        mContext = context;
        currentType = type;
        initDialog();
    }

    public  CustomDialog(Context context,int type,DialogCallback callback,String path){
        super(context);
        mRepeatDialogCallback = callback;
        mPath = path;
        mContext = context;
        currentType = type;
        initDialog();
    }

    public CustomDialog(Context context, int type, DialogClockCallback dialogClockCallback){
        super(context);
        clockCallback = dialogClockCallback;
        mContext = context;
        currentType = type;
        initDialog();
    }

    public CustomDialog(Context context, int type, SettingRemindCallback callback){
        super(context);
        dialog_setting_remind_callback = callback;
        mContext = context;
        currentType = type;
        initDialog();
    }



    private void initDialog() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        Activity activity = (Activity)mContext;
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //不需要title(6.0的机器上时自动无title，5.0的机器上自动获得一个白色的title)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        switch (currentType){
            case type_tips:
                setContentView(R.layout.dialog_reminder);
                getWindow().setLayout(displayMetrics.widthPixels/2,1*(displayMetrics.heightPixels/2));
                Button btn_cancel = (Button) findViewById(R.id.dialog_btn_cancel);
                Button btn_stop =  (Button) findViewById(R.id.dialog_btn_stop);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                btn_stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                break;
            case type_record_storage_remind:
                setContentView(R.layout.record_storage_remind);
                getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                break;
            case type_clock_cycle:
                setContentView(R.layout.dialog_record_clock_recycle);
                getWindow().setLayout(displayMetrics.widthPixels/3, WindowManager.LayoutParams.WRAP_CONTENT);
                ll_monday = (LinearLayout) findViewById(R.id.ll_monday);
                ll_tuesday = (LinearLayout) findViewById(R.id.ll_tuesday);
                ll_wednesday  = (LinearLayout) findViewById(R.id.ll_wednesday);
                ll_thursday  = (LinearLayout) findViewById(R.id.ll_thursday);
                ll_friday  = (LinearLayout) findViewById(R.id.ll_friday);
                ll_saturday  = (LinearLayout) findViewById(R.id.ll_saturday);
                ll_sunday  = (LinearLayout) findViewById(R.id.ll_sunday);
                ll_everyday = (LinearLayout) findViewById(R.id.ll_everyday);

                cb_monday = (CheckBox) findViewById(R.id.cb_monday);
                cb_tuesday = (CheckBox) findViewById(R.id.cb_tuesday);
                cb_wednesday = (CheckBox) findViewById(R.id.cb_wednesday);
                cb_thursday = (CheckBox) findViewById(R.id.cb_thursday);
                cb_friday = (CheckBox) findViewById(R.id.cb_friday);
                cb_saturday = (CheckBox) findViewById(R.id.cb_saturday);
                cb_sunday = (CheckBox) findViewById(R.id.cb_sunday);
                cb_everyday = (CheckBox) findViewById(R.id.cb_everyday);
                ll_monday.setOnClickListener(this);
                ll_tuesday.setOnClickListener(this);
                ll_wednesday.setOnClickListener(this);
                ll_thursday.setOnClickListener(this);
                ll_friday.setOnClickListener(this);
                ll_saturday.setOnClickListener(this);
                ll_sunday.setOnClickListener(this);
                ll_everyday.setOnClickListener(this);
                btn_verify = (Button) findViewById(R.id.dialog_record_recycle_verify);
                btn_verify.setOnClickListener(this);
                break;
            case type_time_picker:
                setContentView(R.layout.dialog_time_picker);
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                getWindow().setLayout(displayMetrics.widthPixels/4,1*(displayMetrics.heightPixels/4));
                getWindow().setWindowAnimations(R.style.myDialogstyle);
                hour_picker = (NumberPicker) findViewById(R.id.number_picker3);
                minute_picker = (NumberPicker) findViewById(R.id.number_picker4);
                int[] times = DateUtil.getHourAndMinuteInt();
                hour_picker.setValue(times[0]);
                minute_picker.setValue(times[1]);
                hour_picker.setOnClickListener(this);
                minute_picker.setOnClickListener(this);
                hour_picker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                    }
                });
                break;
            case type_record_choose_storage:
                setContentView(R.layout.dialog_storage_choose);
                getWindow().setLayout(600, WindowManager.LayoutParams.WRAP_CONTENT);
                ListView mListView = (ListView) findViewById(R.id.dialog_storage_rv);
                DialogChooseStorageAdapter mAdapter = new DialogChooseStorageAdapter(mPaths,mContext);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mRepeatDialogCallback.chooseStorageCallback(mPaths.get(position));
                    }
                });
                break;
            case type_record_newStorage_remind:
                setContentView(R.layout.dialog_newstorage_remind);
                getWindow().setLayout(600,WindowManager.LayoutParams.WRAP_CONTENT);
                Button btn_sure = (Button) findViewById(R.id.dialog_btn_sure);
                Button btn_cancle = (Button) findViewById(R.id.dialog_btn_cancel);
                btn_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRepeatDialogCallback.makeSureCallback(mPath);
                    }
                });
                btn_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                break;
            case type_record_clock_tip:
                //设置闹钟提示
                setContentView(R.layout.dialog_clock_tips);
                getWindow().setLayout(800,WindowManager.LayoutParams.WRAP_CONTENT);
                Button btn_ok = (Button) findViewById(R.id.dialog_clock_btn_sure);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                break;
            case type_record_storage_tip:
                //磁盘空间不足1G提示
                setContentView(R.layout.dialog_storage_tip);
                getWindow().setLayout(800,WindowManager.LayoutParams.WRAP_CONTENT);
                Button btn_ok2 = (Button) findViewById(R.id.dialog_storageTip_btn_sure);
                btn_ok2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                break;
            case type_record_setting_save_remind:
                setContentView(R.layout.dialog_save_remind);
                getWindow().setLayout(800,WindowManager.LayoutParams.WRAP_CONTENT);
                Button btn_ok_save = (Button) findViewById(R.id.dialog_clock_btn_sure);
                Button btn_cancel_save = (Button) findViewById(R.id.dialog_clock_btn_cancel);
                btn_ok_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_setting_remind_callback.saveCurrentChoose();
                    }
                });
                btn_cancel_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        dialog_setting_remind_callback.cancelChoose();
                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.number_picker3 || i == R.id.number_picker4) {
            mChooseCallback.chooseCallback(hour_picker.getValue(), minute_picker.getValue(), currentClockTime);
            dismiss();

        } else if (i == R.id.ll_monday) {
            if (cb_monday.isChecked()) {
                cb_monday.setChecked(false);
                checks[0] = 0;
            } else {
                cb_monday.setChecked(true);
                checks[0] = 1;
            }

        } else if (i == R.id.ll_tuesday) {
            if (cb_tuesday.isChecked()) {
                cb_tuesday.setChecked(false);
                checks[1] = 0;
            } else {
                cb_tuesday.setChecked(true);
                checks[1] = 2;
            }

        } else if (i == R.id.ll_wednesday) {
            if (cb_wednesday.isChecked()) {
                cb_wednesday.setChecked(false);
                checks[2] = 0;
            } else {
                cb_wednesday.setChecked(true);
                checks[2] = 3;
            }

        } else if (i == R.id.ll_thursday) {
            if (cb_thursday.isChecked()) {
                cb_thursday.setChecked(false);
                checks[3] = 0;
            } else {
                cb_thursday.setChecked(true);
                checks[3] = 4;
            }

        } else if (i == R.id.ll_friday) {
            if (cb_friday.isChecked()) {
                cb_friday.setChecked(false);
                checks[4] = 0;
            } else {
                checks[4] = 5;
                cb_friday.setChecked(true);
            }

        } else if (i == R.id.ll_saturday) {
            if (cb_saturday.isChecked()) {
                checks[5] = 0;
                cb_saturday.setChecked(false);
            } else {
                checks[5] = 6;
                cb_saturday.setChecked(true);
            }

        } else if (i == R.id.ll_sunday) {
            if (cb_sunday.isChecked()) {
                checks[6] = 0;
                cb_sunday.setChecked(false);
            } else {
                checks[6] = 7;
                cb_sunday.setChecked(true);
            }

        } else if (i == R.id.ll_everyday) {
            if (cb_everyday.isChecked()) {
                checks[7] = 0;
                cb_everyday.setChecked(false);
            } else {
                checks[7] = 8;
                cb_everyday.setChecked(true);
            }

        } else if (i == R.id.dialog_record_recycle_verify) {
            for (int check : checks) {
                if (check > 0) {
                    haschooseSomeDay = true;
                    break;
                }
            }
            if (haschooseSomeDay) {
                mRepeatDialogCallback.repeatCallback(checks);
            } else {
                mRepeatDialogCallback.repeatCallback(new int[]{0, 0, 0, 0, 0, 0, 0, 8});
            }
            dismiss();

        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && currentType == type_record_storage_remind){
//            LogUtils.e("onkeydown","dialog onkeydown");
//            dismiss();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
