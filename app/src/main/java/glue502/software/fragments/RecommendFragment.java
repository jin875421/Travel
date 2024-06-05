package glue502.software.fragments;

import static glue502.software.activities.MainActivity.ip;

import static android.content.Context.MODE_PRIVATE;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import com.baidu.mapapi.search.weather.WeatherSearchLocation;
import com.baidu.mapapi.search.weather.WeatherSearchOption;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.login.LoginActivity;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.activities.posts.PostSearchActivity;
import glue502.software.activities.travelRecord.FunctionActivity;
import glue502.software.activities.travelRecord.SearchActivity;
import glue502.software.activities.travelRecord.TravelAlbumActivity;
import glue502.software.activities.travelRecord.WeatherActivity;
import glue502.software.activities.travelRecord.TravelReviewActivity;
import glue502.software.activities.travelRecord.travelRecordActivity;
import glue502.software.adapters.PostListAdapter;
import glue502.software.models.Post;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.UserInfo;
import glue502.software.utils.Carousel;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RecommendFragment extends Fragment {
    private LinearLayout rltlCreate;
    private LinearLayout rltlFootprint;
    private LinearLayout rltlPhoto;
    private LinearLayout lrltMore;
    private TextView txtLocation;
    private View view;
    private ViewPager2 vp2;
    private LinearLayout dotLinerLayout;
    private String status;
    private String city;
    private ListView localPost;
    private TextView localCity,attractionName;
    private List<Post> posts;
    private List<UserInfo> userInfos;
    private ListView listView;
    private SmartRefreshLayout refreshLayout;
    private String searchUrl="http://"+ip+"/travel/posts/search";
    private String officialUrl="http://"+ip+"/travel/posts/getMypostlist?userId=wanlilu";
    public LocationClient mLocationClient = null;
    SharedPreferences preferences ;
    private Carousel carousel;
    private final Handler handler = new Handler(Looper.getMainLooper());

    //顶部渐变控件
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView search;
    private TextView weather;
    private WeatherResult mWeatherResult;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend,container,false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        preferences = getActivity().getSharedPreferences("local", MODE_PRIVATE);
        status = sharedPreferences.getString("status","");
        rltlCreate = view.findViewById(R.id.lrlt_create);
        rltlFootprint = view.findViewById(R.id.lrlt_footprint);
        rltlPhoto=view.findViewById(R.id.lrlt_photo);
        lrltMore = view.findViewById(R.id.lrlt_more);
        localPost = view.findViewById(R.id.local_post);
        vp2 = view.findViewById(R.id.reco_vp2);
        dotLinerLayout = view.findViewById(R.id.index_dot);
        localCity = view.findViewById(R.id.local_city);
        listView = view.findViewById(R.id.local_post);
        search = view.findViewById(R.id.et_searchtext);
        attractionName = view.findViewById(R.id.attraction_name);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        weather = view.findViewById(R.id.weather);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getCity();
                refreshlayout.finishRefresh(3000/*,false*/);//传入false表示刷新失败
            }
        });

        //顶部渐变控件
        toolbar = view.findViewById(R.id.toolbar);
        appBarLayout=view.findViewById(R.id.appbar);

        getCity();
        setlistener();
        date();
//        //轮播图
        getCarousel();
//        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.top),true);
        MyViewUtils.setISBarWithoutView(getActivity(),true);
        // 获取状态栏高度
        int statusBarHeight = getStatusBarHeight();
        // 获取 Toolbar 实例

        // 动态设置 Toolbar 的高度
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height = statusBarHeight + getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        toolbar.setLayoutParams(params);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                toolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.white),Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange()));

                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // 完全折叠，显示ImageView
//                    lsda.setVisibility(View.VISIBLE);

                } else {
                    // 非完全折叠，隐藏ImageView
//                    lsda.setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }

    private void getCity(){
        //初始化了mLocationClient
        try {
            mLocationClient.setAgreePrivacy(true);
            mLocationClient = new LocationClient(getActivity().getApplicationContext());
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
                            weather.setText(weatherResult.getRealTimeWeather().getPhenomenon()+"  "+weatherResult.getRealTimeWeather().getTemperature()+"℃   ");
                            mWeatherResult = weatherResult;
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

                                preferences.edit().putString("city", city).apply();
                                // 城市信息
                                localCity.setText(city);
                                //删除city中的“市”字
                                if (city.contains("市")) {
                                    city = city.substring(0, city.indexOf("市"));
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        OkHttpClient client = new OkHttpClient();
                                        //创建请求获取Post类
                                        Request request = new Request.Builder()
                                                .url(searchUrl+"?searchText="+city)
                                                .build();
                                        try {
                                            //发起请求并获取响应
                                            Response response = client.newCall(request).execute();
                                            //检测响应是否成功
                                            if (response.isSuccessful()){
                                                //获取响应数据
                                                ResponseBody responseBody = response.body();
                                                if (responseBody!=null){
                                                    //处理数据
                                                    String responseData = responseBody.string();
                                                    Gson gson = new Gson();
                                                    List<PostWithUserInfo> postWithUserInfoList = gson.fromJson(responseData,new TypeToken<List<PostWithUserInfo>>(){}.getType());
                                                    if (postWithUserInfoList.size()<=0){
                                                        //加载官方的帖子
                                                         new Thread(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 Request request1 = new Request.Builder()
                                                                         .url(officialUrl)
                                                                         .build();
                                                                 try {
                                                                     Response response1 = client.newCall(request1).execute();
                                                                     if (response1.isSuccessful()){
                                                                         ResponseBody responseBody1 = response1.body();
                                                                         if (responseBody1!=null){
                                                                             String responseData1 = responseBody1.string();
                                                                             Gson gson1 = new Gson();
                                                                             List<PostWithUserInfo> postList = gson1.fromJson(responseData1,new TypeToken<List<PostWithUserInfo>>(){}.getType());
                                                                             posts = new ArrayList<>();
                                                                             userInfos = new ArrayList<>();
                                                                             for (PostWithUserInfo postWithUserInfo: postList){
                                                                                 posts.add(postWithUserInfo.getPost());
                                                                                 userInfos.add(postWithUserInfo.getUserInfo());
                                                                                 handler.post(new Runnable() {
                                                                                     @Override
                                                                                     public void run() {
                                                                                         if (posts!=null&&userInfos!=null){
                                                                                             if (getActivity() != null) {
                                                                                                 // 在这里使用 getActivity() 获取上下文进行操作
                                                                                                 PostListAdapter postAdapter = new PostListAdapter(getActivity(),R.layout.post_item,posts,userInfos);
                                                                                                 listView.setAdapter(postAdapter);
                                                                                                 setListViewHeightBasedOnChildren(listView);
                                                                                             } else {
                                                                                                 // 处理上下文为 null 的情况，可以考虑给出提示或者其他处理方式
                                                                                             }


                                                                                         }else {

                                                                                         }
                                                                                     }
                                                                                 });
                                                                             }
                                                                         }
                                                                     }
                                                                 }catch (Exception e){
                                                                     e.printStackTrace();
                                                                 }
                                                             }
                                                         }).start();}else {
                                                        posts = new ArrayList<>();
                                                        userInfos = new ArrayList<>();
                                                        for (PostWithUserInfo postWithUserInfo: postWithUserInfoList){
                                                            posts.add(postWithUserInfo.getPost());
                                                            userInfos.add(postWithUserInfo.getUserInfo());
                                                            handler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (posts!=null&&userInfos!=null){
                                                                        if (getActivity() != null) {
                                                                            // 在这里使用 getActivity() 获取上下文进行操作
                                                                            PostListAdapter postAdapter = new PostListAdapter(getActivity(),R.layout.post_item,posts,userInfos);
                                                                            listView.setAdapter(postAdapter);
                                                                            setListViewHeightBasedOnChildren(listView);
                                                                        } else {
                                                                            // 处理上下文为 null 的情况，可以考虑给出提示或者其他处理方式
                                                                        }
                                                                    }else {

                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }

                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
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
    private void getCarousel(){
//        http://localhost:8080/travel/recoAttraction/getRecoAttractionList
        // 创建 OkHttp 客户端
        OkHttpClient client = new OkHttpClient();
        // 构建请求
        Request request = new Request.Builder()
                .url("http://"+ip+"/travel/recoAttraction/getRecoAttractionList")  // 替换为你的后端 API 地址
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 处理请求失败的情况
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                if (!responseData.equals("[]")){
                    // 在 UI 线程中更新
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Reco",responseData);
                            //Carousel为自定义轮播图工具类
                            carousel = new Carousel(getActivity(), dotLinerLayout, vp2,"",attractionName);
                            carousel.initViews1(responseData);
                        }
                    });
                }
            }
        });
    }

    private void date() {



    }

    //顶部渐变控件

    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }
    // 获取状态栏高度
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    //--------------------------------------------------------------------------

    public void setlistener(){
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转天气详情页面，可以查看天气预报
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                //传输weatherResult
                intent.putExtra("weatherResult",mWeatherResult);
                intent.putExtra("city",city);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostSearchActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                PostListAdapter postListAdapter = (PostListAdapter) parent.getAdapter();
                //获取点击项数据对象
                PostWithUserInfo clickItem = (PostWithUserInfo) postListAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), PostDisplayActivity.class);
                intent.putExtra("postwithuserinfo", clickItem);
                startActivityForResult(intent,1);
            }
        });
        lrltMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FunctionActivity.class);
                startActivity(intent);
            }
        });
        rltlCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==""){
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

                }else{
                    SharedPreferences Preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String travelName = Preferences.getString("TravelName","");
                    if (travelName.equals("")){
                        // 弹出输入框
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogStyle);
                        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.input_dialog, null);
                        builder.setTitle("为你的旅行命个名吧")
                               .setView(view1)
                                .setNegativeButton("暂时不了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“取消”按钮后的操作
                                        dialog.dismiss(); // 关闭对话框
                                    }
                                })
                               .setPositiveButton("开始旅程", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“确定”按钮后的操作
                                        // 获取输入框中的内容
                                        EditText editText = view1.findViewById(R.id.editText);
                                        //将travelName存入sharedPreferences
                                        String travelName = editText.getText().toString();
                                        SharedPreferences.Editor editor = Preferences.edit();
                                        editor.putString("TravelName",travelName);
                                        editor.apply();
                                        Intent intent = new Intent(getActivity(), travelRecordActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        builder.show();
                    }else {
                        Intent intent = new Intent(getActivity(), travelRecordActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        rltlFootprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==""){
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

                }else{
                    Intent intent = new Intent(getActivity(), TravelReviewActivity.class);
                    startActivity(intent);
                }
            }
        });
        rltlPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==""){
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

                }else{
                    Intent intent = new Intent(getActivity(), TravelAlbumActivity.class);
                    startActivity(intent);
                }
            }
        });
        //注册轮播图的滚动事件监听器
        vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //当页面空闲状态被改变的时候
                if (state == vp2.SCROLL_STATE_IDLE) {
                    carousel.mHandler.postDelayed(carousel.runnable, 4000);//延时3秒，自动轮播图片
                } else {
                    carousel.mHandler.removeCallbacks(carousel.runnable);//用户手指触摸页面，停止自动轮播图片
                }
            }
        });
    }

    //生命周期管理
    @Override
    public void onResume() {
        super.onResume();
        if(carousel!=null){
            carousel.mHandler.postDelayed(carousel.runnable,3000);//延时2秒，自动轮播图片
        }
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.top),true);
    }

    /* 当应用被暂停时，让轮播图停止轮播 */
    @Override
    public void onPause() {
        super.onPause();
        if(carousel!=null){
            carousel.mHandler.removeCallbacks(carousel.runnable);
        }
    }
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null||listAdapter.getCount()<1) {
            //铺满屏幕
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}