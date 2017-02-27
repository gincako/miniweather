package com.simple.miniweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/2/26 0026.
 */

public class Weather {

    public String status;

    public Aqi aqi;

    public Basic basic;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<DailyWeather> dailyList;

    @SerializedName("hourly_forecast")
    public List<HourWeather> hourList;

}
