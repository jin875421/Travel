package glue502.software.activities.map;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import glue502.software.R;
import glue502.software.adapters.StrategyAdapter;
import glue502.software.models.Comment;
import glue502.software.models.ReturnStrategy;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StrategyActivity extends AppCompatActivity {
    private String url="http://"+ip+"/travel/strategy";
    private OkHttpClient client = new OkHttpClient();
    private ListView listView;
    private String latitude;
    private String longitude;
    private String city;
    private List<ReturnStrategy> returnStrategyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy);
        initData();
        initView();
        setListener();
    }

    private void initData() {
        //获取intent的数据
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        Log.v("StrategyActivity", "lzx StrategyActivity接受到的信息"+city+latitude+" "+longitude);
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
                        Log.v(StrategyActivity.class.getName(), "onResponse: "+ result);
                        //反序列化消息
                        returnStrategyList = new ArrayList<>();
                        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
                        for (JsonElement jsonElement : jsonArray) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            ReturnStrategy returnStrategy = new Gson().fromJson(jsonObject, ReturnStrategy.class);
                            returnStrategyList.add(returnStrategy);
                        }
                        //绑定adapter
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StrategyAdapter strategyAdapter = new StrategyAdapter(StrategyActivity.this, R.layout.activity_strategy_adapter,  returnStrategyList);
                                listView.setAdapter(strategyAdapter);
                            }
                        });
                    }
                });
                //绑定监听器

            }
        }).start();
    }

    private void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击的帖子
                ReturnStrategy returnStrategy = returnStrategyList.get(position);
                Intent intent = new Intent(StrategyActivity.this, StrategyDisplayActivity.class);
                intent.putExtra("strategyId", returnStrategy.getStrategyId());
                //跳转页面
                startActivity(intent);
            }
        });
    }

    private void initView() {
        listView = findViewById(R.id.list_view);
    }
}