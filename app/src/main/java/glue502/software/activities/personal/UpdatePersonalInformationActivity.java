package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import glue502.software.R;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdatePersonalInformationActivity extends AppCompatActivity {
    private EditText edtName;
    private ImageView imgAvatar;
    private RelativeLayout rltlChooseSex;
    private TextView txtSex;
    private RelativeLayout rltlChooseAvatar;
    private EditText edtPhone;
    private EditText edtEmail;
    private TextView txtSave;
    private ImageView imgBcak;
    private String urlAvatar="http://"+ip+"/travel/user/upload";
    private String urlUpdate="http://"+ip+"/travel/user/updateData";
    private String urlFindData="http://"+ip+"/travel/user/getAvatar?userId=";
    private String urlLoadImage="http://"+ip+"/travel/";
    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] yourImageBytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upadte_person_information);
        MyViewUtils.setImmersiveStatusBar(this,findViewById(R.id.top),true);
        edtName=findViewById(R.id.edt_name);
        txtSave=findViewById(R.id.txt_save);
        imgBcak=findViewById(R.id.img_back);
        imgAvatar=findViewById(R.id.img_avatar);
        rltlChooseSex=findViewById(R.id.rltl_choose_sex);
        edtPhone=findViewById(R.id.edt_phone);
        txtSex=findViewById(R.id.txt_sex);
        edtEmail=findViewById(R.id.edt_email);
        rltlChooseAvatar=findViewById(R.id.rltl_choose_avatar);
        MyViewUtils.setISBarWithoutView(this,true);
        //写入用户数据
        writeData();
        rltlChooseSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenderDialog();
            }
        });
        rltlChooseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        imgBcak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取用户输入的昵称
                String userName = edtName.getText().toString();
                String sex=txtSex.getText().toString();
                String userPhoneNumber=edtPhone.getText().toString();
                String email=edtEmail.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", "");

                if (userName.isEmpty()&&yourImageBytes==null) {
                    Toast.makeText(UpdatePersonalInformationActivity.this, "输入的昵称和选择的图片都为空", Toast.LENGTH_LONG).show();
                } else if (!userPhoneNumber.isEmpty()&&!userPhoneNumber.matches("^1[3-9]\\d{9}$")) {
                    Toast.makeText(UpdatePersonalInformationActivity.this, "手机号格式不正确", Toast.LENGTH_LONG).show();
                } else if (!email.isEmpty()&&!email.contains("@")) {
                    Toast.makeText(UpdatePersonalInformationActivity.this, "邮箱格式不正确", Toast.LENGTH_LONG).show();
                } else if (yourImageBytes == null) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 获取用户ID

                            UserInfo user = new UserInfo(userId,userName,sex,userPhoneNumber,email,true);
                            // 使用 Gson 将 User 对象转换为 JSON 数据
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(user);
                            Request request = new Request.Builder()
                                    .url(urlUpdate)//***.***.**.***为本机IP，xxxx为端口，/  /  为访问的接口后缀
                                    .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonString))
                                    .build();//创建Http请求
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Response response = client.newCall(request).execute();
                                final String responseData = response.body().string();

                                // 处理服务器响应，更新UI或执行其他操作
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UpdatePersonalInformationActivity.this, responseData, Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userName", userName);
                                        editor.apply();
                                        Intent resultIntent = new Intent();
                                        setResult(Activity.RESULT_OK, resultIntent);
                                        UpdatePersonalInformationActivity.this.finish();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }else {
                    // 构建Multipart请求体
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", userId+".jpg", RequestBody.create(MediaType.parse("image/*"), yourImageBytes))
                            .addFormDataPart("userName", userName)
                            .addFormDataPart("userId", userId)
                            .addFormDataPart("sex",sex)
                            .addFormDataPart("userPhoneNumber",userPhoneNumber)
                            .addFormDataPart("email",email)
                            .build();

                    // 构建POST请求
                    Request request = new Request.Builder()
                            .url(urlAvatar)
                            .post(requestBody)
                            .build();

                    // 发送请求
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Response response = client.newCall(request).execute();
                                final String responseData = response.body().string();

                                // 处理服务器响应，更新UI或执行其他操作
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UpdatePersonalInformationActivity.this, responseData, Toast.LENGTH_SHORT).show();
                                        if(!userName.isEmpty()){
                                            SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("userName", userName);
                                            editor.apply();
                                        }

                                        Intent resultIntent = new Intent();
                                        setResult(Activity.RESULT_OK, resultIntent);
                                        UpdatePersonalInformationActivity.this.finish();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }


            }
        });
    }

    private void writeData() {
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        // 创建 OkHttp 客户端
        OkHttpClient client = new OkHttpClient();

        // 构建请求
        Request request = new Request.Builder()
                .url(urlFindData + userId)  // 替换为你的后端 API 地址
                .build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 处理请求失败的情况
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                // 获取 avatarUrl 和 userNickname,sex
                Gson gson=new Gson();
                // 获取 avatarUrl 和 userNickname
                UserInfo userInfo = gson.fromJson(responseData,UserInfo.class);
                String avatarUrl=userInfo.getAvatar();
                String userName =userInfo.getUserName();
                String sex=userInfo.getSex();
                String userPhoneNumber=userInfo.getUserPhoneNumber();
                String email=userInfo.getEmail();
                // 在 UI 线程中更新 ImageView
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用 Glide 加载用户头像
                        RequestOptions requestOptions = new RequestOptions()
                                .transform(new CircleCrop());
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(getApplicationContext())
                                    .load(urlLoadImage + avatarUrl)
                                    .placeholder(R.drawable.headimg)  // 设置占位图
                                    .apply(requestOptions)// 设置签名
                                    .into(imgAvatar);

                            // 将其他 UI 更新放在这里
                            edtName.setText(userName);
                            txtSex.setText(sex);
                            edtPhone.setText(userPhoneNumber);
                            edtEmail.setText(email);
                        } else {
                            // 处理返回的不是有效地址的情况
                            // 可以设置默认头像或给用户提示
                            Glide.with(getApplicationContext())
                                    .load(R.drawable.headimg)
                                    .apply(requestOptions)
                                    .into(imgAvatar);

                            // 将其他 UI 更新放在这里
                            edtName.setText(userName);
                            txtSex.setText(sex);
                            edtPhone.setText(userPhoneNumber);
                            edtEmail.setText(email);
                        }
                    }
                });
            }
        });
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // 获取选定的图片的 URI
                Uri selectedImageUri = data.getData();

                // 根据 URI 获取 Bitmap 对象
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                    // 将 Bitmap 转换为字节数组
                    yourImageBytes = bitmapToBytes(bitmap);

                    // 将 Bitmap 显示在 ImageView 中
                    Glide.with(this)
                            .load(selectedImageUri)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(imgAvatar);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 用户取消了选择图片操作，可以在这里添加相应的逻辑
                Toast.makeText(this, "取消选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 辅助方法，将 Bitmap 转换为字节数组
    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
    private void showGenderDialog() {
        final CharSequence[] genderOptions = {"男", "女"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择性别")
                .setItems(genderOptions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 处理选择性别的逻辑
                        String selectedGender = genderOptions[which].toString();
                        // 在这里可以执行相应的操作，比如显示选择的性别
                        txtSex.setText(selectedGender);
                    }
                });
        builder.show();
    }
}
