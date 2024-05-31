package glue502.software.activities.map;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import glue502.software.R;
import glue502.software.activities.login.LoginActivity;
import glue502.software.adapters.StrategyRespondDetailAdapter;
import glue502.software.models.ReturnStrategyCommentRespond;
import glue502.software.models.StrategyComment;
import glue502.software.models.UploadComment;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

public class StrategyRespondDetailActivity extends AppCompatActivity {
    private StrategyComment comment;
    List<ReturnStrategyCommentRespond> returnCommentResponds;
    private StrategyRespondDetailAdapter StrategyAdapter;
    private ListView listView;
    private Button submit;
    private String status;
    private String postId;
    private EditText chatInputEt;
    private ImageView avatar;
    private TextView username;
    private TextView time;
    private TextView text;
    private String url = "http://"+ip+"/travel/";
    private OkHttpClient client = new OkHttpClient();
    private boolean wasOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_respond_detail);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        setView();
        initData();
        setListener();

    }

    public void setView(){
        listView = findViewById(R.id.strategy_respond_list);
        submit = findViewById(R.id.strategy_respond_submit);
        chatInputEt = findViewById(R.id.strategy_chatInputEt);
        username = findViewById(R.id.strategy_username);
        avatar = findViewById(R.id.strategy_avatar);
        time = findViewById(R.id.strategy_time);
        text = findViewById(R.id.strategy_text);
    }

    private void setListener() {
        // 注册根视图全局布局变化监听器
        findViewById(R.id.root_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                int heightDiff = screenHeight - r.bottom;
                if (heightDiff > dpToPx(getApplicationContext(), 200)) { // 高度差大于200dp，通常认为软键盘已打开
                    if (!wasOpened) {
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) findViewById(R.id.root_layout).getLayoutParams();
                        layoutParams.bottomMargin = heightDiff+dpToPx(getApplicationContext(),35);

                        
                        findViewById(R.id.root_layout).setLayoutParams(layoutParams);
                        wasOpened = true;
                    }
                } else if (wasOpened) {
                    // 软键盘关闭
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) findViewById(R.id.root_layout).getLayoutParams();
                    layoutParams.bottomMargin = 0;
                    findViewById(R.id.root_layout).setLayoutParams(layoutParams);
                    wasOpened = false;
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status==""){
                    // 创建AlertDialog构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(StrategyRespondDetailActivity.this);
                    builder.setTitle("账号未登录！")
                            .setMessage("是否前往登录账号")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确定”按钮后的操作
                                    Intent intent = new Intent(StrategyRespondDetailActivity.this, LoginActivity.class);
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
                            //生成评论实体CommentRespond
                            String text = chatInputEt.getText().toString();;
                            String id = UUID.randomUUID().toString();
                            Date date = new Date();
                            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");
                            System.out.println(dateFormat.format(date));
                            String time = dateFormat.format(date);
                            String parentId = comment.getCommentId();
                            SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                            String userId = sharedPreferences.getString("userId","");
                            //UploadComment commentRespond = new UploadComment(id, userId, text, time, comment.getCommentId());
                            UploadComment commentRespond = new UploadComment(postId, userId, text, id, time, parentId);
                            //okHttp
                            Gson gson = new Gson();
                            String json = gson.toJson(commentRespond);
                            RequestBody body = RequestBody.create(
                                    MediaType.parse("application/json;charset=utf-8"),
                                    json
                            );
                            Request request = new Request.Builder()
                                    .post(body)
                                    .url(url + "comment/addStrategyComment")
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
                                                getCommentResponseData();
                                                StrategyAdapter.notifyDataSetChanged();
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
                    hideInput();
                }
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

    private void getCommentResponseData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //okHttp
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json;charset=utf-8"),
                        comment.getCommentId()
                );
                Request request = new Request.Builder()
                        .post(body)
                        .url(url + "comment/getReturnStrategyCommendRespondList")
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        returnCommentResponds = new ArrayList<>();
                        //获取相应的数据
                        String result = response.body().string();
                        //反序列化消息
                        JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
                        for (JsonElement jsonElement : jsonArray) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            ReturnStrategyCommentRespond returnCommentRespond = new Gson().fromJson(jsonObject, ReturnStrategyCommentRespond.class);
                            returnCommentResponds.add(returnCommentRespond);
                        }
                        // 更新UI线程中的ListView
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StrategyAdapter = new StrategyRespondDetailAdapter(
                                        StrategyRespondDetailActivity.this,
                                        R.layout.activity_respond_detail_adapter,
                                        returnCommentResponds
                                );
                                Log.v("StrategyRespondDetailActivity", "lzx 攻略评论回复详情页面的数据"+returnCommentResponds);
                                listView.setAdapter(StrategyAdapter);
                                setListViewHeightBasedOnChildren(listView);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        status = sharedPreferences.getString("status","");
        //获取intent对象
        Intent intent = getIntent();
        //获得封装数据
        Bundle bundle = intent.getBundleExtra("bundle");
        comment = (StrategyComment) bundle.getSerializable("comment");
        //获取评论回复所属帖子Id
        postId = comment.getPostId();
        //显示头像
        Glide.with(StrategyRespondDetailActivity.this)
                .load(url + comment.getAvatar())
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .fallback(R.mipmap.blank)
                .circleCrop()
                .into(avatar);
        username.setText(comment.getUsername());
        time.setText(comment.getUploadTime());
        text.setText(comment.getComment());
        returnCommentResponds = comment.getReturnStrategyCommentResponds();
        //绑定adapter
        StrategyAdapter = new StrategyRespondDetailAdapter(
                StrategyRespondDetailActivity.this,
                R.layout.activity_respond_detail_adapter,
                returnCommentResponds);
        listView.setAdapter(StrategyAdapter);
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

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}