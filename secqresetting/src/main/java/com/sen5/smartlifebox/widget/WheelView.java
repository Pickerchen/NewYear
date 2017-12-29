package com.sen5.smartlifebox.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sen5.smartlifebox.R;

import java.security.Key;
import java.util.List;

public class WheelView extends ScrollView implements View.OnClickListener {

    private int viewWidth;
    private int itemHeight;
    private int padding;

    private OnWheelScrollListener mOnWheelScrollListener;
    private OnItemClickListener mOnItemClickListener;

    private LinearLayout mViewContainer;

    private Paint paint;

    public WheelView(Context context) {
        this(context, null);

    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.wheel_view_attrs);
        itemHeight = (int) a.getDimension(R.styleable.wheel_view_attrs_item_height, 60.0f);
        a.recycle();

        initWheelView();
    }

    private void initWheelView() {
        setVerticalScrollBarEnabled(false);
        //创建Item容器
        mViewContainer = new LinearLayout(getContext());
        mViewContainer.setOrientation(LinearLayout.VERTICAL);
        mViewContainer.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mViewContainer.setLayoutParams(layoutParams);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                padding = (getHeight() - itemHeight) / 2;
                mViewContainer.setPadding(0, padding, 0, padding);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        addView(mViewContainer);

    }

    private TextView createSimpleItem(String str) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        textView.setSingleLine(true);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        textView.setTextColor(Color.parseColor("#bbbbbb"));
        textView.setText(str);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        int padding = dip2px(getContext(), 24);
        textView.setPadding(padding, 0, 0, 0);
        return textView;
    }

    @Override
    public void setBackground(Drawable background) {
        if (viewWidth == 0) {
            viewWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(Color.parseColor("#19ffffff"));
            paint.setStrokeWidth(dip2px(getContext(), 1f));
        }

        //替换掉原来的Background
        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                if (mViewContainer.getChildCount() > 0) {
                    canvas.drawRect(0, padding, viewWidth, padding + itemHeight, paint);
                }
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
        super.setBackground(background);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackground(null);
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    //处理触摸
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {

//        return super.onTouchEvent(ev);
//    }

    private int mCurPosition = 0;

    //处理按键
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && mCurPosition > 0) {
//            this.smoothScrollBy(0, -itemHeight);
//            mCurPosition--;
//            if (mOnWheelScrollListener != null) {
//
//                mOnWheelScrollListener.onScrollPosition(mCurPosition);
//                //使用getScrollY()的话，当按键按的太快会出现计算位置错误
////                mOnWheelScrollListener.onScrollPosition(getScrollY() / itemHeight);
//            }
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && (mCurPosition < getItemCount() - 1)) {
//            this.smoothScrollBy(0, itemHeight);
//            mCurPosition++;
//            if (mOnWheelScrollListener != null) {
//                mOnWheelScrollListener.onScrollPosition(mCurPosition);
////                mOnWheelScrollListener.onScrollPosition(getScrollY() / itemHeight);
//            }
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//            if (mOnItemClickListener != null) {
//                if (mViewContainer.getChildCount() > 0) {
//                    mOnItemClickListener.onItemClick(mCurPosition);
////                    mOnItemClickListener.onItemClick(getScrollY() / itemHeight);
//                }
//            }
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

    private long keyDownTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            //使用smoothScrollBy时，如果按键按下比较快会导致getScrollY不准确，因为平滑移动使用了动画，需要一定时间移动
            //到指定位置，使用scrollBy就没有这个问题，但移动起来不平滑
//            this.smoothScrollBy(0, -itemHeight);
            this.scrollBy(0, -itemHeight);
            if (mOnWheelScrollListener != null) {
                mOnWheelScrollListener.onScrollPosition(getScrollY() / itemHeight);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//            this.smoothScrollBy(0, itemHeight);
            this.scrollBy(0, itemHeight);
            if (mOnWheelScrollListener != null) {
                mOnWheelScrollListener.onScrollPosition(getScrollY() / itemHeight);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (mOnItemClickListener != null) {
                if (mViewContainer.getChildCount() > 0) {
                    mOnItemClickListener.onItemClick(getScrollY() / itemHeight);
                }
            }
            return true;
//        } else if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            keyDownTime = System.currentTimeMillis();
//        } else if (event.getAction() == KeyEvent.ACTION_UP) {
//            if (System.currentTimeMillis() - keyDownTime < 500) {
//                if (mOnItemClickListener != null) {
//                    if (mViewContainer.getChildCount() > 0) {
//                        mOnItemClickListener.onItemClick(getScrollY() / itemHeight);
//                    }
//                }
//            }
//
//            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 添加单个简单条目
     *
     * @param
     */
    public void addItem(String string) {
        addItem(string, -1);
    }

    public void addItem(List<String> list) {
        for (String str : list) {
            addItem(str, -1);
        }
    }

    public void addItem(String string, int index) {
        addItem(createSimpleItem(string), index);
    }

    public void addItem(View view) {
        addItem(view, -1);
    }

    public void addItem(View view, int index) {
        view.setOnClickListener(this);
        mViewContainer.addView(view, index);
    }

    public void removeItems(int start, int count) {
        mViewContainer.removeViews(start, count);
    }

    public void removeAllItem() {
        removeItems(0, mViewContainer.getChildCount());
    }

    public View getItem(int index) {
        return mViewContainer.getChildAt(index);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    public int getItemCount() {
        return mViewContainer.getChildCount();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            for (int i = 0; i < mViewContainer.getChildCount(); i++) {
                if (view == mViewContainer.getChildAt(i)) {

                    this.scrollTo(0, i*itemHeight);

                    mOnItemClickListener.onItemClick(i);

                    break;
                }
            }

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnWheelScrollListener(OnWheelScrollListener listener) {
        this.mOnWheelScrollListener = listener;
    }

    public interface OnWheelScrollListener {
        void onScrollPosition(int position);
    }
}
