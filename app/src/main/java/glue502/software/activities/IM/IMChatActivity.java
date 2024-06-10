package glue502.software.activities.IM;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import glue502.software.R;
import glue502.software.adapters.EMMessageAdapter;
import glue502.software.models.MyEMMessage;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer;

import static glue502.software.activities.MainActivity.ip;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IMChatActivity extends AppCompatActivity implements EMMessageListener{
    private String url = "http://" + ip + "/travel";
    private String urlAvatar="http://"+ip+"/travel/user/getUserInfo?userId=";

    // 聊天信息输入框
    private EditText mInputEdit;
    // 发送按钮
    private Button mSendBtn;
    private RecyclerView mRecyclerView;

    // 消息监听器
    private EMMessageListener mMessageListener;
    // 当前聊天的 ID
    private String mChatId;
    // 当前会话对象
    private EMConversation mConversation;
    //数据源
    List<MyEMMessage> mData=new ArrayList<>();
    //头像
    private String avatar;
    private String userName;
    private ImageView back;
    private TextView user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_chat);
        back = findViewById(R.id.back);
        user_name = findViewById(R.id.title);
        // 获取当前会话的username(如果是群聊就是群id)
        mChatId = getIntent().getStringExtra("ec_chat_id");
        userName = getIntent().getStringExtra("ec_chat_name");
        mMessageListener = (EMMessageListener) this;
        //初始化环信，在这里实现了类似于项目中的token判断，如果没有token则跳转到登录界面
        EMOptions options = new EMOptions();
        options.setAppKey("1117240606210709#travel");
        // 其他 EMOptions 配置。
        EMClient.getInstance().init(this, options);
        //状态栏
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);


        initView();
        initConversation();
        user_name.setText(userName);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * 初始化界面
     */
    private void initView() {
        mInputEdit = (EditText) findViewById(R.id.ec_edit_message_input);
        mSendBtn = (Button) findViewById(R.id.ec_btn_send);
        // 设置textview可滚动，需配合xml布局设置
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,1));


        // 设置发送按钮的点击事件
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String content = mInputEdit.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    mInputEdit.setText("");
                    // 创建一条新消息，第一个参数为消息内容，第二个为接受者username
                    EMMessage message = EMMessage.createTxtSendMessage(content, mChatId);
                    EMMessageToEMMessageAndAdd(message);
                    // 调用发送消息的方法
                    EMClient.getInstance().chatManager().sendMessage(message);
                    // 为消息设置回调
                    message.setMessageStatusCallback(new EMCallBack() {
                        @Override public void onSuccess() {
                            // 消息发送成功，打印下日志，正常操作应该去刷新ui
                            Log.i("IMChatActivity", "send message on success");
                        }

                        @Override public void onError(int i, String s) {
                            // 消息发送失败，打印下失败的信息，正常操作应该去刷新ui
                            Log.i("IMChatActivity", "send message on error " + i + " - " + s);
                        }

                        @Override public void onProgress(int i, String s) {
                            // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt不回调
                        }
                    });
                }
            }
        });
    }
    /**
     * 初始化会话对象，并且根据需要加载更多消息
     */
    private void initConversation() {

        /**
         * 初始化会话对象，这里有三个参数么，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(mChatId, null, true);
        // 设置当前会话未读数为 0
        mConversation.markAllMessagesAsRead();
        int count = mConversation.getAllMessages().size();
        if (count < mConversation.getAllMsgCount() && count < 20) {
            // 获取已经在列表中的最上边的一条消息id
            String msgId = mConversation.getAllMessages().get(0).getMsgId();
            // 分页加载更多消息，需要传递已经加载的消息的最上边一条消息的id，以及需要加载的消息的条数
            mConversation.loadMoreMsgFromDB(msgId, 20 - count);
        }
        // 打开聊天界面获取最后一条消息内容并显示
        if (mConversation.getAllMessages().size() > 0) {
            EMMessage message = mConversation.getLastMessage();
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            EMMessageToEMMessageAndAdd(message);
        }
    }

    /**
     * 自定义实现Handler，主要用于刷新UI操作
     */
    Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    EMMessage message = (EMMessage) msg.obj;
                    // 这里只是简单的demo，也只是测试文字消息的收发，所以直接将body转为EMTextMessageBody去获取内容
                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    EMMessageToEMMessageAndAdd(message);
                    break;
            }
        }
    };

    //如果不注册消息监听的话，就难以在后面实现对面发送消息在我方展示
    @Override protected void onResume() {
        super.onResume();
        // 添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    @Override protected void onStop() {
        super.onStop();
        // 移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }
    /**
     * --------------------------------- Message Listener -------------------------------------
     * 环信消息监听主要方法
     */
    /**
     * 收到新消息
     *
     * @param list 收到的新消息集合
     */
    public void onMessageReceived(List<EMMessage> list) {
        // 循环遍历当前收到的消息
        for (EMMessage message : list) {
            Log.i("IMChatActivity", "收到新消息:" + message);
            if (message.getFrom().equals(mChatId)) {
                // 设置消息为已读
                mConversation.markMessageAsRead(message.getMsgId());

                // 因为消息监听回调这里是非ui线程，所以要用handler去更新ui
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = message;
                mHandler.sendMessage(msg);
            } else {
                // TODO 如果消息不是当前会话的消息发送通知栏通知
            }
        }
    }

    /**
     * 收到新的 CMD 消息
     */
    public void onCmdMessageReceived(List<EMMessage> list) {
        for (int i = 0; i < list.size(); i++) {
            // 透传消息
            EMMessage cmdMessage = list.get(i);
            EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();
            Log.i("lzan13", "收到 CMD 透传消息" + body.action());
        }
    }

    /**
     * 收到新的已读回执
     *
     * @param list 收到消息已读回执
     */
    public void onMessageRead(List<EMMessage> list) {}

    /**
     * 收到新的发送回执
     * TODO 无效 暂时有bug
     *
     * @param list 收到发送回执的消息集合
     */
    public void onMessageDelivered(List<EMMessage> list) {}

    /**
     * 消息撤回回调
     *
     * @param list 撤回的消息列表
     */
    public void onMessageRecalled(List<EMMessage> list) {}

    /**
     * 消息的状态改变
     *
     * @param message 发生改变的消息
     * @param object 包含改变的消息
     */
    public void onMessageChanged(EMMessage message, Object object) {}

    //通过username获取头像和昵称
    public void EMMessageToEMMessageAndAdd(EMMessage emMessage){
        Log.i("IMChatActivity", "lzx 方法执行");
        MyEMMessage myEMMessage = new MyEMMessage();
        EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
        myEMMessage.setMessage(body.getMessage());
        Instant instant = Instant.ofEpochMilli(emMessage.getMsgTime());
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        myEMMessage.setTime(formattedDateTime);
        String username = emMessage.getFrom();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Log.i("aa", "lzx"+username);
                    // 构建请求
                    Request request = new Request.Builder()
                            .url(urlAvatar + username)  // 替换为你的后端 API 地址
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            // 请求失败
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            final String responseData = response.body().string();

                            Gson gson=new Gson();
                            // 获取 avatarUrl 和 userNickname
                            UserInfo userInfo = gson.fromJson(responseData,UserInfo.class);
                            Log.i("IMChatActivity", "lzx 获取头像"+userInfo.getAvatar());
                            myEMMessage.setAvatar(userInfo.getAvatar());
                            myEMMessage.setUsername(userInfo.getUserName());
                            Log.i("aa", "lzx "+userInfo.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mData.add(myEMMessage);
                                    Log.i("IMChatActivity", "lzx 数量"+mData.size());
                                    EMMessageAdapter adapter = new EMMessageAdapter(IMChatActivity.this, mData);
                                    mRecyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}