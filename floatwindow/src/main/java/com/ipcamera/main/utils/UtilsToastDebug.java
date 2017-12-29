package com.ipcamera.main.utils;

import com.ipcamera.main.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UtilsToastDebug {
	private static Toast mToast;
	private static View view;
	private static TextView textView;
	private static LinearLayout mLinearLayout;
	private static final boolean CloseDebug = true;

	
	public static void showToastTest(Context context, String str, int drawableId) {
		if(CloseDebug){
			
		}else{
			Toast makeToast = UtilsToast.makeToast(context, str, drawableId);
			makeToast.show();
		}
	}
	
	public static void showToastTest(Context context, String str) {
		if(CloseDebug){
			
		}else{
			Toast makeToast = UtilsToast.makeToast(context, str);
			makeToast.show();
		}
	}
	
	public static void showToastTest(Context context, int id) {
		if(CloseDebug){
			
		}else{
			Toast makeToast = UtilsToast.makeToast(context, id);
			makeToast.show();
		}
	}
	
	public static void showToastTest(Context context, int strID, int gravity, int xOffset, int yOffset) {
		if(CloseDebug){
		}else{
			Toast makeToast = UtilsToast.makeToast(context, strID, gravity, xOffset, yOffset);
		}
	}
	
	
//	private static Toast makeToast(Context context, String str, int drawableId) {
//		
//		if (mToast == null) {
//			mToast = new Toast(context);
//			
//			android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//					LayoutParams.WRAP_CONTENT);
//			lp.leftMargin = 80;
//			lp.rightMargin = 80;
//			lp.topMargin = 20;
//			lp.bottomMargin = 20;
//			lp.gravity = Gravity.CENTER;
//			
//			mLinearLayout = new LinearLayout(context);
//			textView = new TextView(context);
//			textView.setTextSize(20);
//			textView.setGravity(Gravity.CENTER);
//			textView.setLayoutParams(lp);
//			textView.setTextColor(Color.WHITE);
//			
//			mLinearLayout.setBackgroundResource(drawableId);
//			mLinearLayout.addView(textView);
//			
//			mToast.setView(mLinearLayout);
//			
//		}
//		textView.setText(str);
//		mToast.setGravity(Gravity.BOTTOM, 0, 100);
//		mToast.setDuration(Toast.LENGTH_SHORT);
//		return mToast;
//	}
//
//	private static Toast makeToast(Context context, String str) {
//		if (mToast == null) {
//			mToast = new Toast(context);
//			
//			android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//					LayoutParams.WRAP_CONTENT);
//			lp.leftMargin = 80;
//			lp.rightMargin = 80;
//			lp.topMargin = 20;
//			lp.bottomMargin = 20;
//			lp.gravity = Gravity.CENTER;
//			
//			mLinearLayout = new LinearLayout(context);
//			textView = new TextView(context);
//			textView.setTextSize(20);
//			textView.setGravity(Gravity.CENTER);
//			textView.setLayoutParams(lp);
//			textView.setTextColor(Color.WHITE);
//			
//			mLinearLayout.setBackgroundResource(R.drawable.bg_my_toast);
//			mLinearLayout.addView(textView);
//			
//			mToast.setView(mLinearLayout);
//
//		}
//		textView.setText(str);
//		mToast.setGravity(Gravity.BOTTOM, 0, 100);
//		mToast.setDuration(Toast.LENGTH_SHORT);
//		return mToast;
//	}
//
//	private static Toast makeToast(Context context, int id) {
//		if (mToast == null) {
//			mToast = new Toast(context);
//			
//			android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//					LayoutParams.WRAP_CONTENT);
//			lp.leftMargin = 80;
//			lp.rightMargin = 80;
//			lp.topMargin = 20;
//			lp.bottomMargin = 20;
//			lp.gravity = Gravity.CENTER;
//			
//			mLinearLayout = new LinearLayout(context);
//			textView = new TextView(context);
//			textView.setTextSize(20);
//			textView.setGravity(Gravity.CENTER);
//			textView.setLayoutParams(lp);
//			textView.setTextColor(Color.WHITE);
//			mLinearLayout.setBackgroundResource(R.drawable.bg_my_toast);
////			mLinearLayout.setBackgroundColor(Color.parseColor("#77000000"));
//			mLinearLayout.addView(textView);
//			
//			mToast.setView(mLinearLayout);
//		}
//		textView.setText(context.getResources().getString(id));
//		mToast.setGravity(Gravity.BOTTOM, 0, 100);
//		mToast.setDuration(Toast.LENGTH_SHORT);
//
//		return mToast;
//	}
//	
//	private static Toast makeToast(Context context, int strID, int gravity, int xOffset, int yOffset) {
//		if (mToast == null) {
//			mToast = new Toast(context);
//			
//			android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//					LayoutParams.WRAP_CONTENT);
//			lp.leftMargin = 80;
//			lp.rightMargin = 80;
//			lp.topMargin = 20;
//			lp.bottomMargin = 20;
//			lp.gravity = Gravity.CENTER;
//			
//			mLinearLayout = new LinearLayout(context);
//			textView = new TextView(context);
//			textView.setTextSize(20);
//			textView.setGravity(Gravity.CENTER);
//			textView.setLayoutParams(lp);
//			textView.setTextColor(Color.WHITE);
//			mLinearLayout.setBackgroundResource(R.drawable.bg_my_toast);
////			mLinearLayout.setBackgroundColor(Color.parseColor("#77000000"));
//			mLinearLayout.addView(textView);
//			
//			mToast.setView(mLinearLayout);
//		}
//		textView.setText(context.getResources().getString(strID));
//		mToast.setGravity(gravity, xOffset, yOffset);
//		mToast.setDuration(Toast.LENGTH_SHORT);
//		
//		return mToast;
//	}
}
