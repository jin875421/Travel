package glue502.software.activities.AI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import glue502.software.R;
import glue502.software.activities.travelRecord.FunctionActivity;


/**
 * 这是进入APP后的第一个界面，主要实现的功能包括:
 * 开始一个新的聊天？或者延续旧的聊天？
 * 如果用户设置为启动后默认进入一个旧的聊天，那么直接跳转到旧聊天记录
 */
public class MainWenXinActivity extends Activity {
    private Button btn_startnewchat,btn_startoldchat,btn_startsetting;

    /**
     * onCreate方法是一个非常重要的回调方法，它在Activity被首次创建时调用。
     * onCreate方法接受一个名为savedInstanceState的Bundle参数。
     * 这个参数的主要作用是保存Activity在之前的状态，
     * 以便在Activity被系统销毁并重新创建时能够恢复其状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wenxin_main);
        init();

        Intent intent = getIntent();
        //跳转建立新聊天
        btn_startnewchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainWenXinActivity.this, ChatActivity.class);
                startActivity(intent);
                MainWenXinActivity.this.finish();
            }
        });
        //跳转到历史聊天
        btn_startoldchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainWenXinActivity.this, HistoryChatActivity.class);
                startActivity(intent);
                //MainActivity.this.finish();
            }
        });
//        AI_5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //跳转
//                Intent intent = new Intent(FunctionActivity.this, ImageRecognitionActivityA.class);
//
//                int sign = 5;
//                intent.putExtra("sign",sign);
//
//                startActivity(intent);
//            }
//        });
    }

    /**进行初始化操作
     */
    private void init(){
        //与xml的控件绑定
//        btn_startnewchat=findViewById(R.id.btn_startnewchat);
//        btn_startoldchat=findViewById(R.id.btn_startoldchat);
        btn_startsetting=findViewById(R.id.btn_startsetting);
    }

}