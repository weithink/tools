package cc.weithink.sdk.utils;

import android.util.Log;

/**
 * Created by weithink on 17/12/6.
 * 日志工具类，输出日志使用该类进行输出
 */

public class LogUtils {
    private static final String TAG = "LogUtils";
    private static boolean DEBUG = true;

    /**
     * 初始化logUtil 类
     * 如果不进行初始化操作，那么就是默认的debug模式
     * 希望进行初始化操作
     * @param printLog
     */
    public static void setPrintLog(boolean printLog) {
        Log.i(TAG, "=============DEBUG =" + printLog);
        DEBUG = printLog;
    }

    public static void v(String tag, String message) {
        if(DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if(DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if(DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if(DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if(DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Exception e) {
        if(DEBUG) {
            Log.e(tag, message, e);
        }
    }
    public static boolean isDebug(){
        return DEBUG;
    }
}
