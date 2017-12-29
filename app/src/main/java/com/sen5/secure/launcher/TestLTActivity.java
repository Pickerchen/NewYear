package com.sen5.secure.launcher;


import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sen5.secure.launcher.base.BaseActivity;
import com.sen5.secure.launcher.data.database.CameraProviderControl;

import java.util.List;

import nes.ltlib.LTSDKManager;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.data.LTDevice;

public class TestLTActivity extends BaseActivity {

    public static void launch(Context mContext) {
        Intent intent = new Intent(mContext, TestLTActivity.class);
        mContext.startActivity(intent);
    }

    private LinearLayout mLayout;
    private List<LTDevice> list;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test_lt;
    }

    @Override
    public void initView() {

        mLayout = (LinearLayout) findViewById(R.id.ll_root);

    }

    @Override
    public void initControl() {

    }

    @Override
    public void initData() {

        list = LTSDKManager.getmLTDevices();

        addView();
    }

    private void addView() {
        int n = 0;
        for (LTDevice entity : list) {
            TextView mTextView = new TextView(this);
            mTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mTextView.setText("name:" + n++ + "---------" + "did:" + entity.getGid());
            mLayout.addView(mTextView);
        }

    }
}
