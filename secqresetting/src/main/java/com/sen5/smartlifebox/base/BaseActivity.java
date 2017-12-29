package com.sen5.smartlifebox.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.SecQreSettingApplication;
import nes.ltlib.utils.AppLog;

import com.sen5.smartlifebox.socket.ZigBeeSocket;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by wanglin on 2016/8/25.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private ZigBeeSocket socket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = SecQreSettingApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    public void startActivityAnim(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_right_in,
                R.anim.activity_slide_remain);
    }

    public void startActivityForResultAnim(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_slide_right_in,
                R.anim.activity_slide_remain);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_slide_right_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected boolean isShowingProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return true;
        }
        return false;
    }

    /**
     * Home????Action
     */
    private static final String ACTION_KEYCODE_HOME = "com.amlogic.dvbplayer.homekey";
    /**
     * ?????
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            AppLog.e("--------------mDefaultWifiEnable action = " + action);
            if (ACTION_KEYCODE_HOME.equals(action)) {
//                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//                wifiManager.setWifiEnabled(SecQreSettingApplication.isDefaultWifiEnable());
                resetWifi();
            }
        }
    };

    protected void resetWifi() {

    }

    ;

    protected void registerHomeReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_KEYCODE_HOME);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    protected void unRegisterHomeReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    private boolean flag_3 = false;
    private boolean flag_6 = false;
    private boolean flag_9 = false;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        switch (keyCode) {
            case KeyEvent.KEYCODE_3:
                flag_3 = true;
                break;
            case KeyEvent.KEYCODE_6:
                flag_6 = true;
                break;
            case KeyEvent.KEYCODE_9:
                flag_9 = true;
                break;
            case KeyEvent.KEYCODE_7:

                if (flag_3 && flag_6 && flag_9) {
                    AppLog.e("flag_3:" + flag_3 + "flag_6" + flag_6 + "flag_9" + flag_9);

                }


                break;
            default:
                flag_3 = false;
                flag_9 = false;
                flag_6 = false;
                break;


        }

        return super.onKeyDown(keyCode, event);
    }




}
