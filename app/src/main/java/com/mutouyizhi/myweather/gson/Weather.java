package com.mutouyizhi.myweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DELL on 2017-07-22.
 */

public class Weather {
    @SerializedName("status")
    public String status;

    @SerializedName("basic")
    public Basic basic;

    @SerializedName("aqi")
    public AQI aqi;

    @SerializedName("now")
    public Now now;


    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
