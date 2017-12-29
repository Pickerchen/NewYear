/**
 *WeatherBiz.java[V 1.0.0]
 *classes : com.eric.sen5.weater.WeatherBiz
 * Xlee Create at 2016-4-18 下午5:45:17
 */
package com.eric.sen5.weater;

import com.eric.sen5.location.IpToLocation;
import com.eric.sen5.location.IpToLocation.Bean;
import com.eric.sen5.weater.WeatherBean.QueryBean.ResultsBean.ChannelBean.ItemBean;
import com.eric.sen5.weater.WeatherBean.QueryBean.ResultsBean.ChannelBean.ItemBean.ForecastBean;
import com.eric.sen5.weater.WeatherBean.QueryBean.ResultsBean.ChannelBean.LocationBean;
import com.eric.xlee.lib.utils.LogUtil;

import java.util.List;

import nes.ltlib.utils.AppLog;

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

    public WeatherBiz() {
        mIpToLocation = new IpToLocation();
        mIpToLocation.setCallBack(this);
        mWeaterUtils = new WeaterUtils();
        mWeaterUtils.setCallBack(this);
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
        if (null != bean && mWeaterUtils != null) {
            mWeaterUtils.getTemperature(bean);
            if (null != mLocationCallBack) {
                mLocationCallBack.onLocation(bean);
            }
        }
    }

    @Override
    public void onTemperatureUpdate(WeatherBean bean) {
        if (null != mCallBack) {
            mCallBack.onTemperatureUpdate(bean);
        } else {
            if (null != bean) {
                StringBuilder sb = new StringBuilder();
                LocationBean location = bean.getQuery().getResults().getChannel().getLocation();
                ItemBean itemBean = bean.getQuery().getResults().getChannel().getItem();
                // Weather condition image
                // String imageUrl =
                // WeaterUtils.getImageUrl(itemBean.getDescription());
                sb.append(location.toString());
                List<ForecastBean> list = itemBean.getForecast();
                for (int i = 0; i < list.size(); i++) {
                    ForecastBean forecastBean = list.get(i);
                    sb.append("\n");
                    sb.append("day: ").append(forecastBean.getDate());
                    sb.append(" low: ").append(WeaterUtils.fahrenheitToCentigrade(forecastBean.getLow())).append("℃ ");
                    sb.append(" hight: ").append(WeaterUtils.fahrenheitToCentigrade(forecastBean.getHigh()))
                            .append("℃ ");
                }
               AppLog.i(TAG+ sb.toString());
            }
        }
    }
}
