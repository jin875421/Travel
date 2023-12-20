package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.TravelDetailAdapter;
import glue502.software.models.TravelReview;
import glue502.software.models.travelRecord;
import glue502.software.utils.MyViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TravelDetailActivity extends AppCompatActivity {
    private String travelId;
    private TextView travelName;
    private List<travelRecord> travelRecords;
    private ListView travelRecordList;
    private ImageView back;
    private String url = "http://"+ip+"/travel/travel/showATravel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView());
        //获取上页面传过来的travelId
        travelId = getIntent().getStringExtra("travelId");
        initView();
        setlistener();
        initData();
    }
    public void initView(){
        travelName = findViewById(R.id.travel_title);
        travelRecordList = findViewById(R.id.list_view);
        back = findViewById(R.id.btn_back);
    }
    public void initData(){
        //获取数据
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url+"?travelId="+travelId).build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    travelRecords = new Gson().fromJson(responseData,new TypeToken<List<travelRecord>>(){}.getType());
                    //打开ui线程
                    runOnUiThread(()->{
                        travelName.setText(travelRecords.get(0).getTravelName());
                        TravelDetailAdapter travelRecordAdapter = new TravelDetailAdapter(TravelDetailActivity.this,travelRecords,R.layout.review_item);
                        travelRecordList.setAdapter(travelRecordAdapter);
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public void setlistener(){
        back.setOnClickListener(v -> finish());
    }
}