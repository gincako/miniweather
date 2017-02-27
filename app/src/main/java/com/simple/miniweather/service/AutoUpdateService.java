package com.simple.miniweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.simple.miniweather.activity.WeatherActivity;
import com.simple.miniweather.gson.Weather;
import com.simple.miniweather.utils.Constants;
import com.simple.miniweather.utils.HttpUtil;
import com.simple.miniweather.utils.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class AutoUpdateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        int anHour = 6 * 60 * 60 * 1000;
        long triggerAtMillis = SystemClock.elapsedRealtime() + anHour;
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    //更新天气信息
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.getId();
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=" + Constants.KEY;
            HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("weatherUrl", "onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather newWeather = Utility.handleWeatherResponse(responseText);
                    if (newWeather != null && newWeather.status.equals("ok")) {
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    //更新Bing背景图片
    private void updateBingPic() {
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("picUrl", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                if (responseText != null) {
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("bing_pic", responseText);
                    editor.apply();
                }
            }
        });
    }
}
