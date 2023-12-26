package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.TravelAlbumAdapter;
import glue502.software.adapters.TravelReviewAdapter;
import glue502.software.models.Result;
import glue502.software.models.ShowPicture;
import glue502.software.models.TravelReview;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TravelAlbumActivity extends AppCompatActivity {
//
//    SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId",MODE_PRIVATE);
//    String userId = sharedPreferences.getString("userId","");
//    String userId = "111111";

    private String url = "http://"+ip+"/travel/travel";

    private List<ShowPicture> list;
    private List<ShowPicture> list1,list2,list3,list4;

    private GridView gridView1,gridView2,gridView3,gridView4;

    //这里要添加文字的控件对象，用于修改文本的字体格式
    private TextView text1,text2,text3,text4;

    TravelAlbumAdapter t1,t2,t3,t4;

    //这是一个用于测试的数据源，只有图片，看能不能在相册页面中显示出来
    private List<String> list10 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_album);

        //在这个页面显示很多组照片
        //首先准备数据源,然后将数据源放到适配器当中

        //实现全屏，去掉页面上面蓝色标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViews();

        //初始化数据源
//        initData();

        //获取到userId

//        initData(sharedPreferences.getString("userId",""));

        // 发送网络请求
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();
        new RequestAsyncTask().execute();

//        //现在分配适配器
//        t1 = new TravelAlbumAdapter(list1,this,R.layout.travel_album);
//        t2 = new TravelAlbumAdapter(list2,this,R.layout.travel_album);
//        t3 = new TravelAlbumAdapter(list3,this,R.layout.travel_album);
//        t4 = new TravelAlbumAdapter(list4,this,R.layout.travel_album);
//        System.out.println(list1);
//        gridView1.setAdapter(t1);
//        gridView2.setAdapter(t2);
//        gridView3.setAdapter(t3);
//        gridView4.setAdapter(t4);


    }






    private class RequestAsyncTask extends AsyncTask<Void, Void, String> {

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

//                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+result);

                //然后将这个列表中的数据分配给四个列表,按照时间分配
                //遍历这个列表
                Date date = new Date();
                LocalDate localDate1 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


                for(ShowPicture showPicture:showPictures){
                    Date date1 = showPicture.getTravelDate();
                    LocalDate localDate2 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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


            } else {
                // 处理请求失败的情况
            }
        }
    }






//    private void initData(String userId){
//        //在这里实现向服务器发送请求，并且将返回过来的数据按照日期的分来排列在四个列表中
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder().url(url+"/showPictures?userId="+userId).build();
//                try {
//                    //打开连接接收数据
//                    Response response = client.newCall(request).execute();
//                    //处理数据
//                    if(response.isSuccessful()){
//                        //将response转换为List<travelReview>
//                        if(response.body()!= null){
//                            //将response转换为Result
//                            String responseData = response.body().string();
//                            //将String转换为travelReview
//                            List<TravelReview> travelReview = new Gson().fromJson(responseData,new TypeToken<List<TravelReview>>(){}.getType());
//
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//    private void initData() {
//
//        list1 = new ArrayList<>();
//        list2 = new ArrayList<>();
//        list3 = new ArrayList<>();
//        list4 = new ArrayList<>();
//
//        ShowPicture sp = new ShowPicture();
//        Date date = new Date();
//        sp.setTravelDate(date);
//        sp.setPlaceName("河北师范大学");
//        List<String> li = new ArrayList<>();
//        li.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
//        li.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
//        li.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");
//        li.add("https://picdm.sunbangyan.cn/2023/12/20/fc8965d2da51b0a191386036b824d942.jpeg");
//        li.add("https://picst.sunbangyan.cn/2023/12/20/fefbfc6cbd0e10c7cb11727dc5f2cfae.jpeg");
//        li.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
//        li.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
//        li.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");
//        sp.setPicturePath(li);
//        list1.add(sp);
//        list2.add(sp);
//        list3.add(sp);
//        list4.add(sp);
//    }

    private void findViews() {
        gridView1 = findViewById(R.id.gv_view1);
        gridView2 = findViewById(R.id.gv_view2);
        gridView3 = findViewById(R.id.gv_view3);
        gridView4 = findViewById(R.id.gv_view4);

        text1 = findViewById(R.id.tv_t1);
        text2 = findViewById(R.id.tv_t2);
        text3 = findViewById(R.id.tv_t3);
        text4 = findViewById(R.id.tv_t4);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/幼圆.TTF");
        text1.setTypeface(typeface);
        text2.setTypeface(typeface);
        text3.setTypeface(typeface);
        text4.setTypeface(typeface);


//        //在这里要将文本先隐藏，在文本下有数据的时候才将文本显现出来
//        text1.setVisibility(View.GONE);
//        text2.setVisibility(View.GONE);
//        text3.setVisibility(View.GONE);
//        text4.setVisibility(View.GONE);
    }
}