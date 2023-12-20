package glue502.software.activities.login;

import static glue502.software.activities.MainActivity.ip;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import glue502.software.R;
import glue502.software.models.UserInfo;
import glue502.software.models.LoginResult;
import glue502.software.utils.MyViewUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtEmailOrPhone;
    private EditText edtCode;
    private Button btnGainCode;
    private EditText edtPassword;
    private EditText edtAgainPassword;
    private Button btnChange;
    private String password;
    private String againpassword;
    private final int[] backgroundImages = {
            R.drawable.login_bg001,
            R.drawable.login_background2,
            R.drawable.login_background3,
            R.drawable.login_background4,
            R.drawable.login_background5,
            R.drawable.login_background6,
            R.drawable.login_background7,
            R.drawable.login_background8,
            R.drawable.login_background9,
            R.drawable.login_background13,
            R.drawable.login_background19
    };
    private String url="http://"+ip+"/travel/user/forgotPassword";
    private String urlEmail="http://"+ip+"/travel/user/sendEmail";
    private String urlPhone="http://"+ip+"/travel/user/sendSms";
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
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // 登录失败，显示提示消息
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        // 登录成功，跳转到 LoginActivity页面
                        Toast.makeText(ForgotPasswordActivity.this, message1, Toast.LENGTH_SHORT).show();
                    } else {
                        // 登录失败，显示提示消息
                        Toast.makeText(ForgotPasswordActivity.this, message1, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_forgotpassword);
        edtEmailOrPhone=findViewById(R.id.edt_email_or_phone);
        edtCode=findViewById(R.id.edt_code);
        edtPassword=findViewById(R.id.edt_password);
        edtAgainPassword=findViewById(R.id.edt_again_password);
        edtEmailOrPhone=findViewById(R.id.edt_email_or_phone);
        btnChange=findViewById(R.id.btn_change);
        btnGainCode=findViewById(R.id.btn_gaincode);
        //添加沉浸式导航栏
        MyViewUtils.setISBarWithoutView(this);
//        setRandomBackground();
        btnGainCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailOrPhone=edtEmailOrPhone.getText().toString();
                if (emailOrPhone.length() == 0 ) {
                    Toast.makeText(ForgotPasswordActivity.this, "输入的邮箱或手机号为空", Toast.LENGTH_LONG).show();
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

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edtCode.getText().toString();
                String emailOrPhone=edtEmailOrPhone.getText().toString();
                try {
                    //MD5加密密码
                    String password1 = edtPassword.getText().toString();
                    MessageDigest messageDigest=MessageDigest.getInstance("MD5");
                    byte[] plainTextBytes=password1.getBytes();
                    messageDigest.update(plainTextBytes);
                    byte[] encryptedBytes=messageDigest.digest();
                    BigInteger bigInteger=new BigInteger(1,encryptedBytes);
                    password=String.format("%032x",bigInteger);

                    String againpassword1=edtAgainPassword.getText().toString();
                    MessageDigest messageDigest1=MessageDigest.getInstance("MD5");
                    byte[] plainTextBytes1=againpassword1.getBytes();
                    messageDigest.update(plainTextBytes1);
                    byte[] encryptedBytes1=messageDigest.digest();
                    BigInteger bigInteger1=new BigInteger(1,encryptedBytes1);
                    againpassword=String.format("%032x",bigInteger1);

                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }


                if (emailOrPhone.length() == 0 ) {
                    Toast.makeText(ForgotPasswordActivity.this, "输入的邮箱或手机号为空", Toast.LENGTH_LONG).show();
                } else if (code.length() == 0) {
                    Toast.makeText(ForgotPasswordActivity.this, "输入的验证码为空", Toast.LENGTH_LONG).show();
                } else if (password.length() == 0) {
                    Toast.makeText(ForgotPasswordActivity.this, "输入的密码为空", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6 && password.length() > 1) {
                    Toast.makeText(ForgotPasswordActivity.this, "输入的密码小于六位数", Toast.LENGTH_LONG).show();
                } else if(againpassword.length()==0){
                    Toast.makeText(ForgotPasswordActivity.this, "二次输入的密码为空", Toast.LENGTH_LONG).show();
                } else if(againpassword.equals(password)==false){
                    Toast.makeText(ForgotPasswordActivity.this, "两次输入的密码不相同", Toast.LENGTH_LONG).show();
                } else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserInfo user ;
                            if(isValidPhoneNumber(emailOrPhone)) {
                                user = new UserInfo(password,emailOrPhone,code,1);
                            }else{
                                user = new UserInfo(password,emailOrPhone,code);
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
                btnGainCode.setText("");
            }
        };

        countDownTimer.start();
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        // 进行手机号合法性判断的逻辑，可以使用正则表达式等方式
        // 此处简单示例，你可以根据实际需要扩展
        return phoneNumber.matches("^1[3-9]\\d{9}$");
    }
    private void setRandomBackground() {
        // 获取 ImageView 实例
        ImageView imgBackgroundLogin = findViewById(R.id.img_background_forgot);

        // 随机选择一个索引
        Random random = new Random();
        int randomIndex = random.nextInt(backgroundImages.length);

        // 设置随机选择的背景图片
        imgBackgroundLogin.setImageResource(backgroundImages[randomIndex]);
    }
}
