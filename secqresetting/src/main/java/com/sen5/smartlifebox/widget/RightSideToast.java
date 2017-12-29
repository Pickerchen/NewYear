package com.sen5.smartlifebox.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.common.utils.ReflectUtils;

public class RightSideToast implements Callback {

    private static final int TOAST = 0x00;
    private Context mContext = null;
    private String strToast = "";
    private Handler mHandler = new Handler(this);

    public RightSideToast(Context context, String strToast) {

        mContext = context;

        this.strToast = strToast;
        mHandler.sendEmptyMessage(TOAST);
    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        if (TOAST == msg.what) {
            View layout = LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null);
            TextView tvContent = (TextView) layout.findViewById(R.id.tv_toast_content);
//			int nViewWidth = mContext.getResources().getDisplayMetrics().widthPixels / 4;
//			int nViewHeight = mContext.getResources().getDisplayMetrics().heightPixels;
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(nViewWidth, nViewHeight);
//			tvContent.setLayoutParams(params);
            tvContent.setTextColor(mContext.getResources().getColor(R.color.text_white));
            tvContent.setText(strToast);

            Toast toast = new Toast(mContext);
            toast.setGravity(Gravity.RIGHT, 0, 0);
            toast.setMargin(0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            updateToast(mContext, toast);
            toast.show();
        }
        return false;
    }

    /**
     * 采用反射统一 Toast 动画和高宽
     * 使 Toast 动画不再因不同系统而产生差异
     */
    @SuppressLint("InlinedApi")
    private static void updateToast(Context context, Toast toast) {
        if (toast != null) {
            // Toast 动画
            try {
                Object mTN = ReflectUtils.getField(toast, "mTN");
                if (mTN != null) {
                    Object mParams = ReflectUtils.getField(mTN, "mParams");
                    if (mParams != null && mParams instanceof WindowManager.LayoutParams) {
                        WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
//						params.width = context.getResources().getDisplayMetrics().widthPixels / 4;
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.MATCH_PARENT;
//						int version = android.os.Build.VERSION.SDK_INT;
                        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                        params.windowAnimations = R.style.ToastAnim;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}