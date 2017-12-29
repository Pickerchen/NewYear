package nes.ltlib.utils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by wanglin on 2016/9/18.
 */
public class AppLog {

    private static final String TAG = "secQre_Settings";
    private static final boolean mDebug = true;

    /**
     * initialize the logger.
     */
    public static void init() {

        Logger.addLogAdapter(new AndroidLogAdapter());

//        Logger.init(TAG);
//                .methodCount(1) //设置显示调用层级层数 default 2
//                .methodOffset(1) //设置调用方法的偏移 default 0
//                .hideThreadInfo() //隐藏线程信息 default shown
//                .logLevel(LogLevel.FULL) //LogLevel.FULL打印所有日志，LogLevel.NONE不打印日志;default LogLevel.FULL

    }

    /**
     * log.v
     *
     * @param msg
     */
    public static void v(String msg) {
        if (mDebug) {
            Logger.v(msg);
        }
    }

    /**
     * log.i
     *
     * @param msg
     */
    public static void i(String msg) {
        if (mDebug) {
            Logger.i(msg);
        }
    }

    /**
     * log.d
     *
     * @param msg
     */
    public static void d(String msg) {
        if (mDebug) {
            Logger.d(msg);
        }
    }

    /**
     * log.d
     *
     * @param obj
     */
    public static void d(Object obj) {
        if (mDebug) {
            Logger.d(obj);
        }
    }

    /**
     * log.w
     *
     * @param msg
     */
    public static void w(String msg) {
        if (mDebug) {
            Logger.w(msg);
        }
    }

    /**
     * log.e
     *
     * @param msg
     */
    public static void e(String msg) {
        if (mDebug) {
            Logger.e(msg);
        }
    }

    public static void e(Throwable e) {
        if (mDebug) {
            Logger.e(e, "");
        }
    }

    /**
     * log.wtf ASSERT级别
     *
     * @param msg
     */
    public static void wtf(String msg) {
        if (mDebug) {
            Logger.wtf(msg);
        }
    }

    /**
     * log.jsonad
     *
     * @param jsonStr
     */
    public static void json(String jsonStr) {
        if (mDebug) {
            Logger.json(jsonStr);
        }
    }

    /**
     * log.xml
     *
     * @param xmlStr
     */
    public static void xml(String xmlStr) {
        if (mDebug) {
            Logger.xml(xmlStr);
        }
    }
}
