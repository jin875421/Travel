package glue502.software.activities.map;

import androidx.appcompat.app.AppCompatActivity;
import glue502.software.R;
import glue502.software.models.MarkerIntentInfo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AddLabelActivity extends AppCompatActivity {

    Button toMap;
    MapView mMapView;
    BaiduMap mBaiduMap;
    EditText editText1;
    AutoCompleteTextView editText2;
    //POI搜索
    private SuggestionSearch mSuggestionSearch = null;
    // 搜索关键字输入窗口
    private ListView mSugListView;
    private LocationClient mLocationClient;
    double latitude, longitude;
    String city = "北京市";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationClient.setAgreePrivacy(true);
        SDKInitializer.setAgreePrivacy(this.getApplicationContext(), true);
        SDKInitializer.initialize(this.getApplicationContext());

        setContentView(R.layout.activity_add_label);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
//            startLocation();
        }
        //绑定地图控件
        initView();
        //获取地理位置
        startLocation();
        //Poi搜索
        PoiSugSearch();
        //绑定监听器
        setListener();
    }

    private void initView() {
        toMap = findViewById(R.id.but_toMap);
        editText1 = findViewById(R.id.main_edt_city);
        editText2 = findViewById(R.id.main_edt_poi);
        mMapView = findViewById(R.id.bmapView);
//        mMapView.setVisibility(View.GONE);
        mBaiduMap = mMapView.getMap();
        mSugListView = findViewById(R.id.sug_list);
        editText2.setThreshold(1);
        mSuggestionSearch = SuggestionSearch.newInstance();
    }

    public void startLocation() {
        // 初始化定位客户端
        try {
            Log.v("AddLabelActivity", "lzxAddLabelActivity页面开启");
            Toast.makeText(getApplicationContext(),"开始定位",Toast.LENGTH_LONG).show();
            mLocationClient = new LocationClient(getApplicationContext());
            Toast.makeText(getApplicationContext(),"定位至此",Toast.LENGTH_LONG).show();
            // 配置定位选项
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(0); // 设置扫描时间
            option.setIsNeedAddress(true); // 设置需要获取地址信息
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation == null || mMapView == null) {
                        return;
                    }
                    latitude = bdLocation.getLatitude();
                    longitude = bdLocation.getLongitude();
                    city = bdLocation.getCity(); // 获取详细地址信息

                    Toast.makeText(getApplicationContext(), "Latitude: " + latitude + ", Longitude: " + longitude + ", city: " + city,Toast.LENGTH_LONG).show();
                    // 在这里处理获取到的经纬度信息
                }
            });
            // 开始定位
            mLocationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        // 当输入关键字变化时，动态更新建议列表
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString()) // 关键字
                        .city(city)); // 城市
            }
        });

        // 设置触摸事件监听器
        findViewById(R.id.main_content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断点击的位置是否在输入框外
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isPointInsideView(event.getRawX(), event.getRawY(), editText2)) {
                        // 输入框外部被点击，让输入框失去焦点
                        editText2.clearFocus();
                    }
                }
                return false;
            }
        });

        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(getApplicationContext(), "focus", Toast.LENGTH_LONG).show();
                    mSugListView.setVisibility(View.VISIBLE);
                } else {
                    mSugListView.setVisibility(View.GONE);
                }
            }
        });

        //设置marker可拖拽
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
                Toast.makeText(getApplicationContext(), "拖拽了marker", Toast.LENGTH_LONG).show();
                Log.v("AddLabelActivity", "lzx拖拽了marker");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(getApplicationContext(), "拖拽结束了marker", Toast.LENGTH_LONG).show();
                Log.v("AddLabelActivity", "lzx拖拽结束了marker");
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });

        //绑定marker点击事件监听器
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.v("AddLabelActivity", "lzx点击了marker");
                Intent intent = new Intent(AddLabelActivity.this, AddMarkerActivity.class);
                Bundle bundle = new Bundle();
                MarkerIntentInfo markerIntentInfo = new MarkerIntentInfo(
                        editText2.getText().toString(),
                        marker.getPosition().latitude,
                        marker.getPosition().longitude
                );
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });
    }

    //开始POI搜索，此下位POI搜索方法
    //POI
    private void PoiSugSearch() {
        OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
            /**
             * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
             *
             * @param suggestionResult    Sug检索结果
             */
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }

                List<HashMap<String, String>> suggest = new ArrayList<>();
                for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                    if (info.getKey() != null && info.getDistrict() != null && info.getCity() != null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("key", info.getKey());
                        map.put("city", info.getCity());
                        map.put("dis", info.getDistrict());
                        suggest.add(map);
                    }
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                        suggest,
                        R.layout.item_layout,
                        new String[]{"key", "city", "dis"},
                        new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis});
                mSugListView.setVisibility(View.VISIBLE);
                mSugListView.setAdapter(simpleAdapter);
                mSugListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // 获取点击的建议项的数据
                        HashMap<String, String> selectedItem = suggest.get(position);

                        String selectedKey = selectedItem.get("key");
                        String selectedCity = selectedItem.get("city");
                        String selectedDistrict = selectedItem.get("dis");
                        Log.v("AddLabelActivity", "lzx key"+selectedKey);
                        Log.v("AddLabelActivity", "lzx city"+selectedCity.toString());
                        Log.v("AddLabelActivity", "lzx dis"+selectedDistrict.toString());

                        // 使用地理编码服务获取经纬度坐标
                        GeoCoder geoCoder = GeoCoder.newInstance();
                        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                            @Override
                            public void onGetGeoCodeResult(GeoCodeResult result) {
                                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                    // 没有检索到结果，处理错误
                                    Log.v("AddLabelActivity", "lzx没东西");
                                } else {
                                    // 获取坐标信息
                                    LatLng location = result.getLocation();

                                    // 打印输出经纬度信息
                                    Log.v("AddLabelActivity", "lzx Latitude: " + location.latitude);
                                    Log.v("AddLabelActivity", "lzx Longitude: " + location.longitude);

                                    // 在这里你可以将坐标信息存储到成员变量中，或者进行其他操作
                                    LatLng point = location;

                                    //创建marker
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                                            .fromResource(R.drawable.ic_marker);
                                    //构件MarkerOption，用于在地图上添加Marker
                                    OverlayOptions option = new MarkerOptions()
                                           .position(point)
                                           .icon(bitmap)
                                            .draggable(true)
                                           .zIndex(1);

                                    //在地图上添加并显示
                                    mBaiduMap.addOverlay(option);

                                    //将地图中心移动到当前位置
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
                                    //将地图比例尺缩放到合适的级别
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));

                                    // 设置文本框的值
                                    editText2.setText(selectedKey);

                                    // 隐藏建议列表
                                    mSugListView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                                // 反地理编码的结果，如果需要的话可以处理
                            }
                        });

                        // 设置地理编码检索参数
                        GeoCodeOption geoCodeOption = new GeoCodeOption()
                                .city(selectedCity)
                                .address(selectedDistrict + selectedKey);
                        geoCoder.geocode(geoCodeOption);

                        // 释放地理编码检索实例
                        geoCoder.destroy();

                        if (selectedKey != null) {
                            editText2.setText(selectedKey);
                        }

                        // 隐藏建议列表（假设 mSugListView 是你的建议列表）
                        mSugListView.setVisibility(View.GONE);
                    }
                });
                simpleAdapter.notifyDataSetChanged();
            }

        };
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);

    }

    // 判断点击的位置是否在 View 的范围内
    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        return (x > viewX && x < (viewX + viewWidth) && y > viewY && y < (viewY + viewHeight));
    }

    /*生命周期管理*/
    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if(mLocationClient!=null) {
            if (!mLocationClient.isStarted()) {
                mLocationClient.start();
            }
        }
    }


    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mLocationClient!=null) {
            mLocationClient.stop();
        }
    }
}