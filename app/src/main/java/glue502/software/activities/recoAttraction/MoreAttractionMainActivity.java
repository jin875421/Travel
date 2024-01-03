package glue502.software.activities.recoAttraction;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.AttractionAdapter;
import glue502.software.models.RecoAttraction;
import glue502.software.utils.Carousel;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoreAttractionMainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView backButton;
    ListView listView;
    List<RecoAttraction> attractionList = new ArrayList<>();
    AttractionAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_attraction_main);
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        getViews();
        getAttractions();
    }

    private void getViews() {
        backButton = findViewById(R.id.ad_back);
        listView = findViewById(R.id.listView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ad_back:
                finish();
                break;
        }
    }
    private void getAttractions(){
//        http://localhost:8080/travel/recoAttraction/getRecoAttractionList
        // 创建 OkHttp 客户端
        OkHttpClient client = new OkHttpClient();
        // 构建请求
        Request request = new Request.Builder()
                .url("http://"+ip+"/travel/recoAttraction/getAllRecoAttraction")  // 替换为你的后端 API 地址
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("allAttraction",responseData);
                            Type RecoAttractionListType = new TypeToken<List<RecoAttraction>>(){}.getType();
                            attractionList = new Gson().fromJson(responseData,RecoAttractionListType);
                            adapter = new AttractionAdapter(MoreAttractionMainActivity.this,attractionList,R.layout.attraction_item);
                            listView.setAdapter(adapter);
                        }
                    });
                }
            }
        });
    }
}