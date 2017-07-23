package com.mutouyizhi.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DELL on 2017-07-22.
 */

public class Forecast {
    @SerializedName("date")
    public String date;

    @SerializedName("tmp")
    public Temprature temprature;

    @SerializedName("cond")
    public More more;

    public class Temprature {
        @SerializedName("max")
        public String max;

        @SerializedName("min")
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
