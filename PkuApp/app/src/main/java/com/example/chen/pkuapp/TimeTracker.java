package com.example.chen.pkuapp;

import android.os.Looper;
import android.util.Log;

/**
 * Created by jianhu on 2015/12/14.
 */
public class TimeTracker {

    private static final String TAG = "TimeTracker";
    public static final String NEWS_DETAIL = "News_Detail";
    public static final String ANSWER_DETAIL_QA = "Answer_Detail_QA";
    public static final String BOOT_START_UP = "bootstartup" + android.os.Process.myPid();

    private final static boolean enable = false;
    private static long firstTrackTime = 0L;
    private static long lastTrackTime = 0L;

    private static final class ClassHolder {
        private static final TimeTracker instance = new TimeTracker();
    }

    public static TimeTracker getInstance() {
        return ClassHolder.instance;
    }

    public boolean isEnable() {
        return enable;
    }

    public void track(String module, String where, boolean enableTrace) {
        if (!enable) {
            return;
        }

        String log = TAG + " " + module + " " + where;
//        if (enableTrace) {
//            String trace = " mainThreadId:" + Looper.getMainLooper().getThread().getId()
//                    + " curThreadId:" + Thread.currentThread().getId()
//                    + SLog.getStackTraceString(new Throwable());
//            log += trace;
//        }
        if (Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId()) {
            track(log);
        } else {
            Log.d(TAG, log);
        }
    }

    public void track(String module, String where) {
        track(module, where, false);
    }

    public static String composite(String module, long delta, long total) {
        return String.format("[+%d] %s , total: %d", delta, module, total);
    }

    /**
     * 主线程track
     *
     * @param module
     */
    public static void track(String module) {
        if (!enable) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (lastTrackTime == 0L) {
            lastTrackTime = currentTime;
            firstTrackTime = currentTime;
        }
        Log.d("delta", "delta " + composite(module, currentTime - lastTrackTime, currentTime - firstTrackTime));
        lastTrackTime = currentTime;
    }
}
