package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.weather.OnGetWeatherResultListener;
import com.baidu.mapapi.search.weather.WeatherDataType;
import com.baidu.mapapi.search.weather.WeatherResult;
import com.baidu.mapapi.search.weather.WeatherSearch;
import com.baidu.mapapi.search.weather.WeatherSearchForecasts;
import com.baidu.mapapi.search.weather.WeatherSearchOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.PostListAdapter;
import glue502.software.adapters.WeatherAdapter;
import glue502.software.models.PostWithUserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WeatherActivity extends AppCompatActivity {

    private TextView mTvLocation, mTvRealTimeWeather, mTvRealTimeTemperature;
    private ListView mLvForecasts;
    private WeatherAdapter mWeatherAdapter;
    private String city;
    public LocationClient mLocationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        city = getIntent().getStringExtra("city");
        mTvLocation = findViewById(R.id.tv_location);
        mTvRealTimeWeather = findViewById(R.id.tv_realtime_weather);
        mTvRealTimeTemperature = findViewById(R.id.tv_realtime_temperature);
        mLvForecasts = findViewById(R.id.lv_forecasts);
        mTvLocation.setText(city); // 假设Location对象有一个getName()方法
        getweather();
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
    }
    private void getweather(){
        //初始化了mLocationClient
        try {
            mLocationClient.setAgreePrivacy(true);
            mLocationClient = new LocationClient(WeatherActivity.this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置定位监听器
        BDAbstractLocationListener myLocationListener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location != null) {
                    // 获取经纬度信息
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //获取天气信息
                    WeatherSearchOption weatherSearchOption = new WeatherSearchOption()
                            .weatherDataType(WeatherDataType.WEATHER_DATA_TYPE_ALL)
                            .districtID("130108");
                    WeatherSearch mWeatherSearch = WeatherSearch.newInstance();
                    //发起天气检索请求
                    mWeatherSearch.setWeatherSearchResultListener(new OnGetWeatherResultListener() {
                        @Override
                        public void onGetWeatherResultListener(final WeatherResult weatherResult) {
                            mTvRealTimeWeather.setText(weatherResult.getRealTimeWeather().getPhenomenon()); // 假设RealTimeWeather有一个getWeather()方法
                            mTvRealTimeTemperature.setText(weatherResult.getRealTimeWeather().getTemperature() + "°C"); // 假设RealTimeWeather有一个getTemperature()方法
                            mWeatherAdapter = new WeatherAdapter(WeatherActivity.this,weatherResult.getForecasts());
                            mLvForecasts.setAdapter(mWeatherAdapter);
                        }
                    });
                    mWeatherSearch.request(weatherSearchOption);


                    //获取天气信息

                    // 逆地理编码获取城市信息
                    GeoCoder geoCoder = GeoCoder.newInstance();
                    geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                            if (reverseGeoCodeResult != null && reverseGeoCodeResult.getAddressDetail() != null) {
                                city = reverseGeoCodeResult.getAddressDetail().city;
                                // 存储城市信息
                                //删除city中的“市”字
                                if (city.contains("市")) {
                                    city = city.substring(0, city.indexOf("市"));
                                }
                            }
                            // 释放资源
                            geoCoder.destroy();
                        }

                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                            // 这里一般不需要实现
                        }
                    });

                    // 发起逆地理编码请求
                    LatLng latLng = new LatLng(latitude, longitude);
                    geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                }
            }
        };

        // 注册监听器
        mLocationClient.registerLocationListener(myLocationListener);

        // 设置定位选项
        LocationClientOption option = new LocationClientOption();
        // 设置需要获取详细地址信息
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);

        // 启动定位
        mLocationClient.start();
    }
}
