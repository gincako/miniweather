package com.simple.miniweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.simple.miniweather.R;
import com.simple.miniweather.activity.MainActivity;
import com.simple.miniweather.activity.WeatherActivity;
import com.simple.miniweather.database.City;
import com.simple.miniweather.database.County;
import com.simple.miniweather.database.Province;
import com.simple.miniweather.utils.HttpUtil;
import com.simple.miniweather.utils.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/26 0026.
 */

public class SelectAreaFragment extends Fragment {

    private ImageView imvSelectBack;
    private TextView tvAreaName;
    private ListView lvArea;
    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ProgressDialog dialog;

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int currentType;

    //区域列表
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    //选中的省份或城市
    private Province selectProvince;
    private City selectCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_area, container, false);
        imvSelectBack = (ImageView) view.findViewById(R.id.imv_selectBack);
        tvAreaName = (TextView) view.findViewById(R.id.tv_areaName);
        lvArea = (ListView) view.findViewById(R.id.lv_area);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        lvArea.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imvSelectBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentType == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });

        lvArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentType == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCities();
                } else if (currentType == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCounties();
                } else if (currentType == LEVEL_COUNTY) {
                    County county = countyList.get(position);
                    if (getActivity() instanceof MainActivity) {
                        MainActivity activity = (MainActivity) getActivity();
                        Intent intent = new Intent(activity, WeatherActivity.class);
                        intent.putExtra("weatherId", county.getWeatherId());
                        startActivity(intent);
                        activity.finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.weatherSFL.setRefreshing(true);
                        activity.requestWeather(county.getWeatherId());
                    }
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        tvAreaName.setText("全国");
        imvSelectBack.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList)
                dataList.add(province.getProvinceName());
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            currentType = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }


    private void queryCities() {
        tvAreaName.setText(selectProvince.getProvinceName());
        imvSelectBack.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectProvince.getId()))
                .find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            currentType = LEVEL_CITY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectProvince.getProvinceCode();
            queryFromServer(address, "city");
        }
    }

    private void queryCounties() {
        tvAreaName.setText(selectCity.getCityName());
        imvSelectBack.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?", String.valueOf(selectCity.getCityCode()))
                .find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            currentType = LEVEL_COUNTY;
        } else {
            String address = "http://guolin.tech/api/china/" +
                    +selectCity.getProvinceId() + "/" + selectCity.getCityCode();
            queryFromServer(address, "county");
        }

    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "获取数据失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.e("onResponse", "onResponse:===========================" + json);
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(json);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(json, selectProvince.getProvinceCode());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(json, selectCity.getCityCode());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }


    private void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("加载中...");
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    private void closeProgressDialog() {
        if (dialog != null)
            dialog.dismiss();
    }
}
