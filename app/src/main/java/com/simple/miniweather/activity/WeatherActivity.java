package com.simple.miniweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.simple.miniweather.R;
import com.simple.miniweather.gson.DailyWeather;
import com.simple.miniweather.gson.Weather;
import com.simple.miniweather.service.AutoUpdateService;
import com.simple.miniweather.utils.Constants;
import com.simple.miniweather.utils.HttpUtil;
import com.simple.miniweather.utils.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView countyNameTV;
    private TextView updateTimeTV;
    private TextView nowdegreeTV;
    private TextView nowInfoTV;
    private LinearLayout dailyLayoutLL;
    private TextView aqiIndexTV;
    private TextView pmIndexTV;
    private TextView comfortTV;
    private TextView carwashTV;
    private TextView sportsTV;
    private NestedScrollView weatherNSV;
    private TextView airTv;
    private ImageView imvBingPic;
    public SwipeRefreshLayout weatherSFL;
    private ImageView homeImv;
    public DrawerLayout drawerLayout;

    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        initView();
        inData();
    }

    private void initView() {
        imvBingPic = (ImageView) findViewById(R.id.imv_bing_pic);
        weatherNSV = (NestedScrollView) findViewById(R.id.nsv_weather);
        countyNameTV = (TextView) findViewById(R.id.tv_countyName);
        updateTimeTV = (TextView) findViewById(R.id.tv_updateTime);
        nowdegreeTV = (TextView) findViewById(R.id.tv_now_degree);
        nowInfoTV = (TextView) findViewById(R.id.tv_now_info);
        dailyLayoutLL = (LinearLayout) findViewById(R.id.daily_layout);
        aqiIndexTV = (TextView) findViewById(R.id.aqi_index);
        pmIndexTV = (TextView) findViewById(R.id.pm_index);
        comfortTV = (TextView) findViewById(R.id.comfort_tv);
        carwashTV = (TextView) findViewById(R.id.carwash_tv);
        sportsTV = (TextView) findViewById(R.id.sports_tv);
        airTv = (TextView) findViewById(R.id.air_tv);
        homeImv = (ImageView) findViewById(R.id.home_imv);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        weatherSFL = (SwipeRefreshLayout) findViewById(R.id.weather_sfl);
        weatherSFL.setColorSchemeResources(R.color.refreshColor);
    }


    private void inData() {
        //读取缓存
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            weatherNSV.setVisibility(View.GONE);
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.getId();
            setData(weather);
        } else {
            //没有缓存时请求天气数据
            weatherNSV.setVisibility(View.GONE);
            weatherId = getIntent().getStringExtra("weatherId");
            requestWeather(weatherId);
        }

        //加载Bing背景图
        String bingPicUrl = sharedPreferences.getString("bing_pic", null);
        if (bingPicUrl != null) {
            Glide.with(this).load(bingPicUrl).into(imvBingPic);
        } else {
            loadBingPic();
        }

        weatherSFL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        homeImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void loadBingPic() {
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPicUrl = response.body().string();
                if (!TextUtils.isEmpty(bingPicUrl)) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("bing_pic", bingPicUrl);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(bingPicUrl).into(imvBingPic);
                        }
                    });
                }
            }
        });
    }

    private void setData(Weather weather) {
        countyNameTV.setText(weather.basic.getCity());
        updateTimeTV.setText(weather.basic.getUpdate().getLoc().split(" ")[1]);
        nowdegreeTV.setText(weather.now.getTmp() + "℃");
        nowInfoTV.setText(weather.now.getCond().getTxt());
        dailyLayoutLL.removeAllViews();
        for (DailyWeather dailyWeather : weather.dailyList) {
            View view = LayoutInflater.from(this).inflate(R.layout.daily_item, dailyLayoutLL, false);
            TextView dailyDate = (TextView) view.findViewById(R.id.daily_date);
            TextView dailyInfo = (TextView) view.findViewById(R.id.daily_info);
            TextView dailyTemp = (TextView) view.findViewById(R.id.daily_Temp);
            ImageView dailyInfoImv = (ImageView) view.findViewById(R.id.daily_infoImv);

            dailyDate.setText(dailyWeather.getDate().substring(dailyWeather.getDate().indexOf("-") + 1));
            DailyWeather.CondBean cond = dailyWeather.getCond();
            int infoImageId = Constants.getInfoImageId(cond.getTxt_d());
            if (infoImageId != 0) {
                dailyInfoImv.setImageResource(infoImageId);
            } else {
                dailyInfoImv.setImageResource(R.mipmap.skyicon_default);
            }
            if (cond.getTxt_d() != null) {
                if (!cond.getTxt_d().equals(cond.getTxt_n())) {
                    dailyInfo.setText(cond.getTxt_d() + "转" + cond.getTxt_n());
                } else {
                    dailyInfo.setText(cond.getTxt_d());
                }
            } else {
                    dailyInfo.setText(cond.getTxt_n());
            }
            dailyTemp.setText(dailyWeather.getTmp().getMax() + "℃/"
                    + dailyWeather.getTmp().getMin() + "℃");
            dailyLayoutLL.addView(view);
        }
        if (weather.aqi != null) {
            aqiIndexTV.setText(weather.aqi.getCity().getAqi());
            pmIndexTV.setText(weather.aqi.getCity().getPm25());
        }
        airTv.setText("空气指数：" + weather.suggestion.getAir().getBrf() + "，"
                + weather.suggestion.getAir().getTxt());
        comfortTV.setText("舒适度：" + weather.suggestion.getComf().getBrf() + "，"
                + weather.suggestion.getComf().getTxt());
        carwashTV.setText("洗车指数：" + weather.suggestion.getCw().getBrf() + "，"
                + weather.suggestion.getCw().getTxt());
        sportsTV.setText("运动建议：" + weather.suggestion.getSport().getBrf() + "，"
                + weather.suggestion.getSport().getTxt());
        weatherNSV.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }


    public void requestWeather(String weatherId) {
        this.weatherId = weatherId;
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=" + Constants.KEY;
        HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        weatherSFL.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("responseString", "onResponse: ===================" + responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences preferences = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            setData(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        }
                        weatherSFL.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

}
