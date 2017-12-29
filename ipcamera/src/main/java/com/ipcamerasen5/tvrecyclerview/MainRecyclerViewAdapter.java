package com.ipcamerasen5.tvrecyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.common.GrideRoundTransform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hsl.p2pipcam.entity.EntityDevice;

/**
 * Created by chenqianghua on 517/7/7.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    public List<EntityDevice> datas = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private Drawable errorDrawable;

    public MainRecyclerViewAdapter(Context context, List<EntityDevice> datas) {
        mContext = context;
        this.datas = datas;
        errorDrawable =  mContext.getDrawable(R.mipmap.bg_camera_default);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_main,null);
        MyViewHolder mHolder = new MyViewHolder(mView);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final MyViewHolder mHolder = (MyViewHolder) holder;
        final EntityDevice data = datas.get(position);
        if (position == 0){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(330, 345);
            lp.leftMargin = 20;
            mHolder.mRelativeLayout.setLayoutParams(lp);
            mHolder.mRelativeLayout.setGravity(RelativeLayout.CENTER_VERTICAL);
        }
        //名字
        mHolder.tv_name.setText(data.DeviceName);
        //展示当期摄像头状态
        switch (data.status){
            case "0":
                mHolder.tv_recording.setVisibility(View.GONE);
                mHolder.tv_onLineStatus.setText(mContext.getString(R.string.status_off_line));
                break;
            case "1":
                mHolder.tv_recording.setVisibility(View.GONE);
                mHolder.tv_onLineStatus.setText(mContext.getString(R.string.status_online));
                break;
            case "2":
                mHolder.tv_recording.setVisibility(View.VISIBLE);
                mHolder.tv_onLineStatus.setText(mContext.getString(R.string.status_online));
                break;
        }
        Bitmap mBitmap = getFileJPG(data.videoPath);
        //根据position展示不同的默认图片
        switch (position){
            case 0:
                if (null != data.videoPath && !data.videoPath.equals("")){
//                    Glide.with(mContext)
//                            .load(data.videoPath)
//                            .transform(new GrideRoundTransform(mContext,5))
//                            .error(errorDrawable)
//                            .crossFade()
//                            .into(new GlideDrawableImageViewTarget(mHolder.iv){
//                                @Override
//                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                    super.onLoadFailed(e, errorDrawable);
//                                    String path = CommonTools.getPreference(Constant.lastImagePath);
//                                    if (null != path && !path.equals("")){
//                                        Glide.with(mContext).load(path).transform(new GrideRoundTransform(mContext,5))
//                                                .crossFade().into(mHolder.iv);
//                                    }
//                                }
//
//                                @Override
//                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                    super.onResourceReady(resource, animation);
//                                    mHolder.iv.setImageDrawable(resource);
//                                    //保存上次的jpgpath到内存中
//                                    CommonTools.savePreference( Constant.lastImagePath,data.videoPath);
//                                }
//                            });
//                }
//                else {
//                    Glide.with(mContext)
//                            .load(R.drawable.default_pic1)
//                            .transform(new GrideRoundTransform(mContext,5))
//                            .into(new GlideDrawableImageViewTarget(mHolder.iv){
//                                @Override
//                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                    super.onLoadFailed(e, errorDrawable);
//                                    String path = CommonTools.getPreference(Constant.lastImagePath);
//                                    if (null != path && !path.equals("")){
//                                        Glide.with(mContext).load(path).transform(new GrideRoundTransform(mContext,5))
//                                                .crossFade().into(mHolder.iv);
//                                    }
//                                }
//
//                                @Override
//                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                    super.onResourceReady(resource, animation);
//                                    mHolder.iv.setImageDrawable(resource);
//                                    //保存上次的jpgpath到内存中
//                                    CommonTools.savePreference( Constant.lastImagePath,data.videoPath);
//                                }
//                            });

                    if (null == mBitmap){
                        mHolder.iv.setImageResource(R.drawable.default_pic1);
                    }
                    else {
                        mHolder.iv.setImageBitmap(mBitmap);
                    }
                }

                break;
            case 1:
                if (null != data.videoPath && !data.videoPath.equals("")){

                    if (null == mBitmap){
                        mHolder.iv.setImageResource(R.drawable.default_pic2);
                    }
                    else {
                        mHolder.iv.setImageBitmap(mBitmap);
                    }

//                    Glide.with(mContext)
//                            .load(data.videoPath)
//                            .transform(new GrideRoundTransform(mContext,5))
//                            .error(errorDrawable)
//                            .crossFade()
//                            .into(new GlideDrawableImageViewTarget(mHolder.iv){
//                                @Override
//                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                    super.onLoadFailed(e, errorDrawable);
//                                    String path = CommonTools.getPreference(Constant.lastImagePath2);
//                                    if (null != path && !path.equals("")){
//                                        Glide.with(mContext).load(path).transform(new GrideRoundTransform(mContext,5))
//                                                .crossFade().into(mHolder.iv);
//                                    }
//                                }
//
//                                @Override
//                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                    super.onResourceReady(resource, animation);
//                                    mHolder.iv.setImageDrawable(resource);
//                                    //保存上次的jpgpath到内存中
//                                    CommonTools.savePreference(Constant.lastImagePath2,data.videoPath);
//                                }
//                            });
//                }
//                else {
//                    Glide.with(mContext)
//                            .load(R.drawable.default_pic2)
//                            .transform(new GrideRoundTransform(mContext,5))
//                            .into(mHolder.iv);
                }
                break;
            case 2:
                if (null != data.videoPath && !data.videoPath.equals("")){
//
//                    Glide.with(mContext)
//                            .load(data.videoPath)
//                            .transform(new CenterCrop(mContext),new GrideRoundTransform(mContext,5))
//                            .error(errorDrawable)
//                            .crossFade()
//                            .into(new GlideDrawableImageViewTarget(mHolder.iv){
//                                @Override
//                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                    super.onLoadFailed(e, errorDrawable);
//                                    String path = CommonTools.getPreference(Constant.lastImagePath3);
//                                    if (null != path && !path.equals("")){
//                                        Glide.with(mContext).load(path).transform(new GrideRoundTransform(mContext,5))
//                                                .crossFade().into(mHolder.iv);
//                                    }
//                                }
//
//                                @Override
//                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                    super.onResourceReady(resource, animation);
//                                    mHolder.iv.setImageDrawable(resource);
//                                    //保存上次的jpgpath到内存中
//                                    CommonTools.savePreference(Constant.lastImagePath3,data.videoPath);
//                                }
//                            });
                    if (null == mBitmap){
                        mHolder.iv.setImageResource(R.drawable.default_pic3);
                    }
                    else {
                        mHolder.iv.setImageBitmap(mBitmap);
                    }
                }
//                else {
//                    Glide.with(mContext)
//                            .load(R.drawable.default_pic3)
//                            .transform(new CenterCrop(mContext),new GrideRoundTransform(mContext,5))
//                            .into(mHolder.iv);
//                }
                break;
            case 3:
                if (null != data.videoPath && !data.videoPath.equals("")){
//                    Glide.with(mContext)
//                            .load(data.videoPath)
//                            .transform(new GrideRoundTransform(mContext,5))
//                            .error(errorDrawable)
//                            .crossFade()
//                            .into(new GlideDrawableImageViewTarget(mHolder.iv){
//                                @Override
//                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                    super.onLoadFailed(e, errorDrawable);
//                                    String path = CommonTools.getPreference(Constant.lastImagePath4);
//                                    if (null != path && !path.equals("")){
//                                        Glide.with(mContext).load(path).transform(new GrideRoundTransform(mContext,5))
//                                                .crossFade().into(mHolder.iv);
//                                    }
//                                }
//
//                                @Override
//                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                    super.onResourceReady(resource, animation);
//                                    mHolder.iv.setImageDrawable(resource);
//                                    //保存上次的jpgpath到内存中
//                                    CommonTools.savePreference( Constant.lastImagePath4,data.videoPath);
//                                }
//                            });
                    if (null == mBitmap){
                        mHolder.iv.setImageResource(R.drawable.default_pic4);
                    }
                    else {
                        mHolder.iv.setImageBitmap(mBitmap);
                    }
                }
//                else {
//                    Glide.with(mContext)
//                            .load(R.drawable.default_pic4)
//                            .transform(new GrideRoundTransform(mContext,5))
//                            .into(mHolder.iv);
//                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout mRelativeLayout;
        private ImageView iv;
        private ImageView iv_bg;
        private TextView tv_recording;
        private TextView tv_onLineStatus;
        private TextView tv_name;
        public MyViewHolder(final View itemView) {
            super(itemView);
            iv_bg = (ImageView) itemView.findViewById(R.id.item_recyclerView_iv_bg);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_rl);
            iv = (ImageView) itemView.findViewById(R.id.item_recyclerView_iv);
            tv_onLineStatus = (TextView) itemView.findViewById(R.id.item_recyclerView_tv_status);
            tv_recording = (TextView) itemView.findViewById(R.id.item_recyclerView_tv_recording);
            tv_name = (TextView) itemView.findViewById(R.id.item_recyclerView_tv_name);

            Glide.with(mContext)
                    .load(R.drawable.mask_camera_online)
                    .transform(new GrideRoundTransform(mContext,5))
                    .into(iv_bg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,getAdapterPosition());
                }
            });
            mRelativeLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        ViewCompat.animate(v).scaleX(1.17f).scaleY(1.17f).translationZ(1.2f).start();
                    }
                    else {
                        ViewCompat.animate(v).scaleX(1.0f).scaleY(1.0f).translationZ(1.0f).start();
                    }
                }
            });
        }
    }

    public interface  OnItemClickListener{
        void onItemClick(View view, int position);
    }

    /**
     * 获取文件bitmap
     * @param path
     * @return
     */
    public Bitmap getFileJPG(String path) {
        File mFile = new File(path);
        if (mFile.exists()) {
            Bitmap mBitmap = null;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(path);
                mBitmap = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                if (null != fis){
                    try {
                        fis.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            finally {
                if (null != fis){
                    try {
                        fis.close();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return mBitmap;
        }
        else {
            return null;
        }
        }
}
