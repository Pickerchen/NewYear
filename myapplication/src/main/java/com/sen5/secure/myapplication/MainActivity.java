package com.sen5.secure.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nes.ipc.CameraEntity;
import com.nes.ipc.CameraManager;
import com.nes.ipc.CameraSearchListener;
import com.nes.ipc.LTCameraView;

import java.util.List;

import glnk.io.OnDeviceStatusChangedListener;
import glnk.media.GlnkDataSourceListener;
import glnk.media.GlnkPlayer;

public class MainActivity extends AppCompatActivity implements OnDeviceStatusChangedListener, GlnkDataSourceListener {

    private Intent i;
    int j = 0;
    private LTCameraView cameraView;
    private String TAG = this.getClass().getSimpleName();
    private boolean reConnect = false;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cameraView = (LTCameraView) findViewById(R.id.Camera);
//        cameraView.play("by0177dfa4", "admin", "admin", this);
        cameraView.setSource("by0177e5d6", "admin", "admin", this);

        CameraManager.getInstance().searchCamera(new CameraSearchListener() {
            @Override
            public void onSearchFinish(List<CameraEntity> list) {
                final StringBuffer sb = new StringBuffer();
                for (CameraEntity entity : list) {
                    sb.append(entity.getDeviceID()).append(entity.getDeviceName());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

        CameraManager.getInstance().setOnDeviceStatusChangedListener(this);



        findViewById(R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.play();

                v.setVisibility(View.GONE);
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraManager.getInstance().destorySDK();
    }


    @Override
    public void onChanged(String s, int i) {
        if (i <= 3 && i > 0) {


            cameraHandler.sendEmptyMessage(1);

        } else {

            cameraHandler.sendEmptyMessage(0);
        }


        Log.e(TAG, "status:" + i);
    }

    @Override
    public void onPushSvrInfo(String s, String s1, int i) {

    }

    Handler cameraHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:


                    cameraView.setCameraStatusText(LTCameraView.status.offline);
                    reConnect = true;
                    cameraView.stop();


                    break;
                case 1:
                    cameraView.setCameraStatusText(LTCameraView.status.online);
                    if (reConnect) {
                        cameraView.reStart();
                        reConnect = false;
                        return;
                    }


                    break;
            }


        }
    };

    @Override
    public void onTalkingResp(int i) {
    }

    @Override
    public void onIOCtrl(int i, byte[] bytes) {

    }

    @Override
    public void onIOCtrlByManu(byte[] bytes) {

    }

    @Override
    public void onRemoteFileResp(int i, int i1, int i2) {

    }

    @Override
    public void onRemoteFileEOF() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onConnected(int i, String s, int i1) {
        Log.e(TAG, "onConnected---i:" + i + "  s:" + s + "   i1:" + i1);

    }

    @Override
    public void onAuthorized(int i) {
        Log.e(TAG, "onAuthorized---i:" + i);

    }

    @Override
    public void onPermision(int i) {
        Log.e(TAG, "onPermision---i:" + i);
    }

    @Override
    public void onModeChanged(int i, String s, int i1) {
        Log.e(TAG, "onModeChanged---i:" + i + "  s:" + s + "   i1:" + i1);
    }

    @Override
    public void onDisconnected(int i) {
        Log.e(TAG, "onDisconnected---i:" + i);
    }

    @Override
    public void onDataRate(int i) {

    }

    @Override
    public void onReConnecting() {
        Log.e(TAG, "onReConnecting");
    }

    @Override
    public void onEndOfFileCtrl(int i) {

    }

    @Override
    public void onLocalFileOpenResp(int i, int i1) {

    }

    @Override
    public void onLocalFilePlayingStamp(int i) {

    }

    @Override
    public void onLocalFileEOF() {

    }

    @Override
    public void onOpenVideoProcess(int i) {

    }

    @Override
    public void onVideoFrameRate(int i) {

    }

    @Override
    public void onAppVideoFrameRate(int i) {

    }
}
