package com.open.androidtvwidget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 如果有控件放大被挡住，可以使用 LinearMainLayout, <p>
 * 它继承于 LinearLayout.<p>
 * 使用方式和LinerLayout是一样的<p>
 * @author hailongqiu
 *
 */
public class LinearMainLayout extends LinearLayout {

	private int indexOfChild;

	public LinearMainLayout(Context context) {
		super(context);
		init(context);
	}

	public LinearMainLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	WidgetTvViewBring mWidgetTvViewBring;

	private void init(Context context) {
		this.setChildrenDrawingOrderEnabled(true);
		mWidgetTvViewBring = new WidgetTvViewBring(this);
	}

	@Override
	public void bringChildToFront(View child) {
//		mWidgetTvViewBring.bringChildToFront(this, child);
		bringChildToFrontMy(this, child);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
//		int position = mWidgetTvViewBring.getChildDrawingOrder(childCount, i);
		int position = getChildOrderMy(childCount, i);
		Log.e("AA","-----------getChildDrawingOrder position = " + position);
		return position;
	}

	private void bringChildToFrontMy(ViewGroup view, View childView){
		indexOfChild = view.indexOfChild(childView);
		if(indexOfChild != -1){
			this.postInvalidate();
		}
	}

	/**
	 * @param childCount  该方法的目的是交换最后一个绘制view和当前获取焦点的View的绘制顺序。
	 * @param i
	 * @return
	 */
	private int getChildOrderMy(int childCount, int i){
		Log.e("AA","-----------getChildDrawingOrder i = " + i);
		if(indexOfChild != -1){

			if(i == childCount -1){
				return indexOfChild;
			}else if(i == indexOfChild){
				return childCount -1;
			}
		}
		return i;
	}
}
