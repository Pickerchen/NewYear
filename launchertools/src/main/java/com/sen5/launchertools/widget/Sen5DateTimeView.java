package com.sen5.launchertools.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.sen5.launchertools.R;
import com.sen5.launchertools.weather.TextClock;

import java.util.Calendar;

/**
 * @author JesseYao
 * @version 2017 2017年1月11日 上午9:24:28 ClassName：Sen5DateTimeView.java
 *          Description：
 */
public class Sen5DateTimeView extends RelativeLayout {

	private Context mContext = null;
	private TextClock tvTime;
	private Sen5TextView tvDate, tvWeekday;
    private View vVerticalDivider;
	private IntentFilter mIntentFilter;

	public Sen5DateTimeView(Context context) {
		this(context, null);
	}

	public Sen5DateTimeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Sen5DateTimeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		LayoutInflater lInflater = LayoutInflater.from(mContext);
		View view = lInflater.inflate(R.layout.layout_date_time, this, true);

        vVerticalDivider = findViewById(R.id.v_divider);
		tvTime = (TextClock) view.findViewById(R.id.txt_clock);
		tvDate = (Sen5TextView) view.findViewById(R.id.txt_date);
		tvWeekday = (Sen5TextView) view.findViewById(R.id.txt_weekday);
		
		updateDateDisplay();
	}

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateDateDisplay();
		}
	};
	
	@Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceiver();
    }
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		unregisterReceiver();
	}

	private void registerReceiver() {
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
		mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);
		getContext().registerReceiver(mIntentReceiver, mIntentFilter);
	}
	
	private void unregisterReceiver() {
		getContext().unregisterReceiver(mIntentReceiver);
	}

	private void updateDateDisplay() {
		final Calendar now = Calendar.getInstance();
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(mContext);
		String date = dateFormat.format(now.getTime());
		tvDate.setText(date.toUpperCase());

		int weekday = now.get(Calendar.DAY_OF_WEEK);
		switch (weekday) {
		case SUNDAY:
			tvWeekday.setText(R.string.sunday);
			break;
		case MONDAY:
			tvWeekday.setText(R.string.monday);
			break;
		case TUESDAY:
			tvWeekday.setText(R.string.tuesday);
			break;
		case WEDNESDAY:
			tvWeekday.setText(R.string.wednesday);
			break;
		case THURSDAY:
			tvWeekday.setText(R.string.thursday);
			break;
		case FRIDAY:
			tvWeekday.setText(R.string.friday);
			break;
		case SATURDAY:
			tvWeekday.setText(R.string.saturday);
			break;
		default:
			break;
		}
	}

    public void setDateTimeTextColor(String color) {
        try {
            setDateTimeTextColor(Color.parseColor(color));
        } catch (Exception e) {
            Log.d(TAG, "Set Date time text color failed! Reset to default!");
            setTextColor(getResources().getColor(R.color.color_text));
        }
    }

    public void setDateTimeTextColor(int color) {
        try {
            setTextColor(color);
        }catch (Exception e) {
            Log.d(TAG, "Set Date time text color failed! Reset to default!");
            setTextColor(getResources().getColor(R.color.color_text));
        }
    }

    private void setTextColor(int color) {
        vVerticalDivider.setBackgroundColor(color);
        tvDate.setTextColor(color);
        tvTime.setTextColor(color);
        tvWeekday.setTextColor(color);
    }

    private static final String TAG = Sen5DateTimeView.class.getSimpleName();


	private static final int SUNDAY = 1;
	private static final int MONDAY = 2;
	private static final int TUESDAY = 3;
	private static final int WEDNESDAY = 4;
	private static final int THURSDAY = 5;
	private static final int FRIDAY = 6;
	private static final int SATURDAY = 7;
}
