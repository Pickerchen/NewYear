package com.ipcamerasen5.record.ui.view;

import android.app.Activity;
import android.os.Bundle;

import com.ipcamerasen5.main.MainApplication;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.event.IpcameraExit;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import nes.ltlib.utils.LogUtils;

/**
 * Created by chenqianghua on 2017/5/8.
 */

public class VideoFileActivity extends Activity{
    private List<IpCamDevice> devices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devices = DataSupport.findAll(IpCamDevice.class);
        EventBus.getDefault().register(this);
        if (devices.size() == 0){
//            finish();
            setContentView(R.layout.activity_video_file_novideo);
        }
        else {
            setContentView(R.layout.activity_video_file);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.activityName = VideoFileActivity.class.getSimpleName();
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void dealwithExit(IpcameraExit event){
        LogUtils.e("dealwithExit","exit");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
