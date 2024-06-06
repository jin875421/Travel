package glue502.software.activities.photoMeld;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import glue502.software.R;
import glue502.software.models.Personal;
import glue502.software.models.PictureEdit;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowPictureEdit extends AppCompatActivity {
    private String loadUrl="http://"+ip+"/travel/pictureEdit/getPictureList";
    private String url="http://"+ip+"/travel/";
    private List<String> imageUrls = new ArrayList<>();
    private ShowPictureResultAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showpicture_result);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 设置两列
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        MyViewUtils.setImmersiveStatusBar(this,findViewById(R.id.recyclerView),true);
        loadPictureList(userId);
        adapter = new ShowPictureResultAdapter(this, imageUrls);
        recyclerView.setAdapter(adapter);
    }

    private void loadPictureList(String userId) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .build();
        // 构建POST请求
        Request request = new Request.Builder()
                .url(loadUrl)
                .post(requestBody)
                .build();
        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 处理请求失败的情况
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<PictureEdit>>() {}.getType();
                List<PictureEdit> pictureEditList = gson.fromJson(responseData, listType);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在这里处理返回的 pictureEditList
                        // 例如：更新 RecyclerView 的适配器
                        if (pictureEditList != null) {
                            for (PictureEdit pictureEdit : pictureEditList){
                                imageUrls.add(url+pictureEdit.getPictureUrl());
                                System.out.println(url+pictureEdit.getPictureUrl());
                            }
                            adapter.notifyDataSetChanged();  // 通知适配器数据变化
                        }
                    }
                });
            }
        });
    }

}
