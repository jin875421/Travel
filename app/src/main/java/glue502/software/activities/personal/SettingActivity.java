package glue502.software.activities.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import com.bumptech.glide.Glide;

import glue502.software.R;
import glue502.software.activities.IM.MainIMActivity;
import glue502.software.activities.login.CodeLoginActivity;
import glue502.software.utils.MyViewUtils;

public class SettingActivity extends AppCompatActivity {
    private Button btnDestroy;
    private ImageView imgBcak;
    private RelativeLayout rltlAccount, rltlWakeup;
    private RelativeLayout rltlCustomerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //初始化环信，在这里实现了类似于项目中的token判断，如果没有token则跳转到登录界面
        EMOptions options = new EMOptions();
        options.setAppKey("1117240606210709#travel");
        // 其他 EMOptions 配置。
        EMClient.getInstance().init(this, options);

        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        btnDestroy=findViewById(R.id.btn_destroy);
        imgBcak=findViewById(R.id.img_back);
        rltlAccount=findViewById(R.id.rltl_account);
        rltlWakeup=findViewById(R.id.rltl_wakeup);
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);
        btnDestroy = findViewById(R.id.btn_destroy);
        imgBcak = findViewById(R.id.img_back);
        rltlAccount = findViewById(R.id.rltl_account);
        rltlCustomerService = findViewById(R.id.rltl_customer_service);
//        MyViewUtils.setISBarWithoutView(this,true);
        rltlAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, UpdatePersonalInformationActivity.class);
                startActivity(intent);
            }
        });
        rltlCustomerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("联系客服");
                builder.setMessage("请拨打1008611或发送邮件到2391835196@qq.com");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 在这里执行确定按钮被点击后的操作
                        dialogInterface.dismiss(); // 关闭对话框
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 在这里执行取消按钮被点击后的操作
                        dialogInterface.dismiss(); // 关闭对话框
                    }
                });
                // 创建并显示AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        rltlWakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MainIMActivity.class);
                startActivity(intent);
            }
        });

        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
                String status = sharedPreferences.getString("status", "");
                if (!status.equals("1") && status.isEmpty()) {
                    showLoginAlertDialog();
                } else {
                    showLogoutConfirmationDialog();
                }
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
            //退出IM
            signOut();
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

    /**
     * 退出登录，实现了sdk和用户解绑的过程,如果不进行解绑的话，用户再次登录时，任然会直接进入输入页面
     */
    private void signOut() {
        Log.i("SettingActivity", "lzx 用户退出IM");
        // 调用sdk的退出登录方法，第一个参数表示是否解绑推送的token，没有使用推送或者被踢都要传false
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override public void onSuccess() {
                Log.i("lzan13", "logout success");
                // 调用退出成功，结束app
                finish();
            }

            @Override public void onError(int i, String s) {
                Log.i("lzan13", "logout error " + i + " - " + s);
            }

            @Override public void onProgress(int i, String s) {

            }
        });
    }


    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
            // 清除SharedPreferences中的用户信息
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        SharedPreferences sharedPreferences1 = getSharedPreferences("personalStatu", Context.MODE_PRIVATE);
        // 清除SharedPreferences中的用户信息
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.clear();
        editor1.apply();
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
    }

    private void showLoginAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("您未登录，是否登录？");
        builder.setPositiveButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 跳转到登录页面
                Intent intent = new Intent(SettingActivity.this, CodeLoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 取消操作
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
