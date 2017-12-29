package com.ipcamerasen5.main.utils;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipcamerasen5.main1.R;

public class UtilsToast {
	private static Toast mToast;
	private static View view;
	private static TextView textView;
	private static LinearLayout mLinearLayout;

	public static Toast makeToast(Context context, String str, int drawableId) {
		if (mToast == null) {
			mToast = new Toast(context);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.leftMargin = 80;
			lp.rightMargin = 80;
			lp.topMargin = 20;
			lp.bottomMargin = 20;
			lp.gravity = Gravity.CENTER;
			
			mLinearLayout = new LinearLayout(context);
			textView = new TextView(context);
			textView.setTextSize(20);
			textView.setGravity(Gravity.CENTER);
			textView.setLayoutParams(lp);
			textView.setTextColor(Color.WHITE);
			
			mLinearLayout.setBackgroundResource(drawableId);
			mLinearLayout.addView(textView);
			
			mToast.setView(mLinearLayout);
			
		}
		textView.setText(str);
		mToast.setGravity(Gravity.BOTTOM, 0, 100);
		mToast.setDuration(Toast.LENGTH_SHORT);
		return mToast;
	}
	
	public static Toast makeToast(Context context, String str) {
		if (mToast == null) {
			mToast = new Toast(context);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.leftMargin = 80;
			lp.rightMargin = 80;
			lp.topMargin = 20;
			lp.bottomMargin = 20;
			lp.gravity = Gravity.CENTER;
			
			mLinearLayout = new LinearLayout(context);
			textView = new TextView(context);
			textView.setTextSize(20);
			textView.setGravity(Gravity.CENTER);
			textView.setLayoutParams(lp);
			textView.setTextColor(Color.WHITE);
			
			mLinearLayout.setBackgroundResource(R.drawable.bg_my_toast);
			mLinearLayout.addView(textView);
			
			mToast.setView(mLinearLayout);

		}
		textView.setText(str);
		mToast.setGravity(Gravity.BOTTOM, 0, 100);
		mToast.setDuration(Toast.LENGTH_SHORT);
		return mToast;
	}

	public static Toast makeToast(Context context, int id) {
		if (mToast == null) {
			mToast = new Toast(context);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.leftMargin = 80;
			lp.rightMargin = 80;
			lp.topMargin = 20;
			lp.bottomMargin = 20;
			lp.gravity = Gravity.CENTER;
			
			mLinearLayout = new LinearLayout(context);
			textView = new TextView(context);
			textView.setTextSize(20);
			textView.setGravity(Gravity.CENTER);
			textView.setLayoutParams(lp);
			textView.setTextColor(Color.WHITE);
			mLinearLayout.setBackgroundResource(R.drawable.bg_my_toast);
//			mLinearLayout.setBackgroundColor(Color.parseColor("#77000000"));
			mLinearLayout.addView(textView);
			
			mToast.setView(mLinearLayout);
		}
		textView.setText(context.getResources().getString(id));
		mToast.setGravity(Gravity.BOTTOM, 0, 100);
		mToast.setDuration(Toast.LENGTH_SHORT);

		return mToast;
	}
	
	public static Toast makeToast(Context context, int strID, int gravity, int xOffset, int yOffset) {
		if (mToast == null) {
			mToast = new Toast(context);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.leftMargin = 80;
			lp.rightMargin = 80;
			lp.topMargin = 20;
			lp.bottomMargin = 20;
			lp.gravity = Gravity.CENTER;
			
			mLinearLayout = new LinearLayout(context);
			textView = new TextView(context);
			textView.setTextSize(20);
			textView.setGravity(Gravity.CENTER);
			textView.setLayoutParams(lp);
			textView.setTextColor(Color.WHITE);
			mLinearLayout.setBackgroundResource(R.drawable.bg_my_toast);
//			mLinearLayout.setBackgroundColor(Color.parseColor("#77000000"));
			mLinearLayout.addView(textView);
			
			mToast.setView(mLinearLayout);
		}
		textView.setText(context.getResources().getString(strID));
		mToast.setGravity(gravity, xOffset, yOffset);
		mToast.setDuration(Toast.LENGTH_SHORT);
		
		return mToast;
	}
}
