package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.adapters.PostListAdapter;
import glue502.software.models.Post;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.UserInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MineStarActivity extends AppCompatActivity {
    private ListView postList;
    private String url = "http://"+ip+"/travel/posts/getstarlist";
    private List<Post> posts;
    private List<UserInfo> userInfos;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_star);

        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");
        initView();
        setlistener();
        initData();
    }
    public void initView(){
        postList = findViewById(R.id.post_display);
    }
    public void initData(){
        posts = new ArrayList<>();
        userInfos = new ArrayList<>();
        //开启线程查找收藏的帖子
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url+"?userId="+userId)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        ResponseBody responseBody = response.body();
                        if (responseBody!=null){
                            String responseData = responseBody.string();
                            List<PostWithUserInfo> postWithUserInfoList = gson.fromJson(responseData,new TypeToken<List<PostWithUserInfo>>(){}.getType());
                            posts = new ArrayList<>();
                            userInfos = new ArrayList<>();
                            for (PostWithUserInfo postWithUserInfo: postWithUserInfoList){
                                posts.add(postWithUserInfo.getPost());
                                userInfos.add(postWithUserInfo.getUserInfo());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (posts !=null&&userInfos!=null){
                                        PostListAdapter postAdapter = new PostListAdapter(MineStarActivity.this,R.layout.post_item,posts,userInfos);
                                        postList.setAdapter(postAdapter);

                                    }else {

                                    }

                                }
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
        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                PostListAdapter postListAdapter = (PostListAdapter) parent.getAdapter();
                //获取点击项数据对象
                PostWithUserInfo clickItem = (PostWithUserInfo) postListAdapter.getItem(i);
                Intent intent = new Intent(MineStarActivity.this, PostDisplayActivity.class);
                intent.putExtra("postwithuserinfo", clickItem);
                System.out.println(clickItem.getUserInfo().getAvatar());
                startActivity(intent);

            }
        });
    }
}