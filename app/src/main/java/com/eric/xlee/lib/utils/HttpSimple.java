package com.eric.xlee.lib.utils;

import android.util.Log;


import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import nes.ltlib.utils.AppLog;

/**
 * Created by mygica-hsj on 13-8-27.
 */
public class HttpSimple {
    private static final String TAG = "Utils";

    public static String getContent(String baseUrl, NameValuePair[] pairs) {
        String content = null;
        AppLog.e("--------------getContent weather = " + baseUrl);
        HttpResult result = HttpClientHelper.get(baseUrl, pairs);
        if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
            content = result.getHtml();
        }
        return content;
    }

    // public static String getContent(String baseUrl, NameValuePair[] pairs,
    // String cookie) {
    // String content = null;
    // Header[] header = new Header[]{new BasicHeader(Constants.USER_COOKIE,
    // cookie)};
    // HttpResult result = HttpClientHelper.get(baseUrl, header, pairs);
    // if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
    // content = result.getHtml();
    // }
    // Log.d(TAG+ "HttpSimple content= " + content);
    // return content;
    // }

    public static String postContent(String baseUrl, NameValuePair[] pairs) {
        String content = null;
        HttpResult result = HttpClientHelper.post(baseUrl, pairs);
        if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
            content = result.getHtml();
        }
        AppLog.d(TAG+ "HttpSimple content= " + content);
        return content;
    }

    // public static String postContent(String baseUrl, NameValuePair[] pairs,
    // String cookie) {
    // String content = null;
    // Header[] header = new Header[]{new BasicHeader(Constants.USER_COOKIE,
    // cookie)};
    // HttpResult result = HttpClientHelper.post(baseUrl, header, pairs);
    // if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
    // content = result.getHtml();
    // }
    // Log.d(TAG+ "HttpSimple content= " + content);
    // return content;
    // }
}
