package com.sen5.launchertools.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

import com.sen5.launchertools.R;

/**
 * @author JesseYao
 * @version 2016 2016年12月8日 下午2:45:24 ClassName：ScaleItemView.java Description：
 */

public class SlideRemoveListView extends ListView {
	private int slidePosition;
	private int downY;
	private int downX;
	private int screenWidth;
	private View itemView;
	private Scroller scroller;
	private static final int SNAP_VELOCITY = 600;
	private VelocityTracker velocityTracker;
	private boolean isSlide = false;
	private int mTouchSlop;
	private OnRemoveListener mRemoveListener;
	private RemoveDirection removeDirection;
	private int mCurrentSelectedPosition = 0;
	private int mRemoveTranslateX = 0;
	private boolean mIsRemovable = true;
	private boolean mRemoveCompleted = true;

	public enum RemoveDirection {
		RIGHT, LEFT;
	}

	public SlideRemoveListView(Context context) {
		this(context, null);
	}

	public SlideRemoveListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideRemoveListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// screenWidth = ((WindowManager)
		// context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		screenWidth = context.getResources()
				.getDimensionPixelSize(R.dimen.dimen_notification_window_width);
		mRemoveTranslateX = context.getResources().getDimensionPixelOffset(
				R.dimen.dimen_notification_window_width);
		scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public void setOnRemoveListener(OnRemoveListener removeListener) {
		this.mRemoveListener = removeListener;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		mIsRemovable = mRemoveListener.isRemovable(getSelectedItemPosition());
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (event.getAction() == KeyEvent.ACTION_DOWN && mRemoveCompleted
					&& mIsRemovable) {
				Log.d(TAG, "----------left to remove item------");
				mRemoveCompleted = false;
				getSelectedView().animate().translationX(-mRemoveTranslateX)
						.setStartDelay(0).setDuration(500).withLayer().start();
				mCurrentSelectedPosition = getSelectedItemPosition();
				removeDirection = RemoveDirection.LEFT;
				mHandler.sendEmptyMessageDelayed(MSG_REMOVE_ITEM, 500);
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (event.getAction() == KeyEvent.ACTION_DOWN && mRemoveCompleted
					&& mIsRemovable) {
				Log.d(TAG, "----------right to remove item------");
				mRemoveCompleted = false;
				getSelectedView().animate().translationX(mRemoveTranslateX)
						.setStartDelay(0).setDuration(500).withLayer().start();
				mCurrentSelectedPosition = getSelectedItemPosition();
				removeDirection = RemoveDirection.RIGHT;
				mHandler.sendEmptyMessageDelayed(MSG_REMOVE_ITEM, 500);
			}
			return true;
		default:
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			addVelocityTracker(event);

			if (!scroller.isFinished()) {
				return super.dispatchTouchEvent(event);
			}
			downX = (int) event.getX();
			downY = (int) event.getY();

			slidePosition = pointToPosition(downX, downY);

			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}

			itemView = getChildAt(slidePosition - getFirstVisiblePosition());
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
					|| (Math.abs(event.getX() - downX) > mTouchSlop
							&& Math.abs(event.getY() - downY) < mTouchSlop)) {
				isSlide = true;

			}
			break;
		}
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	private void scrollRight() {
		removeDirection = RemoveDirection.RIGHT;
		final int delta = (screenWidth + itemView.getScrollX());
		scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate();
	}

	private void scrollLeft() {
		removeDirection = RemoveDirection.LEFT;
		final int delta = (screenWidth - itemView.getScrollX());
		scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
				Math.abs(delta));
		postInvalidate();
	}

	private void scrollByDistanceX() {
		if (itemView.getScrollX() >= screenWidth / 3) {
			scrollLeft();
		} else if (itemView.getScrollX() <= -screenWidth / 3) {
			scrollRight();
		} else {
			itemView.scrollTo(0, 0);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
			addVelocityTracker(ev);
			final int action = ev.getAction();
			int x = (int) ev.getX();
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				int deltaX = downX - x;
				downX = x;
				itemView.scrollBy(deltaX, 0);
				break;
			case MotionEvent.ACTION_UP:
				int velocityX = getScrollVelocity();
				if (velocityX > SNAP_VELOCITY) {
					scrollRight();
				} else if (velocityX < -SNAP_VELOCITY) {
					scrollLeft();
				} else {
					scrollByDistanceX();
				}

				recycleVelocityTracker();
				isSlide = false;
				break;
			}

			return true;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
			if (scroller.isFinished()) {
				if (mRemoveListener == null) {
					throw new NullPointerException(
							"RemoveListener is null, we should called setRemoveListener()");
				}

				itemView.scrollTo(0, 0);
				mRemoveListener.removeItem(removeDirection, slidePosition);
			}
		}
	}

	private void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}

		velocityTracker.addMovement(event);
	}

	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	private int getScrollVelocity() {
		velocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}

	public interface OnRemoveListener {
		public boolean removeItem(RemoveDirection direction, int position);

		public boolean isRemovable(int position);
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message arg0) {
			switch (arg0.what) {
			case MSG_REMOVE_ITEM:
				mRemoveCompleted = mRemoveListener.removeItem(removeDirection,
						mCurrentSelectedPosition);
				try {
					getSelectedView().animate().translationX(
							removeDirection == RemoveDirection.LEFT
									? mRemoveTranslateX : -mRemoveTranslateX)
							.start();
				} catch (Exception e) {
					Log.e(TAG, "---------remove last item-------");
				}
				break;

			default:
				break;
			}
			return true;
		}
	});

	private static final int MSG_REMOVE_ITEM = 0x50;
	private static final String TAG = SlideRemoveListView.class.getSimpleName();
}
