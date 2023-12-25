package glue502.software.fragments;

import static glue502.software.activities.MainActivity.ip;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.map.AddLabelActivity;
import glue502.software.activities.map.StrategyActivity;
import glue502.software.models.Comment;
import glue502.software.models.MarkerInfo;

import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FunctionFragment extends Fragment {
    private String url="http://"+ip+"/travel/strategy/getAllMarker";
    private ImageView uploadBtn;
    private ListView listView;
    private List<MarkerInfo> markerList;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private OkHttpClient client;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocationClient.setAgreePrivacy(true);
        SDKInitializer.setAgreePrivacy(getActivity().getApplicationContext(), true);
        SDKInitializer.initialize(getActivity().getApplicationContext());

        view = inflater.inflate(R.layout.fragment_function, container, false);
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.function_top),true);
        initView();
        initData();
        setListener();

        return view;
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                //创建请求获取Post类
                Request request = new Request.Builder()
                        .url(url)
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
                        for (int i = 0; i < markerList.size(); i++) {
                            MarkerInfo markerInfo = markerList.get(i);
                            LatLng point = new LatLng(markerInfo.getLatitude(), markerInfo.getLongitude());
                            // 创建额外信息的 Bundle 对象
                            Bundle extraInfo = new Bundle();
                            extraInfo.putString("city", "Beijing");
                            addMarker(point, extraInfo);
                        }
                    }
                });
            }
        }).start();
    }

    private void initView() {
        uploadBtn = view.findViewById(R.id.uploadBtn);
        //获取地图控件引用
        mMapView = view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void setListener() {
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddLabelActivity.class);
                startActivity(intent);
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.v("FunctionFragment", "lzx点击marker");
                Log.v("FunctionFragment", "lzx 上下文环境"+FunctionFragment.this.getActivity());
                Log.v("FunctionFragment", "lzx 城市"+marker.getExtraInfo()+"经度"+marker.getPosition().longitude+"纬度"+marker.getPosition().latitude);
                Intent intent = new Intent(FunctionFragment.this.getActivity(), StrategyActivity.class);
                intent.putExtra("city", marker.getExtraInfo().getString("city"));
                intent.putExtra("latitude", String.valueOf(marker.getPosition().latitude));
                intent.putExtra("longitude", String.valueOf(marker.getPosition().longitude));
                startActivity(intent);
                return true;
            }
        });
    }

    private void addMarker(LatLng point, Bundle extraInfo) {
        // 添加Marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(point)
                .extraInfo(extraInfo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.function_top),true);
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

}