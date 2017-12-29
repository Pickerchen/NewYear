/**
 *Log.java[V 1.0.0]
 *classes : com.sen5.wifidvbt.utils.Log
 * Xlee Create at 17 Nov 2015 14:14:54
 */
package com.eric.xlee.lib.utils;

import android.text.TextUtils;
import android.util.Log;

import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.data.p2p.P2PClient;

import nes.ltlib.utils.AppLog;

/**
 * com.sen5.wifidvbt.utils.Log
 * 
 * @author Xlee <br/>
 *         create at 17 Nov 2015 14:14:54
 */

public class LogUtil {
    private static boolean sIsDebug = SecQreSettingApplication.DEBUG;

    public static void i(String paramString) {
//        if (sIsDebug) {
//            String[] paramStrings = getLogTextAndFileName(paramString);
//            Log.i(paramStrings[0], paramStrings[1]);
//        }

        AppLog.i(paramString);
    }

    public static void i(String paramString1, String paramString2) {
//        if (sIsDebug)
//            Log.i(paramString1, getLogText(paramString2));


        AppLog.i(paramString1+paramString2);
    }

    public static void isDebug(boolean paramBoolean) {
        sIsDebug = paramBoolean;
    }

    public static void d(String paramString) {
//        if (sIsDebug) {
//            String[] paramStrings = getLogTextAndFileName(paramString);
//            Log.d(paramStrings[0], paramStrings[1]);
//        }

        AppLog.d(paramString);

    }

    public static void d(String paramString1, String paramString2) {
//        if (sIsDebug)
//            Log.d(paramString1, getLogText(paramString2));

        AppLog.d(paramString1+paramString2);
    }

    public static void e(String paramString) {
//        if (sIsDebug) {
//            String[] paramStrings = getLogTextAndFileName(paramString);
//            Log.e(paramStrings[0], paramStrings[1]);
//        }

        AppLog.e(paramString);
    }

    public static void e(String paramString1, String paramString2) {
//        if (sIsDebug)
//            Log.e(paramString1, getLogText(paramString2));

        AppLog.e(paramString1+paramString2);
    }

    public static void w(String paramString) {
        if (sIsDebug) {
            String[] paramStrings = getLogTextAndFileName(paramString);
            Log.w(paramStrings[0], paramStrings[1]);
        }
    }

    public static void w(String paramString1, String paramString2) {
//        if (sIsDebug)
//            Log.w(paramString1, getLogText(paramString2));


        AppLog.w(paramString1+paramString2);
    }

    public static void v(String paramString) {
//        if (sIsDebug) {
//            String[] paramStrings = getLogTextAndFileName(paramString);
//            Log.v(paramStrings[0], paramStrings[1]);
//        }

        AppLog.v(paramString);
    }

    public static void v(String paramString1, String paramString2) {
//        if (sIsDebug) {
//            Log.v(paramString1, getLogText(paramString2));
//        }


        AppLog.v(paramString1+paramString2);
    }




    private static String getLogText(String paramString) {
        StackTraceElement localStackTraceElement = new java.lang.Throwable().fillInStackTrace().getStackTrace()[2];
        return new StringBuilder().append(" [").append(localStackTraceElement.getFileName()).append(".")
                .append(localStackTraceElement.getMethodName()).append("#")
                .append(localStackTraceElement.getLineNumber()).append("]").append(paramString).toString();
    }

    private static String[] getLogTextAndFileName(String paramString) {
        StackTraceElement localStackTraceElement = new java.lang.Throwable().fillInStackTrace().getStackTrace()[2];
        String fileName = localStackTraceElement.getFileName();
        if (TextUtils.isEmpty(fileName) || (null != fileName && !fileName.contains("."))) {
            return new String[] {
                    "",
                    new StringBuilder().append(" [").append(localStackTraceElement.getFileName()).append(".")
                            .append(localStackTraceElement.getMethodName()).append("#")
                            .append(localStackTraceElement.getLineNumber()).append("]").append(paramString).toString() };
        } else {
            return new String[] {
                    localStackTraceElement.getFileName().split("\\.")[1],
                    new StringBuilder().append(" [").append(localStackTraceElement.getFileName()).append(".")
                            .append(localStackTraceElement.getMethodName()).append("#")
                            .append(localStackTraceElement.getLineNumber()).append("]").append(paramString).toString() };
        }
    }
}