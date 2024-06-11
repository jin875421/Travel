package glue502.software.activities.AI;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import glue502.software.R;
import glue502.software.utils.MyViewUtils;


public class SettingActivity extends Activity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button btn_settingfinish,btn_settinggiveup;
    private EditText et_APIKEY,et_SECRETKEY,et_role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wenxin_setting);
        init();
        MyViewUtils.setISBarWithoutView(this,true);
        //把原有设置填进去
        preferences= this.getSharedPreferences("usersetting",MODE_PRIVATE);
        et_APIKEY.setText(preferences.getString("API_Key","oQtUEMpGo1M9vsMfxmrwePzF"));
        et_SECRETKEY.setText(preferences.getString("Secret_Key","LxfNECAa2nNu5fLQERgNlUyL4W2UW0eX"));
        et_role.setText(preferences.getString("Role","你的名字是ERNIE，你是一位英语对话练习助手，你只能以英语进行回答"));





        //点击按键，结束设置
        btn_settingfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences=getSharedPreferences("usersetting",MODE_PRIVATE);
                editor=preferences.edit();
                //将输入的数据写入SharedPreference
                editor.putString("API_Key",et_APIKEY.getText().toString());
                editor.putString("Secret_Key",et_SECRETKEY.getText().toString());
                editor.putString("Role",et_role.getText().toString());
                editor.commit();


                SettingActivity.this.finish();
            }
        });

        //点击按键，放弃设置，啥也不改直接退出
        btn_settinggiveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

    }
    public void init(){
        btn_settingfinish=findViewById(R.id.btn_settingfinish);
        btn_settinggiveup=findViewById(R.id.btn_settinggiveup);
        et_APIKEY=findViewById(R.id.et_APIKEY);
        et_SECRETKEY=findViewById(R.id.et_SECRETKEY);
        et_role=findViewById(R.id.et_role);
    }
}