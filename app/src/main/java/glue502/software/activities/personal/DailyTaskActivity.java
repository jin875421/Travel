package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.AchievementAdapter;
import glue502.software.adapters.UserDailyTaskAdapter;
import glue502.software.models.Achievement;
import glue502.software.models.UserDailyTask;
import glue502.software.models.UserExtraInfo;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DailyTaskActivity extends AppCompatActivity {
    private static final String TAG = "DailyTaskActivity";
    private String urlAvatar="http://"+ip+"/travel/user/getAvatar?userId=";
    private String url = "http://" + ip + "/travel";
    private String urlLoadImage="http://"+ip+"/travel/";
    private String userId;
    private Handler handler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private ImageView btnBack;
    private UserExtraInfo userExtraInfo;
    private List<UserDailyTask> userDailyTaskList;
    private TextView user_name,TvLevel,signInStatu;
    private ImageView signIn,avatar;
    private ProgressBar progressBar;
    private String userName;
    private String status;

//    private AchievementAdapter achievementAdapter;
//    private List<Achievement> achievementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_task);

        SharedPreferences sharedPreferences = this.getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        userName = sharedPreferences.getString("userName", "");
        status=sharedPreferences.getString("status","");

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
        //设置签到点击事件
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("userId", userId)
                                .addFormDataPart("tag","每日登录打卡")
                                .build();
                        Request request = new Request.Builder()
                                .url(url + "/userDailyTask/updateByUserIdAndTag")
                                .post(requestBody)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful()){
                                //获取字符串
                                String responseData = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DailyTaskActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                                        signInStatu.setText("已连续签到"+responseData+"天");
                                        signIn.setEnabled(false);
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
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
        if (!status.equals("")){
            loadUserAvatar(true);
            loadUserExtraInfo();
        }

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
                                //遍历list
                                for (int i = 0; i < userDailyTaskList.size(); i++) {
                                    UserDailyTask task = userDailyTaskList.get(i);
                                    if (task.getTaskName().equals("每日登录打卡")) {
                                        //执行时间判断
                                        if(task!=null){
                                            // 如果任务中的时间戳小于当前时间戳(只判断日期)，进度重置在发送给用户，同步更新数据库
                                            LocalDate localDate;
                                            LocalDate nowDate = LocalDate.now(ZoneId.of("Asia/Shanghai")).atStartOfDay().toLocalDate();
                                            localDate = Instant.ofEpochMilli(task.getLastUpdated().getTime())
                                                    .atZone(ZoneId.of("Asia/Shanghai")) // 指定时区为上海，代表北京时区
                                                    .toLocalDate();
                                            if (!localDate.isBefore(nowDate)&&task.isCompleted()) {
                                                //设置已打卡
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        signInStatu.setText("已连续签到"+task.getProgress()+"天");
                                                        //设置签到键不可点击
                                                        signIn.setEnabled(false);
                                                    }
                                                });
                                            }
                                        }

                                    }
                                }
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
    public void checkAndUpdateTask(UserDailyTask task) {
        // 获取当前时间戳
        Timestamp now = new Timestamp(System.currentTimeMillis());
        // 获取当前日期（上海时区）
        LocalDate nowDate = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        // 获取任务最后更新时间的日期（上海时区）
        LocalDate lastUpdatedDate = Instant.ofEpochMilli(task.getLastUpdated().getTime())
                .atZone(ZoneId.of("Asia/Shanghai"))
                .toLocalDate();

        // 判断任务的最后更新时间是否在当前日期之前
        if (lastUpdatedDate.isBefore(nowDate)) {
            // 重置任务进度
            task.setProgress(0);
            task.setCompleted(false);
            task.setLastUpdated(now);

        }
    }
    private void initView() {

        user_name = findViewById(R.id.txt_name);
        TvLevel = findViewById(R.id.tv_level);
        progressBar = findViewById(R.id.experienceBar);
        signIn = findViewById(R.id.sign_in);
        signInStatu = findViewById(R.id.sign_in_statu);
        avatar = findViewById(R.id.img_avatar);

        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 0;
                outRect.right = 0;
                outRect.top = 0;
                outRect.bottom = 40;
            }
        });
        btnBack = findViewById(R.id.back);
    }
    private void loadUserAvatar(boolean a) {
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        // 创建 OkHttp 客户端
        OkHttpClient client = new OkHttpClient();

        // 构建请求
        Request request = new Request.Builder()
                .url(urlAvatar + userId)  // 替换为你的后端 API 地址
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

                Gson gson=new Gson();
                // 获取 avatarUrl 和 userNickname
                UserInfo userInfo = gson.fromJson(responseData,UserInfo.class);
                String avatarUrl=userInfo.getAvatar();
                String userName =userInfo.getUserName();
                // 在 UI 线程中更新 ImageView
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用 Glide 加载用户头像
                        if (avatarUrl!=null&&!avatarUrl.isEmpty()) {
                            loadImageWithGlide(avatarUrl,a);
                        } else {
                            // 处理返回的不是有效地址的情况
                            // 可以设置默认头像或给用户提示
                            RequestOptions requestOptions = new RequestOptions()
                                    .transform(new CircleCrop());
                            Glide.with(DailyTaskActivity.this)
                                    .load(R.drawable.headimg)
                                    .apply(requestOptions)
                                    .into(avatar);
                        }
                        if(!userName.isEmpty()){
                            user_name.setText(userName);
                        }
                    }
                });
            }
        });
    }
    //加载用户头像
    private void loadImageWithGlide(String avatarUrl, boolean forceRefresh) {
        // 使用 Glide 加载用户头像，并进行圆形裁剪
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        if(forceRefresh==false){
            Glide.with(DailyTaskActivity.this)
                    .load(urlLoadImage + avatarUrl)
                    .skipMemoryCache(true)  //允许内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 使用磁盘缓存
                    .placeholder(R.drawable.headimg)  // 设置占位图
                    .apply(requestOptions)
                    .signature(new ObjectKey(userId))  // 设置签名
                    .into(avatar);
        }else{
            Glide.get(DailyTaskActivity.this).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(DailyTaskActivity.this).clearDiskCache();
                }
            }).start();
            Glide.with(DailyTaskActivity.this)
                    .load(urlLoadImage + avatarUrl)
                    .skipMemoryCache(true)  //允许内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 使用磁盘缓存
                    .placeholder(R.drawable.headimg)  // 设置占位图
                    .apply(requestOptions)
                    .signature(new ObjectKey(userId))  // 设置签名
                    .into(avatar);
        }

    }
    private void loadUserExtraInfo() {
        Log.i("PersonalInformationFragment", "开始获取用户额外数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("userId", userId)
                        .build();
                Request request = new Request.Builder()
                        .url(url+"/userExtraInfo/getUserExtraInfo")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        String responseData = response.body().string();
                        if(responseData.equals("")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("PersonalInformationFragment", "无数据");
                                }
                            });
                        } else {
                            userExtraInfo = new Gson().fromJson(responseData, UserExtraInfo.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("PersonalInformationFragment", "获取用户额外数据成功");
                                    int level = userExtraInfo.getLevel();
                                    // 设置经验和等级
                                    TvLevel.setText("Lv." + level);
                                    // 0->1 :(lv+1)*100 + lv*50=100   1->2 :250   2->3 :400   3->4 :600   4->5 :750
                                    progressBar.setMax((level+1)*100 + level*50);
                                    progressBar.setProgress(userExtraInfo.getExperience());
                                }
                            });
                        }
                    } else {
                        Log.i("PersonalInformationFragment", "获取用户额外数据失败");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}