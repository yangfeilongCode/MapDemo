package com.feicuiedu.treasure.commons;

/**
 * Log工具类
 */
@SuppressWarnings("unused")
public final class LogUtils {

    private static final String TAG = "treasure_hunter"; //Log标记：宝藏搜索

    private static final String TAG_TRACE = "method_trace"; //Log标记踪迹：方法踪迹

    /**
     * 堆栈跟踪调试
     */
    private static final class StackTraceDebug extends RuntimeException {
        final static private long serialVersionUID = 27058374L; //串行版本唯一标示符
    }

    /**
     * The debug flag is cached here so that we don't need to access the settings every time we have to evaluate it.
     * 这里的调试标记缓存,这样我们不需要访问设置每次我们必须评估它。
     */
    private static boolean isDebug = true; //是否调试

    private static final boolean logTrace = true; //log跟踪

    private LogUtils() {
        // utility class
    }

    /**
     * 跟踪方法
     * @param object
     */
    public static void trace(Object object) {
        if (logTrace) { //Log跟踪
            StackTraceElement[] traces = Thread.currentThread().getStackTrace();
            StackTraceElement trace = traces[3]; //？？？？为何是3
            android.util.Log.d(TAG_TRACE, addThreadInfo(object.getClass().getSimpleName() + " : " + trace.getMethodName()));
        }
    }

//是否调试
    public static boolean isDebug() {  return isDebug;  }
 //是否跟踪
    public static boolean isLogTrace() {
        return logTrace;
    }

    /**
     * Save a copy of the debug flag from the settings for performance reasons.
     * 保存一个副本的调试标记设置性能的原因。
     * 设置调试
     */
    public static void setDebug(final boolean isDebug) {
        LogUtils.isDebug = isDebug; //把调试状态赋给Log工具类
    }

//添加线程信息
    private static String addThreadInfo(final String msg) {
        final String threadName = Thread.currentThread().getName();//获取当前线程名
        final String shortName = threadName.startsWith("OkHttp") ? "OkHttp" : threadName; //从。。。开始
        return "[" + shortName + "] " + msg;
    }

    public static void v(final String msg) {
        if (isDebug) { //调试状态
            android.util.Log.v(TAG, addThreadInfo(msg));
        }
    }

    public static void v(final String msg, final Throwable t) {
        if (isDebug) {
            android.util.Log.v(TAG, addThreadInfo(msg), t);
        }
    }

    public static void d(final String msg) {
        if (isDebug) {
            android.util.Log.d(TAG, addThreadInfo(msg));
        }
    }

    public static void d(final String msg, final Throwable t) {
        if (isDebug) {
            android.util.Log.d(TAG, addThreadInfo(msg), t);
        }
    }

    public static void i(final String msg) {
        if (isDebug) {
            android.util.Log.i(TAG, addThreadInfo(msg));
        }
    }

    public static void i(final String msg, final Throwable t) {
        if (isDebug) {
            android.util.Log.i(TAG, addThreadInfo(msg), t);
        }
    }

    public static void w(final String msg) {
        android.util.Log.w(TAG, addThreadInfo(msg));
    }

    public static void w(final String msg, final Throwable t) {
        android.util.Log.w(TAG, addThreadInfo(msg), t);
    }

    public static void e(final String msg) {
        android.util.Log.e(TAG, addThreadInfo(msg));
    }

    public static void e(final String msg, final Throwable t) {
        android.util.Log.e(TAG, addThreadInfo(msg), t);
    }

    /**
     * Record a debug message with the actual stack trace.
     * 记录与实际的堆栈跟踪调试消息。
     *
     * @param msg the debug message
     */
    public static void logStackTrace(final String msg) {
        try {
            throw new StackTraceDebug();
        } catch (final StackTraceDebug dbg) {
            d(msg, dbg);
        }
    }
}
