package com.sen5.secure.launcher.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by ZHOUDAO on 2017/6/14.
 */

public class MyHorizontalScrollView extends HorizontalScrollView {

    private int mSelectedItemOffsetStart;
    private int mSelectedItemOffsetEnd;
    private int offset = -1;

    public MyHorizontalScrollView(Context context) {
        this(context, null);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void requestChildFocus(View child, View focused) {

        if (null != child) {

            mSelectedItemOffsetStart = getFreeWidth() - child.getWidth();
            mSelectedItemOffsetStart /= 2;
            mSelectedItemOffsetEnd = mSelectedItemOffsetStart;

        }

        super.requestChildFocus(child, focused);

    }


    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = getHeight() - getPaddingBottom();

        final int childLeft = child.getLeft() + rect.left;
        final int childTop = child.getTop() + rect.top;

//        final int childLeft = child.getLeft() + rect.left - child.getScrollX();
//        final int childTop = child.getTop() + rect.top - child.getScrollY();

        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();

        final int offScreenLeft = Math.min(0, childLeft - parentLeft - mSelectedItemOffsetStart);
        final int offScreenTop = Math.min(0, childTop - parentTop - mSelectedItemOffsetStart);
        final int offScreenRight = Math.max(0, childRight - parentRight + mSelectedItemOffsetEnd);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + mSelectedItemOffsetEnd);


        // Favor the "start" layout direction over the end when bringing one side or the other
        // of a large rect into view. If we decide to bring in end because start is already
        // visible, limit the scroll such that start won't go out of bounds.
        final int dx;
        if (true) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                dx = offScreenRight != 0 ? offScreenRight
                        : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                dx = offScreenLeft != 0 ? offScreenLeft
                        : Math.min(childLeft - parentLeft, offScreenRight);
            }
        } else {
            dx = 0;
        }

        // Favor bringing the top into view over the bottom. If top is already visible and
        // we should scroll to make bottom visible, make sure top does not go out of bounds.
        final int dy = 0;

        offset = dx;
        if (dx != 0 || dy != 0) {
            if (immediate) {
                scrollBy(dx, dy);
            } else {
                smoothScrollBy(dx, dy);
            }
            return true;
        }


        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        postInvalidate();

        return false;
    }

    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
}
