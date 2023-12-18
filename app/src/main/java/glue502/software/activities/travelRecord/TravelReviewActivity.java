package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.TravelReviewAdapter;
import glue502.software.models.TravelReview;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TravelReviewActivity extends AppCompatActivity {
    private ListView travelReviewList;
    private ImageView back;
    private String url = "http://"+ip+"/travel/travel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_review);
        SharedPreferences sharedPreferences = getSharedPreferences("userId",MODE_PRIVATE);
        initview();
        setlistener();
        initData(sharedPreferences.getString("userId",""));
    }
    private void initview(){
        travelReviewList = findViewById(R.id.travel_review);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
    }
    private void initData(String userId){
        //从服务器获取个人旅游回顾数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url+"/showTravels?userId="+userId).build();
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
                                TravelReviewAdapter travelReviewAdapter = new TravelReviewAdapter(TravelReviewActivity.this,travelReview,R.layout.travel_review_item);
                                travelReviewList.setAdapter(travelReviewAdapter);
                            });
                        }

                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public void setlistener(){
        travelReviewList.setOnItemClickListener((parent, view, position, id) -> {
            TravelReviewAdapter travelReviewAdapter = (TravelReviewAdapter) parent.getAdapter();
            TravelReview travelReview = (TravelReview) travelReviewAdapter.getItem(position);
            //将travelReview数据传递到TravelReviewActivity中
            Intent intent = new Intent(TravelReviewActivity.this,TravelDetailActivity.class);
            intent.putExtra("travelReviewId",travelReview.getTravelId());
            startActivity(intent);
        });
    }
}