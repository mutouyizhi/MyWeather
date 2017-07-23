package com.mutouyizhi.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DELL on 2017-07-22.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("update")
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
