package com.sen5.launchertools.widget;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sen5.launchertools.GeneralUtils;
import com.sen5.launchertools.R;

import java.util.List;

public class ClearMemoryView extends TextView implements Callback {

	private static final int REFRESHING = 0x01;
	private static final int REFRESH_UI = 0x02;
	private static final int REFRESH_END = 0x03;
	private Context mContext = null;
	private Handler mHandler = null;
	/** 画圆的画笔 */
	private Paint mCircularPaint;
	/** 写字体的画笔 */
	private Paint mTxtPaint;
	private RectF mRectF;
	private RectF mRectFrame;
	/** View的宽度 */
	private int mViewWidth = 0;
	/** View的高度 */
	private int mViewHeight = 0;
	/** 圆的宽度 */
	private int mCircularWidth = 0;
	/** 当前的进度 */
	private int mCurrentProgress = 100;
	private int mMax = 100;
	private long mMaxValue = 0;
	private float mSweepAngle;
	private float mTxtSize = 0.0f;
	/** ClearAppsIcon的进度 */
	private int mProgress = 0;
	/** 允许清除apps的标志 */
	private boolean mAllowClear = true;
	/** 之前的内存 */
	private long mBeforeMem = 0l;
	private float mIconPadding = getResources().getDimensionPixelSize(R.dimen.dimen_icon_padding);

	private float paintwidth = 10;
	private Paint mCenterPaint;
	private Paint mPerentPaint;
	private List<String> mBackgroundAppList;

	public ClearMemoryView(Context context) {
		this(context, null);
	}

	public ClearMemoryView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClearMemoryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mHandler = new Handler(this);
		mViewWidth = (int) mContext.getResources()
				.getDimension(R.dimen.dimen_clean_view_width);
		mViewHeight = (int) mContext.getResources()
				.getDimension(R.dimen.dimen_clean_view_width);
		mBackgroundAppList = GeneralUtils.getAllowBackgroundAppList();
		if (null != mBackgroundAppList) {
			for (int i = 0; i < mBackgroundAppList.size(); i++) {
//				Log.e(TAG, "------------------mBackgroundAppList = "
//						+ mBackgroundAppList.get(i));
			}
		}
		initData();

	}

	private void initData() {

		mCircularWidth = (int) (mViewWidth * 0.9f);
		//mCircularWidth = (int) (mViewWidth * 1.0f);

//		Log.d(TAG, "Clear View Circular==" + mCircularWidth + "::CellWidth=="
//				+ mViewWidth + ":::getScrollX() = " + getScrollX());
		mRectF = new RectF();
		mRectFrame = new RectF();
		// 初始化画圆形的画笔
		mCircularPaint = new Paint();
		mCircularPaint.setStyle(Paint.Style.STROKE);
		mCircularPaint.setStrokeCap(Paint.Cap.ROUND);
		mCircularPaint.setStrokeJoin(Paint.Join.ROUND);
		mCircularPaint.setDither(true);
		mCircularPaint.setAntiAlias(true);
		mCircularPaint.setStrokeWidth(paintwidth);

		mCenterPaint = new Paint();
		mCenterPaint.setStyle(Paint.Style.FILL);
		mCenterPaint.setDither(true);
		mCenterPaint.setAntiAlias(true);
		mCenterPaint.setColor(Color.parseColor("#eeeeee"));
		// 初始化写文字的画笔
		mTxtPaint = new Paint();
		mTxtSize = 15;

		mTxtPaint.setTextSize(mTxtSize);
		mTxtPaint.setAntiAlias(true);
		Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
		mTxtPaint.setTypeface(typeface);
		mTxtPaint.setColor(Color.BLACK);

		mPerentPaint = new Paint();
		mPerentPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_percent_symbol_txt_size));
		mPerentPaint.setAntiAlias(true);
		mPerentPaint.setTypeface(typeface);
		mPerentPaint.setColor(Color.BLACK);
		// 初始化值
		setMaxValue(GeneralUtils.getTotalMemory(mContext));
		setProgressValue(GeneralUtils.getUsedMemory(mContext));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// 圆环
		float fLeft = mViewWidth - mCircularWidth;
		// float fTop = (mViewHeight - mCircularWidth) / 2 + mIconPadding;
		float fTop = (mViewHeight - mCircularWidth);
//		Log.e(TAG, "=========----------getScrollX() = " + getScrollX());
		mRectFrame.set(getScrollX() + fLeft - 8, fTop - 8,
				getScrollX() + mCircularWidth + 8, mCircularWidth + 8);
		mCircularPaint.setStrokeWidth(1);
        mCircularPaint.setColor(Color.parseColor("#9A9C9E"));
        mCircularPaint.setAntiAlias(true);
		//canvas.drawArc(mRectFrame, 275, 360, false, mCircularPaint);

		mRectF.set(getScrollX() + fLeft, fTop, getScrollX() + mCircularWidth,
				mCircularWidth);

		mCircularPaint.setStrokeWidth(paintwidth);
		mCircularPaint.setColor(Color.parseColor("#757575"));
		canvas.drawArc(mRectF, 275, 360, false, mCircularPaint);
		mSweepAngle = ((float) mCurrentProgress / mMax) * 360;
		if (mSweepAngle >= 324) {
			// 90% 324
			mCircularPaint.setColor(Color.parseColor("#6d58e8"));

		} else if (mSweepAngle >= 216) {
			// 60% 216
			mCircularPaint.setColor(Color.parseColor("#1875d1"));

		} else {
			mCircularPaint.setColor(Color.parseColor("#0699fb"));
		}
		canvas.drawArc(mRectF, 275, mSweepAngle, false, mCircularPaint);

		mRectF.set(getScrollX() + fLeft + 6, fTop + 5,
				getScrollX() + mCircularWidth - 5, mCircularWidth - 5);
		canvas.drawArc(mRectF, 275, 360, false, mCenterPaint);

		// 文字内容
		long nPercentage = mCurrentProgress * 100 / mMax;
		String strPercentage = String.valueOf(nPercentage);
		String percentage = "%";
		// 文字的长度
        mTxtPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_percentage_txt_size));
		float fTxtLeght = mTxtPaint.measureText(strPercentage);
		float fPercentLenght = mPerentPaint.measureText("%");
		float fTxtHeight = mTxtPaint.measureText("+");
		float nLeft = (mViewWidth - fTxtLeght) / 2;

		float nTop = (mCircularWidth + fTxtHeight) / 2 + mIconPadding;

		canvas.drawText(strPercentage,
				getScrollX() + nLeft - fPercentLenght / 2, nTop, mTxtPaint);

		canvas.drawText(percentage, getScrollX() + nLeft + fTxtLeght * 3 / 4,
				nTop, mPerentPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mViewWidth, mViewHeight);
	}

	/**
	 * 获取比例值
	 * 
	 * @return
	 */
	public int getProgress() {
		return mCurrentProgress;
	}

	/**
	 * 当前的比例
	 * 
	 * @param progress
	 */
	public void setProgressValue(long progress) {
		if (progress < 0) {
			progress = 0;
		}
		if (progress > mMaxValue) {
			progress = mMaxValue;
		}
		mCurrentProgress = (int) (progress * 100 / mMaxValue);
		invalidate();
	}

	public void setProgress(int progress) {
		if (progress < 0) {
			progress = 0;
		}
		if (progress > mMax) {
			progress = mMax;
		}
		mCurrentProgress = progress;
		invalidate();
	}

	/**
	 * 定义的最大值
	 * 
	 * @param max
	 */
	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}

	/**
	 * 
	 */
	public void setMaxValue(long maxValue) {
		mMaxValue = maxValue;
	}

	/**
	 * 清除Apps，释放内存
	 */
	private void clearApps() {

		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
		// List<ActivityManager.RunningServiceInfo> serviceInfos =
		// am.getRunningServices(100);
		mBeforeMem = GeneralUtils.getAvailMemory(mContext);
		if (infoList != null) {
			for (int i = 0; i < infoList.size(); ++i) {
				RunningAppProcessInfo appProcessInfo = infoList.get(i);
				// Log.d(TAG, "process name : " + appProcessInfo.processName);
				// importance 该进程的重要程度 分为几个级别，数值越低就越重要。
//				Log.d(TAG, "importance : " + appProcessInfo.importance);

				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
				if (appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					String[] pkgList = appProcessInfo.pkgList;
					for (int j = 0; j < pkgList.length; ++j) {// pkgList
																// 得到该进程下运行的包名
						if ("com.sen5.launcher".equals(pkgList[j])
								|| "com.amlogic.DVBPlayer".equals(pkgList[j])) {
							continue;
						}
						boolean isClear = true;
						if (null != mBackgroundAppList) {
							int size = mBackgroundAppList.size();
							for (int j2 = 0; j2 < size; j2++) {
								String string = mBackgroundAppList.get(j2);

								if (!TextUtils.isEmpty(string)
										&& string.equals(pkgList[j])) {
									isClear = false;
								}
							}
						}

						// DLog.e("-------------------- clear name = " +
						// pkgList[j]);
						if (isClear) {

							try {
								am.killBackgroundProcesses(pkgList[j]);
							} catch (SecurityException e) {
//								Log.e(TAG, "killBackgroundProcesses error=="
//										+ e.toString());
							}
						} else {
//							Log.e(TAG, "---------------------not clear name = "
//									+ pkgList[j]);
						}
					}
				}
			}
		}
	}

	/**
	 * 更新界面的动画
	 */
	private void refeshUI() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				for (; mProgress > -1; mProgress--) {

					mHandler.sendEmptyMessage(REFRESH_UI);
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				int nUsedMemory = (int) (GeneralUtils.getUsedMemory(mContext)
						* getMax() / GeneralUtils.getTotalMemory(mContext));
				for (; mProgress <= nUsedMemory; mProgress++) {

					mHandler.sendEmptyMessage(REFRESH_UI);
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nUsedMemory = (int) (GeneralUtils.getUsedMemory(mContext)
							* getMax() / GeneralUtils.getTotalMemory(mContext));
				}
				mHandler.sendEmptyMessage(REFRESH_END);
			}
		}).start();

	}

	/**
	 * 开始清除内存
	 */
	public void startClearApps() {

		if (!mAllowClear) {
			return;
		}
		mProgress = getProgress();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mAllowClear = false;
				mHandler.sendEmptyMessage(REFRESHING);
				clearApps();
			}
		}).start();

	}

	/**
	 * 刷新内存的现实，一般在应用打开时就得调用
	 */
	public void refreshIconValue() {

		setProgressValue(GeneralUtils.getUsedMemory(mContext));
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case REFRESHING:
			refeshUI();
			break;

		case REFRESH_UI:
			setProgress(mProgress);
			break;

		case REFRESH_END:
			mAllowClear = true;
			// 清除内存的结果显示
			long currentMem = GeneralUtils.getAvailMemory(mContext);
//			Log.d(TAG, "AvailMem===" + currentMem + ":::BeforeMem==="
//					+ mBeforeMem);
			String strHint = "";
			if (currentMem > mBeforeMem) {
				String strReleaseMem = mContext
						.getString(R.string.release_memory);
				String strCurrentMem = mContext
						.getString(R.string.currunt_memory);
				strHint = String.format("%s %dM,\n%s %dM", strReleaseMem,
						(currentMem - mBeforeMem), strCurrentMem, currentMem);
			} else {
				strHint = mContext.getString(R.string.best_memory_status);
			}
			// new CustomToast(mContext, strHint);
			Toast.makeText(mContext, strHint, Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}

		return false;
	}

	private static final String TAG = ClearMemoryView.class.getSimpleName();
}
