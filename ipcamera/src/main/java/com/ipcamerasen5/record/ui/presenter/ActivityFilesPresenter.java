package com.ipcamerasen5.record.ui.presenter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.adapter.RecordItemDecoration;
import com.ipcamerasen5.record.adapter.VideoFilesAdapter;
import nes.ltlib.utils.LogUtils;
import com.ipcamerasen5.record.db.ClockPath;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.db.SensorPath;
import com.ipcamerasen5.record.event.IpCamStatusEvent;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenqianghua on 2017/4/25.
 */

public class ActivityFilesPresenter {

    private List<IpCamDevice> mIpCamDevices = new ArrayList<>();
    private List<SensorPath> mSensorPaths = new ArrayList<>();
    private List<ClockPath> mClockPaths = new ArrayList<>();
    private List<String> fileNames = new ArrayList<>();
    private List<Map<String,String>> nameAndPath = new ArrayList<>();//名称和存储路径映射
    private List<Map<String,String>> adapterData = new ArrayList<>();
    private Context mContext;

    public ActivityFilesPresenter(Context context) {
        mContext = context;
        EventBus.getDefault().register(this);
    }

    public void initData(){
                mSensorPaths = DataSupport.findAll(SensorPath.class);
                mClockPaths = DataSupport.findAll(ClockPath.class);
                mIpCamDevices = DataSupport.findAll(IpCamDevice.class);
                LogUtils.e("initData","size is "+mSensorPaths.size()+"mClockPaths size is "+mClockPaths.size());
                for (SensorPath mSensorPath : mSensorPaths){
                    for (IpCamDevice mIpCamDevice : mIpCamDevices){
                        if (mIpCamDevice.getSensorPath().equals(mSensorPath.getSensorPath())){
                            Map<String,String> mStringMap = new HashMap<>();
                            mStringMap.put("name",mIpCamDevice.getAliasName());
                            mStringMap.put("fileName",mSensorPath.getFileName());
                            mStringMap.put("path",mSensorPath.getSensorPath());
                            nameAndPath.add(mStringMap);
                        }
                    }
                }

        for (ClockPath mClockPath : mClockPaths){
            for (IpCamDevice mIpCamDevice : mIpCamDevices){
                if (mIpCamDevice.getClockPath().equals(mClockPath.getClockPath())){
                    Map<String,String> mStringMap = new HashMap<>();
                    mStringMap.put("name",mIpCamDevice.getAliasName());
                    mStringMap.put("fileName",mClockPath.getFileName());
                    mStringMap.put("path",mClockPath.getClockPath());
                    nameAndPath.add(mStringMap);
                }
            }
        }

    }

    int count = 5;
    @TargetApi(Build.VERSION_CODES.M)
    public void initFileRecyclerView(final RecyclerViewTV... recyclerViewTV){

                final MynestAdapter mAdapter = new MynestAdapter(count);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewTV[0].setLayoutManager(mLayoutManager);
        recyclerViewTV[0].setAdapter(mAdapter);
        recyclerViewTV[0].addItemDecoration(new RecordItemDecoration(20));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverEvent(IpCamStatusEvent event){

    }


    public static boolean isVisBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        LogUtils.e("onkey","最后可见position is"+lastVisibleItemPosition+"当前看到个数是："+visibleItemCount+"当前所有子项 "+totalItemCount);
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1){
            return true;
        }else {
            return false;
        }
    }


   class MynestAdapter extends RecyclerView.Adapter<MynestAdapter.RecyclerViewHolder>{

       int count;
       public MynestAdapter(int num) {
           count = num;
       }


       @Override
       public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View mView = LayoutInflater.from(mContext).inflate(R.layout.item_video_recyclerview,null);
           RecyclerViewHolder mViewHolder = new RecyclerViewHolder(mView);
           return mViewHolder;
       }

       @Override
       public void onBindViewHolder(RecyclerViewHolder holder, int position) {
           final VideoFilesAdapter mAdapter = new VideoFilesAdapter(mContext,nameAndPath);
           LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
           mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
           holder.mRecyclerViewTV.setLayoutManager(mLayoutManager);
           holder.mRecyclerViewTV.setAdapter(mAdapter);

       }
       @Override
       public int getItemCount() {
           return count;
       }

       class  RecyclerViewHolder extends RecyclerView.ViewHolder{

           RecyclerViewTV  mRecyclerViewTV;

           public RecyclerViewHolder(View itemView) {
               super(itemView);
               mRecyclerViewTV = (RecyclerViewTV) itemView.findViewById(R.id.item_recyclerView);
           }
       }
   }
}
