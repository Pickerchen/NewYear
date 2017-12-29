/**
 *IpToLocation.java[V 1.0.0]
 *classes : com.eric.sen5.location.IpToLocation
 * Xlee Create at 2016-4-18 上午11:18:02
 */
package com.sen5.launchertools.weather;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * com.eric.sen5.location.IpToLocation
 * 
 * @author Xlee <br/>
 *         create at 2016-4-18 上午11:18:02
 */
public class IpToLocation {
    private static final String TAG = IpToLocation.class.getSimpleName();

    private static final String IP_TO_LOCATION_URL = "http://ip-api.com/json";
    private CallBack mCallBack;
    private boolean mIsLoading = false;

    public void getLocation() {
        if (mIsLoading) {
            Log.w(TAG, "MultiCall getLocation()");
            return;
        }
        new HttpWorkTask<String>(new HttpWorkTask.ParseCallBack<String>() {
            @Override
            public String onParse() {
                mIsLoading = true;
                return HttpSimple.getContent(IP_TO_LOCATION_URL, null);
            }
        }, new HttpWorkTask.PostCallBack<String>() {
            @Override
            public void onPost(String netString) {
                mIsLoading = false;
                if (!TextUtils.isEmpty(netString)) {
                    Log.i(TAG, "content == " + netString);
                    if (null != mCallBack) {
                        Gson gson = new Gson();
                        Bean bean = null;
                        try {
                            bean = gson.fromJson(netString, Bean.class);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if(null != bean){
                            mCallBack.onLocation(bean);
                        }
                    }
                } else {
                    Log.w(TAG, "onFailure()");
                }
            }
        }).execute();
    }

    public boolean isLoadingData() {
        return mIsLoading;
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {
        void onLocation(Bean bean);
    }

    public static class Bean {
        /**
         * as : AS17623 China Unicom Shenzen network city : Shenzhen country :
         * China countryCode : CN isp : China Unicom Guangdong lat : 22.5333 lon
         * : 114.1333 org : China Unicom Shenzen network query : 58.251.146.229
         * region : 44 regionName : Guangdong status : success timezone :
         * Asia/Shanghai zip :
         */

        private String as;
        private String city;
        private String country;
        private String countryCode;
        private String isp;
        private double lat;
        private double lon;
        private String org;
        private String query;
        private String region;
        private String regionName;
        private String status;
        private String timezone;
        private String zip;

        public String getAs() {
            return as;
        }

        public void setAs(String as) {
            this.as = as;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getIsp() {
            return isp;
        }

        public void setIsp(String isp) {
            this.isp = isp;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public String getOrg() {
            return org;
        }

        public void setOrg(String org) {
            this.org = org;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        @Override
        public String toString() {
            return "Bean{" + "as='" + as + '\'' + ", city='" + city + '\'' + ", country='" + country + '\''
                    + ", countryCode='" + countryCode + '\'' + ", isp='" + isp + '\'' + ", lat=" + lat + ", lon=" + lon
                    + ", org='" + org + '\'' + ", query='" + query + '\'' + ", region='" + region + '\''
                    + ", regionName='" + regionName + '\'' + ", status='" + status + '\'' + ", timezone='" + timezone
                    + '\'' + ", zip='" + zip + '\'' + '}';
        }
    }
}
