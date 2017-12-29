package com.sen5.secure.launcher.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.base.BaseViewHolderAdapter;
import com.sen5.secure.launcher.data.entity.DeviceBean;
import com.sen5.secure.launcher.data.interf.ItemOnKeyListener;
import com.sen5.secure.launcher.widget.Sen5TextView;

/**
 * Created by ZHOUDAO on 2017/5/23.
 */

public class MoreDevPresenter  extends BaseViewHolderAdapter<DeviceBean> {


    private Context mContext;

    public MoreDevPresenter(Context context) {
        this.mContext = context;
    }



    private boolean isExtend = false;

    private static final int TYPE_EXTEND = 0;
    private static final int TYPE_MORE = 1;


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_EXTEND;
        }

        return TYPE_MORE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_EXTEND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_more_extend, parent, false);
            return new ViewExtendHolder(view);
        } else if (viewType == TYPE_MORE) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_more, parent, false);
            return new ViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        int type = getItemViewType(position);

        if (type == TYPE_EXTEND) {

            if (viewHolder instanceof ViewExtendHolder) {


                if (!isExtend) {
                    ((ViewExtendHolder) viewHolder).mTvExtend.setText(mContext.getString(R.string.show_more));
                } else {
                    ((ViewExtendHolder) viewHolder).mTvExtend.setText(mContext.getString(R.string.show_less));
                }


            }


        } else if (type == TYPE_MORE) {


            if (isExtend) {
                viewHolder.itemView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.itemView.setVisibility(View.GONE);

            }


            DeviceBean data = list.get(position);
            viewHolder.itemView.setTag(data);
            if (viewHolder instanceof ViewHolder) {
                ((ViewHolder) viewHolder).mIvDev.setImageResource(data.getDevImg());
                ((ViewHolder) viewHolder).mTvDevName.setText(data.getDevName());
            }

            viewHolder.itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (keyListener != null) {
                        return keyListener.onKey(v, keyCode, event, position);
                    }

                    return false;
                }
            });

        }
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvDev;
        private Sen5TextView mTvDevName;

        public ViewHolder(View view) {
            super(view);
            mIvDev = (ImageView) view.findViewById(R.id.iv_dev);
            mTvDevName = (Sen5TextView) view.findViewById(R.id.tv_name);
        }
    }

    private class ViewExtendHolder extends RecyclerView.ViewHolder {

        private Sen5TextView mTvExtend;

        public ViewExtendHolder(View view) {
            super(view);

            mTvExtend = (Sen5TextView) view.findViewById(R.id.tv_name);
        }
    }


    public void setKeyListener(ItemOnKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    private ItemOnKeyListener keyListener;


    public void showExpend(boolean flag) {
        isExtend = flag;
        notifyDataSetChanged();
    }

    public boolean isExpend(){
        return isExtend;
    }

}
