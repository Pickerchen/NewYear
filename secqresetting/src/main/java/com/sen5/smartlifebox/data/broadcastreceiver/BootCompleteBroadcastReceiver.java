package com.sen5.smartlifebox.data.broadcastreceiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.sen5.smartlifebox.common.utils.Utils;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.controlall.RemoteControl;
import com.sen5.smartlifebox.data.service.P2PConnectService;

import java.util.List;

/**
 * Created by wanglin on 2017/2/9.
 */
public class BootCompleteBroadcastReceiver extends BroadcastReceiver {

    //安防遥控器按键监测广播
    private static final String ACTION_SMARTHOME_KEY = "com.sen5.process.smarthome.key";
    private MyHandler mHandler;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
//            mHandler = new MyHandler(context);
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true){
//
//                        if(!isServiceWork(context, "com.sen5.smartlifebox.data.service.P2PConnectService")){
//                            mHandler.sendEmptyMessage(1);
//                        }
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
        }else if(ACTION_SMARTHOME_KEY.equals(action)){
            int keyCode = intent.getIntExtra("keycode", -1);
            String keyName = KeyEvent.keyCodeToString(keyCode);
            RemoteControl.smarthomeKey(context, keyCode);
        }

    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    private class MyHandler extends Handler{

        private Context mContext;

        public MyHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //开机启动P2P连接服务
            AppLog.e("开启安防服务在SecqreSetting");
            String encryptKey = Utils.getQRInfo(mContext);
            Intent serviceIntent = new Intent(mContext, P2PConnectService.class);
            serviceIntent.putExtra("EncryptKey", encryptKey);
            mContext.startService(serviceIntent);
        }
    }

//    private class MyAsyncTask extends AsyncTask<Context, Void, Boolean>{
//
//        private String encryptKey = null;
//        @Override
//        protected Boolean doInBackground(Context... params) {
//            while (encryptKey == null) {
//                encryptKey = Utils.getQRInfo(params[0]);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            super.onPostExecute(aBoolean);
//
//        }
//    }

}
