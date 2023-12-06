package glue502.software.activities;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import glue502.software.R;
import glue502.software.utils.Carousel;
import glue502.software.models.PostWithUserInfo;

public class PostDisplayActivity extends AppCompatActivity {
    private LinearLayout dotLinerLayout;
    private ViewPager2 postImage;
    private PostWithUserInfo postWithUserInfo;
    private TextView content;
    private TextView title;
    private ImageView avatar;
    private TextView userName;
    private String url = "http://"+ip+"/test/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);
        postWithUserInfo = (PostWithUserInfo) getIntent().getSerializableExtra("postwithuserinfo");
        initView();
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
    }
}