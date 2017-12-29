package com.ipcamera.main.view;

import java.util.ArrayList;
import java.util.List;

import com.ipcamera.main.R;
import com.ipcamera.main.cuswidget.MainUpView;
import com.ipcamera.main.cuswidget.MyBottomScaleView;
import com.ipcamera.main.utils.UtilsFingerPrint;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MenuFrameLayout extends FrameLayout implements View.OnClickListener, View.OnFocusChangeListener{
	private MyBottomScaleView ll_left;

	private MyBottomScaleView ll_right;

	private MyBottomScaleView ll_fullscren;

	private MyBottomScaleView ll_close;
	private List<MyBottomScaleView> mListMenuView;

	private MainUpView mMainUpView;
	private Context mContext;
	
	public MenuFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MenuFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		initView();
	}

	public MenuFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		mListMenuView = new ArrayList<MyBottomScaleView>();
		
		View mView = LayoutInflater.from(getContext()).inflate(R.layout.float_layout_menu_ui, null);
		ll_left = (MyBottomScaleView)mView.findViewById(R.id.ll_left);
		ll_right = (MyBottomScaleView)mView.findViewById(R.id.ll_right);
		ll_fullscren = (MyBottomScaleView)mView.findViewById(R.id.ll_fullscren);
		ll_close = (MyBottomScaleView)mView.findViewById(R.id.ll_close);
		
		((ImageView)ll_left.getChildAt(0)).setImageResource(R.drawable.selector_menu_item_left_bg);
		((ImageView)ll_right.getChildAt(0)).setImageResource(R.drawable.selector_menu_item_right_bg);
		((ImageView)ll_fullscren.getChildAt(0)).setImageResource(R.drawable.selector_menu_item_full_bg);
		((ImageView)ll_close.getChildAt(0)).setImageResource(R.drawable.selector_menu_item_close_bg);
		
		mListMenuView.add(ll_left);
		mListMenuView.add(ll_right);
		mListMenuView.add(ll_fullscren);
		mListMenuView.add(ll_close);
		
		mMainUpView = (MainUpView)mView.findViewById(R.id.bottom_effect);
		
		UtilsFingerPrint.setMenuTitle(mContext.getResources().getStringArray(R.array.array_menu_title), mListMenuView);
		int sizeMenuItem = mListMenuView.size();
		for (int i = 0; i < sizeMenuItem; i++) {
			MyBottomScaleView myBottomScaleView = mListMenuView.get(i);
			
			myBottomScaleView.setOnFocusChangeListener(this);
		}
		
		addView(mView);
		setFocusRecycle();
	}
	
	public void setOnKeyListenerByMenuItem(OnKeyListener onKeyListener){
		int sizeMenuItem = mListMenuView.size();
		for (int i = 0; i < sizeMenuItem; i++) {
			MyBottomScaleView myBottomScaleView = mListMenuView.get(i);
			myBottomScaleView.setOnKeyListener(onKeyListener);
			
		}
	}
	
	public void setOnClickListenerByMenuItem(OnClickListener onClickListener){
		int sizeMenuItem = mListMenuView.size();
		for (int i = 0; i < sizeMenuItem; i++) {
			MyBottomScaleView myBottomScaleView = mListMenuView.get(i);
			myBottomScaleView.setOnClickListener(onClickListener);
			
		}
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if(arg1){
			mMainUpView.setFocusView(arg0, 1.2f);
			if(arg0 instanceof MyBottomScaleView){
				(((MyBottomScaleView)arg0).getChildAt(0)).setSelected(true);
				((MyBottomScaleView)arg0).getChildAt(1).setSelected(true);
			}
		}else{
			mMainUpView.setUnFocusView(arg0);
			if(arg0 instanceof MyBottomScaleView){
				(((MyBottomScaleView)arg0).getChildAt(0)).setSelected(false);
				((MyBottomScaleView)arg0).getChildAt(1).setSelected(false);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void setFocusRecycle(){
		ll_left.setNextFocusLeftId(R.id.ll_close);
		ll_close.setNextFocusRightId(R.id.ll_left);
	}
	
	public void hideView(){
		if(getVisibility() == View.VISIBLE){
			Animation loadAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_menu_down);
			startAnimation(loadAnimation);
			setVisibility(View.INVISIBLE);
		}
	}
	
	public void showView(boolean startAnim){
		if(getVisibility() != View.VISIBLE || startAnim){
			
			Animation loadAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_menu_up);
			startAnimation(loadAnimation);
		}
		setVisibility(View.VISIBLE);
	}
	
	
	

}
