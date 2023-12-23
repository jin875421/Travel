package glue502.software.fragments;

import static glue502.software.activities.MainActivity.ip;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import glue502.software.R;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.activities.travelRecord.TravelAlbumActivity;
import glue502.software.activities.travelRecord.travelRecordActivity;
import glue502.software.activities.travelRecord.TravelReviewActivity;
import glue502.software.models.UserInfo;
import glue502.software.utils.Carousel;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendFragment extends Fragment {
    private Button createBtn;
    private Button reviewBtn;
    private LinearLayout rltlCreate;
    private LinearLayout rltlFootprint;
    private LinearLayout rltlPhoto;
    private View view;
    private ViewPager2 vp2;
    private LinearLayout dotLinerLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend,container,false);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.top),true);
        rltlCreate = view.findViewById(R.id.lrlt_create);
        rltlFootprint = view.findViewById(R.id.lrlt_footprint);
        rltlPhoto=view.findViewById(R.id.lrlt_photo);
        vp2 = view.findViewById(R.id.reco_vp2);
        dotLinerLayout = view.findViewById(R.id.index_dot);
        setlistener();
        date();
        getCarousel();
        return view;
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
                // 在 UI 线程中更新
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Reco",responseData);
                        //Carousel为自定义轮播图工具类
                        Carousel carousel = new Carousel(getActivity(), dotLinerLayout, vp2,"ruoyi/uploadPath/");
                        carousel.initViews1(responseData);
                    }
                });
            }
        });

    }

    private void date() {



    }

    public void setlistener(){
        rltlCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), travelRecordActivity.class);
                startActivity(intent);
            }
        });
        rltlFootprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TravelReviewActivity.class);
                startActivity(intent);
            }
        });
        rltlPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TravelAlbumActivity.class);
                startActivity(intent);
            }
        });

    }

    //生命周期管理
    @Override
    public void onResume() {
        super.onResume();
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.top),true);
    }
}