package com.mutouyizhi.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DELL on 2017-07-22.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("drsg")
    public Dressing dressing;

    @SerializedName("sport")
    public Sport sport;

    @SerializedName("trav")
    public Travel travel;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Dressing {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }

    public class Travel {
        @SerializedName("txt")
        public String info;
    }
}
