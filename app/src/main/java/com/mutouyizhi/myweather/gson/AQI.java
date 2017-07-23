package com.mutouyizhi.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DELL on 2017-07-22.
 */

public class AQI {
    @SerializedName("city")
    public AQICity city;

    public class AQICity {
        @SerializedName("aqi")
        public String aqi;

        public String pm25;

        public String qlty;
    }
}
