package com.ipcamerasen5.record.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipcamerasen5.main1.R;
import nes.ltlib.utils.LogUtils;

/**
 * 闹钟设置界面设置闹钟循环
 * Created by chenqianghua on 2017/4/24.
 */

public class RecordClockRecycleAdapte extends BaseAdapter {

    private ChooseCallback mChooseCallback;
    private String[] days;
    private Context mContext;

    public RecordClockRecycleAdapte(ChooseCallback chooseCallback, Context context) {
        mChooseCallback = chooseCallback;
        mContext = context;
        days = mContext.getResources().getStringArray(R.array.record_recycler_days);
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int position) {
        return days[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_record_recycle,null);
        final CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.item_record_recycler_cb);
        LinearLayout item_record_recycler_ll = (LinearLayout) convertView.findViewById(R.id.item_record_recycler_ll);
        TextView mTextView = (TextView) convertView.findViewById(R.id.item_record_recycler_tv);
        mTextView.setText(days[position]);
        item_record_recycler_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("onClick","position is "+position);
                if (mCheckBox.isChecked()){
                    mCheckBox.setChecked(false);
                }
                else {
                    mCheckBox.setChecked(true);
                }
            }
        });
        return convertView;
    }

   public  interface ChooseCallback{
        void  chooseDayCallback();
    }
}
