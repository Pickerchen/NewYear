package com.ipcamerasen5.record.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.ui.view.TenSecondVidoFileActivity;
import com.ipcamerasen5.record.ui.view.VideoFileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqianghua on 2017/4/25.
 */

public class VideoTypeAdapter extends RecyclerView.Adapter<VideoTypeAdapter.VideoTypeViewHolder> {

    private Context mContext;
    private List<String> types = new ArrayList<>();

    public VideoTypeAdapter(Context context,List<String> type) {
        mContext = context;
        this.types = type;
    }

    @Override
    public VideoTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_video_type,null);
        VideoTypeViewHolder mVideoTypeViewHolder = new VideoTypeViewHolder(mView);
        return mVideoTypeViewHolder;
    }

    @Override
    public void onBindViewHolder(VideoTypeViewHolder holder, int position) {
        switch (position){
            case 0:
                holder.mImageView.setImageResource(R.drawable.selector_item_video_type_10);
                holder.mTextView.setText(types.get(position));
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, TenSecondVidoFileActivity.class);
                        intent.putExtra("type","0");
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 1:
                holder.mImageView.setImageResource(R.drawable.selector_item_video_type_reservation);
                holder.mTextView.setText(types.get(position));
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, VideoFileActivity.class);
                        intent.putExtra("type","1");
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 2:
                holder.mImageView.setImageResource(R.drawable.selector_item_video_type_usb);
                holder.mTextView.setText(types.get(position));
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, VideoFileActivity.class);
                        intent.putExtra("type","2");
                        mContext.startActivity(intent);
                    }
                });
                break;
            default:
                holder.mImageView.setImageResource(R.drawable.selector_item_video_type_usb);
                holder.mTextView.setText(types.get(position));
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, VideoFileActivity.class);
                        intent.putExtra("type","2");
                        mContext.startActivity(intent);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class VideoTypeViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        ImageView mImageView;

        public VideoTypeViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.item_video_tv);
            mImageView = (ImageView) itemView.findViewById(R.id.item_video_type_iv);
            mImageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
