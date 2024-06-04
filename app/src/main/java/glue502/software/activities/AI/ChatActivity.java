package glue502.software.activities.AI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import glue502.software.R;
import glue502.software.adapters.ChatlistAdapter;
import glue502.software.models.Chatlist;
import glue502.software.models.WenXin;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//此activity主要用来实现聊天界面
public class ChatActivity extends Activity {

    private EditText et_chat;
    private Button btn_send,btn_chat_return, btn_startsetting;
    private ChatlistAdapter chatAdapter;
    private List<Chatlist> mDatas;

    private SharedPreferences preferences;

    private RecyclerView rc_chatlist;
    private LottieAnimationView lo_msgloading;
    final int MESSAGE_UPDATE_VIEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        //聊天信息
        mDatas = new ArrayList<Chatlist>();

        //读取用户设置里的文心一言的API_Key等信息
        preferences= this.getSharedPreferences("usersetting",MODE_PRIVATE);
        String apikey=preferences.getString("API_Key","oQtUEMpGo1M9vsMfxmrwePzF");
        String secretkey=preferences.getString("Secret_Key","LxfNECAa2nNu5fLQERgNlUyL4W2UW0eX");
        String airole=preferences.getString("Role","你的名字是ERNIE，你是一位英语对话练习助手，你只能以汉语进行回答");


        chatAdapter=new ChatlistAdapter(this,mDatas);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        rc_chatlist.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rc_chatlist.setHasFixedSize(true);
        //创建并设置Adapter
        rc_chatlist.setAdapter(chatAdapter);


        //点击btn_send发送聊天信息
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户的提问
                String user_ask=et_chat.getText().toString();//获取输入框里的信息
                Chatlist C3;
                C3=new Chatlist("USER",user_ask);
                mDatas.add(C3);

                chatAdapter.ResetChatlistAdapter(mDatas);
                rc_chatlist.setAdapter(chatAdapter);
                WenXin wx=new WenXin(apikey,secretkey,airole);

                //文心一言的回答
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        //请求详情

                        // 调用 GetAnswer 方法
                        try {
                            wx.GetAnswer(user_ask, new WenXin.ResponseCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    // 在这里处理获取到的结果
                                    Chatlist C4;
                                    String wxresult=response.toString();
                                    C4=new Chatlist("ERNIE",wxresult);

                                    mDatas.add(C4);
                                    chatAdapter.ResetChatlistAdapter(mDatas);

                                    Message msg = new Message();
                                    msg.what = MESSAGE_UPDATE_VIEW;
                                    ChatActivity.this.gHandler.sendMessage(msg);
                                }

                                @Override
                                public void onError(String error) {
                                    // 在这里处理错误情况
                                    Chatlist C4;
                                    String wxresult="获取信息失败";
                                    C4=new Chatlist("ERNIE",wxresult);

                                    mDatas.add(C4);
                                    chatAdapter.ResetChatlistAdapter(mDatas);

                                    Message msg = new Message();
                                    msg.what = MESSAGE_UPDATE_VIEW;
                                    ChatActivity.this.gHandler.sendMessage(msg);
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }).start();
                et_chat.setText("");
            }
        });



        //点击返回,返回mainActivity
        btn_chat_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this,MainWenXinActivity.class);
                startActivity(intent);
                ChatActivity.this.finish();
            }
        });

        //跳转到设置界面
        btn_startsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this, SettingActivity.class);
                startActivity(intent);
                //MainActivity.this.finish();
            }
        });
    }

    private void init(){
        btn_send=findViewById(R.id.btn_send);
        et_chat=findViewById(R.id.et_chat);
        btn_chat_return=findViewById(R.id.btn_chat_return);
        rc_chatlist=findViewById(R.id.rc_chatlist);
        btn_startsetting=findViewById(R.id.btn_startsetting);
    }


    public Handler gHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_UPDATE_VIEW) {
                rc_chatlist.setAdapter(chatAdapter);
                btn_send.setVisibility(View.VISIBLE);//恢复按钮
                //读出回答
                //at.ReadOut("Hello?");
            }
        }
    };
}