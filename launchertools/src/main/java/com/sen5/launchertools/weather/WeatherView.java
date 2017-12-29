/**
 *WeatherView.java[V 1.0.0]
 *classes : com.sen5.launcher.widget.WeatherView
 * Xlee Create at 2016-6-3 上午9:53:11
 */
package com.sen5.launchertools.weather;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sen5.launchertools.R;

/**
 *
 */
public class WeatherView extends RelativeLayout implements WeaterUtils.CallBack, IpToLocation.CallBack {
    private static final String TAG = WeatherView.class.getSimpleName();

    private View mVLineVertical, mVLineHorizontal;
    private TextView mTxtLocation, mTxtWeatherCondition, mTxtDegree, mTxtClock;
    private TextView mOneTitle, mTwoTitle, mThreeTitle, mFourTitle;
    private TextView mWeather_status_one, mWeather_status_two, mWeather_status_three, mWeather_status_four;
    private RelativeLayout mLeftRL, mRightRL;
    private WeatherBiz mWeatherBiz = null;
    private WeatherReceiver mWeatherReceiver = null;
    private Context mContext;


    public WeatherView(Context context) {
        this(context, null);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.weather_view_new, this, true);
        mTxtLocation = (TextView) findViewById(R.id.txt_location);
        mTxtWeatherCondition = (TextView) findViewById(R.id.txt_weather_condition);
        mTxtDegree = (TextView) findViewById(R.id.txt_degree);
        mTxtClock = (TextView) findViewById(R.id.txt_clock);
        mVLineVertical = findViewById(R.id.v_line_vertical);
        mVLineHorizontal = findViewById(R.id.v_line_horizontal);

        mOneTitle = (TextView) findViewById(R.id.one);
        mTwoTitle = (TextView) findViewById(R.id.two);
        mThreeTitle = (TextView) findViewById(R.id.three);
        mFourTitle = (TextView) findViewById(R.id.four);

        mWeather_status_one = (TextView)findViewById(R.id.weather_status_one);
        mWeather_status_two = (TextView)findViewById(R.id.weather_status_two);
        mWeather_status_three = (TextView)findViewById(R.id.weather_status_three);
        mWeather_status_four = (TextView)findViewById(R.id.weather_status_four);

        mLeftRL = (RelativeLayout) findViewById(R.id.weather_left_rl);
        mRightRL = (RelativeLayout) findViewById(R.id.weather_right_rl);

        mWeatherBiz = new WeatherBiz(context);
        mWeatherBiz.setLocationCallBack(this);
        mWeatherBiz.setWeatherCallBack(this);
    }

    public void setColor(int color) {
        if (null != mVLineVertical) {
            mVLineVertical.setBackgroundColor(color);
        }
        if (null != mVLineHorizontal) {
            mVLineHorizontal.setBackgroundColor(color);
        }
        if (null != mTxtLocation) {
            mTxtLocation.setTextColor(color);
        }
        if (null != mTxtWeatherCondition) {
            mTxtWeatherCondition.setTextColor(color);
        }
        if (null != mTxtDegree) {
            mTxtDegree.setTextColor(color);
        }
        if (null != mTxtClock) {
            mTxtClock.setTextColor(color);
        }
    }

    public void setColor(String color) {
        int colorParsed = Color.parseColor(color);
        if (null != mVLineVertical) {
            mVLineVertical.setBackgroundColor(colorParsed);
        }
        if (null != mVLineHorizontal) {
            mVLineHorizontal.setBackgroundColor(colorParsed);
        }
        if (null != mTxtLocation) {
            mTxtLocation.setTextColor(colorParsed);
        }
        if (null != mTxtWeatherCondition) {
            mTxtWeatherCondition.setTextColor(colorParsed);
        }
        if (null != mTxtDegree) {
            mTxtDegree.setTextColor(colorParsed);
        }
        if (null != mTxtClock) {
            mTxtClock.setTextColor(colorParsed);
        }
    }

    public void setLocationDrawable(Drawable icon) {
        if (null != mTxtLocation) {
            float density = getContext().getResources().getDisplayMetrics().density;
            int right = 8;
            float bottom = 11.33f;
            icon.setBounds(0, 0, (int) (right * density), (int) (bottom * density));
            mTxtLocation.setCompoundDrawables(null, null, icon, null);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null != mWeatherBiz) {
        	mWeatherBiz.loadWeather();
            registerWeatherReceiver();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mWeatherBiz) {
        	mWeatherBiz.clear();
            mWeatherBiz = null;
            unregisterWeatherReceiver();
        }
    }

    private void registerWeatherReceiver() {
        if (null == mWeatherReceiver) {
            mWeatherReceiver = new WeatherReceiver();
            mWeatherReceiver.setWeatherUpdateable(
                    new WeatherReceiver.WeatherUpdateable() {
                        @Override
                        public void updateWeather() {
                            Log.i(TAG, "updating weather");
                            if (null != mWeatherBiz) {
                                mWeatherBiz.loadWeather();
                            } else {
                                Log.w(TAG, "null == mWeatherBiz");
                            }
                        }
                    });
            IntentFilter intentFilter = new IntentFilter(
                    Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
            intentFilter.addAction(WeatherReceiver.REFRESH_WETAHTER);
            intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            getContext().registerReceiver(mWeatherReceiver, intentFilter);
        }
    }

    private void unregisterWeatherReceiver() {
        if (null != mWeatherReceiver) {
            getContext().unregisterReceiver(mWeatherReceiver);
        }
    }

    @Override
    public boolean onTemperatureUpdate(WeatherBean bean) {

        //这里可以拿到天气网站返回的bean类，所有的天气数据在这里获取
        if(null != bean){

            WeatherBean.CurrentObservationBean currentObservationBean = bean.getCurrent_observation();
            if(null != currentObservationBean){
                String dewpoint =currentObservationBean.getDewpoint_c();
                String humidity =currentObservationBean.getRelative_humidity();
                String uv =currentObservationBean.getUV();
                String windKph =currentObservationBean.getWind_kph();
                String temp_c = currentObservationBean.getTemp_c();
                String weather = currentObservationBean.getWeather();

                mWeather_status_one.setText(dewpoint + "°");
                mWeather_status_two.setText(humidity + "");
                mWeather_status_three.setText(uv);
                mWeather_status_four.setText(windKph + "kph");

                mTxtDegree.setText(temp_c + "°");
                mTxtWeatherCondition.setText(weather);

            }
        }
        return false;
    }


    @Override
    public void onLocation(IpToLocation.Bean bean) {
        //这里是请求获取IP的回调，可以获取IP地址及地理位置
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            disableWeather(true);
        } else {
            disableWeather(false);
        }
    }

    /**
     * 在WeatherView不可见的情况下，取消掉WeatherView的所有网络请求
     */
    private void disableWeather(boolean disable) {
        if (!disable) {
            mWeatherBiz = new WeatherBiz(mContext);
            mWeatherBiz.setLocationCallBack(this);
            mWeatherBiz.setWeatherCallBack(this);
        } else {
            mWeatherBiz = null;
        }
    }

    /**
     * 设置天气数据的字体颜色
     * @param color
     */
    public void setWeatherTextColor(String color) {
        try {
            int textColor = Color.parseColor(color);
            setWeatherTextColor(textColor);
        } catch (Exception e) {
            Log.d(TAG, "Set weather condition color failed! Reset to default!");
            setWeatherTextColor(getResources().getColor(R.color.color_text));
        }
    }

    /**
     * 设置天气数据的字体颜色
     * @param color
     */
    public void setWeatherTextColor(int color) {
        try {
            mTxtDegree.setTextColor(color);
            mOneTitle.setTextColor(color);
            mTwoTitle.setTextColor(color);
            mThreeTitle.setTextColor(color);
            mFourTitle.setTextColor(color);
            mWeather_status_one.setTextColor(color);
            mWeather_status_two.setTextColor(color);
            mWeather_status_three.setTextColor(color);
            mWeather_status_four.setTextColor(color);
        } catch (Exception e) {
            Log.d(TAG, "Set weather condition color failed! Reset to default!");
            setWeatherTextColor(getResources().getColor(R.color.color_text));
        }
    }

    public void setWeatherBackgroundColor(String color) {
        try {
            setWeatherBackgroundColor(Color.parseColor(color));
        } catch (Exception e) {
            Log.d(TAG, "Set weather background color failed! Reset to default!");
            mLeftRL.setBackgroundResource(R.drawable.selector_weather_bg_one);
            mRightRL.setBackgroundResource(R.drawable.selector_weather_bg_two);
        }
    }

    public void setWeatherBackgroundDrawable(int backgroundColor, int tempColor) {
    	float corner = getResources().getDimension(R.dimen.w_12);
        //设置总体背景
        GradientDrawable drawable1 = new GradientDrawable();
        float[] corners1 = {0, 0, corner, corner, corner, corner, 0, 0};
        drawable1.setCornerRadii(corners1);
        drawable1.setColor(backgroundColor);
        mRightRL.setBackground(drawable1);
        //设置温度的背景
        GradientDrawable drawable2 = new GradientDrawable();
        float[] corners2 = {corner, corner, 0, 0, 0, 0, corner, corner};
        drawable2.setCornerRadii(corners2);
        drawable2.setColor(tempColor);
        mLeftRL.setBackground(drawable2);
    }

    public void setWeatherBackgroundColor(int color) {
        mLeftRL.setBackgroundColor(color);
        mRightRL.setBackgroundColor(color);
    }
}
