package com.example.chen.pkuapp;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

/**
 * Created by chen on 17/10/3.
 */
public class MyApplication extends Application {

    private final long appStartTime;
    private static Application instance = null;

    public MyApplication() {
        super();
        instance = this;
        appStartTime = System.currentTimeMillis();
    }

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        throw new NullPointerException("test for holy crash");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e("Application", ex.getMessage(), ex);
                try {
                    // 防止循环crash
                    SpInitApp.CrashInfo.recordCrashStates(ex);
                } catch (Throwable e) {

                } finally {
                     Log.i("Application", "uncaughtException, kill myself");
                    try {
                        Process.killProcess(Process.myPid());
                    } catch (Throwable e3) {
                    }
                    try {
                        System.exit(0);
                    } catch (Throwable e4) {

                    }
                }
            }
        });
    }
}
