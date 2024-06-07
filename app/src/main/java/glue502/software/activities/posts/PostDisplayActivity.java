package glue502.software.activities.posts;


import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;

import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import glue502.software.R;

import glue502.software.activities.login.LoginActivity;

import glue502.software.activities.personal.UserInfoActivity;
import glue502.software.adapters.CommentListAdapter;
import glue502.software.adapters.PostListAdapter;
import glue502.software.models.Comment;

import glue502.software.models.LikeAndStarStatus;

import glue502.software.models.UploadComment;
import glue502.software.utils.Carousel;
import glue502.software.models.PostWithUserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PostDisplayActivity extends AppCompatActivity {
    private ImageView star_btn;
    private ImageView like_btn;
    private int checkedItemId = R.id.edit;
    private ImageView back_btn;
    private Button submit;
    private LinearLayout dotLinerLayout;
    private ViewPager2 postImage;
    private PostWithUserInfo postWithUserInfo;
    private PostWithUserInfo post;
    private TextView content;
    private TextView title;
    private TextView text;
    private ImageView avatar;
    private TextView userName;
    private ListView listView;
    private EditText chatInputEt;
    private ScrollView view;
    private String url = "http://"+ip+"/travel/";
    private String userId;
    private String status;
    private String postId;
    private List<Comment> commentList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();

    private Button follow;
    private ImageView menuBtn;
    private CommentListAdapter commentListAdapter;
    //记录用户收藏和点赞状态
    private int likeStatus, starStatus;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isFollow = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);
        initView();
        try {
            initData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setListener();
        //添加沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        displayPost();

    }
    private void initData() throws InterruptedException {
        postWithUserInfo = (PostWithUserInfo) getIntent().getSerializableExtra("postwithuserinfo");
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        //获取用户状态和用户名
        status = sharedPreferences.getString("status","");
        userId = sharedPreferences.getString("userId","");
        postId = postWithUserInfo.getPost().getPostId();
        if(isFollowed(postWithUserInfo.getUserInfo().getUserId())){
            isFollow = true;
            follow.setText("已关注");
            follow.setTextColor(Color.parseColor("#181A23"));
            follow.setBackground(getResources().getDrawable(R.drawable.round_button_followed_background));
        }
        if(!postWithUserInfo.getUserInfo().getUserId().equals(userId)){
            addExp("浏览3个帖子");
        }
    }

    private void addExp(String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .addFormDataPart("userId", userId)
                        .addFormDataPart("tag", tag)
                        .build();
                Request request = new Request.Builder()
                        .url(url+"userDailyTask/updateByUserIdAndTag")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData.equals("success")) {
                            Log.i("PostDisplayActivity","增加浏览帖子经验");
                        } else {
                            Log.i("PostDisplayActivity","增加浏览帖子经验失败");
                        }
                    } else {
                        Log.i("PostDisplayActivity","增加浏览帖子经验失败");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private boolean isFollowed(String authorId) throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);
            final Boolean[] result = new Boolean[1]; // 用于存储结果

            new Thread(() -> {
                String urlWithParams = url + "follow/isFollow?userId=" + userId + "&followId=" + authorId;
                Request request = new Request.Builder()
                        .url(urlWithParams)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    String responseData = response.body().string();
                    if ("true".equals(responseData)) {
                        result[0] = true;
                    } else {
                        result[0] = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    result[0] = false; // 或根据需要处理异常情况
                } finally {
                    latch.countDown(); // 通知主线程任务已完成
                }
            }).start();

            latch.await(); // 阻塞主线程，等待子线程完成
            return result[0];
        }
        // 如果没有提前返回，说明服务器返回的不是"true"


    private void getCommentData() {
        //向服务器发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttp
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),postId);
                Request request = new Request.Builder()
                        .url(url+"comment/getReturnCommentList")
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue((new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        commentList.clear();
                        //获取响应的数据
                        String result = response.body().string();
                        //反序列化消息
                        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
                        for (JsonElement jsonElement : jsonArray) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            Comment comment = new Gson().fromJson(jsonObject, Comment.class);
                            commentList.add(comment);
                        }
                        // 更新UI线程中的ListView
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("PostDisplayActivity", "lzx getComment执行，获取的评论列表"+commentList);
                                commentListAdapter = new CommentListAdapter(
                                        PostDisplayActivity.this,
                                        R.layout.activity_comment_list_adapter,
                                        commentList
                                );
                                listView.setAdapter(commentListAdapter);
                                setListViewHeightBasedOnChildren(listView);
                                //绑定adapter点击事件监听器
                                setAdapterListener();
                            }
                        });
                    }
                }));
            }
        }).start();
    }

    public void displayPost(){
        //通过postid从服务器获取帖子内容
        OkHttpClient client1 = new OkHttpClient();
        Request request = new Request.Builder()
               .url(url+"posts/getPostById?postId="+postId)
               .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client1.newCall(request).execute();
                    String result = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
                    post = new Gson().fromJson(jsonObject, PostWithUserInfo.class);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Carousel为自定义轮播图工具类
                            Carousel carousel = new Carousel(PostDisplayActivity.this, dotLinerLayout, postImage,"");
                            carousel.initViews(post.getPost().getPicturePath());
                            content.setText(post.getPost().getPostContent());
                            title.setText(post.getPost().getPostTitle());
                            userName.setText(post.getUserInfo().getUserName());
                            RequestOptions requestOptions = new RequestOptions()
                                    .transform(new CircleCrop());
                            Glide.with(PostDisplayActivity.this)
                                    .load(url+post.getUserInfo().getAvatar())
                                    .apply(requestOptions)
                                    .into(avatar);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //通过postId和userId获取用户点赞和收藏状态
        Request request1 = new Request.Builder()
               .url(url+"posts/getLikeAndStarStatus?postId="+postId+"&userId="+userId)
               .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client1.newCall(request1).execute();
                    //从请求中获取用户点赞和收藏状态
                    String result = response.body().string();
                    //转换为likeAndStarStatus对象
                    LikeAndStarStatus likeAndStarStatus = new Gson().fromJson(result, LikeAndStarStatus.class);
                    likeStatus = likeAndStarStatus.getLikeStatus();
                    starStatus = likeAndStarStatus.getStarStatus();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(likeStatus == 1){
                                like_btn.setImageResource(R.mipmap.like);
                            }else{
                                like_btn.setImageResource(R.mipmap.like1);
                            }
                            if(starStatus == 1){
                                star_btn.setImageResource(R.mipmap.star);
                            }else{
                                star_btn.setImageResource(R.mipmap.star1);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void initView(){
        follow = findViewById(R.id.follow);
        like_btn = findViewById(R.id.btn_like);
        menuBtn = findViewById(R.id.popupmenu);
        postImage = findViewById(R.id.post_image);
        dotLinerLayout = findViewById(R.id.index_dot);
        content = findViewById(R.id.post_content);
        title = findViewById(R.id.post_title);
        userName = findViewById(R.id.user_name);
        avatar = findViewById(R.id.avatar);
        back_btn = findViewById(R.id.btn_back);
        star_btn = findViewById(R.id.btn_star);
        listView = findViewById(R.id.comment_list);
        text = findViewById(R.id.text);
        submit = findViewById(R.id.submit);
        chatInputEt = findViewById(R.id.chatInputEt);
        view = findViewById(R.id.view);
    }
    public void setListener(){
        //点击头像跳转个人信息页
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDisplayActivity.this, UserInfoActivity.class);
                intent.putExtra("AuthorId", postWithUserInfo.getUserInfo().getUserId());
                startActivity(intent);
            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理关注点击事件
                if (status==""){
                    showLoginDialog();
                } else if (userId.equals(postWithUserInfo.getUserInfo().getUserId())) {
                    //提示
                    Toast.makeText(PostDisplayActivity.this, "不能关注自己", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        //未关注，进行关注
                        if(!isFollowed(postWithUserInfo.getUserInfo().getUserId())){
                            follow.setBackground(getResources().getDrawable(R.drawable.round_button_followed_background));
                            follow.setTextColor(Color.parseColor("#181A23"));
                            follow.setText("已关注");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //进行关注
                                    OkHttpClient client1 = new OkHttpClient();
                                    RequestBody formBody = new FormBody.Builder()
                                            .add("userId", userId)
                                            .add("followId", postWithUserInfo.getUserInfo().getUserId())
                                            .build();
                                    Request request = new Request.Builder()
                                            .url(url+"follow/addFollow")
                                            .post(formBody)
                                            .build();
                                    //发起请求
                                    try {
                                        Response response = client1.newCall(request).execute();
                                        //检测请求是否成功
                                        if (response.isSuccessful()){

                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }else{
                            //已关注，取消关注
                            //弹出确认窗口
                            AlertDialog.Builder builder = new AlertDialog.Builder(PostDisplayActivity.this);
                            builder.setTitle("取消关注");
                            builder.setMessage("确定取消关注吗？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //已关注，取消关注
                                    follow.setText("关注");
                                    follow.setTextColor(Color.parseColor("#ffffff"));
                                    follow.setBackground(getResources().getDrawable(R.drawable.round_button_unfollowed_background));
                                    isFollow = false;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String deleteUrl = url+"follow/deleteFollow";
                                            //进行关注
                                            OkHttpClient client1 = new OkHttpClient();
                                            RequestBody formBody = new FormBody.Builder()
                                                    .add("userId", userId)
                                                    .add("followId", postWithUserInfo.getUserInfo().getUserId())
                                                    .build();
                                            Request request = new Request.Builder()
                                                    .url(deleteUrl)
                                                    .post(formBody)
                                                    .build();
                                            //发起请求
                                            try {
                                                Response response = client1.newCall(request).execute();
                                                //检测请求是否成功
                                                if (response.isSuccessful()){

                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();

                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行收藏代码
                if (status==""){
                   showLoginDialog();
                }else if (likeStatus==0){
                    // TODO 加经验
                    addExp("完成5次点赞");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String postId = postWithUserInfo.getPost().getPostId();
                            client = new OkHttpClient();
                            MultipartBody.Builder builder = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("postId",postId)
                                    .addFormDataPart("userId",userId);
                            RequestBody requestBody = builder.build();
                            Request request = new Request.Builder()
                                    .url(url+"posts/like")
                                    .post(requestBody)
                                    .build();
                            try {
                                Response response = client.newCall(request).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    likeStatus=1;
                    YoYo.with(Techniques.RubberBand)
                            .duration(700)
                            .playOn(like_btn);
                    like_btn.setImageResource(R.mipmap.like);
                } else if (likeStatus==1) {
                    //取消点赞
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String postId = postWithUserInfo.getPost().getPostId();
                            client = new OkHttpClient();
                            MultipartBody.Builder builder = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("postId",postId)
                                    .addFormDataPart("userId",userId);
                            RequestBody requestBody = builder.build();
                            Request request = new Request.Builder()
                                    .url(url+"posts/like")
                                    .post(requestBody)
                                    .build();
                            try {
                                Response response = client.newCall(request).execute();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    likeStatus=0;
                    YoYo.with(Techniques.RubberBand)
                            .duration(700)
                            .playOn(like_btn);
                    like_btn.setImageResource(R.mipmap.like1);
                }
            }
        });
        star_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行收藏代码
                if (status == "") {
                    showLoginDialog();
                } else if (starStatus == 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String postId = postWithUserInfo.getPost().getPostId();
                            client = new OkHttpClient();
                            MultipartBody.Builder builder = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("postId", postId)
                                    .addFormDataPart("userId", userId);
                            RequestBody requestBody = builder.build();
                            Request request = new Request.Builder()
                                    .url(url + "posts/star")
                                    .post(requestBody)
                                    .build();
                            try {
                                Response response = client.newCall(request).execute();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    star_btn.setImageResource(R.mipmap.star);
                    starStatus = 1;
                    YoYo.with(Techniques.RubberBand)
                            .duration(700)
                            .playOn(star_btn);
                } else if (starStatus == 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String postId = postWithUserInfo.getPost().getPostId();
                            client = new OkHttpClient();
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
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    starStatus = 0;
                    YoYo.with(Techniques.RubberBand)
                            .duration(700)
                            .playOn(star_btn);
                    star_btn.setImageResource(R.mipmap.star1);
                }
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chatInputEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatInputEt.setHint("请友好交流哦");
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (status==""){
                            showLoginDialog();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //生成评论实体comment
                                    String text = chatInputEt.getText().toString();;
                                    String id = UUID.randomUUID().toString();
                                    Date date = new Date();
                                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");
                                    String time = dateFormat.format(date);
                                    UploadComment uploadComment = new UploadComment(postId,
                                            userId,
                                            text,
                                            id,
                                            time);
                                    //okHttp
                                    Gson gson = new Gson();
                                    String json = gson.toJson(uploadComment);
                                    RequestBody body = RequestBody.create(
                                            MediaType.parse("application/json;charset=utf-8"),
                                            json
                                    );
                                    Request request = new Request.Builder()
                                            .post(body)
                                            .url(url + "comment/addComment")
                                            .build();
                                    //3.Call对象
                                    Call call = client.newCall(request);
                                    call.enqueue((new Callback() {
                                        @Override
                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                        }
                                        // 这里可以包含获取数据的逻辑，比如使用OkHttp请求数据
                                        // 返回模拟的数据
                                        @Override
                                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                            //获取响应的数据
                                            String result = response.body().string();
                                            if (result!=null){
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getCommentData();
                                                        commentListAdapter.notifyDataSetChanged();
                                                        setListViewHeightBasedOnChildren(listView);
                                                    }
                                                });
                                            }
                                            //清空EditText
                                            chatInputEt.setText("");
                                        }
                                    }));
                                }
                            }).start();
                            //点击提交后收回键盘
                            InputMethodManager inputMethodManager = (InputMethodManager) PostDisplayActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                });
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status==""){
                    showLoginDialog();
                } else if(chatInputEt.getText().toString().equals("")){
                    View toastView = getLayoutInflater().inflate(R.layout.toast_layout, null);

                    // 获取自定义布局中的 TextView
                    TextView textView = toastView.findViewById(R.id.toast_text);
                    textView.setText("请填写完整信息");
                    // 创建并显示自定义 Toast
                    Toast toast = new Toast(PostDisplayActivity.this);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastView);
                    toast.show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //更改编辑框提示内容
                            chatInputEt.setHint("请友好交流哦");
                            //生成评论实体comment
                            String text = chatInputEt.getText().toString();;
                            String id = UUID.randomUUID().toString();
                            Date date = new Date();
                            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");
                            String time = dateFormat.format(date);
                            UploadComment uploadComment = new UploadComment(postId,
                                    userId,
                                    text,
                                    id,
                                    time);
                            //okHttp
                            Gson gson = new Gson();
                            String json = gson.toJson(uploadComment);
                            RequestBody body = RequestBody.create(
                                    MediaType.parse("application/json;charset=utf-8"),
                                    json
                            );
                            Request request = new Request.Builder()
                                    .post(body)
                                    .url(url + "comment/addComment")
                                    .build();
                            //3.Call对象
                            Call call = client.newCall(request);
                            call.enqueue((new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                }
                                // 这里可以包含获取数据的逻辑，比如使用OkHttp请求数据
                                // 返回模拟的数据
                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    //获取响应的数据
                                    String result = response.body().string();
                                    if (result!=null){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getCommentData();
                                            }
                                        });
                                    }
                                    //清空EditText
                                    chatInputEt.setText("");
                                }
                            }));
                        }
                    }).start();
                    //点击提交后收回键盘
                    InputMethodManager inputMethodManager = (InputMethodManager) PostDisplayActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    public void setAdapterListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //更改输入框提示内容
                chatInputEt.setHint("回复@" + commentList.get(position).getUsername());
                //获取点击的评论
                Comment comment = commentList.get(position);
                //获取评论的id
                String commentId = comment.getCommentId();
                SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                //获取用户状态和用户名
                status = sharedPreferences.getString("status","");
                String commenterId = sharedPreferences.getString("userId","");
                //弹出软键盘后用户输入文本内容
                showInput(chatInputEt);
                //点击submit后获取输入的内容并提交
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (status==""){
                            showLoginDialog();
                        } else if(chatInputEt.getText().toString().equals("")){
                            View toastView = getLayoutInflater().inflate(R.layout.toast_layout, null);

                            // 获取自定义布局中的 TextView
                            TextView textView = toastView.findViewById(R.id.toast_text);
                            textView.setText("请填写完整信息");
                            // 创建并显示自定义 Toast
                            Toast toast = new Toast(PostDisplayActivity.this);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(toastView);
                            toast.show();
                        }else {
                            String text = chatInputEt.getText().toString();
                            //生成回复实体
                            //获取时间
                            Date date = new Date();
                            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");
                            String time = dateFormat.format(date);
                            //生成UUID
                            String commentRespondId = UUID.randomUUID().toString();
                            if (text.length() > 0) {
                                UploadComment commentRespond = new UploadComment(postId,
                                        commenterId,
                                        text,
                                        commentRespondId,
                                        time,
                                        commentId);
                                //生成线程提交回复内容
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //okHttp
                                        Gson gson = new Gson();
                                        String json = gson.toJson(commentRespond);
                                        RequestBody body = RequestBody.create(
                                                MediaType.parse("application/json;charset=utf-8"),
                                                json
                                        );
                                        Request request = new Request.Builder()
                                                .post(body)
                                                .url(url + "comment/addComment")
                                                .build();
                                        //3.Call对象
                                        Call call = client.newCall(request);
                                        call.enqueue((new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                            }
                                            // 这里可以包含获取数据的逻辑，比如使用OkHttp请求数据
                                            // 返回模拟的数据
                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                //获取响应的数据
                                                String result = response.body().string();
                                                if (result!=null){
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            getCommentData();
                                                            commentListAdapter.notifyDataSetChanged();
                                                            setListViewHeightBasedOnChildren(listView);
                                                        }
                                                    });
                                                }
                                                //清空EditText
                                                chatInputEt.setText("");
                                                hideInput();
                                            }
                                        }));
                                    }
                                }).start();
                            }else {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //放在UI线程弹Toast
                                        Toast.makeText(PostDisplayActivity.this, "评论内容不能为空", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        commentListAdapter.setOnRespondClickListener(new CommentListAdapter.onRespondClickListener() {
            @Override
            public void onRespondClick(int i) {
                Intent intent = new Intent(PostDisplayActivity.this, RespondDetail.class);
                Comment comment = commentList.get(i);
                Bundle bundle = new Bundle();
                bundle.putSerializable("comment", (Serializable) comment);
                //把bundle对象添加到intent对象中
                intent.putExtra("bundle", bundle);
                //启动跳转页面
                startActivity(intent);
            }
        });
    }

    //动态设定ListView的高度
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int last = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            last = listItem.getMeasuredHeight();
        }
        totalHeight = totalHeight + last;

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view){
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(PostDisplayActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        //如果为作者，提供修改和删除功能
        if (userId.equals(postWithUserInfo.getUserInfo().getUserId())){
            popupMenu.getMenu().getItem(2).setVisible(false);
        }else {
            //设置隐藏
            popupMenu.getMenu().getItem(0).setVisible(false);
            popupMenu.getMenu().getItem(1).setVisible(false);

        }
        //设置选中
        popupMenu.getMenu().findItem(checkedItemId).setChecked(true);
        //使用反射。强制显示菜单图标
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //显示PopupMenu
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.delete:
                        //执行删除操作
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostDisplayActivity.this);
                            builder.setTitle("删除")
                                   .setMessage("是否删除该帖子？")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 点击“取消”按钮后的操作
                                            // 关闭对话框
                                            dialog.dismiss();
                                        }
                                    })
                                   .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 点击“确定”按钮后的操作
                                            // 发送请求
                                            OkHttpClient client = new OkHttpClient();
                                            Request request = new Request.Builder()
                                                   .url(url+"posts/deletePost?postId="+postId)
                                                   .build();
                                            //开启线程发送请求
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        //开启线程
                                                        Response response = client.newCall(request).execute();
                                                        //关闭帖子
                                                        Intent resultIntent = new Intent();
                                                        setResult(Activity.RESULT_OK, resultIntent); // 设置删除完成的结果码
                                                        finish(); // 关闭页面
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                        }
                                           }).show();
                        break;
                    case R.id.edit:
                        //进行编辑操作
                        Intent intent = new Intent(PostDisplayActivity.this, PostEditActivity.class);
                                    intent.putExtra("postwithuserinfo",post);
                        startActivityForResult(intent,1);
                        break;
                    case R.id.report:
                            if (status.equals("")){
                                showLoginDialog();
                            }else {
                                // 弹出输入框
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(PostDisplayActivity.this);
                                View view1 = LayoutInflater.from(PostDisplayActivity.this).inflate(R.layout.input_dialog, null);
                                builder1.setTitle("请输入举报理由")
                                        .setView(view1)
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 点击“取消”按钮后的操作
                                                dialog.dismiss(); // 关闭对话框
                                            }
                                        })
                                        .setPositiveButton("举报", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 点击“确定”按钮后的操作
                                                // 获取输入框中的内容
                                                EditText editText = view1.findViewById(R.id.editText);
                                                String reason = editText.getText().toString();
                                                //获取当前时间
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                String time = sdf.format(new Date());
                                                OkHttpClient client1 = new OkHttpClient();
                                                Request request1 = new Request.Builder()
                                                       .url(url+"posts/reportPost?postId="+postId+"&reason="+reason+"&userId="+userId+"&time="+time)
                                                       .build();
                                                new Thread(new Runnable() {
                                                    @SuppressLint("SuspiciousIndentation")
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            //开启线程
                                                            Response response1 = client1.newCall(request1).execute();
                                                            if (response1.isSuccessful()){
                                                                //运行ui线程
                                                                handler.post(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(PostDisplayActivity.this, "感谢你的举报，我们将会核实！", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }else
                                                            //关闭对话框
                                                            dialog.dismiss();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();

                                            }
                                        }).show();
                            }


                    case R.id.cancel:

                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        Log.v("PostDisplayActivity", "lzx onResume执行");
        super.onResume();
        getCommentData();
    }

    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // 检查请求码是否与上传页面的请求码一致
            if (resultCode == Activity.RESULT_OK) {
                // 检查是否上传完成
                // 进行刷新操作，重新加载数据
                getCommentData();
                displayPost();
            }
        }
    }
    //未登录提示

    public void showLoginDialog() {
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
    }
}