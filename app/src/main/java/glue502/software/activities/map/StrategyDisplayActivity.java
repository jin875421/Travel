package glue502.software.activities.map;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.viewpager2.widget.ViewPager2;

import glue502.software.R;
import glue502.software.activities.login.LoginActivity;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.activities.posts.PostEditActivity;
import glue502.software.activities.posts.RespondDetail;
import glue502.software.adapters.CommentListAdapter;
import glue502.software.models.Comment;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.ReturnStrategy;
import glue502.software.models.UploadComment;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StrategyDisplayActivity extends AppCompatActivity {
    private OkHttpClient client = new OkHttpClient();
    private String url = "http://"+ip+"/travel/strategy";
    private ReturnStrategy returnStrategy;
    private String strategyId;
    private TextView strategyTitle;
    private TextView strategyUserName;
    private TextView strategyDescribe;
    private TextView strategyTime;
    private Double latitude;
    private Double longitude;
    private ImageView avatar;
    private LinearLayout images;
    private String userId;
    private String status;
    private List<Comment> commentList = new ArrayList<>();

    private ImageView menuBtn;
    private CommentListAdapter commentListAdapter;
    private ListView listView;
    private ImageView star_btn;
    private ImageView like_btn;
    private int checkedItemId = R.id.edit;
    private ImageView back_btn;
    private ImageView submit;
    private LinearLayout dotLinerLayout;
    private ViewPager2 postImage;
    private PostWithUserInfo postWithUserInfo;
    private PostWithUserInfo post;
    private TextView content;
    private TextView title;
    private TextView text;
    private TextView userName;
    private EditText chatInputEt;
    private ScrollView view;
    private String postId;
    //记录用户收藏和点赞状态
    private int likeStatus, starStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_display);

        initData();
        initView();
        setListener();
        getCommentData();
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);

    }

    public void setAdapterListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击的评论
                Comment comment = commentList.get(position);
                //获取评论的id
                String commentId = comment.getCommentId();
                //获取评论者的Id
                String userId = comment.getUserId();
                //弹出软键盘后用户输入文本内容
                showInput(chatInputEt);
                //点击submit后获取输入的内容并提交
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = chatInputEt.getText().toString();
                        //生成回复实体
                        //获取时间
                        Date date = new Date();
                        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                        String time = dateFormat.format(date);
                        //生成UUID
                        String commentRespondId = UUID.randomUUID().toString();
                        if (text.length() > 0) {
                            UploadComment commentRespond = new UploadComment(postId,
                                    userId,
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
                                    Toast.makeText(StrategyDisplayActivity.this, "评论内容不能为空", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        commentListAdapter.setOnRespondClickListener(new CommentListAdapter.onRespondClickListener() {
            @Override
            public void onRespondClick(int i) {
                Intent intent = new Intent(StrategyDisplayActivity.this, RespondDetail.class);
                Comment comment = commentList.get(i);
                System.out.println(comment);
                Bundle bundle = new Bundle();
                bundle.putSerializable("comment", (Serializable) comment);
                //把bundle对象添加到intent对象中
                intent.putExtra("bundle", bundle);
                //启动跳转页面
                startActivity(intent);
            }
        });
    }
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
        PopupMenu popupMenu = new PopupMenu(StrategyDisplayActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(StrategyDisplayActivity.this);
                        builder.setTitle("删除")
                                .setMessage("是否删除该帖子？")
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
                        Intent intent = new Intent(StrategyDisplayActivity.this, PostEditActivity.class);
                        intent.putExtra("postwithuserinfo",post);
                        startActivity(intent);
                        break;
                    case R.id.cancel:

                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCommentData();
        commentListAdapter = new CommentListAdapter(
                StrategyDisplayActivity.this,
                R.layout.activity_comment_list_adapter,
                commentList
        );
        listView.setAdapter(commentListAdapter);
        setListViewHeightBasedOnChildren(listView);
        //绑定adapter点击事件监听器
        setAdapterListener();
        commentListAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listView);
    }

    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    public void setListener(){
        //如果为作者，提供修改和删除功能
        if (userId.equals(postWithUserInfo.getUserInfo().getUserId())){
            //弹出选择框，修改，删除，取消
            menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(view);
                }
            });

        }else {
            //设置隐藏
            menuBtn.setVisibility(View.GONE);
        }
        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行收藏代码
                if (status==""){
                    // 创建AlertDialog构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(StrategyDisplayActivity.this);
                    builder.setTitle("账号未登录！")
                            .setMessage("是否前往登录账号")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确定”按钮后的操作
                                    Intent intent = new Intent(StrategyDisplayActivity.this, LoginActivity.class);
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
                }else if (likeStatus==0){
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
                    like_btn.setImageResource(R.mipmap.like1);
                }
            }
        });
        star_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行收藏代码
                if (status == "") {
                    // 创建AlertDialog构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(StrategyDisplayActivity.this);
                    builder.setTitle("账号未登录！")
                            .setMessage("是否前往登录账号")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确定”按钮后的操作
                                    Intent intent = new Intent(StrategyDisplayActivity.this, LoginActivity.class);
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
                    star_btn.setImageResource(R.mipmap.star1);
                    starStatus = 0;
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
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (status==""){
                            // 创建AlertDialog构建器
                            AlertDialog.Builder builder = new AlertDialog.Builder(StrategyDisplayActivity.this);
                            builder.setTitle("账号未登录！")
                                    .setMessage("是否前往登录账号")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 点击“确定”按钮后的操作
                                            Intent intent = new Intent(StrategyDisplayActivity.this, LoginActivity.class);
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

                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //生成评论实体comment
                                    String text = chatInputEt.getText().toString();;
                                    String id = UUID.randomUUID().toString();
                                    Date date = new Date();
                                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
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
                            InputMethodManager inputMethodManager = (InputMethodManager) StrategyDisplayActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
                    // 创建AlertDialog构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(StrategyDisplayActivity.this);
                    builder.setTitle("账号未登录！")
                            .setMessage("是否前往登录账号")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确定”按钮后的操作
                                    Intent intent = new Intent(StrategyDisplayActivity.this, LoginActivity.class);
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

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //生成评论实体comment
                            String text = chatInputEt.getText().toString();;
                            String id = UUID.randomUUID().toString();
                            Date date = new Date();
                            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
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
                    InputMethodManager inputMethodManager = (InputMethodManager) StrategyDisplayActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    private void initView() {
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

    private void initData() {
        //获取用户状态和用户名
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        status = sharedPreferences.getString("status","");
        userId = sharedPreferences.getString("userId","");
        Intent intent = getIntent();
        strategyId = intent.getStringExtra("strategyId");
        //向服务器请求数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttp
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),strategyId);
                Request request = new Request.Builder()
                        .url(url+"/getStrategyById")
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        //获取响应的数据
                        String result = response.body().string();
                        Gson gson = new Gson();
                        returnStrategy = gson.fromJson(result, ReturnStrategy.class);
                    }
                });
            }
        }).start();
    }
    private void getCommentData() {
        //向服务器发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttp
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),strategyId);
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
                        commentList = new ArrayList<>();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commentListAdapter = new CommentListAdapter(
                                        StrategyDisplayActivity.this,
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
}