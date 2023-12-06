package glue502.software.activities;

import static glue502.software.activities.MainActivity.ip;
import android.app.Activity;
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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import glue502.software.R;
import glue502.software.models.UserInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdatePersonalInformationActivity extends AppCompatActivity {
    private EditText edtNickname;
    private ImageView imgAvatar;
    private Button btnChoose;
    private Button btnSubmit;
    private Button btnBcak;
    private String urlAvatar="http://"+ip+"/test/user/upload";
    private String urlName="http://"+ip+"/test/user/updateData";
    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] yourImageBytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upadte_person_information);
        edtNickname=findViewById(R.id.edt_nickname);
        btnSubmit=findViewById(R.id.btn_submit);
        btnBcak=findViewById(R.id.btn_back);
        imgAvatar=findViewById(R.id.img_avatar);
        btnChoose=findViewById(R.id.btn_choose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnBcak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取用户输入的昵称
                String userName = edtNickname.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", "");
                if (userName.isEmpty()&&yourImageBytes==null) {
                    Toast.makeText(UpdatePersonalInformationActivity.this, "输入的昵称和选择的图片都为空", Toast.LENGTH_LONG).show();
                } else if (yourImageBytes == null) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 获取用户ID
                            UserInfo user = new UserInfo(userId,userName,true);
                            // 使用 Gson 将 User 对象转换为 JSON 数据
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(user);
                            Request request = new Request.Builder()
                                    .url(urlName)//***.***.**.***为本机IP，xxxx为端口，/  /  为访问的接口后缀
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
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                finish();
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
                    imgAvatar.setImageBitmap(bitmap);
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

}
