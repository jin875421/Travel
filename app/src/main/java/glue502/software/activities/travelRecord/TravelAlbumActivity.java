package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.MainActivity;
import glue502.software.adapters.TravelAlbumAdapter;
import glue502.software.adapters.TravelReviewAdapter;
import glue502.software.models.Result;
import glue502.software.models.ShowPicture;
import glue502.software.models.TravelReview;
import glue502.software.utils.MyViewUtils;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TravelAlbumActivity extends AppCompatActivity {

    private String url = "http://"+ip+"/travel/travel";

    private List<ShowPicture> list1,list2,list3,list4;

    private GridView gridView1,gridView2,gridView3,gridView4;

    //这里要添加文字的控件对象，用于修改文本的字体格式
    private TextView text1,text2,text3,text4;

    TravelAlbumAdapter t1,t2,t3,t4;

    private ImageView back;

    //这是加载动画的控件
    private ProgressBar pbPicture;
    //这是加载动画的背景
    private RelativeLayout rlBackground;

    private LocalDate localDate2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_album);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);

        findViews();
        setlistener();

        // 发送网络请求
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();
        new RequestAsyncTask().execute();


    }

    private void setlistener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class RequestAsyncTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {

            pbPicture.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // 创建一个OkHttpClient对象
                OkHttpClient client = new OkHttpClient();

                //这里当不了全局变量，就在这里做一个试验
                SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId",MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId","");

                RequestBody requestBody = new FormBody.Builder()
                        .add("userId", userId)
                        .build();

                // 构建请求
                Request request = new Request.Builder()
                        .url(url+"/showPictures")
                        .post(requestBody)
                        .build();

                // 发送请求并获取响应
                Response response = client.newCall(request).execute();

                // 解析响应数据
                if (response.isSuccessful()) {
//                    System.out.println(response.body().string());
                    return response.body().string();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            // 在这里处理返回的数据
            if (response != null) {
                // 更新UI或者进行其他操作
                //在这里将数据分配给四个不同的列表
                //首先将数据转换类型
                List<ShowPicture> showPictures = new ArrayList<>();
                showPictures = new Gson().fromJson(response, new TypeToken<List<ShowPicture>>(){}.getType());

                //然后将这个列表中的数据分配给四个列表,按照时间分配
                //遍历这个列表
                Date date = new Date();
                LocalDate localDate1 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


                for(ShowPicture showPicture:showPictures){
                    String date1 = showPicture.getTravelDate();
                    //这里尝试改善，用其他方式来将字符串类型转化成Date类型
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date date2 = sdf.parse(date1);
                        System.out.println(date2);
                        localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //判断这个时间是否在当前时间的一个月之内，ture则代表是
                    boolean isAtLeastOneMonthBefore = localDate2.isAfter(localDate1.minusMonths(1));
                    if(isAtLeastOneMonthBefore){
                        //填充到list1中
                        list1.add(showPicture);
                    }

                    //判断是否在半年内,但不在一个月内
                    boolean isWithinSixMonths = localDate1.isAfter(localDate2.minusMonths(6));
                    if(isWithinSixMonths && !isAtLeastOneMonthBefore){
                        //填充到list2中
                        list2.add(showPicture);
                    }

                    //判断是否在一年内，但不在半年内
                    boolean isWithinOneYear = localDate1.isAfter(localDate2.minusYears(1));
                    if(isWithinOneYear && !isWithinSixMonths){
                        //填充到list2中
                        list3.add(showPicture);
                    }

                    //判断是否在一年多以前
                    if(!isWithinOneYear){
                        //填充到list2中
                        list4.add(showPicture);
                    }

                }

                //在这里要将文本先隐藏，在文本下有数据的时候才将文本显现出来
                text1.setVisibility(View.GONE);
                text2.setVisibility(View.GONE);
                text3.setVisibility(View.GONE);
                text4.setVisibility(View.GONE);

                //如果有数据就显现出来
                if(list1.size() != 0){
                    text1.setVisibility(View.VISIBLE);
                }
                if(list2.size() != 0){
                    text2.setVisibility(View.VISIBLE);
                }
                if(list3.size() != 0){
                    text3.setVisibility(View.VISIBLE);
                }
                if(list4.size() != 0){
                    text4.setVisibility(View.VISIBLE);
                }


                t1 = new TravelAlbumAdapter(list1,TravelAlbumActivity.this,R.layout.travel_album);
                t2 = new TravelAlbumAdapter(list2,TravelAlbumActivity.this,R.layout.travel_album);
                t3 = new TravelAlbumAdapter(list3,TravelAlbumActivity.this,R.layout.travel_album);
                t4 = new TravelAlbumAdapter(list4,TravelAlbumActivity.this,R.layout.travel_album);
                System.out.println(list1);
                gridView1.setAdapter(t1);
                gridView2.setAdapter(t2);
                gridView3.setAdapter(t3);
                gridView4.setAdapter(t4);

                // 数据加载完成后隐藏加载动画
                pbPicture.setVisibility(View.GONE);
                rlBackground.setVisibility(View.GONE);

            } else {
                // 处理请求失败的情况
            }
        }
    }
    private void findViews() {
        gridView1 = findViewById(R.id.gv_view1);
        gridView2 = findViewById(R.id.gv_view2);
        gridView3 = findViewById(R.id.gv_view3);
        gridView4 = findViewById(R.id.gv_view4);
        back = findViewById(R.id.back);
        pbPicture = findViewById(R.id.pb_picture);
        rlBackground = findViewById(R.id.rl_background);
        text1 = findViewById(R.id.tv_t1);
        text2 = findViewById(R.id.tv_t2);
        text3 = findViewById(R.id.tv_t3);
        text4 = findViewById(R.id.tv_t4);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/幼圆.TTF");
        text1.setTypeface(typeface);
        text2.setTypeface(typeface);
        text3.setTypeface(typeface);
        text4.setTypeface(typeface);

    }
}