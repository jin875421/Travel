package glue502.software.activities.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import glue502.software.R;
import glue502.software.utils.MyViewUtils;

public class SettingActivity extends AppCompatActivity {
    private Button btnDestroy;
    private ImageView imgBcak;
    private RelativeLayout rltlAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView());
        btnDestroy=findViewById(R.id.btn_destroy);
        imgBcak=findViewById(R.id.img_back);
        rltlAccount=findViewById(R.id.rltl_account);
        rltlAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, UpdatePersonalInformationActivity.class);
                startActivity(intent);
            }
        });
        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();

            }
        });
        imgBcak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("退出登录");
        builder.setMessage("确定要退出登录吗？");

        builder.setPositiveButton("确认", (dialog, which) -> {
            // 清除SharedPreferences中的用户信息
            clearSharedPreferences();
            // 提示注销成功
            Toast.makeText(SettingActivity.this, "已退出", Toast.LENGTH_SHORT).show();

        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            // 用户选择取消，不执行任何操作
        });

        builder.show();
    }


    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences =
                getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);

        // 清除SharedPreferences中的用户信息
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
