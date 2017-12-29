package com.sen5.smartlifebox.ui.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseActivity;
import com.sen5.smartlifebox.common.utils.WifiApAdmin;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.event.BackFragmentEvent;
import com.sen5.smartlifebox.data.service.IPCameraService;
import com.sen5.smartlifebox.ui.main.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;



public class ConnectCameraActivity extends BaseActivity {


    TextView tvInfo;

    LinearLayout llProgress;

    TextView tvTimer;
    private IPCameraService mIPCameraService;
    private MyHandler mHandler;

    public static final int WIFI_SET_SUCCESS = 1;
    public static final int WIFI_SET_FAILURE = 2;
    public static final int FINISH_ACTIVITY = 3;
    private static final int SET_WIFI_TIME_OUT = 4;
    public static final int WIFI_SET_FAILURE_DELAY = 5;

    private static final int TIME_OUT = 120;
    private int mSecond = TIME_OUT;


    private boolean mIsStartConnectService = false;

    private String wifiName;
    private String wifiPassword;

    static class MyHandler extends Handler {


        private ConnectCameraActivity mActivity;

        public MyHandler(ConnectCameraActivity activity) {
            WeakReference<ConnectCameraActivity> reference = new WeakReference<ConnectCameraActivity>(activity);
            mActivity = reference.get();

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WIFI_SET_SUCCESS: {
                    mActivity.llProgress.setVisibility(View.INVISIBLE);
                    mActivity.tvInfo.setText(mActivity.getString(R.string.set_wifi_successfully));
                    sendEmptyMessageDelayed(FINISH_ACTIVITY, 3000);
                }
                break;

                case WIFI_SET_FAILURE_DELAY:
                    mActivity.tvInfo.setText(mActivity.getString(R.string.set_wifi_failure));
                    mActivity.llProgress.setVisibility(View.INVISIBLE);

                    removeMessages(WIFI_SET_FAILURE);
                    sendEmptyMessageDelayed(WIFI_SET_FAILURE, 3000);
                    break;

                case WIFI_SET_FAILURE: {
                    Intent intent = new Intent(mActivity, MainActivity.class);
                    mActivity.startActivity(intent);
                }
                break;

                case FINISH_ACTIVITY: {
                    BackFragmentEvent event = new BackFragmentEvent(BackFragmentEvent.IP_CAMERA_FRAGMENT);
                    event.setSwitchFindLANCameraFragment(true);
                    EventBus.getDefault().post(event);
                    Intent intent = new Intent(mActivity, MainActivity.class);
                    mActivity.startActivity(intent);
                }
                break;

                case SET_WIFI_TIME_OUT:

                    if (mActivity.mSecond == 10) {
                        mActivity.stopConnectService();
                    }


                    if (mActivity.mSecond == 0) {

                        mActivity.tvInfo.setText(mActivity.getString(R.string.set_wifi_timeout));
                        mActivity.llProgress.setVisibility(View.INVISIBLE);

                        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                                .setMessage(mActivity.getString(R.string.set_wifi_failure))
                                .setPositiveButton(mActivity.getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                sendEmptyMessage(WIFI_SET_FAILURE);
                                            }
                                        })
                                .setNegativeButton(mActivity.getString(R.string.try_again),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mActivity.startConnectService();
                                                mActivity.mSecond = TIME_OUT;
                                                mActivity.tvTimer.setText(String.format("%sS", mActivity.mSecond));
                                                sendEmptyMessageDelayed(SET_WIFI_TIME_OUT, 1000);
                                                mActivity.tvInfo.setText(mActivity.getString(
                                                        R.string.restart_camera));
                                                mActivity.llProgress.setVisibility(View.VISIBLE);

                                                dialog.dismiss();
                                            }
                                        })
                                .create();
                        dialog.show();
                    } else {
                        sendEmptyMessageDelayed(SET_WIFI_TIME_OUT, 1000);
                        mActivity.mSecond--;
                        mActivity.tvTimer.setText(String.format("%sS", mActivity.mSecond));
                    }

                    break;

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcamera_connect);
        initView();
        registerHomeReceiver();
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterHomeReceiver();
        stopConnectService();
        mHandler.removeMessages(SET_WIFI_TIME_OUT);
    }

    @Override
    protected void resetWifi() {
        super.resetWifi();
        stopConnectService();
        mHandler.removeMessages(SET_WIFI_TIME_OUT);
    }


    private void initView() {
        tvInfo = (TextView) findViewById(R.id.tv_info);
        llProgress = (LinearLayout) findViewById(R.id.ll_progress);
        tvTimer = (TextView) findViewById(R.id.tv_timer);
    }

    private void initData() {
        wifiName = getIntent().getStringExtra("WifiName");
        wifiPassword = getIntent().getStringExtra("WifiPassword");
        tvTimer.setText(String.format("%sS", mSecond));
        mHandler = new MyHandler(this);

        startConnectService();
        mHandler.sendEmptyMessageDelayed(SET_WIFI_TIME_OUT, 1000);
    }

    /**
     * 开启连接服务。包括：开启IP Camera Socket服务、创建Wifi热点
     */
    private void startConnectService() {
        //创建SmartLife WI-FI热点
        WifiApAdmin.startWifiAp(getApplicationContext(), "SmartLife", "12345678");
        //开启IPCamera服务
        mIPCameraService = new IPCameraService(new IPCameraService.CallBack() {
            @Override
            public void onSuccess() {
                AppLog.d("IP Camera配置wifi成功！");
                mHandler.removeMessages(SET_WIFI_TIME_OUT);
                stopConnectService();
                // TODO: 2017/1/19  连接到IP Camera配置的wifi，为扫描IPCamera做准备

                //延时一定时间等待Camera去连接配置的wifi，并且由于创建热点会关闭wifi，顺便等待盒子本身去还原wifi状态
                mHandler.sendEmptyMessageDelayed(WIFI_SET_SUCCESS, 15000);
            }

            @Override
            public void onFail() {
                AppLog.d("IP Camera配置wifi失败！");
                mHandler.removeMessages(SET_WIFI_TIME_OUT);
                stopConnectService();

                mHandler.sendEmptyMessageDelayed(WIFI_SET_FAILURE_DELAY, 13000);

            }
        });
        mIPCameraService.setWifiName(wifiName);
        mIPCameraService.setWifiPassword(wifiPassword);
        mIPCameraService.setAuthType(3);
        mIPCameraService.start();

        mIsStartConnectService = true;
    }

    private void stopConnectService() {
        if (mIsStartConnectService) {
            //关掉IPCamera服务
            mIPCameraService.stop();
            //关闭wifi热点、此时会还原wifi状态
            WifiApAdmin.closeWifiAp(getApplicationContext());

            mIsStartConnectService = false;
        }
    }

    private Animation getAnimation() {
        Animation animation = AnimationUtils.loadAnimation(ConnectCameraActivity.this, R.anim.fade_in);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AppLog.d("回到MainActivity");
                Intent intent = new Intent(ConnectCameraActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

}
