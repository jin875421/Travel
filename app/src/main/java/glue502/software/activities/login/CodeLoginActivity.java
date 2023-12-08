package glue502.software.activities.login;

import static glue502.software.activities.MainActivity.ip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import glue502.software.R;
import glue502.software.activities.MainActivity;
import glue502.software.models.UserInfo;
import glue502.software.models.LoginResult;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CodeLoginActivity extends AppCompatActivity {
    private EditText edtEmailOrPhone;
    private EditText edtCode;
    private Button btnGainCode;
    private Button btnLogin;
    private TextView txtAccount;
    private TextView txtRegister;
    private String url="http://"+ip+"/boot/user/emailOrPhoneLogin";
    private String urlEmail="http://"+ip+"/boot/user/sendEmail";
    private String urlPhone="http://"+ip+"/boot/user/sendSms";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    Gson gson = new Gson();
                    LoginResult loginResult = gson.fromJson(result, LoginResult.class);
                    int resultCode = loginResult.getResultCode();
                    String message = loginResult.getMsg();

                    // 根据 resultCode 判断登录是否成功
                    if (resultCode == 1) {
                        // 登录成功，跳转到 LoginActivity页面
                        String userId = loginResult.getUserId();
                        String userName = loginResult.getUserName();
                        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", userName);
                        editor.putString("userId", userId);
                        editor.putString("status","1");
                        editor.apply();

                        Intent intent = new Intent(CodeLoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // 登录失败，显示提示消息
                        Toast.makeText(CodeLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    String result1 = (String) msg.obj;
                    Gson gson1 = new Gson();
                    LoginResult loginResult1 = gson1.fromJson(result1, LoginResult.class);
                    int resultCode1 = loginResult1.getResultCode();
                    String message1 = loginResult1.getMsg();

                    // 根据 resultCode 判断登录是否成功
                    if (resultCode1 == 1) {
                        // 验证码获取成功
                        Toast.makeText(CodeLoginActivity.this, message1, Toast.LENGTH_SHORT).show();
                    } else {
                        // 验证码获取失败
                        Toast.makeText(CodeLoginActivity.this, message1, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_login);
        edtEmailOrPhone=findViewById(R.id.edt_email_or_phone);
        edtCode=findViewById(R.id.edt_code);
        edtEmailOrPhone=findViewById(R.id.edt_email_or_phone);
        btnGainCode=findViewById(R.id.btn_gaincode);
        btnLogin=findViewById(R.id.btn_login);
        txtAccount=findViewById(R.id.txt_account);
        txtRegister=findViewById(R.id.txt_register);
        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CodeLoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CodeLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        //获得验证码
        btnGainCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailOrPhone=edtEmailOrPhone.getText().toString();
                if (emailOrPhone.length() == 0 ) {
                    Toast.makeText(CodeLoginActivity.this, "输入的邮箱或手机号为空", Toast.LENGTH_LONG).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Request request;
                            UserInfo userInfo;
                            if(isValidPhoneNumber(emailOrPhone)) {
                                userInfo=new UserInfo(emailOrPhone,1);
                            }else{
                                userInfo=new UserInfo(emailOrPhone);
                            }
                            // 使用 Gson 转换为 JSON 数据
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(userInfo);
                            OkHttpClient client = new OkHttpClient();//创建Http客户端
                            if(isValidPhoneNumber(emailOrPhone)) {
                                request = new Request.Builder()
                                        .url(urlPhone)//***.***.**.***为本机IP，xxxx为端口，/  /  为访问的接口后缀
                                        .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonString))
                                        .build();//创建Http请求
                            }else{
                                request = new Request.Builder()
                                        .url(urlEmail)//***.***.**.***为本机IP，xxxx为端口，/  /  为访问的接口后缀
                                        .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonString))
                                        .build();//创建Http请求
                            }
                            try {
                                // 发送 POST 请求，并发送 JSON 数据
                                Response response = client.newCall(request).execute();//执行发送的指令
                                final String responseData = response.body().string();//获取返回的结果

                                // 通过 Handler 或者 EventBus 将结果传回主线程
                                Message message = Message.obtain();
                                message.what = 2;
                                message.obj = responseData;
                                mHandler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                disableButtonForSomeTime();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edtCode.getText().toString();
                String emailOrPhone=edtEmailOrPhone.getText().toString();
                if (emailOrPhone.length() == 0 ) {
                    Toast.makeText(CodeLoginActivity.this, "输入的邮箱或手机号为空", Toast.LENGTH_LONG).show();
                } else if (code.length() == 0) {
                    Toast.makeText(CodeLoginActivity.this, "输入的验证码为空", Toast.LENGTH_LONG).show();
                } else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserInfo user;
                            if(isValidPhoneNumber(emailOrPhone)) {
                                user = new UserInfo(emailOrPhone,code,0.1);
                            }else{
                                user = new UserInfo(emailOrPhone,code,1);
                            }
                            // 使用 Gson 将 User 对象转换为 JSON 数据
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(user);
                            OkHttpClient client = new OkHttpClient();//创建Http客户端

                            Request request = new Request.Builder()
                                    .url(url)//***.***.**.***为本机IP，xxxx为端口，/  /  为访问的接口后缀
                                    .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonString))
                                    .build();//创建Http请求
                            try {
                                // 发送 POST 请求，并发送 JSON 数据
                                Response response = client.newCall(request).execute();//执行发送的指令
                                final String responseData = response.body().string();//获取返回的结果

                                // 通过 Handler 或者 EventBus 将结果传回主线程
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = responseData;
                                mHandler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

    }
    private void disableButtonForSomeTime() {
        btnGainCode.setEnabled(false);

        long delayMillis = 60000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 在延迟后启用按钮
                btnGainCode.setEnabled(true);
                btnGainCode.setText(""); // 倒计时结束后清空文本
            }
        }, delayMillis);

        startCountdown();
    }

    private void startCountdown() {
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 更新倒计时文本
                btnGainCode.setText("重新获取(" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                // 倒计时结束后清空文本
                btnGainCode.setText("获取验证码");
            }
        };

        countDownTimer.start();
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        // 进行手机号合法性判断的逻辑，可以使用正则表达式等方式
        // 此处简单示例，你可以根据实际需要扩展
        return phoneNumber.matches("^1[3-9]\\d{9}$");
    }
}
