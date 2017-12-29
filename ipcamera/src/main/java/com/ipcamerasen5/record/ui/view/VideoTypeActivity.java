package com.ipcamerasen5.record.ui.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.ipcamerasen5.main.MainApplication;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.adapter.RecordItemDecoration;
import com.ipcamerasen5.record.adapter.VideoTypeAdapter;
import com.open.androidtvwidget.leanback.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;

import java.util.ArrayList;
import java.util.List;

public class VideoTypeActivity extends Activity {

    private RecyclerViewTV mRecyclerViewTV;
    private List<String> types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_type);
        types = new ArrayList<>();
        types.add(getString(R.string.tenSecondVideo));
        types.add(getString(R.string.reservation));
//        types.add(getString(R.string.usbdrive));
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.activityName = VideoTypeActivity.class.getSimpleName();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        mRecyclerViewTV = (RecyclerViewTV) findViewById(R.id.video_type_rv);
        final VideoTypeAdapter adapter = new VideoTypeAdapter(VideoTypeActivity.this,types);
        LinearLayoutManagerTV manager = new LinearLayoutManagerTV(VideoTypeActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewTV.setLayoutManager(manager);
        mRecyclerViewTV.setAdapter(adapter);
        mRecyclerViewTV.addItemDecoration(new RecordItemDecoration(120));
    }
}
