package com.ipcamerasen5.tvrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipcamerasen5.main1.R;


public class MaulCarouselAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private OnItemStateListener mListener;

    public MaulCarouselAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemStateListener(OnItemStateListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(View.inflate(mContext, R.layout.item_recyclerview_maul, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
        viewHolder.mName.setText(position+"");
    }

    @Override
    public int getItemCount() {
        return  6;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FocusRelativeLayout mRelativeLayout;
        TextView mName;
        ImageView mImageView;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_item_tip);
            mRelativeLayout = (FocusRelativeLayout) itemView.findViewById(R.id.fl_main_layout);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_item);
            mRelativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemStateListener {
        void onItemClick(View view, int position);
    }
}
