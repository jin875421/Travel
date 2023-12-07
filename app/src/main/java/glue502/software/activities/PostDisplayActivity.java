package glue502.software.activities;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

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
    private String url = "http://"+ip+"/test/";
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);
        postWithUserInfo = (PostWithUserInfo) getIntent().getSerializableExtra("postwithuserinfo");
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
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
        Glide.with(content)
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
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}