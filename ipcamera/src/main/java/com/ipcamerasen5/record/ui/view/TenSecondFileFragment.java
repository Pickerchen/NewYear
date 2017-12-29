package com.ipcamerasen5.record.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.adapter.VideoFileAdapter;
import nes.ltlib.utils.LogUtils;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.db.SecondPath;
import com.ipcamerasen5.record.entity.VideoFileEntity;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqianghua on 2017/5/8.
 */

public class TenSecondFileFragment extends BrowseFragment {
    private static final String TAG = "VideoFileFragment";

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private DisplayMetrics mMetrics;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);
        initData();

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setHeadersState(HEADERS_DISABLED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        VideoFileAdapter.VideoFilePresenter mPresenter = new VideoFileAdapter.VideoFilePresenter();

        int i;
        for (i = 0; i < deviceName.size(); i++) {
            VideoFileAdapter mVideoFileAdapter = new VideoFileAdapter(1,mPresenter,this.getActivity());
            for (int j = 0; j < videoFiles.size(); j++) {
                if (deviceName.get(i).equals(videoFiles.get(j).getDeviceName()) && deviceDid.get(i).equals(videoFiles.get(j).getDid())){
                    mVideoFileAdapter.add(videoFiles.get(j));
                }
            }
            HeaderItem header = new HeaderItem(i, deviceName.get(i));
            mRowsAdapter.add(new ListRow(header, mVideoFileAdapter));
        }
        setAdapter(mRowsAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                ListRow mListRow = (ListRow) row;
                LogUtils.e("onItemClicked","position is "+row.getId());
                VideoFileAdapter mFileAdapter = (VideoFileAdapter) mListRow.getAdapter();
                VideoFileEntity mFileEntity = (VideoFileEntity) item;
                LogUtils.e("onItemClicked",mFileEntity.getVideoFilePath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String bpath = "file://" + mFileEntity.getVideoFilePath();
                intent.setDataAndType(Uri.parse(bpath), "video/*");
                startActivity(intent);
            }
        });
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
//        setTitle(""); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_DISABLED);

        // set fastLane (or headers) background color
//        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.colorAccent));
    }


    private List<IpCamDevice> mIpCamDevices = new ArrayList<>();
    private List<SecondPath> mSecondPaths = new ArrayList<>();
    private List<VideoFileEntity> videoFiles = new ArrayList<>();
    private List<String> deviceName = new ArrayList<>();
    private List<String> deviceDid = new ArrayList<>();


    public void initData(){
//        mSensorPaths = DataSupport.findAll(SensorPath.class);
//        mClockPaths = DataSupport.findAll(ClockPath.class);
//        mRoundPaths = DataSupport.findAll(RoundPath.class);

        mSecondPaths = DataSupport.findAll(SecondPath.class);


        mIpCamDevices = DataSupport.findAll(IpCamDevice.class);
//        LogUtils.e("initData","size is "+mSensorPaths.size()+"mClockPaths size is "+mClockPaths.size());

        for (IpCamDevice mIpCamDevice : mIpCamDevices){
            deviceName.add(mIpCamDevice.getAliasName());
            deviceDid.add(mIpCamDevice.getDid());
        }

//        for (SensorPath mSensorPath : mSensorPaths){
//            for (IpCamDevice mIpCamDevice : mIpCamDevices){
//                if (mIpCamDevice.getSensorPath().equals(mSensorPath.getSensorPath())){
//                    VideoFileEntity mFileEntity = new VideoFileEntity();
//                    mFileEntity.setDate(mSensorPath.getTime());
//                    mFileEntity.setDeviceName(mIpCamDevice.getAliasName());
//                    mFileEntity.setDuration(mSensorPath.getDuration());
//                    mFileEntity.setThumpPath(mIpCamDevice.getThumpPath()+ File.separator+mSensorPath.getLastThumpPath());
//                    videoFiles.add(mFileEntity);
//                }
//            }
//        }
//
//        for (ClockPath mClockPath : mClockPaths){
//            for (IpCamDevice mIpCamDevice : mIpCamDevices){
//                if (mIpCamDevice.getClockPath().equals(mClockPath.getClockPath())){
//                    VideoFileEntity mFileEntity = new VideoFileEntity();
//                    mFileEntity.setDate(mClockPath.getTime());
//                    mFileEntity.setDeviceName(mIpCamDevice.getAliasName());
//                    mFileEntity.setDuration(mClockPath.getDuration());
//                    mFileEntity.setThumpPath(mIpCamDevice.getThumpPath()+ File.separator+mClockPath.getLastThumpPath());
//                    videoFiles.add(mFileEntity);
//                }
//            }
//        }

        for (SecondPath mSecondPath : mSecondPaths){
            for (IpCamDevice mIpCamDevice : mIpCamDevices){
                if (mIpCamDevice.getSecondPath().equals(mSecondPath.getSecondPath())){
                    VideoFileEntity mFileEntity = new VideoFileEntity();
                    mFileEntity.setVideoFilePath(mSecondPath.getSecondPath()+File.separator+mSecondPath.getFileName());
                    mFileEntity.setDate(mSecondPath.getTime());
                    mFileEntity.setDeviceName(mIpCamDevice.getAliasName());
                    mFileEntity.setDuration(mSecondPath.getDuration());
                    mFileEntity.setThumpPath(mIpCamDevice.getThumpPath()+ File.separator+mSecondPath.getLastThumpPath());
                    mFileEntity.setDid(mIpCamDevice.getDid());
                    videoFiles.add(mFileEntity);
                }
            }
        }
        LogUtils.e("initdata","videoFiles.size is "+videoFiles.size());
    }
}
