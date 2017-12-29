package com.ipcamerasen5.main.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipcamerasen5.main1.R;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;

public class GridViewHolder extends OpenPresenter.ViewHolder {
	
	public ImageView img;
	public ImageView imgStatus;
	public TextView tv;

	
	public GridViewHolder(View itemView) {
		super(itemView);
//		iv = (ImageView)itemView.findViewById(R.id.);
		img = (ImageView)itemView.findViewById(R.id.img_menu_horizontal_item);
		tv = (TextView)itemView.findViewById(R.id.textView);
		imgStatus = (ImageView)itemView.findViewById(R.id.img_menu_status);
	}

}
