package com.mutouyizhi.myweather.util;

import android.text.TextUtils;

import com.mutouyizhi.myweather.db.City;
import com.mutouyizhi.myweather.db.County;
import com.mutouyizhi.myweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DELL on 2017-07-21.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    Province province = new Province();
                    JSONObject jsonProvince = allProvinces.getJSONObject(i);
                    province.setProvinceCode(jsonProvince.getInt("id"));
                    province.setProvinceName(jsonProvince.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    City city = new City();
                    JSONObject jsonCity = allCities.getJSONObject(i);
                    city.setProvinceId(provinceId);
                    city.setCityCode(jsonCity.getInt("id"));
                    city.setCityName(jsonCity.getString("name"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    County county = new County();
                    JSONObject jsonCounty = allCounties.getJSONObject(i);
                    county.setCityId(cityId);
                    county.setWeatherId(jsonCounty.getString("weather_id"));
                    county.setCountyName(jsonCounty.getString("name"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
