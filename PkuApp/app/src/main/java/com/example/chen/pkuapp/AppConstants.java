package com.example.chen.pkuapp;

/**
 * 重要, 误删!!!
 * 且不能改为final static
 * <p>
 * hotpatch运行时业务资源常量
 * <p>
 * Created by cadenliu on 16-9-8.
 */

public class AppConstants {
    public static int ICON_RES_ID = R.mipmap.ic_launcher;
    public static int APP_BASE_VERSION_CODE = BuildConfig.VERSION_CODE;
    public static String APP_BASE_VERISON_NAME = BuildConfig.VERSION_NAME;
    public static String PACKAGE_NAME = "com.tencent.reading";
    public static String APPLICATION_CLASS_NAME = "com.example.chen.pkuapp.MyApplication";
    public static String ACTION_PERMISSION = "";
    public static String APP_NAME = "天天快报";
    public static String APP_EN_NAME = "Reading";
    public static String BASE_READ_URL = "http://101.236.31.218/";
    public static String BASE_WRITE_URL = "http://101.236.31.218/";
    public static String VALUE_REFERER = "http://cnews.qq.com/cnews/android/";
    public static String HTTP_REQUEST_PRODUCT_LINE = "_areading_";
    public static String DEXES_INI_FILE = "dexes.ini";
    public static boolean IS_OEM = BuildConfig.FLAVOR.contains("oem");
    public static boolean SKIP_PATCH = BuildConfig.BUILD_TYPE.contains("debug") && !BuildConfig.FLAVOR.contains("rdm");
}
