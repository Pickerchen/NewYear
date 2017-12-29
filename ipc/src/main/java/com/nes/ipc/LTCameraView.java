package com.nes.ipc;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import glnk.client.GlnkClient;
import glnk.media.AView;
import glnk.media.AViewRenderer;
import glnk.media.GlnkDataSource;
import glnk.media.GlnkDataSourceListener;
import glnk.media.GlnkPlayer;
import glnk.media.VideoRenderer;

/**
 * Created by ZHOUDAO on 2017/8/16.
 */

public class LTCameraView extends FrameLayout {


    private final String TAG = LTCameraView.this.getClass().getSimpleName();



    public RelativeLayout mLoadUI;
    private LinearLayout mLlCamera;
    public TextView mTextView;
    public ImageView mIvDef;
    public FrameLayout mFlDef;


    public enum status {
        online, offline, other, connecting
    }

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


        View inflate = LayoutInflater.from(context).inflate(R.layout.lt_camera, null);
        addView(inflate);

        mTextView = (TextView) inflate.findViewById(R.id.lgls_tv_camera_status);
        mLoadUI = (RelativeLayout) inflate.findViewById(R.id.lgls_load_ui);
        mFlDef = (FrameLayout) inflate.findViewById(R.id.fl_def);
        mIvDef = (ImageView) inflate.findViewById(R.id.iv_def);
        mLlCamera = (LinearLayout) inflate.findViewById(R.id.ll_camera);


        ;

    }


    public void setCameraStatusText(status status) {

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


    private void addCameraView(AView view) {
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        mLlCamera.addView(view, viewParams);
    }

    private void removeView() {
        View view = ((LinearLayout) mLlCamera.getChildAt(0)).getChildAt(0);
        if (view instanceof AView) {
            mLlCamera.removeView(view);
        }
    }


}
