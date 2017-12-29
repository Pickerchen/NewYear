package com.ipcamerasen5.record.ui.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.ipcamerasen5.main1.R;
import nes.ltlib.utils.LogUtils;
import com.ipcamerasen5.record.ui.presenter.ActivityFilesPresenter;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VideoFilesActivity extends Activity {

    private RecyclerViewTV mrecyclerView;
    private RecyclerViewTV mrecyclerView2;
    private RecyclerViewTV mrecyclerView3;
    private LinearLayout recyclerViewContainer;
    private ActivityFilesPresenter mActivityFilesPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_files);
        mActivityFilesPresenter = new ActivityFilesPresenter(VideoFilesActivity.this);
        inteView();
        EventBus.getDefault().register(this);
        mActivityFilesPresenter.initData();
        mActivityFilesPresenter.initFileRecyclerView(mrecyclerView);
    }

    private void inteView() {
        mrecyclerView = (RecyclerViewTV) findViewById(R.id.video_rv);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealWithEvent(Object o){
        LogUtils.e("dealWithEvent","is coming");
//        mActivityFilesPresenter.initFileRecyclerView(mrecyclerView,mrecyclerView2);
    }
}
