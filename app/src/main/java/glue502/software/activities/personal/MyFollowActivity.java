package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.FollowListAdapter;
import glue502.software.adapters.PostListAdapter;
import glue502.software.models.Follow;
import glue502.software.models.PostWithUserInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyFollowActivity extends AppCompatActivity {
    private String url = "http://"+ip+"/travel/posts/getFollowList";
    private String userId;
    private RelativeLayout title;
    private ImageView back;
    private TextView tvTitle;
    private SmartRefreshLayout refreshLayout;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ListView followListView;
    private List<Follow> followList;
    private FollowListAdapter followListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);

        SharedPreferences sharedPreferences = this.getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");

        // 初始化布局中的组件
        initViews();

        //初始化数据
        initData();

        //设置沉浸式状态栏


        // 设置标题和返回按钮的点击事件
        setClickListeners();

        // 设置ListView的Adapter
        setListAdapter();

        // 设置SmartRefreshLayout的刷新和加载更多监听
        setRefreshLayoutListener();
    }

    private void initViews() {
        ConstraintLayout rootLayout = findViewById(R.id.root_layout); // 如果有ConstraintLayout的id
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        tvTitle = findViewById(R.id.title_text);
        refreshLayout = findViewById(R.id.refreshLayout);
        followListView = findViewById(R.id.travel_review);
    }

    private void setClickListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回上一个Activity
            }
        });
    }

    private void setListAdapter() {
        // 创建并设置Adapter
        followListAdapter = new FollowListAdapter(getApplicationContext(),R.layout.adapter_fellow_item,followList);
        followListView.setAdapter(followListAdapter);
    }

    private void setRefreshLayoutListener() {
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
        followList = new ArrayList<>();
        //开启线程获取关注列表
        new Thread(new Runnable() {
            public void run() {
                //获取关注列表
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url+"?userId="+userId)
                        .build();
                try {
                    //发起请求并获取响应
                    Response response = client.newCall(request).execute();
                    //检测响应是否成功
                    if (response.isSuccessful()){
                        //获取响应数据
                        ResponseBody responseBody = response.body();
                        if (responseBody!=null){
                            //处理数据
                            String responseData = responseBody.string();
                            Gson gson = new Gson();
                            followList = gson.fromJson(responseData,new TypeToken<List<Follow>>(){}.getType());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (followList != null){
                                        followListAdapter = new FollowListAdapter(getApplicationContext(),R.layout.adapter_fellow_item,followList);
                                        followListView.setAdapter(followListAdapter);
                                    }else {
                                        //处理空数据
                                    }
                                }
                            });
                        }else {
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}