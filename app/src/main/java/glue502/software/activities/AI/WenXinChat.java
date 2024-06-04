package glue502.software.activities.AI;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.ChatlistAdapter;
import glue502.software.models.Chatlist;

//此activity主要用来实现聊天界面
public class WenXinChat extends AppCompatActivity {

    private ChatlistAdapter chatAdapter;
    private List<Chatlist> mDatas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wen_xin_chat);


        //聊天信息（示例）
        mDatas = new ArrayList<Chatlist>();
        Chatlist C1;
        C1=new Chatlist("ABC：","Hello,world!");
        mDatas.add(C1);
        Chatlist C2;
        C2=new Chatlist("DEF：","This is a new app.");
        mDatas.add(C2);
        //可以通过数据库插入数据

        chatAdapter=new ChatlistAdapter(this,mDatas);
        RecyclerView rc_chatlist=(RecyclerView) findViewById(R.id.rc_chatlist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        rc_chatlist.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rc_chatlist.setHasFixedSize(true);
        //创建并设置Adapter
        rc_chatlist.setAdapter(chatAdapter);
    }
}