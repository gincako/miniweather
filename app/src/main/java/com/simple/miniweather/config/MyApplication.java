package com.simple.miniweather.config;


import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2017/2/25 0025.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(getApplicationContext());
    }
}
