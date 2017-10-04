package com.example.chen.pkuapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by chen on 17/9/26.
 */

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClassLoader classLoader = SecondActivity.class.getClassLoader();
        /*
         * dalvik.system.PathClassLoader
         * [DexPathList[
         *      [zip file "/data/app/com.example.chen.pkuapp-1/base.apk"],
         *      nativeLibraryDirectories=[/data/app/com.example.chen.pkuapp-1/lib/arm64,...]
         *  ]]
         */
        Log.d("app_SecondActivity", "hashCode = " + classLoader.hashCode() + "");
        Log.d("app_SecondActivity", classLoader.toString());
        /* java.lang.BootClassLoader@a95206c  */
        Log.d("app_SecondActivity", classLoader.getParent().toString());

        setContentView(R.layout.activity_second);
//        ((TextView)findViewById(R.id.tv)) .setText("second");
    }

    @Override
    protected void onDestroy() {
        Log.e("wcc", "destroy");
        super.onDestroy();
    }
}
