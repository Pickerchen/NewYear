package com.sen5.secure.launcher.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.base.BaseViewHolderAdapter;
import com.sen5.secure.launcher.widget.Sen5TextView;
import com.sen5.smartlifebox.data.entity.SceneData;

/**
 * Created by ZHOUDAO on 2017/5/27.
 */

public class SceneAdapter extends BaseViewHolderAdapter<SceneData> {



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scene_more, parent, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final SceneData sceneData = list.get(position);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).mTvScene.setText(sceneData.getScene_name());

        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private Sen5TextView mTvScene;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvScene = (Sen5TextView) itemView.findViewById(R.id.tv_scene);
        }
    }
}
