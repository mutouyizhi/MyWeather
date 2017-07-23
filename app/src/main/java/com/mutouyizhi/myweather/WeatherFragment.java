package com.mutouyizhi.myweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mutouyizhi.myweather.db.City;
import com.mutouyizhi.myweather.db.County;
import com.mutouyizhi.myweather.db.Province;
import com.mutouyizhi.myweather.util.HttpUtil;
import com.mutouyizhi.myweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by DELL on 2017-07-21.
 */

public class WeatherFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog mProgressDialog;
    private ImageButton mBackButton;
    private TextView mTitleTextView;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> mProvinces;
    private List<City> mCities;
    private List<County> mCounties;
    private Province mSelectedProvince;
    private City mSelectedCity;
    private int mCurrentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_fragment, container, false);
        mBackButton = (ImageButton) v.findViewById(R.id.btn_back);
        mTitleTextView = (TextView) v.findViewById(R.id.weather_title);
        mListView = (ListView) v.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentLevel == LEVEL_PROVINCE) {
                    mSelectedProvince = mProvinces.get(position);
                    queryCities();
                } else if (mCurrentLevel == LEVEL_CITY) {
                    mSelectedCity = mCities.get(position);
                    queryCounties();
                } else if (mCurrentLevel == LEVEL_COUNTY) {
                    String weatherId = mCounties.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getContext(), WeatherActivity.class);
                        intent.putExtra("weather", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mDrawerLayout.closeDrawers();
                        activity.weatherId = weatherId;
                        activity.mRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (mCurrentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        mTitleTextView.setText("中国");
        mBackButton.setVisibility(View.GONE);
        mProvinces = DataSupport.findAll(Province.class);
        if (mProvinces.size() > 0) {
            dataList.clear();
            for (Province province : mProvinces) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_PROVINCE;
        } else {
            String addrres = "http://guolin.tech/api/china";
            queryFromServer(addrres, "province");
        }
    }

    private void queryCities() {
        mTitleTextView.setText(mSelectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCities = DataSupport.where("provinceid = ?", String.valueOf(mSelectedProvince.getId())).find(City.class);
        if (mCities.size() > 0) {
            dataList.clear();
            for (City city : mCities) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
        } else {
            int provinceId = mSelectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceId;
            queryFromServer(address, "city");
        }
    }

    private void queryCounties() {
        mTitleTextView.setText(mSelectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCounties = DataSupport.where("cityid = ?", String.valueOf(mSelectedCity.getId())).find(County.class);
        if (mCounties.size() > 0) {
            dataList.clear();
            for (County county : mCounties) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
        } else {
            int provinceId = mSelectedProvince.getProvinceCode();
            int cityId = mSelectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceId + "/" + cityId;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address, final String level) {
        showDialogProgess();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialogProgess();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if (level.equals("province")) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if (level.equals("city")) {
                    result = Utility.handleCityResponse(responseText, mSelectedProvince.getId());
                } else if (level.equals("county")) {
                    result = Utility.handleCountyResponse(responseText, mSelectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeDialogProgess();
                            if (level.equals("province")) {
                                queryProvinces();
                            } else if (level.equals("city")) {
                                queryCities();
                            } else if (level.equals("county")) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void closeDialogProgess() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showDialogProgess() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("正在加载....");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }
}
