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
import com.ipcamerasen5.record.db.ClockPath;
import com.ipcamerasen5.record.db.DynamicPath;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.db.RoundPath;
import com.ipcamerasen5.record.db.SensorPath;
import com.ipcamerasen5.record.entity.VideoFileEntity;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nes.ltlib.utils.LogUtils;

/**
 * Created by chenqianghua on 2017/5/8.
 */

public class VideoFileFragment extends BrowseFragment {
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
            VideoFileAdapter mVideoFileAdapter1 = new VideoFileAdapter(1,mPresenter,this.getActivity());
            VideoFileAdapter mVideoFileAdapter2 = new VideoFileAdapter(2,mPresenter,this.getActivity());
            int device_record_file_count = 0;
            for (int j = 0; j < videoFiles.size(); j++) {
                if (deviceName.get(i).equals(videoFiles.get(j).getDeviceName()) && deviceDid.get(i).equals(videoFiles.get(j).getDid())){
                    device_record_file_count+=1;
                    if (device_record_file_count <= 8){
                        LogUtils.e("count <= 8","count is "+device_record_file_count);
                        mVideoFileAdapter1.add(videoFiles.get(j));
                    }
                    else if (device_record_file_count > 8){
                        LogUtils.e("count > 8","count is "+device_record_file_count);
                        if (device_record_file_count%2 == 0){
                            mVideoFileAdapter1.add(videoFiles.get(j));
                        }
                        else {
                            mVideoFileAdapter2.add(videoFiles.get(j));
                        }
                    }
                }
            }
            HeaderItem header = new HeaderItem(i, deviceName.get(i));
            if (null != mVideoFileAdapter1){
                mRowsAdapter.add(new ListRow(header, mVideoFileAdapter1));
            }
            if (null != mVideoFileAdapter2 && device_record_file_count > 8){
                mRowsAdapter.add(new ListRow(new HeaderItem(i,""),mVideoFileAdapter2));
            }
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
    private List<SensorPath> mSensorPaths = new ArrayList<>();
    private List<ClockPath> mClockPaths = new ArrayList<>();
    private List<RoundPath> mRoundPaths = new ArrayList<>();
    private List<DynamicPath> mDynamicPaths = new ArrayList<>();
    private List<VideoFileEntity> videoFiles = new ArrayList<>();
    private List<String> deviceName = new ArrayList<>();
    private List<String> deviceDid = new ArrayList<>();


    public void initData(){
        //倒序
        mSensorPaths = DataSupport.where("duration > ?","0").order("id desc").find(SensorPath.class);
        mClockPaths = DataSupport.where("duration > ?","0").order("id desc").find(ClockPath.class);
        mDynamicPaths = DataSupport.where("duration > ?","0").order("id desc").find(DynamicPath.class);
        mRoundPaths = DataSupport.where("duration > ?","0").order("id desc").find(RoundPath.class);
//        mSensorPaths = DataSupport.findAll(SensorPath.class);
//        mClockPaths = DataSupport.findAll(ClockPath.class);
//        mRoundPaths = DataSupport.findAll(RoundPath.class);
//        mDynamicPaths = DataSupport.findAll(DynamicPath.class);

        mIpCamDevices = DataSupport.findAll(IpCamDevice.class);
        LogUtils.e("initData","size is "+mSensorPaths.size()+"mClockPaths size is "+mClockPaths.size());

        for (IpCamDevice mIpCamDevice : mIpCamDevices){
            deviceName.add(mIpCamDevice.getAliasName());
            deviceDid.add(mIpCamDevice.getDid());
        }
        for (SensorPath mSensorPath : mSensorPaths){
            if (null != mSensorPath.getSensorPath()){
            for (IpCamDevice mIpCamDevice : mIpCamDevices){
                if (mIpCamDevice.getSensorPath().equals(mSensorPath.getSensorPath())) {
                    File thisFile = new File(mSensorPath.getSensorPath() + File.separator + mSensorPath.getFileName());
                    if (thisFile.exists()) {
                        VideoFileEntity mFileEntity = new VideoFileEntity();
                        mFileEntity.setVideoFilePath(mSensorPath.getSensorPath() + File.separator + mSensorPath.getFileName());
                        mFileEntity.setDate(mSensorPath.getTime());
                        mFileEntity.setDeviceName(mIpCamDevice.getAliasName());
                        mFileEntity.setDuration(mSensorPath.getDuration());
                        mFileEntity.setThumpPath(mIpCamDevice.getThumpPath() + File.separator + mSensorPath.getLastThumpPath());
                        mFileEntity.setDid(mIpCamDevice.getDid());
                        videoFiles.add(mFileEntity);
                    }
                }
                }
            }
        }

        for (ClockPath mClockPath : mClockPaths){
            for (IpCamDevice mIpCamDevice : mIpCamDevices){
                if (null != mIpCamDevice.getClockPath()){
                if (mIpCamDevice.getClockPath().equals(mClockPath.getClockPath())) {
                    File thisFile = new File(mClockPath.getClockPath() + File.separator + mClockPath.getFileName());
                    if (thisFile.exists()) {
                        VideoFileEntity mFileEntity = new VideoFileEntity();
                        mFileEntity.setVideoFilePath(mClockPath.getClockPath() + File.separator + mClockPath.getFileName());
                        mFileEntity.setDate(mClockPath.getTime());
                        mFileEntity.setDeviceName(mIpCamDevice.getAliasName());
                        mFileEntity.setDuration(mClockPath.getDuration());
                        mFileEntity.setThumpPath(mIpCamDevice.getThumpPath() + File.separator + mClockPath.getLastThumpPath());
                        mFileEntity.setDid(mIpCamDevice.getDid());
                        videoFiles.add(mFileEntity);
                    }
                }
                }
            }
        }

        for (RoundPath mRoundPath : mRoundPaths){
            for (IpCamDevice mIpCamDevice : mIpCamDevices){
                if (null != mIpCamDevice.getRoundPath()){
                if (mIpCamDevice.getRoundPath().equals(mRoundPath.getRoundPath())){
                    File thisFile = new File(mRoundPath.getRoundPath()+File.separator+mRoundPath.getFileName());
                    if (thisFile.exists()) {
                        VideoFileEntity mFileEntity = new VideoFileEntity();
                        mFileEntity.setVideoFilePath(mRoundPath.getRoundPath() + File.separator + mRoundPath.getFileName());
                        mFileEntity.setDate(mRoundPath.getTime());
                        mFileEntity.setDeviceName(mIpCamDevice.getAliasName());
                        mFileEntity.setDuration(mRoundPath.getDuration());
                        mFileEntity.setThumpPath(mIpCamDevice.getThumpPath() + File.separator + mRoundPath.getLastThumpPath());
                        mFileEntity.setDid(mIpCamDevice.getDid());
                        videoFiles.add(mFileEntity);
                    }
                }
                }
            }
        }

        for (DynamicPath mDynamicPath : mDynamicPaths){
            for (IpCamDevice mIpCamDevice : mIpCamDevices){
                if (null != mIpCamDevice.getDynamicPath()) {
                    if (mIpCamDevice.getDynamicPath().equals(mDynamicPath.getClockPath())) {
                        File thisFile = new File(mDynamicPath.getClockPath() + File.separator + mDynamicPath.getFileName());
                        if (thisFile.exists()) {
                            VideoFileEntity mFileEntity = new VideoFileEntity();
                            mFileEntity.setVideoFilePath(mDynamicPath.getClockPath() + File.separator + mDynamicPath.getFileName());
                            mFileEntity.setDate(mDynamicPath.getTime());
                            mFileEntity.setDeviceName(mIpCamDevice.getAliasName());
                            mFileEntity.setDuration(mDynamicPath.getDuration());
                            mFileEntity.setThumpPath(mIpCamDevice.getThumpPath() + File.separator + mDynamicPath.getLastThumpPath());
                            mFileEntity.setDid(mIpCamDevice.getDid());
                            videoFiles.add(mFileEntity);
                        }
                    }
                }
            }
        }
        LogUtils.e("initdata","videoFiles.size is "+videoFiles.size());
    }
}
