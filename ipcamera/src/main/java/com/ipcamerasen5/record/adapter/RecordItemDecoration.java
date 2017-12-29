package com.ipcamerasen5.record.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by chenqianghua on 2017/4/10.
 */

public class RecordItemDecoration extends RecyclerView.ItemDecoration {
//        private Paint mPaint;
//        private Drawable mDivider;
//        private int mDividerHeight = 2;//分割线高度，默认为1px
//        private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
//        private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
//
//        /**
//         * 默认分割线：高度为2px，颜色为灰色
//         *
//         * @param context
//         * @param orientation 列表方向
//         */
//        public RecordItemDecoration(Context context, int orientation) {
//            if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
//                throw new IllegalArgumentException("请输入正确的参数！");
//            }
//            mOrientation = orientation;
//
//            final TypedArray a = context.obtainStyledAttributes(ATTRS);
//            mDivider = a.getDrawable(0);
//            a.recycle();
//        }
//
//        /**
//         * 自定义分割线
//         *
//         * @param context
//         * @param orientation 列表方向
//         * @param drawableId  分割线图片
//         */
//        public RecordItemDecoration(Context context, int orientation, int drawableId) {
//            this(context, orientation);
//            mDivider = ContextCompat.getDrawable(context, drawableId);
//            mDividerHeight = mDivider.getIntrinsicHeight();
//        }
//
//        /**
//         * 自定义分割线
//         *
//         * @param context
//         * @param orientation   列表方向
//         * @param dividerHeight 分割线高度
//         * @param dividerColor  分割线颜色
//         */
//        public RecordItemDecoration(Context context, int orientation, int dividerHeight, int dividerColor) {
//            this(context, orientation);
//            mDividerHeight = dividerHeight;
//            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            mPaint.setColor(dividerColor);
//            mPaint.setStyle(Paint.Style.FILL);
//        }
//
//
//        //获取分割线尺寸
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            super.getItemOffsets(outRect, view, parent, state);
//            outRect.set(0, 0, 0, mDividerHeight);
//        }
//
//        //绘制分割线
//        @Override
//        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//            super.onDraw(c, parent, state);
//            if (mOrientation == LinearLayoutManager.VERTICAL) {
//                drawVertical(c, parent);
//            } else {
//                drawHorizontal(c, parent);
//            }
//        }
//
//        //绘制横向 item 分割线
//        private void drawHorizontal(Canvas canvas, RecyclerView parent) {
//            final int left = parent.getPaddingLeft()+30;
//            final int right = parent.getMeasuredWidth() - parent.getPaddingRight()-30;
//            final int childSize = parent.getChildCount();
//            for (int i = 0; i < childSize; i++) {
//                final View child = parent.getChildAt(i);
//                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
//                final int top = child.getBottom() + layoutParams.bottomMargin;
//                final int bottom = top + mDividerHeight;
//                if (mDivider != null) {
//                    mDivider.setBounds(left, top, right, bottom);
//                    mDivider.draw(canvas);
//                }
//                if (mPaint != null) {
//                    canvas.drawRect(left, top, right, bottom, mPaint);
//                }
//            }
//        }
//
//        //绘制纵向 item 分割线
//        private void drawVertical(Canvas canvas, RecyclerView parent) {
//            final int top = parent.getPaddingTop();
//            final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
//            final int childSize = parent.getChildCount();
//            for (int i = 0; i < childSize; i++) {
//                final View child = parent.getChildAt(i);
//                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
//                final int left = child.getRight() + layoutParams.rightMargin;
//                final int right = left + mDividerHeight;
//                if (mDivider != null) {
//                    mDivider.setBounds(left, top, right, bottom);
//                    mDivider.draw(canvas);
//                }
//                if (mPaint != null) {
//                    canvas.drawRect(left, top, right, bottom, mPaint);
//                }
//            }
//        }

    private int space;

    public RecordItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildPosition(view) == 0)
//            outRect.top = space;
    }
}
