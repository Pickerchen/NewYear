package com.ipcamerasen5.record.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.common.CommonTools;
import nes.ltlib.utils.LogUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 视频文件按时间显示
 * Created by chenqianghua on 2017/4/11.
 */

public class VideoFilesAdapter extends RecyclerView.Adapter<VideoFilesAdapter.VideoFilesViewHolder>{

    private Context mContext;
    private List<Map<String,String>> fileVideos;
    public final static int item_1 = 1;
    public final static int item_2 = 2;
    public final static int item_3 = 3;
    public final static int item_4 = 4;
    public final static int item_5 = 5;
    public final static int item_6 = 6;
    public final static int item_7 = 7;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:

                    break;
            }
        }
    };

    public VideoFilesAdapter(Context context, List<Map<String,String>> clockVideos) {
        this.mContext = context;
        this.fileVideos = clockVideos;
    }

    @Override
    public VideoFilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_video_files,null);
        VideoFilesViewHolder mViewHolder = new VideoFilesViewHolder(mView);
        return mViewHolder;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(VideoFilesViewHolder holder, final int position) {
        LogUtils.e("VideoFilesAdapter","position is "+position);
        if (position == 0){
            holder.tv_title_time.setTextColor(mContext.getColor(R.color.tv_white));
        }
        else {
            holder.tv_title_time.setTextColor(mContext.getColor(R.color.transparent));
        }
        holder.tv_name.setText(fileVideos.get(position).get("name"));
        final String filePath = fileVideos.get(position).get("path")+ File.separator+fileVideos.get(position).get("fileName");
        LogUtils.e("VideoFilesAdapter","filePath is " + filePath);
        holder.item_video_files_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("VideoFilesAdapter","position is "+position);
                File mFile = new File(filePath);
                mFile.delete();
                fileVideos.remove(position);
            }
        });
        holder.mImageView.setImageBitmap(CommonTools.getVideoThumbnail(filePath, mHandler));
    }

    @Override
    public int getItemCount() {
        return fileVideos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class VideoFilesViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title_time;
        TextView tv_name;
        TextView tv_time_below;
        ImageView mImageView;
        LinearLayout mLinearLayout_content;
        RelativeLayout item_video_files_rl;
        public VideoFilesViewHolder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.item_video_thumb);
            tv_name = (TextView) itemView.findViewById(R.id.item_tv_device_name);
            tv_time_below = (TextView) itemView.findViewById(R.id.item_tv_time_content);
            tv_title_time = (TextView) itemView.findViewById(R.id.item_tv_time_title);
            mLinearLayout_content = (LinearLayout) itemView.findViewById(R.id.item_video_ll_content);
            item_video_files_rl = (RelativeLayout) itemView.findViewById(R.id.item_video_files_rl);
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        ViewCompat.animate(v).scaleX(1.17f).scaleY(1.17f).translationZ(1.2f).start();
                    }
                    else {
                        ViewCompat.animate(v).scaleX(1f).scaleY(1f).translationZ(1f).start();
                    }
                }

            });
        }
    }
}
