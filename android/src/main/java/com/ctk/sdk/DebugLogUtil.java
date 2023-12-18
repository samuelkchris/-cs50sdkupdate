package com.ctk.sdk;


//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import android.text.TextUtils;
import android.util.Log;

public class DebugLogUtil {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static int LEVEL = 1;

    public DebugLogUtil() {
    }

    public static void setLevel(int Level) {
        LEVEL = Level;
    }

    public static void v(String TAG, String msg) {
        if (LEVEL <= 1 && !TextUtils.isEmpty(msg)) {
            MyLog(1, TAG, msg);
        }

    }

    public static void d(String TAG, String msg) {
        if (LEVEL <= 2 && !TextUtils.isEmpty(msg)) {
            MyLog(2, TAG, msg);
        }

    }

    public static void i(String TAG, String msg) {
        if (LEVEL <= 3 && !TextUtils.isEmpty(msg)) {
            MyLog(3, TAG, msg);
        }

    }

    public static void w(String TAG, String msg) {
        if (LEVEL <= 4 && !TextUtils.isEmpty(msg)) {
            MyLog(4, TAG, msg);
        }

    }

    public static void e(String TAG, String msg) {
        if (LEVEL <= 5 && !TextUtils.isEmpty(msg)) {
            MyLog(5, TAG, msg);
        }

    }

    private static void MyLog(int type, String TAG, String msg) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 4;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ");
        stringBuilder.append(msg);
        String logStr = stringBuilder.toString();
        switch(type) {
            case 1:
                Log.v(TAG, logStr);
                break;
            case 2:
                Log.d(TAG, logStr);
                break;
            case 3:
                Log.i(TAG, logStr);
                break;
            case 4:
                Log.w(TAG, logStr);
                break;
            case 5:
                Log.e(TAG, logStr);
        }

    }
}
