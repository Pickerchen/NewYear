package com.ipcamera.main.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipcamera.main.R;
import com.ipcamera.main.utils.DLog;
import com.ipcamera.main.utils.Utils;

import glnk.media.AViewRenderer;
import nes.ltlib.data.LTDevice;

/**
 * Created by ZHOUDAO on 2017/9/4.
 */

public class LTCameraUI {

    private Context mContext;
    private LinearLayout mLlCamera;
    private TextView mTvStatus;

    private MenuFrameLayout mFlmenu;
    private View mLoadUi;

    private View LtView;

    private LTDevice ltDevice;


    public LTCameraUI(Context mContext) {
        this.mContext = mContext;

        LtView = LayoutInflater.from(mContext).inflate(R.layout.float_lt_camera, null);

        mLlCamera = (LinearLayout) LtView.findViewById(R.id.ll_camera);
        mFlmenu = (MenuFrameLayout) LtView.findViewById(R.id.fl_menu);
        mLoadUi = LtView.findViewById(R.id.load_ui);
        mTvStatus = (TextView) LtView.findViewById(R.id.tv_camera_status);

    }

    public void setMenuFrameKeyListener(View.OnKeyListener listener) {
        mFlmenu.setOnKeyListenerByMenuItem(listener);
    }

    public void setMenuFrameClickListener(View.OnClickListener listener) {
        mFlmenu.setOnClickListenerByMenuItem(listener);
    }

    public void addCameraView(LTDevice LTDevice) {
        this.ltDevice = LTDevice;
        if (LTDevice == null)
            return;
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (ltDevice.getAView() != null) {
            mLlCamera.addView(LTDevice.getAView(), viewParams);
            mTvStatus.setText("");
        } else {
            mTvStatus.setText(mContext.getString(R.string.status_off_line));
        }
        int[] screenWH = Utils.getScreenWH(mContext);
        screenWH[0] = (int) (screenWH[0] / 2.6f);
        screenWH[1] = (int) (screenWH[1] / 2.6f);
        float scaleX;
        float scaleY;

        if (ltDevice.getGid().contains("zz")) {
            scaleX = ((float) screenWH[0]) / 640;
            scaleY = ((float) screenWH[1]) / 360;
        } else {

            scaleX = ((float) screenWH[0]) / 640;
            scaleY = ((float) screenWH[1]) / 480;
        }

        DLog.i("ScaleX ：" + scaleX + "ScaleY:" + scaleY);

        if (ltDevice.getVideoRenderer() != null)
            ((AViewRenderer) ltDevice.getVideoRenderer()).getMatrix().setScale(scaleX, scaleY, 0, 0);
    }


    public void removeCameraView() {
        if (ltDevice != null) {
            mLlCamera.removeView(ltDevice.getAView());
        }
    }


    public void refreshStatu(int status) {
        if (status == 0) {
            mLoadUi.setVisibility(View.GONE);
            mTvStatus.setText(mContext.getString(R.string.status_online));
        } else {
            mLoadUi.setVisibility(View.VISIBLE);
            mTvStatus.setText(mContext.getString(R.string.status_off_line));
        }


    }


    public View getLtView() {
        return LtView;
    }

    public MenuFrameLayout getmFlmenu() {
        return mFlmenu;
    }
}
