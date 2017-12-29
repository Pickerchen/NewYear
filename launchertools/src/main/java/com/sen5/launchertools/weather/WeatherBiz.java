/**
 *WeatherBiz.java[V 1.0.0]
 *classes : com.eric.sen5.weater.WeatherBiz
 * Xlee Create at 2016-4-18 下午5:45:17
 */
package com.sen5.launchertools.weather;

import com.sen5.launchertools.weather.IpToLocation.Bean;

import android.content.Context;
import android.util.Log;

/**
 * com.eric.sen5.weater.WeatherBiz
 * 
 * <uses-permission android:name="android.permission.INTERNET" />
 * 
 * @author Xlee <br/>
 *         create at 2016-4-18 下午5:45:17
 */
public class WeatherBiz implements IpToLocation.CallBack, WeaterUtils.CallBack {
    private static final String TAG = WeatherBiz.class.getSimpleName();

    private IpToLocation mIpToLocation;
    private WeaterUtils mWeaterUtils;
    private WeaterUtils.CallBack mCallBack;
    private IpToLocation.CallBack mLocationCallBack;
    private Context mContext;

    public WeatherBiz(Context context) {
        mIpToLocation = new IpToLocation();
        mIpToLocation.setCallBack(this);
        mWeaterUtils = new WeaterUtils(context);
        mWeaterUtils.setCallBack(this);
        mContext = context;
    }

    /**
     * setCallBack() to dosome while loadWeater successfully;
     */
    public void loadWeather() {
        mIpToLocation.getLocation();
    }

    /**
     * if getWeather successfully ,we can do something in callBack
     * 
     * @param callBack
     *            WeaterUtils.CallBack
     */
    public void setWeatherCallBack(WeaterUtils.CallBack callBack) {
        mCallBack = callBack;
    }

    /**
     * if getLocation successfully ,we can do something in callBack
     * 
     * @param callBack
     *            IpToLocation.CallBack
     */
    public void setLocationCallBack(IpToLocation.CallBack callBack) {
        mLocationCallBack = callBack;
    }

    public void clear() {
        if (null != mIpToLocation) {
            mIpToLocation = null;
        }
        if (null != mWeaterUtils) {
            mWeaterUtils = null;
        }
        if (null != mCallBack) {
            mCallBack = null;
        }
        if (null != mLocationCallBack) {
            mLocationCallBack = null;
        }
    }

    @Override
    public void onLocation(Bean bean) {
        if (null != bean) {
            mWeaterUtils.getTemperature(bean);
            if (null != mLocationCallBack) {
                mLocationCallBack.onLocation(bean);
            }
        }
    }

    @Override
    public boolean onTemperatureUpdate(WeatherBean bean) {
        if (null != mCallBack) {
            return mCallBack.onTemperatureUpdate(bean);
        } else {
            if (null != bean) {
//                StringBuilder sb = new StringBuilder();
//                LocationBean location = bean.getQuery().getResults().getChannel().getLocation();
//                ItemBean itemBean = bean.getQuery().getResults().getChannel().getItem();
//                // Weather condition image
//                // String imageUrl =
//                // WeaterUtils.getImageUrl(itemBean.getDescription());
//                sb.append(location.toString());
//                List<ForecastBean> list = itemBean.getForecast();
//                for (int i = 0; i < list.size(); i++) {
//                    ForecastBean forecastBean = list.get(i);
//                    sb.append("\n");
//                    sb.append("day: ").append(forecastBean.getDate());
//                    sb.append(" low: ").append(WeaterUtils.fahrenheitToCentigrade(forecastBean.getLow())).append("℃ ");
//                    sb.append(" hight: ").append(WeaterUtils.fahrenheitToCentigrade(forecastBean.getHigh()))
//                            .append("℃ ");
//                }
                Log.i(TAG, bean.toString());
            }
        }
        return false;
    }
}
