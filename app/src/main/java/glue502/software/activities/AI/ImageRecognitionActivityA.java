package glue502.software.activities.AI;

import static glue502.software.activities.MainActivity.PERMISSION_REQUEST_CODE;
import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import glue502.software.R;
import glue502.software.activities.MainActivity;
import glue502.software.activities.posts.UploadPostActivity;
import glue502.software.models.AIResult;
import glue502.software.models.IRResult;
import glue502.software.models.Post;
import glue502.software.utils.MyViewUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ImageRecognitionActivityA extends AppCompatActivity {

    private String url = "http://"+ip+"/travel/AI/recognition";
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1234;
    private final int RESULT_LOAD_IMAGES = 1, RESULT_CAMERA_IMAGE = 2;
    private String mCurrentPhotoPath;
    private List<View> viewList = new ArrayList<>();
    private List<File> fileList = new ArrayList<>();
    private LinearLayout imgLinerLayout;
    private Button upload;

    private Button choose_photo;
    private ImageView uploadImage;
    private Button btn_camera;
    private ImageView imageView;
    private TextView source, root, keyword, description;
    private LinearLayout webViewLayout;
    private WebView webView;

    public static final int TAKE_CAMERA = 101;
    public static final int PICK_PHOTO = 102;
    private Uri imageUri;
    private int sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition_1);
        //添加沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);

        //接受intent
        // 获取传递过来的int数据
        Intent intent = getIntent();
        if (intent != null) {
            sign = intent.getIntExtra("sign", -1); // "key"应该与A页面设置的一致，-1是默认值
        }
        System.out.println("sign="+sign);


        choose_photo = (Button) findViewById(R.id.btn_photo);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        imgLinerLayout = findViewById(R.id.imageContainer);
        upload = findViewById(R.id.upload);
        source = findViewById(R.id.source);
        root = findViewById(R.id.root);
        keyword = findViewById(R.id.keyword);
        description = findViewById(R.id.description);
        webViewLayout = findViewById(R.id.web_view);
        webView = (WebView) findViewById(R.id.wv_webview);

        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });

        //上传
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 禁用按钮防止多次点击触发上传
                upload.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Post post = new Post();
                        post.setPostContent(String.valueOf(sign));
                        post.setPostTitle(String.valueOf(sign));
                        //设定1位用户发帖，2为用户分享旅游经历
                        // id需从本地获取，待个人信息模块完成后补充实现
                        post.setuserId(String.valueOf(sign));
                        System.out.println("上传的post="+post.getPostTitle());
                        Date currentTime = new Date();
                        // 定义日期时间格式
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        // 格式化当前时间
                        String formattedTime = sdf.format(currentTime);
                        post.setCreateTime(formattedTime);
                        post.setPictureNumber(fileList.size());
                        OkHttpClient client = new OkHttpClient();
                        Gson gson = new Gson();
                        String json = gson.toJson(post);
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("post", json, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json));
                        //循环处理图片
                        for (int i = 0; i < fileList.size(); i++) {
                            File file = fileList.get(i);
                            if (file != null && file.exists()) {
                                int totalChunks = calculateTotalChunks(file);//计算分片数
                                String identifier = UUID.randomUUID().toString();;//生成唯一标识符
                                int sequenceNumber = 0;
                                try(InputStream inputStream = new FileInputStream(file)) {
                                    byte[] buffer = new byte[1024*1024];//设定分片大小
                                    int bytesRead;
                                    while ((bytesRead = inputStream.read(buffer))!=-1){
                                        byte[] actualBuffer = Arrays.copyOfRange(buffer, 0, bytesRead);
                                        builder.addFormDataPart("identifiers", identifier);
                                        builder.addFormDataPart("sequenceNumbers", String.valueOf(sequenceNumber));
                                        builder.addFormDataPart("totalChunks", String.valueOf(totalChunks));
                                        builder.addFormDataPart("images", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), actualBuffer));
                                        sequenceNumber++;
                                    }
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }else {}
                        }
                        RequestBody requestBody = builder.build();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();
                        try {
                            //发送请求
                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                String result = response.body().string();
                                System.out.println("返回的信息"+result);
                                IRResult irResult = gson.fromJson(result, IRResult.class);
                                System.out.println("irResult="+irResult.toString());
                                System.out.println("百科url"+irResult.getBaike_info().getBaike_url());

                                String myDescription = irResult.getBaike_info().getDescription();
                                System.out.println("描述="+irResult.getBaike_info().getDescription());
                                // 动态改变TextView的宽度
                                source.setText("置信度："+String.format("%.2f",irResult.getScore()));
                                root.setText("类别："+irResult.getRoot());
                                keyword.setText("关键字："+irResult.getKeyword());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        description.setText("描述："+myDescription);
                                        // 获取LinearLayout的布局参数
                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) webViewLayout.getLayoutParams();

                                        // 切换高度
                                        if (layoutParams.height == 0) {
                                            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                                        } else {
                                            layoutParams.height = 0;
                                        }

                                        // 应用新的布局参数
                                        webViewLayout.setLayoutParams(layoutParams);

                                        webView.loadUrl(httpToHttps(irResult.getBaike_info().getBaike_url()));
                                    }
                                });

                            } else {
                                // 请求失败处理错误
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        //从相册选择图片
        choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("打开文件选择器");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGES);
            }
        });
        //用户选择拍照上传
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ImageRecognitionActivityA.this,
                        Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {

                    // 如果权限尚未授予，则请求权限
                    ActivityCompat.requestPermissions(ImageRecognitionActivityA.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                }
                //如果权限已经授予
                if (ContextCompat.checkSelfPermission(ImageRecognitionActivityA.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takeCamera(RESULT_CAMERA_IMAGE);
                }
            }
        });
    }

    //把http转换为https
    public static String httpToHttps(String url) {
        if (url.startsWith("http://")) {
            url = url.replace("http://", "https://");
        }
        return url;
    }

    //启动相机
    private void takeCamera(int num) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, num);
            }
        }
    }

    //生成文件名
    private String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "JPEG_" + timeStamp + "_";
    }

    //处理拍摄的图片
    private File createImageFile() {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = null;
        try {
            image = File.createTempFile(generateFileName(), ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("==onActivityResult");
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGES && data != null) {
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    int count = clipData.getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri selectedImage = clipData.getItemAt(i).getUri();
                        File file = getFileFromUri(selectedImage);
                        //获取文件名
                        String fileName1 = file.getName();
                        fileList.add(file);
                        displaySelectedImage(selectedImage,fileName1);
                    }
                } else if(data.getData() != null) {
                    Uri selectedImage = data.getData();
                    File file = getFileFromUri(selectedImage);
                    //获取文件名
                    String fileName1 = file.getName();
                    fileList.add(file);
                    displaySelectedImage(selectedImage,fileName1);
                }
            } else if (requestCode == RESULT_CAMERA_IMAGE) {
                displayCapturedPhoto();
            }
        }
    }

    //通过uri获取文件
    private File getFileFromUri(Uri uri) {
        System.out.println("通过uri获取文件");
        try {
            ContentResolver contentResolver = getContentResolver();
            String displayName = null;
            String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                displayName = cursor.getString(index);
            }
            cursor.close();

            if (displayName != null) {
                InputStream inputStream = contentResolver.openInputStream(uri);
                if (inputStream != null) {
                    File file = new File(getCacheDir(), displayName);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();

                    return file;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //展示拍摄的图片
    private void displayCapturedPhoto() {
        ImageView imageView = new ImageView(this);
        // 接下来可以将图片以文件形式保存到您的应用内部存储或缓存目录中
        File file = new File(mCurrentPhotoPath);
        //给ImageView设置图片
        imageView.setImageURI(Uri.fromFile(file));
        fileList.add(file);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                convertDpToPixel(300), // 宽度 150dp 转换为像素
                convertDpToPixel(300) // 高度 150dp 转换为像素
        );
        layoutParams.setMargins(0, 0, 5, 16);
        //设置图片填充满
        imageView.setLayoutParams(layoutParams);
        imageView.setTag(file.getName());
        imgLinerLayout.addView(imageView);
        imgLinerLayout.removeView(uploadImage);
//        imgLinerLayout.addView(uploadImage);
        viewList.add(imageView);
    }

    //展示所选择的图片
    private void displaySelectedImage(Uri selectedImage,String fileName) {
        System.out.println("展示");
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(selectedImage);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                convertDpToPixel(150), // 宽度 150dp 转换为像素
                convertDpToPixel(150) // 高度 150dp 转换为像素
        );
        layoutParams.setMargins(0, 0, 5, 16);
        imageView.setLayoutParams(layoutParams);
        //设置Tag
        imageView.setTag(fileName);
        imgLinerLayout.addView(imageView);
        imgLinerLayout.removeView(uploadImage);
        //imgLinerLayout.addView(uploadImage);
        viewList.add(imageView);
    }
    private int convertDpToPixel(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    private void uploadComplete() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent); // 设置上传完成的结果码
        finish(); // 结束上传页面
    }

    //上传
    //图片分片上传，计算文件总分片数
    private int calculateTotalChunks(File file) {
        // 计算分片数的逻辑，根据文件大小和分片大小计算
        return (int) Math.ceil((double) file.length() / (1024 * 1024));
    }


}