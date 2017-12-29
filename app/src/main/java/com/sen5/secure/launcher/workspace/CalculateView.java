package com.sen5.secure.launcher.workspace;

import android.content.Context;

import com.sen5.secure.launcher.R;


public class CalculateView {
    /** 实例 */
    private static CalculateView mCalculateView = new CalculateView();
    /** 横向有6个App */
    public static final int CellCountX = 5;
    /** 纵向有2个App */
    public static final int CellCountY = 2;
    /** 每页Apps的总数 */
    private int mCellsCount = 0;
    /** workspace占整个布局的比例 */
    private static final float WORKSPACE_RATIO = 0.85f;
    private int mPaddingTop = 0;
    private int mPaddingBottom = 0;
    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;
    /** Cell的宽度 */
    private int mCellWidth = 0;
    /** Cell的高度 */
    private int mCellHeight = 0;
    /** Cell的横向间隔 */
    private int mCellWidthGap = 0;
    /** Cell的纵向间隔 */
    private int mCellHeightGap = 0;
    /** 标题的高度 */
    private int mTitleHeight = 0;

    /**
     * 饿汉
     * 
     * @return
     */
    public static CalculateView getInstance() {

        return mCalculateView;
    }

    public void init(Context context) {

        mCellsCount = CellCountX * CellCountY;
        int nScreenHeight = context.getResources().getDisplayMetrics().heightPixels;

        mPaddingTop = context.getResources().getDimensionPixelSize(R.dimen.pageLayoutPaddingTop);
        mPaddingBottom = context.getResources().getDimensionPixelSize(R.dimen.pageLayoutPaddingBottom);
        mPaddingLeft = context.getResources().getDimensionPixelSize(R.dimen.pageLayoutPaddingLeft);
        mPaddingRight = context.getResources().getDimensionPixelSize(R.dimen.pageLayoutPaddingRight);
        mCellWidthGap = context.getResources().getDimensionPixelSize(R.dimen.apps_width_gap);
        mCellHeightGap = context.getResources().getDimensionPixelSize(R.dimen.apps_height_gap);

        mCellWidth = context.getResources().getDimensionPixelSize(R.dimen.view_width);
        mCellHeight = mCellWidth;

        mTitleHeight = (int) (nScreenHeight * (1 - WORKSPACE_RATIO));
    }

    public int getCellsCount() {
        return mCellsCount;
    }

    public int getPaddingTop() {
        return mPaddingTop;
    }

    public int getPaddingBottom() {
        return mPaddingBottom;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    /**
     * 获取Cell的宽度
     * 
     * @return
     */
    public int getCellWidth() {

        return mCellWidth;
    }

    /**
     * 获取Cell的高度
     * 
     * @return
     */
    public int getCellHeight() {

        return mCellHeight;
    }

    /**
     * 获取Cell的横向间隔
     * 
     * @return
     */
    public int getCellWidthGap() {

        return mCellWidthGap;
    }

    /**
     * 获取Cell的纵向间隔
     * 
     * @return
     */
    public int getCellHeightGap() {

        return mCellHeightGap;
    }

    /**
     * 获取标题的高度
     * 
     * @return
     */
    public int getTitleHeight() {

        return mTitleHeight;
    }
}
