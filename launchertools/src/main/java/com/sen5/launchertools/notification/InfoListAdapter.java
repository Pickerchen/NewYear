package com.sen5.launchertools.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sen5.launchertools.R;

import java.util.List;

/**
 * @author JesseYao 
 * @version 2016 2016年12月13日 下午8:18:08
 * ClassName：InfoListAdapter.java 
 * Description：
*/
public class InfoListAdapter extends BaseAdapter {

    private List<NTBean> nInfoList;
    private LayoutInflater nInflater;
    private Context nContext;

    public InfoListAdapter(Context cxt, List<NTBean> source) {
        nInfoList = source;
        nInflater = LayoutInflater.from(cxt);
        nContext = cxt;
    }

    @Override
    public int getCount() {
        return nInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return nInfoList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationPopupWindow.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = nInflater.inflate(R.layout.item_notification, parent, false);
            viewHolder = new NotificationPopupWindow.ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.notiTitel = (TextView) convertView.findViewById(R.id.noti_title);
            viewHolder.notiText = (TextView) convertView.findViewById(R.id.noti_text);
            viewHolder.notiSubText = (TextView) convertView.findViewById(R.id.noti_sub_text);
            viewHolder.smallIcon = (ImageView) convertView.findViewById(R.id.noti_small_icon);
            viewHolder.largeIcon = (ImageView) convertView.findViewById(R.id.noti_large_icon);
            viewHolder.contentView = (ViewGroup) convertView.findViewById(R.id.noti_content_view);
            viewHolder.bigContentView = (ViewGroup) convertView.findViewById(R.id.noti_big_content_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NotificationPopupWindow.ViewHolder) convertView.getTag();
        }
        NTBean bean = nInfoList.get(position);
        viewHolder.title.setText(bean.info);
        viewHolder.notiTitel.setText(bean.title);
        viewHolder.notiSubText.setText(bean.subText);
        viewHolder.notiText.setText(bean.text);
        viewHolder.largeIcon.setImageBitmap(bean.largeIcon);
        //viewHolder.smallIcon.setImageIcon(bean.smallIcon);//high api level
        viewHolder.contentView.removeAllViews();
        viewHolder.bigContentView.removeAllViews();

        if (bean.viewS != null) {
            View view = bean.viewS.apply(nContext, viewHolder.contentView);
            viewHolder.contentView.addView(view);
        }
        if (bean.viewL != null) {
            View view = bean.viewL.apply(nContext, viewHolder.bigContentView);
            viewHolder.bigContentView.addView(view);
        }
        return convertView;
    }
    
    public void notifyDataSetChanged(View view) {
    	// TODO Auto-generated method stub
    	this.notifyDataSetChanged();
    	if (nInfoList.size() == 0) {
    		view.setVisibility(View.VISIBLE);
    	} else {
    		view.setVisibility(View.GONE);
    	}
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
