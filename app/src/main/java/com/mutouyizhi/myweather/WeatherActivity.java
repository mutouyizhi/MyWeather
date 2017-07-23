package com.mutouyizhi.myweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Until;
import com.mutouyizhi.myweather.gson.Forecast;
import com.mutouyizhi.myweather.gson.Weather;
import com.mutouyizhi.myweather.util.HttpUtil;
import com.mutouyizhi.myweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView mWeatherLayout;
    private TextView mTitleCity;
    private TextView mTitleUpdateTime;
    private TextView mDegressText;
    private TextView mWeatherInfoText;
    private LinearLayout mForecastLayout;
    private TextView mAqiText;
    private TextView mPm25Text;
    private TextView mQualityText;
    private TextView mComfortText;
    private TextView mCarWashingText;
    private TextView mDressingText;
    private TextView mSportText;
    private TextView mTravelText;
    private ImageView mBingPicImg;
    public SwipeRefreshLayout mRefreshLayout;
    public DrawerLayout mDrawerLayout;
    private Button mNavButton;
    public String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if (Build.VERSION.SDK_INT >= 21) {
            View decoView = getWindow().getDecorView();
            decoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        mNavButton = (Button) findViewById(R.id.nav_button);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mWeatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        mTitleCity = (TextView) findViewById(R.id.title_city);
        mTitleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        mDegressText = (TextView) findViewById(R.id.degress_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        mAqiText = (TextView) findViewById(R.id.aqi_text);
        mPm25Text = (TextView) findViewById(R.id.pm25_text);
        mQualityText = (TextView) findViewById(R.id.quality_text);
        mComfortText = (TextView) findViewById(R.id.comfort_text);
        mCarWashingText = (TextView) findViewById(R.id.car_washing_text);
        mDressingText = (TextView) findViewById(R.id.dressing_text);
        mSportText = (TextView) findViewById(R.id.sport_text);
        mTravelText = (TextView) findViewById(R.id.travel_text);
        mBingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherContent = pres.getString("weather", null);
        String bingPic = pres.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(mBingPicImg);
        } else {
            loadBing();
        }
        if (weatherContent != null) {
            Weather weather = Utility.handlerWeatherResponse(weatherContent);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra("weather");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    private void loadBing() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mBingPicImg);
                    }
                });
            }
        });
    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=9e5ff079548d4617af8526e22e1fd1d2";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weatherContent = response.body().string();
                final Weather weather = Utility.handlerWeatherResponse(weatherContent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", weatherContent);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degress = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTitleCity.setText(cityName);
        mTitleUpdateTime.setText(updateTime);
        mDegressText.setText(degress);
        mWeatherInfoText.setText(weatherInfo);
        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temprature.max);
            minText.setText(forecast.temprature.min);
            mForecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            mAqiText.setText(weather.aqi.city.aqi);
            mPm25Text.setText(weather.aqi.city.pm25);
            mQualityText.setText(weather.aqi.city.qlty);
        }
        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车指数:" + weather.suggestion.carWash.info;
        String dressing = "穿衣指数:" + weather.suggestion.dressing.info;
        String sport = "运动指数:" + weather.suggestion.sport.info;
        String travel = "旅游指数:" + weather.suggestion.travel.info;
        mComfortText.setText(comfort);
        mCarWashingText.setText(carWash);
        mDressingText.setText(dressing);
        mSportText.setText(sport);
        mTravelText.setText(travel);
        mWeatherLayout.setVisibility(View.VISIBLE);
    }
}
