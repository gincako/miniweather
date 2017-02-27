package com.simple.miniweather.utils;

import com.simple.miniweather.R;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/2/26 0026.
 */

public class Constants {

    public static final String KEY = "48422fd7bca8412baf7b79324e551a24";

    public static final HashMap<String, Integer> infoImageId = new HashMap<>();

    public static int getInfoImageId(String infoTxt) {
        if (infoImageId.size() == 0) {
            infoImageId.put("晴", R.mipmap.skyicon_sunshine_normal);
            infoImageId.put("多云", R.mipmap.skyicon_partly_cloud_normal);
            infoImageId.put("阴", R.mipmap.skyicon_cloud_normal);
            infoImageId.put("小雨", R.mipmap.skyicon_rain_light);
            infoImageId.put("中雨", R.mipmap.skyicon_rain_normal);
            infoImageId.put("大雨", R.mipmap.skyicon_rain_storm);
        }
        Integer imageId = infoImageId.get(infoTxt);
        return imageId;
    }
}
