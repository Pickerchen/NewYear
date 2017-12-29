package com.ipcamerasen5.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ipcamerasen5.main.message.MessageReceiver;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jiangyicheng on 2016/11/15.
 */

public class ReceiverCameraKey extends BroadcastReceiver{
    public static final String ACTION_IPCAMERA_KEY_FLOAT = "com.sen5.process.camera.key";
    private  EventBus eventBus;

    public ReceiverCameraKey(){
        eventBus = EventBus.getDefault();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null != intent){
            String action = intent.getAction();
            if(ACTION_IPCAMERA_KEY_FLOAT.equals(action)){
                eventBus.post(new MessageReceiver(true));
            }
        }
    }
}
