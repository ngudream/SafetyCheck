package com.safety.free.utils;

import android.util.Log;

public class SCLog {
    
    public static final String TAG = "SafetyCommonLibrary";
    private static boolean bInit = false;
    private static boolean bDebug = false;


    public SCLog() {
    }

    public static void d(String tag, String value) {
        if (isDebugging())
            Log.d(tag, value);
    }

    public static void e(String tag, String value) {
        if (isDebugging())
            Log.e(tag, value);
    }

    public static void i(String tag, String value) {
        if (isDebugging())
            Log.i(tag, value);
    }

    public static void v(String tag, String value) {
        if (isDebugging())
            Log.v(tag, value);
    }

    public static void w(String tag, String value) {
        if (isDebugging())
            Log.w(tag, value);
    }

    public static boolean isDebugging() {
        return bDebug;
    }

    public static void setDebug(boolean isDebug) {
        bDebug = isDebug;
    }
}