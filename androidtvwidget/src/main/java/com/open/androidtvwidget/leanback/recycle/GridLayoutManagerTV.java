package com.open.androidtvwidget.leanback.recycle;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hailongqiu on 2016/8/25.
 */
public class GridLayoutManagerTV extends GridLayoutManager {
    public GridLayoutManagerTV(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerTV(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public GridLayoutManagerTV(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
        View nextFocus = super.onFocusSearchFailed(focused, focusDirection, recycler, state);
        return null;
    }

    /**
     * 重写该方法，返回false
     */
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    /**
     * 重写该方法，去捕捉该异常
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try{
            super.onLayoutChildren(recycler, state);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

    }
}
