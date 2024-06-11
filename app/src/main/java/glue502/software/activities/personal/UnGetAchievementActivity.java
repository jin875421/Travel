package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.AchievementAdapter;
import glue502.software.models.Achievement;
import glue502.software.utils.MyViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UnGetAchievementActivity extends AppCompatActivity {
    private static final String TAG = "UnGetAchievementActivity";
    private String url = "http://" + ip + "/travel";
    private String userId;
    private Handler handler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private AchievementAdapter achievementAdapter;
    private List<Achievement> achievementList;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_get_achievement);

        SharedPreferences sharedPreferences = this.getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);
        initView();
        setListener();
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request achievementRequest = new Request.Builder()
                        .url(url + "/achievement/getUnattained?userId=" + userId)
                        .build();
                // 同步请求
                try {
                    Response response= client.newCall(achievementRequest).execute();
                    if(response.isSuccessful()){
                        ResponseBody responseBody = response.body();
                        if (responseBody != null){
                            String responseData = responseBody.string();
                            Log.i(TAG, "===获取未获得的成就列表==="+responseData);
                            achievementList = Achievement.parseAchievement(responseData);
                            handler.post(new Runnable() {
                                public void run() {
                                    achievementAdapter = new AchievementAdapter(UnGetAchievementActivity.this, achievementList);
                                    recyclerView.setAdapter(achievementAdapter);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    Log.i(TAG, "===获取未获得的成就列表===失败");
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 刷新框架
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        btnBack = findViewById(R.id.back);
    }
}