/**
 *WeaterUtils.java[V 1.0.0]
 *classes : com.eric.sen5.weater.WeaterUtils
 * Xlee Create at 2016-4-18 下午3:08:17
 */
package com.sen5.launchertools.weather;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sen5.launchertools.R;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.eric.sen5.weater.WeaterUtils
 * 
 * @author Xlee <br/>
 *         create at 2016-4-18 下午3:08:17
 */
public class WeaterUtils {
    private static final String TAG = WeaterUtils.class.getSimpleName();
    private Gson mGson;
    private CallBack mCallBack;
    private boolean mIsLoading = false;
    private Context mContext = null;
    
    private int mKeyNumber = 0;

    public WeaterUtils(Context context) {
        mGson = new Gson();
        mContext = context;
        mLanguageCode = mContext.getResources().getStringArray(R.array.local_languages);
        mLanguageWeatherCode = mContext.getResources().getStringArray(R.array.weather_languages);
    }

    public void getTemperature(final IpToLocation.Bean bean) {
        if (mIsLoading) {
            Log.w(TAG, "MultiCall getTemperature()");
            return;
        }
        requestWeatherCondition(bean);
    }
    
    private void requestWeatherCondition(final IpToLocation.Bean locationBean) {
    	new HttpWorkTask<WeatherBean>(new HttpWorkTask.ParseCallBack<WeatherBean>() {
            @Override
            public WeatherBean onParse() {
                mIsLoading = true;
                String netString = HttpSimple.getContent(getTemperatureUrl(locationBean, mWeatherKey[mKeyNumber]), null);
                WeatherBean weatherBean1 = null;
                if (!TextUtils.isEmpty(netString)) {
                    Log.i(TAG, "content == " + netString);
                    try {
                        weatherBean1 = mGson.fromJson(netString, WeatherBean.class);
                    } catch (Exception e) {
                    	if (mKeyNumber <= mWeatherKey.length -2) {
	                    	Log.e(TAG, "onParse::::Request weather condition failed by key: " + mWeatherKey[mKeyNumber] + 
	                    			"\n Trying to request weather by key: " + mWeatherKey[mKeyNumber + 1]);
	                    	mKeyNumber++;
                    		requestWeatherCondition(locationBean);
                    	} else {
                    		Log.e(TAG, "All weather api key is not avalible!");
                    		e.printStackTrace();
                    	}
                    }
                }
                Log.d(TAG, "onParse---weatherBean1 = " + weatherBean1);
                return weatherBean1;
            }
        }, new HttpWorkTask.PostCallBack<WeatherBean>() {
            @Override
            public void onPost(WeatherBean weatherBean2) {
                Log.i(TAG, "return bean");
                mIsLoading = false;
                if (null != weatherBean2 && mCallBack.onTemperatureUpdate(weatherBean2)) {
                    Log.d(TAG, "------update weather condition successfully----------");
                } else {
                	if (mKeyNumber <= mWeatherKey.length -2) {
                    	Log.e(TAG, "onPost::::Request weather condition failed by key: " + mWeatherKey[mKeyNumber] + 
                    			"\n Trying to request weather by key: " + mWeatherKey[mKeyNumber + 1]);
                    	mKeyNumber++;
                		requestWeatherCondition(locationBean);
                	} else {
                		Log.e(TAG, "----------------------onFailure()--------------------------------");
                	}
                }
            }
        }).execute();
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    private String getTemperatureUrl(IpToLocation.Bean bean, String weatherKey) {
        return getUrlByLocation(getLangCode(), bean.getQuery(), weatherKey);
    }

    private String getUrlByLocation(String langCode, String ipAddress, String weatherKey) {
        if (null == langCode) {
        	langCode = "";
        }
        if (null == ipAddress) {
        	ipAddress = "";
        }
        langCode = Uri.encode(langCode);
        ipAddress = Uri.encode(ipAddress);
        StringBuilder sb = new StringBuilder();
        sb.append("http://api.wunderground.com/api/");
        sb.append(weatherKey);
        sb.append("/conditions/lang:");
        sb.append(langCode);
        sb.append("/q/autoip.json?geo_ip=");
        sb.append(ipAddress);
        sb.append(".json");
        return sb.toString();
    }

    public interface CallBack {
        boolean onTemperatureUpdate(WeatherBean bean);
    }

    public static String fahrenheitToCentigrade(String strDegree) {
        String result = "";
        try {
            double degree = Double.parseDouble(strDegree);
            result = String.format("%.0f", fahrenheitToCentigrade(degree));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double fahrenheitToCentigrade(double degree) {
        return Math.round((((double) 5 / 9) * (degree - 32)));
    }

    public static String getImageUrl(String description) {
        description = getCDATAString(description);
        if (!TextUtils.isEmpty(description)) {
            int start = description.indexOf("<img src=\"");
            start += "<img src=\"".length();
            int end = description.indexOf("\"/>");
            if (end > start && 0 <= start && 0 <= end) {
                description = description.substring(start, end);
                Log.i(TAG, "result == " + description);
            }
        }
        return description;
    }

    private static String getCDATAString(String description) {
        if (TextUtils.isEmpty(description)) {
            return "";
        }
        Matcher m = Pattern.compile("<!\\[CDATA\\[([^\\]]+)\\]").matcher(description);
        if (m.find()) {
            String result = m.group(1);
            return result;
        }
        return "";
    }
    
    private String getLangCode(){
    	Locale locale = mContext.getResources().getConfiguration().locale;
    	String language = locale.getLanguage();
    	for(int i = 0; i < mLanguageCode.length; i++){
    		if (mLanguageCode[i].equals(language)) {
				return mLanguageWeatherCode[i];
			}
    	}
		return "EN";
    }
    
    private String[] mLanguageWeatherCode, mLanguageCode;
    
    /**
     * http://api.wunderground.com/ 
     *  Account: enterprise@sen5.COM
     * Password: ep3Qqa4c
     *       by JESSE
     * API KEYS
     */
    private final String[] mWeatherKey = {
    		"8c4a5191e02079bf",
    		"8e54eb7f21681824",
    		"ed41bcf3fd6bd4d6",
    		"3f3c0445d3a32ed6",
    		"7fe1172b8f52c922",
    		"d12a24b4ba6677f1",
    		"8988c4e21d3ea26e",
    		"1cf02b07d601a9d8",
    		"19b24d63a6d41062",
    		"1020f09db6c1b142"
    };
}
