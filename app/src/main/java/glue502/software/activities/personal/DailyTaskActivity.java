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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.AchievementAdapter;
import glue502.software.adapters.UserDailyTaskAdapter;
import glue502.software.models.Achievement;
import glue502.software.models.UserDailyTask;
import glue502.software.utils.MyViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DailyTaskActivity extends AppCompatActivity {
    private static final String TAG = "DailyTaskActivity";
    private String url = "http://" + ip + "/travel";
    private String userId;
    private Handler handler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private ImageView btnBack;
    private List<UserDailyTask> userDailyTaskList;
//    private AchievementAdapter achievementAdapter;
//    private List<Achievement> achievementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_task);

        SharedPreferences sharedPreferences = this.getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this, findViewById(R.id.title), true);

        initView();
        initData();
        setListener();

//        coinCount.setText(String.valueOf(coins));
//        signInText.setText("打卡\n+" + (signIn == 0 ? "30" : "完成"));
//        browsePostsText.setText("浏览3个帖子\n+" + (browsePosts < 3 ? "20" : "完成"));
//        likePostsText.setText("完成5次点赞\n+" + (likePosts < 5 ? "30" : "完成"));
//        sharePostText.setText("分享帖子\n+" + (sharePost == 0 ? "10" : "完成"));

//        signInProgress.setProgress(signIn == 0 ? 0 : 100);
//        browsePostsProgress.setProgress((browsePosts * 100) / 3);
//        likePostsProgress.setProgress((likePosts * 100) / 5);
//        sharePostProgress.setProgress(sharePost == 0 ? 0 : 100);
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

    private void initData() {
        new Thread(new Runnable() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "/userDailyTask/getTasks?userId="+userId)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String responseData = response.body().string();
                        Log.i(TAG, "===获取每日任务列表==="+responseData);
                        userDailyTaskList = UserDailyTask.parseJson(responseData);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                UserDailyTaskAdapter userDailyTaskAdapter = new UserDailyTaskAdapter(
                                        DailyTaskActivity.this,
                                        userDailyTaskList);
                                recyclerView.setAdapter(userDailyTaskAdapter);
                            }
                        });
                    } else {
                        Log.i(TAG, "===获取每日任务列表失败===");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        btnBack = findViewById(R.id.back);
    }
}