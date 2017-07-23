package com.mutouyizhi.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DELL on 2017-07-22.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
