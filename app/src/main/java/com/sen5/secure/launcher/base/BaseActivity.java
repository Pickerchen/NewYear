package com.sen5.secure.launcher.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.eric.xlee.lib.utils.LogUtil;
import com.sen5.smartlifebox.SecQreSettingApplication;
import com.squareup.leakcanary.RefWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import nes.ltlib.utils.AppLog;

/**
 * Created by zhoudao on 2017/5/18.
 */


public abstract class BaseActivity extends Activity {
    private String TAG = BaseActivity.class.getSimpleName();
    private static int num = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(getLayoutId());

        AppLog.e(TAG + "启动次数" + num);
        num++;
        EventBus.getDefault().register(this);
        initView();
        initControl();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        RefWatcher refWatcher = SecQreSettingApplication.getRefWatcher();
        refWatcher.watch(this);

    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initControl();

    public abstract void initData();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object o) {

    }

}
