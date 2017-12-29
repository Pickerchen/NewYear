package com.eric.xlee.lib.utils;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class HttpUtils {
    // /**
    // * 抓取网页源码
    // *
    // */
    //
    // public static String getContent(String url, Header[] headers,
    // NameValuePair[] pairs, String mod) {
    // String content = null;
    // HttpResult result = null;
    // if (mod.equals("GET")) {
    // result = HttpClientHelper.get(url, headers, pairs);
    // } else {
    // result = HttpClientHelper.post(url, headers, pairs);
    // }
    // if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
    // content = result.getHtml();
    // }
    // return content;
    // }

    // public static String getResultRedirecUrl(String url, Header[] headers,
    // NameValuePair[] pairs) {
    // HttpURLConnection conn = null;
    // if (!TextUtils.isEmpty(url)) {
    // Header[] extra = new Header[] { new BasicHeader("User-Agent",
    // MyApp.User_Agent) };
    //
    // if (headers == null) {
    // headers = extra;
    // } else {
    // Header[] temp = new Header[headers.length + extra.length];
    // System.arraycopy(headers, 0, temp, 0, headers.length);
    // System.arraycopy(extra, 0, temp, extra.length, extra.length);
    // headers = temp;
    // }
    // }
    // try {
    // conn = (HttpURLConnection) new URL(url).openConnection();
    // if (headers != null && headers.length > 0) {
    // for (Header header : headers) {
    // conn.setRequestProperty(header.getName(), header.getValue());
    // }
    // }
    // return conn.getURL().toString();
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // } finally {
    // if (conn != null)
    // conn.disconnect();
    // }
    // return url;
    // }

    public static byte[] getBinary(String url, Header[] headers,
            NameValuePair[] pairs) {
        byte[] binary = null;
        HttpResult result = HttpClientHelper.get(url, headers, pairs, null, 0);
        if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
            binary = result.getResponse();
        }
        return binary;
    }

    // ------------------------------------------------------------------------------------------
    // 网络连接判断
    // 判断是否有网络
    public static boolean isNetworkAvailable(Context context) {
        return NetWorkHelper.isNetworkAvailable(context);
    }

    // 判断以太网络是否可用
    public static boolean isEthernetDataEnable(Context context) {
        try {
            return NetWorkHelper.isEthernetDataEnable(context);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 判断wifi网络是否可用
    public static boolean isWifiDataEnable(Context context) {
        try {
            return NetWorkHelper.isWifiDataEnable(context);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String get_user_mac(Context context) {
        String u_mac = fetch_mac_eth();
        // Log.i("fetch_mac_eth==" + u_mac);
        if (u_mac == null || u_mac.length() != 17) {
            u_mac = getLocalMacAddress(context);
            // SecqLogUtil.i("loacal=="+u_mac);
        }
        if (u_mac == null || u_mac.length() != 17) {
            u_mac = fetch_mac_wlan();
            // SecqLogUtil.i("fetch_mac_wlan=="+u_mac);
        }
        if (u_mac != null) {
            return u_mac.trim();
        }
        return u_mac;
    }

    public static String fetch_mac_wlan() {
        String result = null;
        CMDExecute cmdexe = new CMDExecute();
        try {
            String[] args = { "/system/bin/cat", "/sys/class/net/wlan0/address" };
            result = cmdexe.run(args, "system/bin/");
            if (result != null && result.length() > 0) {
                result = result.substring(0, result.indexOf("\n"));
            }
            if (result != null && result.length() > 28) {
                result = result.substring(0, 28);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String fetch_mac_eth() {
        String result = null;
        CMDExecute cmdexe = new CMDExecute();
        try {
            String[] args = { "/system/bin/cat", "/sys/class/net/eth0/address" };
            result = cmdexe.run(args, "system/bin/");
            if (result != null && result.length() > 0) {
                result = result.substring(0, result.indexOf("\n"));
            }
            if (result != null && result.length() > 27) {
                result = result.substring(0, 27);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 获取网络无线物理地址
     * 
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }
}
