package com.example.chen.pkuapp;

import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClassLoader classLoader = MainActivity.class.getClassLoader();
        /*
         * dalvik.system.PathClassLoader
         * [DexPathList[
         *      [zip file "/data/app/com.example.chen.pkuapp-1/base.apk"],
         *      nativeLibraryDirectories=[/data/app/com.example.chen.pkuapp-1/lib/arm64,...]
         *  ]]
         */
        Log.i("app_MainActivity", "hashCode = " + classLoader.hashCode() + "");
        Log.i("app_MainActivity", classLoader.toString());
        /* java.lang.BootClassLoader@a95206c  */
        Log.i("app_MainActivity", classLoader.getParent().toString());

        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.tv)) .setText("first");
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class)  ;
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onDestroy() {
        Log.e("MainActivity", "destroy");
        super.onDestroy();
    }
}
