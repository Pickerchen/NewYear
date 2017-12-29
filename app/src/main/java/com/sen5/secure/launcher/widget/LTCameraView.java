package com.sen5.secure.launcher.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sen5.secure.launcher.R;

import glnk.media.AViewRenderer;
import nes.ltlib.data.LTDevice;
import nes.ltlib.LTSDKManager;

/**
 * Created by ZHOUDAO on 2017/8/16.
 */

public class LTCameraView extends FrameLayout {


    private final String TAG = LTCameraView.this.getClass().getSimpleName();


    public RelativeLayout mLoadUI;
    public RelativeLayout mLlCamera;
    public TextView mTextView;
    public ImageView mIvDef;
    public FrameLayout mFlDef;

    public Bitmap getmLastFrame() {
        try {
            if (ltDevice == null || ltDevice.getGlnkPlayer() == null) {
                return null;
            }
            return ltDevice.getGlnkPlayer().getFrame();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public Bitmap mLastFrame;

    public LTDevice getLtDevice() {
        return ltDevice;
    }

    private LTDevice ltDevice;

    private Context mContext;

    private TextView mTvFrame;


    public enum UIStatus {
        online, offline, other, connecting
    }

    public UIStatus getmUIStatus() {
        return mUIStatus;
    }

    private UIStatus mUIStatus;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    private String gid;

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    private boolean isOnLine;


    public LTCameraView(Context context) {
        this(context, null);

    }


    public LTCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LTCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    /**
     * 需要创建一个play
     * 需要一个render
     * 需要一个soure
     */
    private void init(Context context) {

        this.mContext = context;

        View inflate = LayoutInflater.from(context).inflate(R.layout.lt_camera, null);
        addView(inflate);

        mTextView = (TextView) inflate.findViewById(R.id.lgls_tv_camera_status);
        mLoadUI = (RelativeLayout) inflate.findViewById(R.id.lgls_load_ui);
        mFlDef = (FrameLayout) inflate.findViewById(R.id.fl_def);
        mIvDef = (ImageView) inflate.findViewById(R.id.iv_def);
        mLlCamera = (RelativeLayout) inflate.findViewById(R.id.rl_LTCamera);
//        mLastFrame = (ImageView) inflate.findViewById(R.id.last_fame);
        mTvFrame = (TextView) inflate.findViewById(R.id.tv_frame);

    }


    public void setCameraStatusText(UIStatus UIStatus) {

        mUIStatus = UIStatus;
        mFlDef.setVisibility(View.GONE);
//        mLlCamera.setBackgroundResource(R.color.bg_black_ap_30);
        switch (UIStatus) {
            case online:
                mLoadUI.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                mTextView.setText("");
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


    public void addCameraView(LTDevice LTDevice) {
        this.ltDevice = LTDevice;
        if (LTDevice == null)
            return;


        Log.i("LTSDK", "AView个数" + mLlCamera.getChildCount());

        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (mLlCamera.getChildCount() > 0) {
            mLlCamera.removeAllViews();
        }
        /**
         * 前提：摄像头突然离线 结果：AView被注销 解决：增加非空判断
         */
        if (LTDevice.getAView() != null) {
            mLlCamera.addView(LTDevice.getAView(), viewParams);
        } else {
            setCameraStatusText(UIStatus.offline);
        }
    }

    public void removeCameraView() {
        if (mLlCamera.getChildCount() > 0) {
            mLlCamera.removeAllViews();
        }

        showLastFrame();
    }

    public void removeLtDevice() {
        removeCameraView();
        if (ltDevice != null) {
            LTSDKManager.getInstance().releaseDevice(ltDevice);
        }

    }

    public void showLastFrame() {

        if (getmLastFrame() == null) {
            mLlCamera.setBackgroundResource(R.color.bg_black_ap_30);
        } else {

            mLlCamera.setBackground(new BitmapDrawable(mContext.getResources(), getmLastFrame()));
        }


    }


    public void setLTCameraBg(int resId) {
        mLlCamera.setBackgroundResource(resId);
    }

    public void setFocusFrame(boolean isFocus) {
        if (isFocus) {
            mTvFrame.setVisibility(VISIBLE);
        } else {
            mTvFrame.setVisibility(GONE);
        }
    }


    public void setVideoSize() {
        if (ltDevice != null && ltDevice.getVideoRenderer() != null) {

            if (ltDevice.getGid().contains("zz")) {
                ((AViewRenderer) ltDevice.getVideoRenderer()).getMatrix().setScale(mContext.getResources().getDimension(R.dimen.dimen_290) / 640, mContext.getResources().getDimension(R.dimen.dimen_163) / 360);

            } else {

                ((AViewRenderer) ltDevice.getVideoRenderer()).getMatrix().setScale(mContext.getResources().getDimension(R.dimen.dimen_290) / 640, mContext.getResources().getDimension(R.dimen.dimen_163) / 480);
            }
        }
    }


}
