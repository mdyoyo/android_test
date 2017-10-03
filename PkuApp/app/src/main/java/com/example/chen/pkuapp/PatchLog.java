package com.example.chen.pkuapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatchLog {

    private static final String TAG = "PatchLog";
    private static final String PREFIX = "PatchLog_";
    private static File logFile;
    private static String appver;
    private static Object writeLock = new Object();
    private static SimpleDateFormat timeFormatter;
    private static String sSdcardLogPath;

    private static String getSdCardLogPath(Context context) {
        if (TextUtils.isEmpty(sSdcardLogPath)) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                sSdcardLogPath = new File(cacheDir.getParentFile(), "files/onlinelog").getAbsolutePath();
            } else {
                sSdcardLogPath = new File("/sdcard/Android/data/" + context.getPackageName() + "/files/onlinelog").getAbsolutePath();
            }
        }

        return sSdcardLogPath;
    }

    private static void init(Context context) {
        if (logFile == null) {
            logFile = new File(getSdCardLogPath(context), PREFIX + getVersionCode());
        }
        if (timeFormatter == null) {
            timeFormatter = new SimpleDateFormat("HH:mm:ss:SSS", Locale.US);
        }
        if (TextUtils.isEmpty(appver)) {
            appver = Build.VERSION.SDK_INT + "_android_" + getAppVersionName();
        }

    }

    private static void logToFile(Context context, String className, String tag, String level, String msg, Throwable tr) {
        synchronized (writeLock) {
            FileWriter writer = null;
            try {
                init(context);
                Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

                if (!logFile.exists()) {
                    logFile = createFile(logFile);
                    writer = new FileWriter(logFile);
                } else {
                    writer = new FileWriter(logFile, true);
                }

                String logTime = timeFormatter.format(curDate);
                if (className == null || "".equals(className)) {
                    writer.write(logTime + " [" + Thread.currentThread().getId() + "]-[" + appver + "]-[" + level + "]-[]-[" + tag + "] ");
                } else {
                    writer.write(logTime + " [" + Thread.currentThread().getId() + "]-[" + appver + "]-[" + className + "]-[]-[" + level + "]-[" + tag + "] ");
                }
                writer.write(msg);
                writer.write("\r\n");

                if (null != tr) {
                    writer.write(getStackTraceString(tr) + "\r\n");
                }
                writer.write("\r\n");
            } catch (Throwable e) {
                Log.e("", e.getMessage());
            } finally {
                try {
                    if (writer != null) {
                        writer.flush();
                    }
                } catch (Throwable e) {
                    Log.e("", e.getMessage());
                }
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Throwable e) {
                    Log.e("", e.getMessage());
                }
            }
        }
    }

    public static File createFile(File file) {
           // 已经存在略过
           if (file == null || file.exists()) {
               return file;
           }

           try {
               file.getParentFile().mkdirs();
               file.createNewFile();
           } catch (IOException e) {
               Log.e("Application", e.getMessage(), e);
           }

           return file;
       }

    static void e(Context context, String tag, String msg) {
        e(context, tag, msg, null);
    }

    static void e(Context context, String tag, String msg, Throwable tr) {
//        if (InitAppUtils.isDebuggable()) {
            if (null == tr) {
                Log.e(tag, msg);
            } else {
                Log.e(tag, msg, tr);
            }
//        }
        logToFile(context, "", tag, "error", msg, tr);
    }

    static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 获取当前app 版本号
     *
     * @return
     */
    public static int getVersionCode() {
        int sVersionCode = 0;
        try {
            PackageInfo pm = MyApplication.getInstance().getPackageManager()
                    .getPackageInfo(MyApplication.getInstance().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            sVersionCode = pm.versionCode;
        } catch (Exception e) {
        }

        return sVersionCode;
    }

    public static String getAppVersionName() {
        try {
            PackageInfo pm = MyApplication.getInstance().getPackageManager()
                    .getPackageInfo(MyApplication.getInstance().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pm.versionName;
        } catch (Exception e) {
            return "";
        }
    }
}
