package com.ipcamerasen5.record.ui.view;

import android.app.Activity;
import android.os.Bundle;

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

public class TenSecondVidoFileActivity extends Activity {

    private List<IpCamDevice> devices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devices = DataSupport.findAll(IpCamDevice.class);
        EventBus.getDefault().register(this);
        if (devices.size() == 0){
//            finish();
            setContentView(R.layout.activity_ten_second_novideo_file);
        }
        else {
            setContentView(R.layout.activity_ten_second_vido_file);
        }
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
