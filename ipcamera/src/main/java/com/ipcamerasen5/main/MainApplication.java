package com.ipcamerasen5.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.facebook.stetho.Stetho;
import com.ipcamerasen5.main.utils.MySharePre;
import com.ipcamerasen5.record.common.Constant;
import com.ipcamerasen5.record.event.IpcameraHomePress;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import nes.ltlib.utils.LogUtils;

import static com.ipcamerasen5.main.nobuffer.CameraProviderHelper.ACTION_RECEIVER_CLOSE_SERVICE;

/**
 * Created by jiangyicheng on 2017/2/28.
 */

public class MainApplication {
    private static Context mContext;
    public static String activityName = "MainActivity";
    public static boolean isFront = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.isFromLauncher = true;
                sendHomeBroadCast();
                EventBus.getDefault().post(new IpcameraHomePress());
        }
    };

    public  MainApplication(Context context){
        this.mContext = context;
        onCreate();
    }

    @Subscribe
    public void onCreate() {
        LogUtils.init();
        EventBus.getDefault().register(this);
        MySharePre.initMySharePre(mContext);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constant.homeAction);
        mContext.registerReceiver(mReceiver,mIntentFilter);
        initStetho();
    }
    /**
     * 提供全局的context
     * @return
     */
    public static  Context getWholeContext(){
            return mContext;
    }

    /**
     * 初始化stetho调试
     */
    private void initStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(mContext)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(mContext))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(mContext)).build());
    }
    /**
     * 发送广播通知launcher
     */
    public void  sendHomeBroadCast(){
        Intent intent = new Intent();
        intent.setAction(ACTION_RECEIVER_CLOSE_SERVICE);
        intent.putExtra("activityName",activityName);
        mContext.sendBroadcast(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event){

    }
}
