package glue502.software.activities.login;


import static glue502.software.activities.MainActivity.ip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import glue502.software.R;
import glue502.software.activities.IM.IMLoginActivity;
import glue502.software.activities.IM.MainIMActivity;
import glue502.software.activities.MainActivity;
import glue502.software.models.UserInfo;
import glue502.software.models.LoginResult;
import glue502.software.utils.MyViewUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {
    private ProgressDialog mDialog;
    private Button btnLogin;
    private TextView txtCode;
    private TextView txtRegister;
    private TextView txtForget;
    private EditText edtUserId;
    private EditText edtPassword;
    private ImageView  eyeImageView;
    private String password;
    private String url="http://"+ip+"/travel/user/login";
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
                        // 登录成功，跳转到 MainActivity 页面

                        //存id
                        String userId = loginResult.getUserId();
                        String userName = loginResult.getUserName();
                        String userPhoneNumber=loginResult.getUserPhoneNumber();
                        String email=loginResult.getEmail();
                        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", userName);
                        editor.putString("userId", userId);
                        editor.putString("userPhoneNumber",userPhoneNumber);
                        editor.putString("email",email);
                        editor.putString("status","1");
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        // 登录失败，显示提示消息
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化环信，在这里实现了类似于项目中的token判断，如果没有token则跳转到登录界面
        EMOptions options = new EMOptions();
        options.setAppKey("1117240606210709#travel");
        // 其他 EMOptions 配置。
        EMClient.getInstance().init(this, options);

        edtUserId =findViewById(R.id.edt_account);
        edtPassword =findViewById(R.id.edt_password);
        btnLogin =findViewById(R.id.btn_login);
        txtCode =findViewById(R.id.txt_code);
        txtRegister=findViewById(R.id.txt_register);
        txtForget=findViewById(R.id.txt_forget);
        eyeImageView = findViewById(R.id.img_eye);
//        setRandomBackground();
//        //添加沉浸式导航栏
        MyViewUtils.setISBarWithoutView(this,true);


        eyeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });
        txtCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CodeLoginActivity.class);
                startActivity(intent);
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        txtForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取用户名和密码
                String userId = edtUserId.getText().toString();
                try {
                    //MD5加密密码
                    String password1 = edtPassword.getText().toString();
                    MessageDigest messageDigest=MessageDigest.getInstance("MD5");
                    byte[] plainTextBytes=password1.getBytes();
                    messageDigest.update(plainTextBytes);
                    byte[] encryptedBytes=messageDigest.digest();
                    BigInteger bigInteger=new BigInteger(1,encryptedBytes);
                    password=String.format("%032x",bigInteger);

                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

                if (userId.length() == 0 && password.length() == 0) {
                    Toast.makeText(LoginActivity.this, "输入的用户名和密码都为空", Toast.LENGTH_LONG).show();
                } else if (userId.length() == 0) {
                    Toast.makeText(LoginActivity.this, "输入的用户名为空", Toast.LENGTH_LONG).show();
                } else if (password.length() == 0) {
                    Toast.makeText(LoginActivity.this, "输入的密码为空", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6 && password.length() > 1) {
                    Toast.makeText(LoginActivity.this, "输入的密码小于六位数", Toast.LENGTH_LONG).show();
                } else {
                    signIn();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserInfo user = new UserInfo(userId,password);
                            // 使用 Gson 将 User 对象转换为 JSON 数据

                            try {
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(user);
                                OkHttpClient client = new OkHttpClient();//创建Http客户端
                                Request request = new Request.Builder()
                                        .url(url)//***.***.**.***为本机IP，xxxx为端口，/  /  为访问的接口后缀
                                        .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonString))
                                        .build();//创建Http请求
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

    private void changeImage() {
        // 切换密码可见性
        boolean isPasswordVisible = (edtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD));
        edtPassword.setInputType(isPasswordVisible
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        // 切换眼睛状态
        int eyeIconResId = isPasswordVisible ? R.drawable.baseline_visibility_off_black_48 : R.drawable.baseline_visibility_black_48;
        eyeImageView.setImageResource(eyeIconResId);

        // 将光标移到最后
        edtPassword.setSelection(edtPassword.getText().length());

        // 强制刷新视图
        eyeImageView.postInvalidate();
    }
    private void setRandomBackground() {
        // 获取 ImageView 实例
        ImageView imgBackgroundLogin = findViewById(R.id.img_background_login);

        // 随机选择一个索引
        Random random = new Random();
        int randomIndex = random.nextInt(backgroundImages.length);

        // 设置随机选择的背景图片
        imgBackgroundLogin.setImageResource(backgroundImages[randomIndex]);
    }
    //TODO 在实现了登入进入后，如果直接退出会导致帐户状态码未修改，直接导致下次登入失败，所以需要在登入成功后，修改帐户状态码
    /**
     * 登录方法
     */
    private void signIn() {
        //登入方法是个异步方法，直接进行调用即可
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在登陆，请稍后...");
        mDialog.show();
        String username = edtUserId.getText().toString().trim();
        String password = edtUserId.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        EMClient.getInstance().login(username, password, new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        mDialog.dismiss();
                        // 加载所有会话到内存
                        EMClient.getInstance().chatManager().loadAllConversations();
                        // 加载所有群组到内存，如果使用了群组的话
                        // EMClient.getInstance().groupManager().loadAllGroups();
                        finish();
                    }
                });
            }

            /**
             * 登陆错误的回调
             * @param i
             * @param s
             */
            @Override public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        mDialog.dismiss();
                        Log.d("lzan13", "登录失败 Error code:" + i + ", message:" + s);
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        switch (i) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                Toast.makeText(LoginActivity.this,
                                        "网络错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                Toast.makeText(LoginActivity.this,
                                        "无效的用户名 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                Toast.makeText(LoginActivity.this,
                                        "无效的密码 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                Toast.makeText(LoginActivity.this,
                                                "用户认证失败，用户名或密码错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG)
                                        .show();
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                Toast.makeText(LoginActivity.this,
                                        "用户不存在 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                Toast.makeText(LoginActivity.this,
                                        "无法访问到服务器 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                Toast.makeText(LoginActivity.this,
                                                "等待服务器响应超时 code: " + i + ", message:" + s, Toast.LENGTH_LONG)
                                        .show();
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                Toast.makeText(LoginActivity.this,
                                        "服务器繁忙 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 未知 Server 异常 303 一般断网会出现这个错误
                            case EMError.SERVER_UNKNOWN_ERROR:
                                Toast.makeText(LoginActivity.this,
                                        "未知的服务器异常 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(LoginActivity.this,
                                        "ml_sign_in_failed code: " + i + ", message:" + s,
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }

            @Override public void onProgress(int i, String s) {

            }
        });
    }
}