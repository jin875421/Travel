package glue502.software.activities;

import static glue502.software.activities.MainActivity.ip;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import glue502.software.R;
import glue502.software.models.LoginResult;
import glue502.software.models.UserInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtAccount;
    private EditText edtPassword;
    private EditText edtAgainPassword;
    private EditText edtEmail;
    private EditText edtNickname;
    private EditText edtPhoneNumber;
    private Button btnregister;
    private String password;
    private String againpassword;


    private String url="http://"+ip+"/test/user/register";
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
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, CodeLoginActivity.class);
                        startActivity(intent);
                    } else {
                        // 登录失败，显示提示消息
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_register);
        edtAccount=findViewById(R.id.edt_account);
        edtPassword=findViewById(R.id.edt_password);
        edtAgainPassword=findViewById(R.id.edt_again_password);
        edtEmail=findViewById(R.id.edt_email);
        edtNickname=findViewById(R.id.edt_nickname);
        btnregister=findViewById(R.id.btn_register);
        edtPhoneNumber=findViewById(R.id.edt_phone_number);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtAccount.getText().toString();
                String email=edtEmail.getText().toString();
                String userNickname=edtNickname.getText().toString();
                String userPhoneNumber=edtPhoneNumber.getText().toString();
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

                if (username.length() == 0 && password.length() == 0) {
                    Toast.makeText(RegisterActivity.this, "输入的用户名和密码都为空", Toast.LENGTH_LONG).show();
                } else if (username.length() == 0) {
                    Toast.makeText(RegisterActivity.this, "输入的用户名为空", Toast.LENGTH_LONG).show();
                } else if (password.length() == 0) {
                    Toast.makeText(RegisterActivity.this, "输入的密码为空", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6 && password.length() > 1) {
                    Toast.makeText(RegisterActivity.this, "输入的密码小于六位数", Toast.LENGTH_LONG).show();
                } else if(againpassword.length()==0){
                    Toast.makeText(RegisterActivity.this, "二次输入的密码为空", Toast.LENGTH_LONG).show();
                } else if(againpassword.equals(password)==false){
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不相同", Toast.LENGTH_LONG).show();
                } else if(!isValidPhoneNumber(userPhoneNumber)){
                    Toast.makeText(RegisterActivity.this, "手机号格式不正确", Toast.LENGTH_LONG).show();
                }else{

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserInfo user = new UserInfo(username,password,email,userNickname,userPhoneNumber);
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

//                    register(username,password);
                }
            }
        });
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        // 进行手机号合法性判断的逻辑，可以使用正则表达式等方式
        // 此处简单示例，你可以根据实际需要扩展
        return phoneNumber.matches("^1[3-9]\\d{9}$");
    }

}