package com.ipcamera.main.utils;


//import android.os.SystemProperties;

import android.util.Log;


public class DLog {
    /*log开关*/
    private final static boolean logFlag = true;
    //	private final static boolean logFlag = SystemProperties.getBoolean("persist.sys.sen5.debug", true);
    /*默认tag*/
    public final static String TAG = "[DVBPlayer]";
    /*Log 默认输出等级*/
    public final static int logLevel = Log.VERBOSE;
    /*实例*/
    private static String ClassName = DLog.class.getName();
    private static String ThreadName = Thread.class.getName();
    /*输出的log附加info，分别为TAG 和 text */
    private static String[] logs = new String[2];
    /*默认log 附加info*/
    private static final String[] NONE = new String[]{TAG, ""};


    /**
     * @return 创建log附加info
     */
    private static String[] getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (null == sts) {
            return NONE;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(ThreadName)) {
                continue;
            }
            if (st.getClassName().equals(ClassName)) {
                continue;
            }

            logs[0] = st.getFileName();
            if (null == logs[0]) {
                logs[0] = TAG;
            }
            logs[1] = "[line:" + st.getLineNumber() + " on:" + st.getMethodName() + "]";
            return logs;
        }
        return NONE;
    }

    private static boolean checkLevel() {

        //输出控制
        if (logFlag) {
            //输出等级
            if (logLevel <= Log.VERBOSE) {
                return true;
            }
        }
        return false;
    }

    public static void v(String str) {

        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.v(log[0], log[1] + " - " + str);
        }

    }

    public static void v(String tag, String str) {
        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.v(tag, log[1] + " - " + str);
        }

    }

    public static void d(String str) {

        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.d(log[0], log[1] + " - " + str);
        }

    }

    public static void d(String tag, String str) {
        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.d(tag, log[1] + " - " + str);
        }

    }

    public static void i(String str) {

        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.i(log[0], log[1] + " - " + str);
        }

    }

    public static void i(String tag, String str) {
        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.i(tag, log[1] + " - " + str);
        }

    }

    public static void w(String str) {

        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.w(log[0], log[1] + " - " + str);
        }

    }

    public static void w(String tag, String str) {
        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.w(tag, log[1] + " - " + str);
        }

    }

    public static void e(String str) {

        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.e(log[0], log[1] + " - " + str);
        }

    }

    public static void e(String tag, String str) {
        if (checkLevel()) {
            String log[] = getFunctionName();
            Log.e(tag, log[1] + " - " + str);
        }

    }

}
