package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.adapters.FollowListAdapter;
import glue502.software.adapters.PostListAdapter;
import glue502.software.models.Follow;
import glue502.software.models.Post;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FollowSearchActivity extends AppCompatActivity {
    private String url = "http://" + ip + "/travel/";
    private EditText searchText1;
    private ListView listView;
    private RefreshLayout refreshLayout;
    private FollowListAdapter followListAdapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Handler adapterHandler;
    private int page = 0;
    private String status;
    private List<UserInfo> userInfos = new ArrayList<>();
    private List<Follow> followList = new ArrayList<>();
    Gson gson = new Gson();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_search);

        //添加沉浸式
        MyViewUtils.setImmersiveStatusBar(FollowSearchActivity.this, findViewById(R.id.coordinator), true);
        initView();
        SharedPreferences sharedPreferences = this.getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        status = sharedPreferences.getString("status", "");
        userId = sharedPreferences.getString("userId", "");
        setListenter();
        adapterHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                return false;
            }
        });
    }

    private void initData() {

    }

    private void setListenter() {
        searchText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String nameContaining = v.getText().toString().trim();
                    if (!nameContaining.isEmpty()) {
                        // 开启线程接收搜索到的用户
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(url + "/user/getUserInfoListByName?nameContaining=" + nameContaining)
                                        .build();
                                Request getFollowList = new Request.Builder()
                                        .url(url + "/follow/getFollowList?userId=" + userId)
                                        .build();
                                try {
                                    // 发起请求并获取响应
                                    Response response = client.newCall(request).execute();
                                    Response followListResponse = client.newCall(getFollowList).execute();
                                    // 检测响应是否成功
                                    if (response.isSuccessful() && followListResponse.isSuccessful()) {
                                        // 获取响应数据
                                        ResponseBody responseBody = response.body();
                                        ResponseBody responseBody1 = followListResponse.body();
                                        if (responseBody != null) {
                                            String responseData = responseBody.string();
                                            String responseData1 = responseBody1.string();
                                            userInfos = gson.fromJson(responseData, new TypeToken<List<UserInfo>>() {
                                            }.getType());
                                            followList = gson.fromJson(responseData1, new TypeToken<List<Follow>>() {
                                            }.getType());
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // 这个操作不安全 使用迭代器
//                                                    for(UserInfo userInfo : userInfos){
//                                                        if(userInfo.getUserId().equals(userId)){
//                                                            userInfos.remove(userInfo);
//                                                        }
//                                                    }
                                                    Iterator<UserInfo> iterator = userInfos.iterator();
                                                    while (iterator.hasNext()) {
                                                        UserInfo userInfo = iterator.next();
                                                        if (userInfo.getUserId().equals(userId)) {
                                                            iterator.remove();
                                                        }
                                                    }
                                                    // 或则lamda表达式
//                                                    userInfos.removeIf(userInfo -> userInfo.getUserId().equals(userId));

                                                    followListAdapter = new FollowListAdapter(
                                                            FollowSearchActivity.this,
                                                            R.layout.adapter_follow_item,
                                                            userInfos,
                                                            userId,
                                                            FollowListAdapter.NORMAL_TYPE,
                                                            adapterHandler);
                                                    int[] isFollow = new int[userInfos.size()];
                                                    Collections.fill(Arrays.asList(isFollow), 0);
                                                    for (int i = 0; i < userInfos.size(); i++) {
                                                        String userId = userInfos.get(i).getUserId();
                                                        for (int j = 0; j < followList.size(); j++) {
                                                            if (followList.get(j).getFollowId().equals(userId)) {
                                                                isFollow[i] = 1;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    followListAdapter.setIsFollow(isFollow);
                                                    listView.setAdapter(followListAdapter);
                                                }
                                            });
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    return true;
                }
                return false;
            }
        });
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                userInfos = new ArrayList<>();

                String nameContaining = searchText1.getText().toString().trim();

                if (!nameContaining.isEmpty()) {
                    // 开启线程接收搜索到的用户
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(url + "/user/getUserInfoListByName?nameContaining=" + nameContaining)
                                    .build();
                            Request getFollowList = new Request.Builder()
                                    .url(url + "/follow/getFollowList?userId=" + userId)
                                    .build();
                            try {
                                // 发起请求并获取响应
                                Response response = client.newCall(request).execute();
                                Response followListResponse = client.newCall(getFollowList).execute();
                                // 检测响应是否成功
                                if (response.isSuccessful() && followListResponse.isSuccessful()) {
                                    // 获取响应数据
                                    ResponseBody responseBody = response.body();
                                    ResponseBody responseBody1 = followListResponse.body();
                                    if (responseBody != null) {
                                        String responseData = responseBody.string();
                                        String responseData1 = responseBody1.string();
                                        userInfos = gson.fromJson(responseData, new TypeToken<List<UserInfo>>() {
                                        }.getType());
                                        followList = gson.fromJson(responseData1, new TypeToken<List<Follow>>() {
                                        }.getType());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                followListAdapter = new FollowListAdapter(
                                                        FollowSearchActivity.this,
                                                        R.layout.adapter_follow_item,
                                                        userInfos,
                                                        userId,
                                                        FollowListAdapter.NORMAL_TYPE,
                                                        adapterHandler);
                                                int[] isFollow = new int[userInfos.size()];
                                                Collections.fill(Arrays.asList(isFollow), 0);
                                                for (int i = 0; i < userInfos.size(); i++) {
                                                    String userId = userInfos.get(i).getUserId();
                                                    for (int j = 0; j < followList.size(); j++) {
                                                        if (followList.get(j).getFollowId().equals(userId)) {
                                                            isFollow[i] = 1;
                                                            break;
                                                        }
                                                    }
                                                }
                                                followListAdapter.setIsFollow(isFollow);
                                                listView.setAdapter(followListAdapter);
                                            }
                                        });
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }

        });
    }

    private void initView() {
        searchText1 = findViewById(R.id.et_searchtext);
        listView = findViewById(R.id.follow_list_display);
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
    }
}