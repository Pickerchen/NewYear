package com.sen5.secure.launcher.ui.adapter;

import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;
import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.data.interf.ItemOnKeyListener;
import com.sen5.secure.launcher.widget.Sen5TextView;
import com.sen5.secure.launcher.workspace.AppInfo;

import java.util.List;

/**
 * Created by zhoudao on 2017/5/18.
 */

public class AppRecycleViewPresenter extends OpenPresenter {

    private List<AppInfo> labels;
    private GeneralAdapter mAdapter;

    public AppRecycleViewPresenter(List<AppInfo> labels) {
        this.labels = labels;
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }

    /**
     * 用于数据加载更多测试.
     */
    public void notifyDataSetChanged() {
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OpenPresenter.ViewHolder viewHolder, final int position) {
        AppInfo appInfo = labels.get(position);
        viewHolder.view.setTag(appInfo);
        ((ViewHolder) viewHolder).mTv.setText(appInfo.title);
        ((ViewHolder) viewHolder).mIv.setImageBitmap(appInfo.iconBitmap);
        viewHolder.view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyListener!=null){
                    return  keyListener.onKey(v,keyCode,event,position);
                }
                return false;
            }
        });

    }

    private class ViewHolder extends OpenPresenter.ViewHolder {

        public Sen5TextView mTv;
        public ImageView mIv;

        public ViewHolder(View view) {
            super(view);
            mTv = (Sen5TextView) view.findViewById(R.id.tv_app);
            mIv = (ImageView) view.findViewById(R.id.iv_app);
        }
    }

    public void setKeyListener(ItemOnKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    private ItemOnKeyListener keyListener;
}
