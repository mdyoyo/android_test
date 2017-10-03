package com.example.chen.pkuapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SpInitApp {
    private static final String SP_FILE_NAME = "sp_init_app";


    public static class CrashInfo {
        //crash时间
        private static final String SP_CRASH_TIMESTAMP = "crashTimeStamp";
        //crash次数
        private static final String SP_CRASH_TIMES = "crashTimes";

        //需要累计crash次数的最小时间间隔  debug 2min, release 5min
        public static final long CRASH_MIN_INTERVAL = 2 * 60 * 1000L;// : 5 * 60 * 1000L; // 5Min

        /**
         * 保存crash栈信息
         *
         * @param throwable
         */
        public static void saveCrashInfo(Throwable throwable) {
            if (null == throwable) {
                return;
            }

            // 更新本次crash时间
            saveCrashTimeStamp();
        }

        /**
         * 保存crash时间戳
         */
        private static void saveCrashTimeStamp() {
            SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(SP_CRASH_TIMESTAMP, System.currentTimeMillis());
            editor.commit();
        }

        /**
         * 读取上次crash时间戳
         *
         * @return
         */
        public static long getCrashTimeStamp() {
            SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_MULTI_PROCESS);
            return sp.getLong(SP_CRASH_TIMESTAMP, 0);
        }


        /**
         * 更新crash次数
         */
        public static void updateCrashTimes() {
            int crashTimes = getCrashTimes();

            /* Log.i("SpInitApp", "updateCrashTimes, already crash times:" + crashTimes); */

            boolean isContinuousCrash = isContinuousCrash();
            // 如果不是短时间内连续crash,重置crash次数
            if (!isContinuousCrash) {
                crashTimes = 0;
            }

            // crash次数
            crashTimes++;

            /* Log.i("SpInitApp", "updateCrashTimes, update crash times:" + crashTimes); */
            saveCrashTimes(crashTimes);

            if (isContinuousCrash && HotPatchCompatibleInfo.needDisablePatch()) {
                HotPatchCompatibleInfo.markNotCompatibleHotPatch();
            }
        }

        /**
         * 保存crash次数
         *
         * @return
         */
        private static void saveCrashTimes(int crashTimes) {
            SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = sp.edit();
            /* Log.i("SpInitApp", "saveCrashTimes, saveCrashTimes crash times:" + crashTimes); */
            editor.putInt(SP_CRASH_TIMES, crashTimes);
            // 同步写,请勿改成apply
            editor.commit();
        }

        /**
         * 读取crash次数
         *
         * @return
         */
        public static int getCrashTimes() {
            SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_MULTI_PROCESS);
            return sp.getInt(SP_CRASH_TIMES, 0);
        }

        /**
         * 是否连续crash.
         *
         * @return
         */
        private static boolean isContinuousCrash() {
            long lastCrashTimeStamp = getCrashTimeStamp();

            return System.currentTimeMillis() - lastCrashTimeStamp < CRASH_MIN_INTERVAL;
        }

        public static void recordCrashStates(Throwable ex) {
            updateCrashTimes();
            saveCrashInfo(ex);
        }
    }

    public static class HotPatchCompatibleInfo {
        // 应用patch后至少在前N次内没有crash,则认为patch sdk与rom兼容
        private static final int PATCH_ACCESS_CONTROL_TIMES = 4;

        static String getAvailablePatchVers() {
            SharedPreferences sp = getHotPatchSp();

            return sp.getString(SpSharedKeyInMain.AVAILABLE_PATCH_VERS, "");
        }

        static void setRevertPatchVer(int revertPatchVer) {
            SharedPreferences sp = getHotPatchSp();
            SharedPreferences.Editor editor = sp.edit();

            editor.putInt(SpSharedKeyInMain.REVERT_PATCH_VER, revertPatchVer);
            editor.commit();

            PatchLog.e(MyApplication.getInstance(), "SpInitApp", "hotpatch try to revert last patch ver:" + revertPatchVer);
        }

        public static void markNotCompatibleHotPatch() {
            /* Log.i("SpInitApp", "markNotCompatibleHotPatch"); */
            SharedPreferences sp = getHotPatchSp();
            SharedPreferences.Editor editor = sp.edit();

            editor.putBoolean(SpSharedKeyInMain.IS_COMPATIBLE, false);
            int usingHotpatchVer = sp.getInt(SpSharedKeyInMain.USING_HOTPATCH_VER, -1);
            editor.putInt(SpSharedKeyInMain.NOT_COMPATIBLE_VER, usingHotpatchVer);
            // 同步写
            editor.commit();

            PatchLog.e(MyApplication.getInstance(), "SpInitApp", "hotpatch framework not compatiable this rom");
        }

        static boolean needDisablePatch() {
            SharedPreferences sp = getHotPatchSp();

            int usingHotpatchVer = sp.getInt(SpSharedKeyInMain.USING_HOTPATCH_VER, -1);
            // 没有加载patch
            if (usingHotpatchVer <= 0) {
                return false;
            }

            int revertPatchVer = getPrevAvailablePatchVer(usingHotpatchVer);
            /* Log.i("SpInitApp", "prev patch ver:" + revertPatchVer); */
            // 尝试回退一个版本
            if (revertPatchVer > 0) {
                setRevertPatchVer(revertPatchVer);
                return false;
            }

            // 加载patch后小于N次, 禁用hotPatch
            int patchUsingTimes = sp.getInt(SpSharedKeyInMain.USING_TIMES + usingHotpatchVer, 0);

            /* Log.i("SpInitApp", "patchUsingTimes:" + patchUsingTimes); */
            return patchUsingTimes <= PATCH_ACCESS_CONTROL_TIMES;
        }

        static int getPrevAvailablePatchVer(int baseVer) {
            String patchVers = getAvailablePatchVers();

            if (TextUtils.isEmpty(patchVers)) {
                return -1;
            }

            String[] vers = patchVers.split(",");
            /* Log.i("SpInitApp", Arrays.toString(vers) + " baseVer:" + baseVer); */
            for (int i = vers.length - 1; i >= 0; i--) {
                if (TextUtils.equals(vers[i], String.valueOf(baseVer))) {
                    if (i - 1 < 0) {
                        return -1;
                    }

                    int ret = -1;

                    try {
                        ret = Integer.parseInt(vers[i - 1]);
                    } catch (Exception e) {

                    }

                    return ret;
                }
            }

            return -1;
        }

        private static SharedPreferences getHotPatchSp() {
            return MyApplication.getInstance().getSharedPreferences(SpSharedKeyInMain.SP_HOTPATCH_NAME, Context.MODE_MULTI_PROCESS);
        }
    }
}
