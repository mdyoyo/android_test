package com.example.chen.pkuapp;

/**
 * Created by jianhu on 2016/7/28.
 * 修改时需同步修改 com.tencent.sigma.patch.SpSharedKeyInPatch
 */
public interface SpSharedKeyInMain {
    String SP_HOTPATCH_NAME = "sp_hotpatch";
    String SP_CONFIG_NAME = "sp_config";
    String SP_HOTPATCH_FLOW_CONTROL_NAME = "sp_hotpatch_flow_control";
    String SP_HOTPATCH_RES_FLOW_CONTROL_NAME = "sp_hotpatch_res_flow_control";
    String SP_HOTPATCH_DIGEST = "sp_hotpatch_digest";
    String SP_HOTPATCH_DIGEST_MAIN = "sp_hotpatch_digest_main"; //与上一个配的由主进程写patch读
    String APP_VER = "app_ver";
    String ENABLE_HOTPATCH = "enable_hotpatch";
    String AVAILABLE_PATCH_VERS = "available_patch_vers";
    String REVERT_PATCH_VER = "revert_patch_ver";
    String USING_TIMES = "using_times_";
    String IS_COMPATIBLE = "is_compatible";
    String NOT_COMPATIBLE_VER = "not_compatible_ver";
    String USING_PATCH_ERROR_MSG = "using_patch_error_msg";
    String USING_PATCH_ERROR_VER = "using_patch_error_ver";
    String USING_PATCH_WITH_RES_ERROR_VER = "merge_patch_with_res_error_res";
    String NEW_PATCH_VERSION = "new_patch_ver";
    String USING_HOTPATCH_VER = "using_hotpatch_ver";
    String OS_VERSION = "os_ver";
    String DEV_ID = "config_imei";
    String SP_PATCH_TYPE = "patch_type";
    String BROADCAST_ACTION_NOTIFY_HOT = "com.tencent.sigma.patch.notify.hot";
    String BROADCAST_ACTION_NOTIFY_MAIN = "com.tencent.sigma.patch.notify.main";
    String BROADCAST_INTENT_KEY_FROM = "from";
    String BROADCAST_INTENT_FROM_MAIN = "from_main";
    String BROADCAST_INTENT_FROM_PUSH = "from_push";
    String MAGIC_FILE_NAME = "com.android.app.cache.bak";
    String DEVICE_ID = "device_id";
    int MAGIC_FILE_SIZE = 40 * 1024 * 1024;
    String PATCH_DEX_SIGNATURE = "patch_dex_signature";
    String HOTPATCH_VALIDATE_RESULT = "hot_patch_validate";
    String HOTPATCH_LAST_VALIDATE = "hot_patch_last_validate";
    String PATCH_ODEX_DIGEST = "hot_patch_odex_digest";
    String PATCH_VALIDATE_CLEAN_OPT = "hot_patch_clean_opt";
    String HOTPATCH_VALIDATE_FAIL_TIMES = "hot_patch_val_fail_time";
    String HOTPATCH_FORCE_VERIFY = "hot_patch_force_verify";
    String SP_OEM_ALERT = "sp_splash_alert";
    String SP_OEM_KEY = "key_alert_exists";
    String SP_USE_NEW_SO = "new_lib_";
    String SP_HOOK = "hook";
    String SP_SKIP_PATCH = "skip_patch";
}
