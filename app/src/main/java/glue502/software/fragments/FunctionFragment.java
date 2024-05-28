package glue502.software.fragments;

import static android.content.Context.MODE_PRIVATE;
import static glue502.software.activities.MainActivity.ip;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.login.LoginActivity;
import glue502.software.activities.map.AddLabelActivity;
import glue502.software.activities.map.GlideCustomTransformation;
import glue502.software.activities.map.StrategyDisplayActivity;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.adapters.RecyclerViewStrategyAdapter;
import glue502.software.models.MarkerInfo;
import glue502.software.models.ReturnStrategy;

import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FunctionFragment extends Fragment {
    private String url="http://"+ip+"/travel/strategy";
    private Button uploadBtn;
    private List<MarkerInfo> markerList;
    private TextureMapView mMapView;
    private TextView cityView;
    private BaiduMap mBaiduMap;
    private OkHttpClient client;
    private List<ReturnStrategy> returnStrategyList = new ArrayList<>();
    private String latitude;
    private String longitude;
    private String status;
    private CoordinatorLayout coordinatorLayout;
    private BottomSheetBehavior<View> behavior;
    private View bottomSheet;
    private Context mContext;
//    private RelativeLayout frameLayout;
    private FrameLayout frameLayout;
    private ImageView btnLoc;
    private boolean isGrantLocation = false;
    private LocationClient mLocationClient;
    AutoCompleteTextView editText2;
    private String city;
    //POI搜索
    private SuggestionSearch mSuggestionSearch = null;
    // 搜索关键字输入窗口
    private ListView mSugListView;
    private String selectedKey;
    private String selectedCity;
    private String selectedDistrict;
    private HashMap<String, String> iconMap = new HashMap<>();
    View view;
    RecyclerView mRecyclerView;
    private RecyclerViewStrategyAdapter recyclerViewStrategyAdapter;

    private static Fragment newInstance() {
        FunctionFragment fragment = new FunctionFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.v("FunctionFragment", "lzx onAttach");
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public void onCreate(@Nullable Bundle saveInstanceState) {
        Log.v("FunctionFragment", "lzx onCreate");
        LocationClient.setAgreePrivacy(true);
        SDKInitializer.setAgreePrivacy(this.getActivity().getApplicationContext(), true);
        SDKInitializer.initialize(this.getActivity().getApplicationContext());
        super.onCreate(saveInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("FunctionFragment", "lzx onCreateView");
        view = inflater.inflate(R.layout.fragment_function, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        //获取用户状态和用户名
        status = sharedPreferences.getString("status","");
        String commenterId = sharedPreferences.getString("userId","");
        initView();
        getIntent();
        PoiSugSearch();

        recyclerViewStrategyAdapter = new RecyclerViewStrategyAdapter(
                getActivity(),
                returnStrategyList
        );
        setListener();
        mRecyclerView.setAdapter(recyclerViewStrategyAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(layoutManager);

        // 隐藏百度LOGO
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 不显示地图上比例尺
        //textureMapView.showScaleControl(false);
        // 不显示地图缩放控件（按钮控制栏）
        mMapView.showZoomControls(false);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            isGrantLocation = true;
        }

        return view;
    }


    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        Log.v("FunctionFragment", "lzx onActivityCreated");
        super.onActivityCreated(saveInstanceState);
    }

    private void getIntent(){
        Intent intent = getActivity().getIntent();
        Double recommendLatitude = 0.00;
        Double recommendLongitude = 0.00;
        if(intent!=null){
            recommendLatitude = intent.getDoubleExtra("latitude", 0.00);
            recommendLongitude = intent.getDoubleExtra("longitude", 0.00);
            // 在这里处理获取到的经纬度信息
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(recommendLatitude, recommendLongitude));
            mBaiduMap.animateMapStatus(mapStatusUpdate);
        }
    }

    private void initData() {
        //获取marker图标的哈希表
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url+"/getIconMap")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String result = response.body().string();
                        Log.v("FunctionFragment", "lzx 获取的HashMap"+result);
                        iconMap = new Gson().fromJson(result, HashMap.class);
                        Log.v("FunctionFragment", "lzx 转换回的HashMap"+iconMap);
                    }
                });
            }
        }).start();
        //隐藏底部抽屉
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //获取marker点
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                //创建请求获取Post类
                Request request = new Request.Builder()
                        .url(url+"/getAllMarker")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        markerList = new ArrayList<>();
                        //获取响应的数据
                        String result = response.body().string();
                        //反序列化消息
                        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
                        for (JsonElement jsonElement : jsonArray) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            MarkerInfo markerInfo = new Gson().fromJson(jsonObject, MarkerInfo.class);
                            markerList.add(markerInfo);
                        }
                        Log.v("FunctionFragment", "lzx 获取的marker点数量" + markerList.size());
                        for (int i = 0; i < markerList.size(); i++) {
                            MarkerInfo markerInfo = markerList.get(i);
                            // 创建额外信息的 Bundle 对象
                            Bundle extraInfo = new Bundle();
                            extraInfo.putString("city", "Beijing");
                            addMarker(markerInfo.getLatitude(), markerInfo.getLongitude(), extraInfo);
                        }
                    }
                });
            }
        }).start();
    }

    private void initView() {
        uploadBtn = view.findViewById(R.id.uploadBtn);
        //底部抽屉
        mRecyclerView = view.findViewById(R.id.recyclerview);
        bottomSheet =  view.findViewById(R.id.bottom_sheet);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        behavior = BottomSheetBehavior.from(bottomSheet);
        frameLayout = view.findViewById(R.id.frame_layout);
        editText2 = view.findViewById(R.id.fragment_main_edt_poi);
        mSugListView = view.findViewById(R.id.fragment_sug_list);
        editText2.setThreshold(1);
        mSuggestionSearch = SuggestionSearch.newInstance();
        cityView = view.findViewById(R.id.city);
        btnLoc = view.findViewById(R.id.btn_Loc);
        //获取地图控件引用
        mMapView = view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void setListener() {
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mLocationClient = new LocationClient(getActivity().getApplicationContext());
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
                            Double latitude = bdLocation.getLatitude();
                            Double longitude = bdLocation.getLongitude();
                            city = bdLocation.getCity(); // 获取详细地址信息
                            cityView.setText(city);

                            // 在这里处理获取到的经纬度信息
                            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                            mBaiduMap.animateMapStatus(mapStatusUpdate);
                            // 设置合适的缩放级别
                            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.0f);
                            mBaiduMap.animateMapStatus(u);
                        }
                    });
                    // 开始定位
                    mLocationClient.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    mSugListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSugListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mSugListView.setVisibility(View.GONE);
                }
            }
        });
        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSugListView.setVisibility(View.GONE);
                } else {
                    mSugListView.setVisibility(View.GONE);
                }
            }
        });
        BaiduMap.OnMapTouchListener listener = new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                // 判断点击的位置是否在输入框外
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isPointInsideView(motionEvent.getRawX(), motionEvent.getRawY(), editText2)) {
                        // 输入框外部被点击，让输入框失去焦点
                        editText2.clearFocus();
                    }
                }
                //触摸地图时收回键盘
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //如果抽屉全展开，将抽屉调至折叠状态
                Log.v("FunctionFragment", "lzx 触摸时抽屉状态"+behavior.getState());
                if ((behavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) != (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        };
        //设置触摸地图事件监听者
        mBaiduMap.setOnMapTouchListener(listener);

        //当输入关键字时，动态更新建议列表
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
        //上传按钮
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==""){
                    showLoginDialog();
                }else {
                    Intent intent = new Intent(getActivity(), AddLabelActivity.class);
                    startActivity(intent);
                }
            }
        });
        //marker按钮
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //获取marker信息
                city = marker.getExtraInfo().getString("city");
                latitude = String.valueOf(marker.getPosition().latitude);
                longitude = String.valueOf(marker.getPosition().longitude);
                Log.v("FunctionFragment", "lzx点击marker");
                Log.v("FunctionFragment", "lzx 上下文环境"+FunctionFragment.this.getActivity());
                Log.v("FunctionFragment", "lzx 城市"+marker.getExtraInfo()+"经度"+marker.getPosition().longitude+"纬度"+marker.getPosition().latitude);
                //召唤底部抽屉
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                //根据经纬度信息查询数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //向服务器查询帖子
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("latitude", latitude)
                                .addFormDataPart("longitude", longitude);
                        RequestBody requestBody = builder.build();
                        Request request = new Request.Builder()
                                .url(url+"/getStrategy")
                                .post(requestBody)
                                .build();
                        Call call = client.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                //获取响应的数据
                                String result = response.body().string();
                                Log.v("mContext", "lzx onResponse: "+ result);
                                //反序列化消息
                                returnStrategyList.clear();
                                JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
                                for (JsonElement jsonElement : jsonArray) {
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    ReturnStrategy returnStrategy = new Gson().fromJson(jsonObject, ReturnStrategy.class);
                                    returnStrategyList.add(returnStrategy);
                                    Log.v("mContext", "lzx 帖子: "+ returnStrategy);
                                }
                                Log.v("mContext", "lzx 数量: "+ returnStrategyList.size());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerViewStrategyAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                }).start();

                return true;
            }
        });
        recyclerViewStrategyAdapter.setOnRespondClickListener(new RecyclerViewStrategyAdapter.onRespondClickListener() {
            @Override
            public void onRespondClick(int i) {
                //获取点击的帖子
                ReturnStrategy returnStrategy = returnStrategyList.get(i);
                Intent intent = new Intent(mContext, StrategyDisplayActivity.class);
                intent.putExtra("strategyId", returnStrategy.getStrategyId());
                //跳转页面
                startActivity(intent);
            }
        });
    }
    public void showLoginDialog() {
        // 创建AlertDialog构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("账号未登录！")
                .setMessage("是否前往登录账号")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确定”按钮后的操作
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“取消”按钮后的操作
                        dialog.dismiss(); // 关闭对话框
                    }
                });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void addMarker(Double latitude, Double longitude, Bundle extraInfo) {
        // 创建一个空的MarkerOptions对象
        LatLng point = new LatLng(latitude, longitude);
        String key = latitude+"+"+longitude;
        //根据key获取照片路径
        String path = iconMap.get(key);
        MarkerOptions markerOptions = new MarkerOptions();
        // 计算像素值
        int widthInDp = 50;
        int heightInDp = 50;

        int widthInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                widthInDp,
                getActivity().getResources().getDisplayMetrics()
        );

        int heightInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                heightInDp,
                getActivity().getResources().getDisplayMetrics()
        );
        Glide.with(getActivity())
                .asBitmap()
                .load("http://"+ip+"/travel/"+path)
                .centerCrop()
                .transform(new GlideCustomTransformation(mContext,4,mContext.getResources().getColor(R.color.white)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(widthInPixels, heightInPixels) // 设置加载图片时的大小
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        // 将Bitmap转换为BitmapDescriptor
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resource);

                        // 设置MarkerOptions的位置和图标
                        markerOptions.position(point)  // 设置位置
                                .icon(bitmapDescriptor)  // 设置图标
                                .animateType(MarkerOptions.MarkerAnimateType.grow)
                                .extraInfo(extraInfo);  // 设置额外信息

                        // 在地图上添加Marker
                        mBaiduMap.addOverlay(markerOptions);
                    }
                });
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        return (x > viewX && x < (viewX + viewWidth) && y > viewY && y < (viewY + viewHeight));
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        mMapView.setVisibility(View.VISIBLE);
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.coordinatorLayout),true);
        mMapView.onResume();
        try {
            Log.v("AddLabelActivity", "lzxAddLabelActivity页面开启");
            mLocationClient = new LocationClient(getActivity().getApplicationContext());
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
                    Double latitude = bdLocation.getLatitude();
                    Double longitude = bdLocation.getLongitude();
                    city = bdLocation.getCity(); // 获取详细地址信息
                    cityView.setText(city);

                    // 在这里处理获取到的经纬度信息
                    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                    mBaiduMap.animateMapStatus(mapStatusUpdate);
                    // 设置合适的缩放级别
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.0f);
                    mBaiduMap.animateMapStatus(u);
                }
            });
            // 开始定位
            mLocationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果通过intent传递过来，则定位到该位置
        if (getActivity().getIntent()!=null){
            Intent intent = getActivity().getIntent();

        }
        Log.v("FunctionFragment", "lzx onResume");
    }
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

                SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity().getApplicationContext(),
                        suggest,
                        R.layout.item_layout,
                        new String[]{"key", "city", "dis"},
                        new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis}
                );
                mSugListView.setVisibility(View.VISIBLE);
                mSugListView.setAdapter(simpleAdapter);
                mSugListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mBaiduMap.clear();
                        // 获取点击的建议项的数据
                        HashMap<String, String> selectedItem = suggest.get(position);

                        selectedKey = selectedItem.get("key");
                        selectedCity = selectedItem.get("city");
                        selectedDistrict = selectedItem.get("dis");
                        Log.v("AddLabelActivity", "lzx key"+selectedKey);
                        Log.v("AddLabelActivity", "lzx city"+selectedCity);
                        Log.v("AddLabelActivity", "lzx dis"+selectedDistrict);
                        System.out.println("===key" +  selectedKey + "city" + selectedCity + "dis" + selectedDistrict + "===");

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
    @Override
    public void onPause() {
        super.onPause();
        mBaiduMap.clear();
        mMapView.onPause();
        if (mLocationClient!= null) {
            mLocationClient.stop();
        }
        Log.v("FunctionFragment", "lzx onPause");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        Log.v("FunctionFragment", "lzx onDestroy");
        mMapView = null;
    }

}