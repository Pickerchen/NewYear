package com.ipcamerasen5.record.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.entity.VideoFileEntity;

import java.io.File;
import java.util.List;

/**
 * Created by chenqianghua on 2017/5/8.
 */

public class VideoFileAdapter extends ArrayObjectAdapter {

    private static Context mContext;
    private List<VideoFileEntity> mVideoFileEntities;
    private static Drawable defaultImage;
    private static int resourceId;//1:R.layout.item_video_files 2:R.layout.item_video_files2
    public VideoFileAdapter(int xmlID,Presenter presenter, Context context) {
        super(presenter);
        this.resourceId = xmlID;
        mContext = context;
        defaultImage = mContext.getDrawable(R.mipmap.bg_camera_default_allscreen);
    }

    public static class VideoFilePresenter extends Presenter{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View mView = null;
            if (resourceId == 1){
                 mView = LayoutInflater.from(mContext).inflate(R.layout.item_video_files,null);
            }
            else if (resourceId == 2){
                mView = LayoutInflater.from(mContext).inflate(R.layout.item_video_files_2,null);
            }
            ViewHolder mViewHolder = new ViewHolder(mView);
            mView.setFocusable(true);
            mView.setFocusableInTouchMode(true);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            VideoFileEntity mFileEntity = (VideoFileEntity) item;
            int length = mFileEntity.getThumpPath().split(File.separator).length;
            View itemView = viewHolder.view;
            ((TextView)itemView.findViewById(R.id.item_tv_device_name)).setText(mFileEntity.getDeviceName());
            int duration = Integer.parseInt(mFileEntity.getDuration());
            String duration_string = "";
            if (duration > 60000){
                int min = duration/60000;
                int duration_left = duration%60000;
                if (duration_left < 1000){
                    duration_string = min+"min";
                }
                else {
                    if (duration_left > 10000){
                        duration_string = min+"min"+duration_left/1000+"s";
                    }
                    else{
                        duration_string = min+"min"+"0"+duration_left/1000+"s";
                    }
                }
            }
            else {
                duration_string = duration/1000+"s";
            }
            ((TextView)itemView.findViewById(R.id.item_tv_time_duration)).setText(duration_string);
            ((TextView)itemView.findViewById(R.id.item_tv_time_content)).setText(mFileEntity.getDate());
            ImageView mImageView = (ImageView) itemView.findViewById(R.id.item_video_thumb);
            Glide.with(viewHolder.view.getContext())
                    .load(mFileEntity.getThumpPath())
                    .centerCrop()
                    .error(defaultImage)
                    .into(mImageView);
        }
        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
            View mView =  viewHolder.view;
        }
    }
}
