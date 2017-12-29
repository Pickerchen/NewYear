package com.ipcamerasen5.record.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ipcamerasen5.main1.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择存储路径的弹出框
 * Created by chenqianghua on 2017/5/22.
 */

public class DialogChooseStorageAdapter extends BaseAdapter{


    private List<String> paths = new ArrayList<>();
    private Context mContext;

    public DialogChooseStorageAdapter(List<String> paths, Context context) {
        this.paths = paths;
        mContext = context;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_storage,null);
        TextView mTextView = (TextView) convertView.findViewById(R.id.choose_storage_tv);
        mTextView.setText(paths.get(position));
        return convertView;
    }
}
