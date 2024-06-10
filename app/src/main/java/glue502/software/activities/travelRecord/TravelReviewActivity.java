package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.activities.posts.PostEditActivity;
import glue502.software.adapters.TravelReviewAdapter;
import glue502.software.models.TravelReview;
import glue502.software.utils.MyViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TravelReviewActivity extends AppCompatActivity {
    private ListView travelReviewList;
    private ImageView back;
    private String url = "http://"+ip+"/travel/travel";
    private String userId;
    private boolean firstLoad = true;
    private String userStatus;
    private String authorId;
    private String authorName;
    private TextView txtReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_review);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,findViewById(R.id.top),true);
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId",MODE_PRIVATE);
        initview();
        setlistener();
        userId = sharedPreferences.getString("userId","");
        //获取传递过来的参数
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userStatus=extras.getString("userStatus");
            authorId=extras.getString("authorId");
            authorName=extras.getString("authorName");
        }
        if(userStatus.equals("1")){
            initData(userId);
        }else{
            txtReview.setText(authorName+"的足迹");
            initData(authorId);
        }

    }
    private void initview(){
        travelReviewList = findViewById(R.id.travel_review);
        txtReview = findViewById(R.id.txt_review);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
    }
    private void initData(String userId){
        //从服务器获取个人旅游回顾数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url+"/getReview?userId="+userId).build();
                try {
                    //打开连接接收数据
                    Response response = client.newCall(request).execute();
                    //处理数据
                    if(response.isSuccessful()){
                        //将response转换为List<travelReview>
                        if(response.body()!= null){
                            //将response转换为String
                            String responseData = response.body().string();
                            //将String转换为travelReview
                            List<TravelReview> travelReview = new Gson().fromJson(responseData,new TypeToken<List<TravelReview>>(){}.getType());
                            //使用travelReviewAdapter适配器将travelReview数据显示到ListView中
                            runOnUiThread(()->{
                                TravelReviewAdapter travelReviewAdapter = new TravelReviewAdapter(TravelReviewActivity.this,travelReview,R.layout.travel_review_item,userStatus);
                                travelReviewList.setAdapter(travelReviewAdapter);
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void setlistener(){


    }
    public void onClick(String travelId){
        if(userStatus.equals("1")){
            Intent intent = new Intent(this, TravelDetailActivity.class);
            intent.putExtra("travelId",travelId);
            intent.putExtra("userStatus1","1");
            startActivityForResult(intent,1);
        }else{
            Intent intent = new Intent(this, TravelDetailActivity.class);
            intent.putExtra("travelId",travelId);
            intent.putExtra("userStatus1","2");
            startActivityForResult(intent,1);
        }
    }

    @Override
    public void onResume() {
        if (!firstLoad) {
            if(userStatus.equals("1")){
                initData(userId);
            }else{
                txtReview.setText(authorName+"的足迹");
                initData(authorId);
            }
        } else {
            firstLoad = false;
        }
        super.onResume();
    }
}