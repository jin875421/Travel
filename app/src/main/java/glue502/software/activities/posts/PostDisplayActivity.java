package glue502.software.activities.posts;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import glue502.software.R;
import glue502.software.activities.login.LoginActivity;
import glue502.software.adapters.CommentListAdapter;
import glue502.software.models.Comment;
import glue502.software.models.UploadComment;
import glue502.software.utils.Carousel;
import glue502.software.models.PostWithUserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostDisplayActivity extends AppCompatActivity {
    private Button star_btn;
    private Button back_btn;
    private Button submit;
    private LinearLayout dotLinerLayout;
    private ViewPager2 postImage;
    private PostWithUserInfo postWithUserInfo;
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
    private CommentListAdapter commentListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);

        initData();
        initView();
        setlistener();
        getCommentData();

        displayPost();
    }

    private void initData() {
        postWithUserInfo = (PostWithUserInfo) getIntent().getSerializableExtra("postwithuserinfo");
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        //获取用户状态和用户名
        status = sharedPreferences.getString("status","");
        userId = sharedPreferences.getString("userId","");
        postId = postWithUserInfo.getPost().getPostId();
    }

    private void getCommentData() {
        //向服务器发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v("MainActivity", "lzx向服务器发送请求启动");
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
                        Log.v("MainActivity", "lzx网络请求失败"+e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        commentList = new ArrayList<>();
                        //获取响应的数据
                        String result = response.body().string();
                        Log.v("MainActivity", "lzx获取服务器返回的数据"+result);
                        //反序列化消息
                        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
                        for (JsonElement jsonElement : jsonArray) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            Comment comment = new Gson().fromJson(jsonObject, Comment.class);
                            commentList.add(comment);
                        }
                        // 更新UI线程中的ListView
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commentListAdapter = new CommentListAdapter(
                                        PostDisplayActivity.this,
                                        R.layout.activity_comment_list_adapter,
                                        commentList
                                );
                                listView.setAdapter(commentListAdapter);
                                setListViewHeightBasedOnChildren(listView);
                            }
                        });
                    }
                }));
            }
        }).start();
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
        listView = findViewById(R.id.comment_list);
        text = findViewById(R.id.text);
        submit = findViewById(R.id.submit);
        chatInputEt = findViewById(R.id.chatInputEt);
        view = findViewById(R.id.view);
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //生成评论实体comment
                        String text = chatInputEt.getText().toString();;
                        String id = UUID.randomUUID().toString();
                        Date date = new Date();
                        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                        System.out.println(dateFormat.format(date));
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

}