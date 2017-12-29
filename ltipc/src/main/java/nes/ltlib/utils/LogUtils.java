package nes.ltlib.utils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;


public class LogUtils {
    private static final String TAG = "Ipcamera";
    private static final boolean mDebug = true;

    /**
     * initialize the logger.
     */
    public static void init() {
//        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
//                .tag(TAG)
//                .build();
//
//        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));

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
        AppLog.v(msg);
    }

    /**
     * log.i
     *
     * @param msg
     */
    public static void i(String msg) {
        AppLog.i(msg);
    }

    /**
     * log.d
     *
     * @param msg
     */
    public static void d(String tag, String msg) {
        AppLog.d(msg);
    }

    /**
     * log.w
     *
     * @param msg
     */
    public static void w(String msg) {
        AppLog.w(msg);
    }

    /**
     * log.e
     *
     * @param msg
     */
    public static void e(String tag, String msg) {
        AppLog.e(msg);
    }

    /**
     * log.wtf ASSERT级别
     *
     * @param msg
     */
    public static void wtf(String msg) {
        AppLog.wtf(msg);
    }

    /**
     * log.json
     *
     * @param jsonStr
     */
    public static void json(String jsonStr) {
        AppLog.json(jsonStr);
    }
}
