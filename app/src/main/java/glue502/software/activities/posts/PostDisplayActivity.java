package glue502.software.activities.posts;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.UUID;

import glue502.software.R;
import glue502.software.activities.login.LoginActivity;
import glue502.software.utils.Carousel;
import glue502.software.models.PostWithUserInfo;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostDisplayActivity extends AppCompatActivity {
    private Button star_btn;
    private Button back_btn;
    private LinearLayout dotLinerLayout;
    private ViewPager2 postImage;
    private PostWithUserInfo postWithUserInfo;
    private TextView content;
    private TextView title;
    private ImageView avatar;
    private TextView userName;
    private String url = "http://"+ip+"/travel/";
    private String userId;
    private String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);
        postWithUserInfo = (PostWithUserInfo) getIntent().getSerializableExtra("postwithuserinfo");
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        //获取用户状态和用户名
        status = sharedPreferences.getString("status","");
        userId = sharedPreferences.getString("userId","");
        initView();
        setlistener();

        displayPost();
    }
    public void displayPost(){
        //Carousel为自定义轮播图工具类
        Carousel carousel = new Carousel(this.getApplicationContext(), dotLinerLayout, postImage);
        carousel.initViews(postWithUserInfo.getPost().getPicturePath());

        content.setText(postWithUserInfo.getPost().getPostContent());
        title.setText(postWithUserInfo.getPost().getPostTitle());
        userName.setText(postWithUserInfo.getUserInfo().getUserName());
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(PostDisplayActivity.this)
                .load(url+postWithUserInfo.getUserInfo().getAvatar())
                .apply(requestOptions)
                .into(avatar);
    }
    public void initView(){
        postImage = findViewById(R.id.post_image);
        dotLinerLayout = findViewById(R.id.index_dot);
        content = findViewById(R.id.post_content);
        title = findViewById(R.id.post_title);
        userName = findViewById(R.id.user_name);
        avatar = findViewById(R.id.avatar);
        back_btn = findViewById(R.id.btn_back);
        star_btn = findViewById(R.id.btn_star);
    }
    public void setlistener(){
        star_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行收藏代码
                if (status==""){
                    // 创建AlertDialog构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDisplayActivity.this);
                    builder.setTitle("账号未登录！")
                            .setMessage("是否前往登录账号")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确定”按钮后的操作
                                    Intent intent = new Intent(PostDisplayActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“取消”按钮后的操作
                                    dialog.dismiss(); // 关闭对话框
                                }
                            });

                    // 创建并显示对话框
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String postId = postWithUserInfo.getPost().getPostId();
                            OkHttpClient client = new OkHttpClient();
                            MultipartBody.Builder builder = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("postId",postId)
                                    .addFormDataPart("userId",userId);
                            RequestBody requestBody = builder.build();
                            Request request = new Request.Builder()
                                    .url(url+"posts/star")
                                    .post(requestBody)
                                    .build();
                            try {
                                Response response = client.newCall(request).execute();
                            } catch (IOException e) {
                                Log.e("NetworkError", "Error: " + e.getMessage());
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                }

            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}