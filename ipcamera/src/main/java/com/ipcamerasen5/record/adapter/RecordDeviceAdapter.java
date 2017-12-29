package com.ipcamerasen5.record.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.callback.OnItemClickListener;
import com.ipcamerasen5.record.common.CommonTools;
import nes.ltlib.utils.LogUtils;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.event.IpcamStopStatusChanges;
import com.ipcamerasen5.record.service.RecordService;
import com.ipcamerasen5.record.ui.view.RecordActivity;
import com.ipcamerasen5.record.ui.view.RecordSettingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqianghua on 2017/4/10.
 */

public class RecordDeviceAdapter extends RecyclerView.Adapter<RecordDeviceAdapter.RecordDeviceViewHolder> {

    private List<IpCamDevice> devices = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private String[] record_types;
    private Activity mActivity;
    private Drawable errorDrawable;
    private boolean[] flags;//用来标记录制开关打开时是否需要通知开启24小时录制

    private boolean withFlag = false;

    public RecordDeviceAdapter(List<IpCamDevice> devices, Context context) {
        this.devices = devices;
        mContext = context;
        record_types = mContext.getResources().getStringArray(R.array.record_setting_pop);
        mActivity = (Activity) mContext;
        errorDrawable = mContext.getDrawable(R.mipmap.bg_camera_default_allscreen);
        EventBus.getDefault().register(this);
        flags = new boolean[devices.size()];
        for (int i=0; i<flags.length; i++){
            flags[i] = false;
        }
    }

    @Override
    public RecordDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_record_device,null);
        RecordDeviceViewHolder mViewHolder = new RecordDeviceViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecordDeviceViewHolder holder, final int position) {
        LogUtils.e("onBindViewHolder","bindViewHolder position is "+position);
         final IpCamDevice mIpCamDevice = devices.get(position);
      final String did = mIpCamDevice.getDid();
        String mLastJPGPath = null;
        mLastJPGPath = mIpCamDevice.getLastJPGPath();
        if (mLastJPGPath != null){
            LogUtils.e("onBindViewHolder","the last sensorPath is"+mLastJPGPath);
            Glide.with(mContext)
                    .load(mLastJPGPath)
                    .error(errorDrawable)
                    .crossFade()
                    .into(holder.iv_deviceImage);
        }
        final String status = mIpCamDevice.getStatus();
        String type = mIpCamDevice.getRecordType();
        if (status != null) {
            switch (status) {
                case "0":
                    holder.item_record_status_ll.setVisibility(View.INVISIBLE);
                    holder.tv_devicestatus.setTextColor(mContext.getResources().getColor(R.color.record_content));
//                    holder.tv_devicestatus.setTextColor(mContext.getColor(R.color.record_content));
                    holder.tv_devicestatus.setText(mContext.getString(R.string.status_off_line));
                    holder.iv_devicestatus.setImageResource(R.drawable.record_ic_offline);
                    break;
                case "1":
                    holder.item_record_status_ll.setVisibility(View.INVISIBLE);
                    holder.tv_devicestatus.setTextColor(mContext.getResources().getColor(R.color.record_setting_status));
                    holder.tv_devicestatus.setText(mContext.getString(R.string.record_item_status_normal));
                    holder.iv_devicestatus.setImageResource(R.drawable.record_ic_normal);
                    break;
                case "2":
                    holder.tv_devicestatus.setTextColor(mContext.getResources().getColor(R.color.record_setting_status));
                    holder.tv_devicestatus.setText(mContext.getString(R.string.record_item_status_normal));
                    holder.item_record_status_ll.setVisibility(View.VISIBLE);
                    holder.tv_record_status.setText(mContext.getString(R.string.record_item_recording));
                    holder.iv_devicestatus.setImageResource(R.drawable.record_ic_normal);
                    break;
            }
        }
        if (type != null){
            switch (type){
                case "0":
//                    holder.divider.setVisibility(View.VISIBLE);
//                    holder.item_record_clock_setting_ll.setVisibility(View.VISIBLE);
                    holder.tv_timed.setText(record_types[0]);
                    break;
                case "1":
                    holder.tv_timed.setText(record_types[1]);
                    break;
                case "2":
                    holder.tv_timed.setText(record_types[2]);
                    break;
                case "3":
                    holder.tv_timed.setText(record_types[3]);
                    break;
                default:
                    holder.tv_timed.setText("Non-recording");
                    break;
            }
        }
        else {
            holder.tv_timed.setText("Non-recording");
        }
        String isStop = mIpCamDevice.getIsStop();
//        if (isStop != null){
//            if (isStop.equals("2")){
//                holder.mSwitchCompat.setChecked(true);
//            }
//            else {
//                //不录
//                holder.mSwitchCompat.setChecked(false);
//            }
//        }
        if (isStop.equals("2")){
                holder.mSwitchCompat.setChecked(true);
            holder.tv_switch_status.setText(mContext.getString(R.string.switch_status_on));
            }
            else {
                //不录
                holder.mSwitchCompat.setChecked(false);
            holder.tv_switch_status.setText(mContext.getString(R.string.switch_status_off));
            }
        holder.iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordActivity.removeLTCamera();
                Intent intent = new Intent(mContext, RecordSettingActivity.class);
                intent.putExtra("did",mIpCamDevice.getDid());
                intent.putExtra("userID", RecordActivity.currentUserId);
                intent.putExtra("position",position);
                intent.putExtra("playPosition",RecordActivity.position);
                mActivity.startActivityForResult(intent,1);
//                Intent intent = new Intent(mContext, VideoTypeActivity.class);
//                intent.putExtra("did",mIpCamDevice.getDid());
//                intent.putExtra("userID", RecordActivity.currentUserId);
//                intent.putExtra("position",position);
//                mActivity.startActivityForResult(intent,1);
            }
        });
        //设置设备名称
        holder.tv_deviceName.setText(mIpCamDevice.getAliasName());

        holder.iv_setting.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.e("onBindViewHolder","iv_setting.onFocusChange hasFocus is "+hasFocus);
                if (hasFocus){
                    holder.tv_switch_status.setVisibility(View.VISIBLE);
                    ViewCompat.animate(holder.mSwitchCompat).scaleX(1.27f).scaleY(1.27f).translationZ(1.2f).setDuration(10).start();
//                    holder.mSwitchCompat.setThumbResource(R.drawable.switch_btn_nomal);
                    holder.mSwitchCompat.setAlpha(1.0f);
                    holder.iv_setting.setAlpha(1.0f);
                    holder.item_record_rl.setSelected(true);
                }
                else {
                    holder.tv_switch_status.setVisibility(View.GONE);
                    holder.mSwitchCompat.setAlpha(0.001f);
                    holder.iv_setting.setAlpha(0.001f);
                    holder.item_record_rl.setSelected(false);
                }
            }
        });
        // // TODO: 2017/5/19  此处应该有更合适的解决方案
        //解决在item条目的iv_setting处点击右键跳到其他控件问题
        holder.iv_setting.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                    return true;
                }
                return false;
            }
        });
        ViewCompat.animate(holder.mSwitchCompat).scaleX(1.27f).scaleY(1.27f).translationZ(1.2f).setDuration(10).start();
        if (!holder.mSwitchCompat.isFocused()){
            holder.mSwitchCompat.setThumbDrawable(mContext.getDrawable(R.drawable.switch_btn_nomal));
        }
//        holder.mSwitchCompat.setThumbDrawable(mContext.getDrawable(R.drawable.switch_btn_nomal));
        holder.mSwitchCompat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    holder.tv_switch_status.setVisibility(View.VISIBLE);
                    holder.mSwitchCompat.setThumbDrawable(mContext.getDrawable(R.drawable.switch_btn));
                    holder.mSwitchCompat.setAlpha(1.0f);
                    ViewCompat.animate(holder.mSwitchCompat).scaleX(1.27f).scaleY(1.27f).translationZ(1.2f).setDuration(10).start();
                    holder.iv_setting.setAlpha(1.0f);
                    holder.item_record_rl.setSelected(true);
                }
                else {
                    holder.tv_switch_status.setVisibility(View.GONE);
                    holder.mSwitchCompat.setThumbDrawable(mContext.getDrawable(R.drawable.switch_btn_nomal));
                    ViewCompat.animate(holder.mSwitchCompat).scaleX(0.01f).scaleY(0.01f).translationZ(0.01f).setDuration(10).start();
                    holder.mSwitchCompat.setAlpha(0.001f);
                    holder.iv_setting.setAlpha(0.001f);
                    holder.item_record_rl.setSelected(false);
                }
            }
        });
        holder.mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IpCamDevice device = mIpCamDevice;
                IpcamStopStatusChanges event = null;
                if (isChecked){
                    holder.tv_switch_status.setText(mContext.getString(R.string.switch_status_on));
                    device = CommonTools.findDeviceFromDB(device.getDid());
                    device.setIsStop("2");
                    device.save();
                    if (null != device.getStatus()) {
                        if (!device.getStatus().equals("0")) {
                            if (!(device.getRecordType() == null)) {
                                event = new IpcamStopStatusChanges(did, "2");
                                event.setIsStop("2");
                                EventBus.getDefault().post(event);
                                RecordService.dealwithRecordIsStop(did, "2");
                                //如果录制方式是24小时录制
                                List<IpCamDevice> mIpCamDevices = DataSupport.where("did = ?", device.getDid()).find(IpCamDevice.class);
                                if (mIpCamDevices.size() != 0) {
                                    if (mIpCamDevices.get(0).getRecordType().equals("1") && !flags[position]) {
                                        LogUtils.e("onCheckedChanged", "will dealRoundRecord");
                                        flags[position] = true;
                                        RecordService.dealwithRoundRecord(did);
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    holder.tv_switch_status.setText(mContext.getString(R.string.switch_status_off));
                    holder.mSwitchCompat.setThumbDrawable(mContext.getDrawable(R.drawable.switch_btn));
                    flags[position] = true;
                    device = CommonTools.findDeviceFromDB(device.getDid());
                    device.setIsStop("1");
                    device.save();
                    if (null != device.getStatus()){
                        if (!device.getStatus().equals("0")){
                            if (!(device.getRecordType() == null)){
                                event = new IpcamStopStatusChanges(did,"1");
                                EventBus.getDefault().post(event);
                                RecordService.dealwithRecordIsStop(did,"1");
                            }
                            if (device.getStatus().equals("2")){
                                device.setStatus("1");
                                device.save();
                            }
                        }
                    }
                    holder.item_record_status_ll.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    //设置onitemClickListener
    public void setOnItemClickListener(OnItemClickListener listener){
       mOnItemClickListener = listener;
    }

    private boolean isFirstFocuse = true;

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void dealwithBus(Object o){

    }

    public void unRegisterBus(){
        EventBus.getDefault().unregister(this);
    }

    class RecordDeviceViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout item_record_rl;
        LinearLayout item_record_status_ll;
        LinearLayout item_record_clock_setting_ll;
         TextView tv_deviceName;
         TextView tv_devicestatus;
        TextView tv_switch_status;
        ImageView iv_devicestatus;
         TextView tv_timed;
        TextView tv_record_status;
        TextView tv_timed_duration;
        TextView tv_timed_time;
         ImageView iv_deviceImage;
        SwitchCompat mSwitchCompat;
        ImageView iv_setting;
        View divider;
        public RecordDeviceViewHolder(final View itemView) {
            super(itemView);
            item_record_rl = (RelativeLayout) itemView.findViewById(R.id.item_record_rl);
            item_record_status_ll = (LinearLayout) itemView.findViewById(R.id.item_record_status_ll);
            item_record_clock_setting_ll = (LinearLayout) itemView.findViewById(R.id.ll_clocksetting);
            divider = itemView.findViewById(R.id.item_line);
            tv_deviceName = (TextView) itemView.findViewById(R.id.item_ll_Name);
            tv_devicestatus = (TextView) itemView.findViewById(R.id.item_device_online_status);
            iv_devicestatus = (ImageView) itemView.findViewById(R.id.item_device_online_iv);
            tv_switch_status = (TextView) itemView.findViewById(R.id.record_item_switch_status);
            tv_timed = (TextView) itemView.findViewById(R.id.item_device_timed_status);
            tv_record_status = (TextView) itemView.findViewById(R.id.item_device_record_status);
            tv_timed_duration = (TextView) itemView.findViewById(R.id.item_tv_time_duration);
            tv_timed_time = (TextView) itemView.findViewById(R.id.item_tv_timed_time);
            mSwitchCompat = (SwitchCompat) itemView.findViewById(R.id.item_switch);

            mSwitchCompat.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                        LogUtils.e("recordDeviceViewHolder","按下左键");
                        return true;
                    }
                    return false;
                }
            });
            iv_setting = (ImageView) itemView.findViewById(R.id.item_iv_setting);
            iv_deviceImage = (ImageView) itemView.findViewById(R.id.item_recording_iv);
        }

    }
}
