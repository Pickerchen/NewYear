package com.sen5.secure.launcher.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.utils.MyRender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

import hsl.p2pipcam.nativecaller.DeviceSDK;
import nes.ltlib.utils.AppLog;


/**
 * Created by jiangyicheng on 2017/2/28.
 */

public class CameraView extends RelativeLayout implements MyRender.RenderListener {

    public GLSurfaceView mGlSurfaceView;
    public RelativeLayout mLoadUI;
    public TextView mTextView;
    public long mUserid;
    public long mRecordId;
    public String mDid;
    protected MyRender mMyRender;
    public ImageView mIvDef;
    public FrameLayout mFlDef;

    private TextView mFrame;

    public int playCode;
    public int recordCode;

    private int Drawable;

    public boolean isUse;

    public enum Mode {
        play, record;
    }


    public enum status {
        online, offline, other, connecting
    }


    public status getmStatus() {
        return mStatus;
    }

    private status mStatus;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CameraView);
        Drawable = typedArray.getResourceId(R.styleable.CameraView_defImage, -1);
        initView();
    }

    protected void initView() {
        AppLog.e("------------------initView");
        View inflate = inflate(getContext(), R.layout.layout_gls, null);
//        mGlSurfaceView = (GLSurfaceView)this.getChildAt(0);
//        mTextView = (TextView)this.getChildAt(1);
        mGlSurfaceView = (GLSurfaceView) inflate.findViewById(R.id.lgls_glsurfaceview);
        mTextView = (TextView) inflate.findViewById(R.id.lgls_tv_camera_status);
        mLoadUI = (RelativeLayout) inflate.findViewById(R.id.lgls_load_ui);
        mFrame = (TextView) inflate.findViewById(R.id.tv_frame);
        mFlDef = (FrameLayout) inflate.findViewById(R.id.fl_def);
        mIvDef = (ImageView) inflate.findViewById(R.id.iv_def);
        if (Drawable != -1)
            mIvDef.setImageResource(Drawable);
        addView(inflate);
    }

    public void initDevices(String did) {
        mDid = did;
        isUse = true;
        mUserid = DeviceSDK.createDevice("admin", "", "", 0, did, 1);
        mRecordId = DeviceSDK.createDevice("admin", "", "", 0, did, 1);


        Log.e("CameraView", "initDevices:" + mUserid);
        Log.e("CameraView", "initDevices:" + mRecordId);

        int start = -2;
        if (mUserid > 0) {
            start = openDevice(mUserid);
        }

        if (mRecordId > 0) {
            openDevice(mRecordId);
        }

    }

    /**
     * 重连机制
     *
     * @param mode 0:播放摄像头 1：录制摄像头
     */
    public void restartDevices(int mode) {
        if (mode == 0) {
            isUse = true;
            mUserid = DeviceSDK.createDevice("admin", "", "", 0, mDid, 1);
            if (mUserid > 0) {
                openDevice(mUserid);
            }
        } else if (mode == 1) {
            mRecordId = DeviceSDK.createDevice("admin", "", "", 0, mDid, 1);
            if (mRecordId > 0) {
                openDevice(mRecordId);
            }
        }
    }

    public int openDevice(long mUserid) {
        return DeviceSDK.openDevice(mUserid);
    }

    public void setRender(MyRender myRender) {
        this.mMyRender = myRender;
        this.mMyRender.setListener(this);
        mGlSurfaceView.setRenderer(myRender);
    }

    public void startPlay() {
        new LoadTask().execute();

    }


    public void stopPlayStream() {
        if (mUserid > 0)
            stopPlayStream(mUserid);
    }

    public void stopPlayStream(long userID) {
        DeviceSDK.stopPlayStream(userID);
        Log.e("CameraView", "stopPlayStream:" + userID);
    }

    /**
     * 根据模式来停止对应的userId
     *
     * @param mode 0:播放摄像头 1：录制摄像头
     */
    public void stopPlayStream(Mode mode) {
        if (mode == Mode.play) {
            isUse = false;
            stopPlayStream(mUserid);

        } else if (mode == Mode.record) {
            stopPlayStream(mRecordId);
        }
    }


    public void closeDevice() {
        isUse = false;
        closeDevice(mUserid);
        Log.e("CameraView", "closeDevice:" + mUserid);
    }


    public void destroyDevice() {
        destroyDevice(mUserid);

    }

    public void destroyDevice(long userId) {
        DeviceSDK.destoryDevice(userId);

    }


    public void closeDevice(long userID) {
        DeviceSDK.closeDevice(userID);
    }

//    public boolean capturePicture(String capPath){
//        boolean b = CameraControlNew.capturePicture(mUserid, capPath);
//        return b;
//    }


    private class LoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            DeviceSDK.setRender(mUserid, mMyRender);
            DeviceSDK.startPlayStream(mUserid, 0, 2);
            AppLog.e("正在播放的CameraView:userId =" + mUserid + " Did =" + mDid);

            try {
                JSONObject obj = new JSONObject();
                obj.put("param", 6);
                obj.put("value", 15);
                DeviceSDK.setDeviceParam(mUserid, 0x2026, obj.toString());


                JSONObject obj1 = new JSONObject();
                obj1.put("param", 13);
                obj1.put("value", 1024);
                DeviceSDK.setDeviceParam(mUserid, 0x2026, obj1.toString());


                setTime(mUserid);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

    }

    @Override
    public void initComplete(int size, int width, int height) {

    }

    @Override
    public void takePicture(byte[] imageBuffer, int width, int height) {

    }

    public void setGLSBackground(Drawable drawable) {
        mGlSurfaceView.setBackground(drawable);
    }

    public void setGLSBackground(int color) {
        mGlSurfaceView.setBackgroundColor(color);
    }

    public void setChildLayoutBackground(Drawable drawable) {
        getChildAt(0).setBackground(drawable);
    }


    public void setCameraStatusText(status status) {
        mStatus = status;
        mFlDef.setVisibility(GONE);

        switch (status) {
            case online:
                mLoadUI.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);


                break;
            case offline:
                mLoadUI.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(getContext().getResources().getString(R.string.Offline));

                break;

            case other:
                mLoadUI.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                mTextView.setText("");

                break;
            case connecting:
                mLoadUI.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(getContext().getResources().getString(R.string.status_connecting));
                break;
        }


    }

    public void setChildVisibility(int visibility) {
        getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
        mGlSurfaceView.setBackgroundColor(Color.TRANSPARENT);

        getChildAt(0).setVisibility(visibility);
        mGlSurfaceView.setVisibility(visibility);

    }


    public void setTime(long userid) throws JSONException {
        //					if(UtilsCameraTime.isTimeGapChange()){
        int timeGap = Integer.parseInt(TimeZone.getDefault().getRawOffset() + "");
        JSONObject obj2 = new JSONObject();
        long currentTime = System.currentTimeMillis();
        AppLog.e("-------------timeGap = " + timeGap + "   currentTime = " + currentTime);
        obj2.put("now", currentTime / 1000);
        obj2.put("ntp_enable", 1);
        obj2.put("ntp_svr", "time.nist.gov");
        //北京时区偏差值，八个小时
        obj2.put("timezone", -(timeGap / 1000));
        DeviceSDK.setDeviceParam(userid, 0x2015, obj2.toString());
//					}
    }


    private long[] time = {30000};

    private int size = 0;

    public long getTime() {

        int i = Math.min(size, time.length - 1);
        AppLog.e("-------Camera-------i=" + i + "");
        long time = this.time[i];
        size++;
        return time;
    }

    public boolean isReConnect() {
        if (size > time.length - 1) {
            return false;
        }

        return true;
    }

    public long getCurrentTime() {

        int i = Math.min(size - 1, time.length - 1);
        long time = this.time[i];
        return time;
    }

    public void setDefImage(int drawable) {
        mIvDef.setImageResource(drawable);
    }


    public void setFocusFrame(boolean isFocus) {
        if (isFocus) {
            mFrame.setVisibility(VISIBLE);
        } else {
            mFrame.setVisibility(GONE);
        }
    }

}
