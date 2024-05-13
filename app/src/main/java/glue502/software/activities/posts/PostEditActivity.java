package glue502.software.activities.posts;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
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
import glue502.software.models.Post;
import glue502.software.models.PostWithUserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostEditActivity extends AppCompatActivity {
    private PostWithUserInfo postWithUserInfo;
    private List<String> deltetedImages = new ArrayList<>();
    private List<View> viewList = new ArrayList<>();
    private List<View> viewList2 = new ArrayList<>();

    private  List<File> fileList = new ArrayList<>();
    private List<String> imageList = new ArrayList<>();

    private Post post = new Post();
    private EditText title, content;
    private Button back, upload;
    private String userId;
    private LinearLayout imgLinerLayout;
    private ImageView uploadImage;
    private String mCurrentPhotoPath;
    private final int RESULT_LOAD_IMAGES = 1, RESULT_CAMERA_IMAGE = 2;
    private String url = "http://"+ip+"/travel/posts/updatewithnewimage";
    private String url2 = "http://"+ip+"/travel/posts/updatewithoutnewimage";

    private String imageurl ="http://"+ip+"/travel/";

    //自适应底部工具栏
    private CoordinatorLayout coordinatorLayout;
    private View bottomToolbar;
    private boolean wasOpened = false;
    ImageView strBtn1,strBtn2,strBtn3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(),true);
        initView();
        initData();
        setListener();
    }
    public void setListener(){
        // 注册根视图全局布局变化监听器
        coordinatorLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                int heightDiff = screenHeight - r.bottom;
                if (heightDiff > dpToPx(getApplicationContext(), 200)) { // 高度差大于200dp，通常认为软键盘已打开
                    if (!wasOpened) {
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottomToolbar.getLayoutParams();
                        layoutParams.bottomMargin = heightDiff;
                        bottomToolbar.setLayoutParams(layoutParams);
                        wasOpened = true;
                    }
                } else if (wasOpened) {
                    // 软键盘关闭
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottomToolbar.getLayoutParams();
                    layoutParams.bottomMargin = 0;
                    bottomToolbar.setLayoutParams(layoutParams);
                    wasOpened = false;
                }
            }
        });

        /*文本替换*/
        strBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceString(content,0);
            }
        });
        /*文本替换*/
        strBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceString(content,1);
            }
        });
        /*文本替换*/
        strBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceString(content,2);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //避免多次上传
                upload.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        post.setPostContent(content.getText().toString());
                        post.setPostTitle(title.getText().toString());
                        //设定1位用户发帖，2为用户分享旅游经历
                        // id需从本地获取，待个人信息模块完成后补充实现
                        post.setuserId(userId);
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
                        //在builder中添加已删除图片路径的list
                        if (deltetedImages.size() > 0){
                            builder.addFormDataPart("deletedPicturePaths", gson.toJson(deltetedImages));
                        }else {
                            deltetedImages.add("[]");
                            builder.addFormDataPart("deletedPicturePaths", gson.toJson(deltetedImages));
                        }
                        builder.addFormDataPart("postId", postWithUserInfo.getPost().getPostId());
                        //循环处理图片，这里是新增的图片列表
                        for (int i = 0; i < fileList.size(); i++) {
                            File file = fileList.get(i);
                            if (file != null && file.exists()) {
                                int totalChunks = calculateTotalChunks(file);//计算分片数
                                String identifier = generateUniqueIdentifier();//生成唯一标识符
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

                            }else {
                            }
                        }
                        RequestBody requestBody = builder.build();
                        Request request;
                        //如果存在新创建的图片
                        if(fileList.size() > 0){
                            request = new Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();
                        }else {
                            request = new Request.Builder()
                                    .url(url2)
                                    .post(requestBody)
                                    .build();
                        }

                        try {
                            //发送请求
                            Response response = client.newCall(request).execute();

                            if (response.isSuccessful()) {
                                String responseData = response.body().string();
                                // 处理响应数据
                            } else {
                                // 请求失败处理错误
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //启用按钮
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                upload.setEnabled(true);
                            }
                        });
                        uploadComplete();
                    }
                }).start();
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //给新增的图片控件添加长按事件
        if(viewList2.size() > 0){
            for(int i = 0; i < viewList2.size(); i++){
                viewList2.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showExistDeleteDialog(v);
                        return false;
                    }
                });
            }
        }
    }
    public void initData(){
        //获取之前页面传来的postWithUserInfo
        postWithUserInfo = (PostWithUserInfo) getIntent().getSerializableExtra("postwithuserinfo");
        userId = postWithUserInfo.getUserInfo().getUserId();
        title.setText(postWithUserInfo.getPost().getPostTitle());
        content.setText(postWithUserInfo.getPost().getPostContent());
        imageList = postWithUserInfo.getPost().getPicturePath();
        //给scrollView添加图片
        for(int i = 0; i < postWithUserInfo.getPost().getPicturePath().size(); i++){
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    convertDpToPixel(150), // 宽度 150dp 转换为像素
                    convertDpToPixel(150) // 高度 150dp 转换为像素
            );
            layoutParams.setMargins(0, 0, 0, 16);
            imageView.setLayoutParams(layoutParams);
            imageView.setTag(postWithUserInfo.getPost().getPicturePath().get(i));
            imgLinerLayout.addView(imageView);
            viewList2.add(imageView);
            Glide.with(this).load(imageurl+"/"+imageList.get(i)).into(imageView);
            imgLinerLayout.removeView(uploadImage);
            imgLinerLayout.addView(uploadImage);
        }
    }
    //处理已有图片
    private void showExistDeleteDialog(View view) {
        // 弹出对话框
        new AlertDialog.Builder(this)
                .setTitle("删除")
                .setMessage("确定删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 点击确定按钮
                        //在页面上删除这个控件
                        imgLinerLayout.removeView(view);
                        //在file列表中删除这个控件对应的文件
                        for(String imagepath : imageList){
                            if(imagepath.equals(view.getTag())){
                                //从imageList中删除这个图片的路径
                                imageList.remove(imagepath);
                                deltetedImages.add(imagepath);
                                viewList2.remove(view);
                                break;
                            }
                        }
                        // 关闭对话框
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(
                        "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 点击取消按钮
                                // 关闭对话框
                                dialogInterface.dismiss();
                            }
                        }
                ).show();
        // 循环遍历控件列表,给新增的图片添加点击事件
        for (View control : viewList) {
            //绑定长按事件
            control.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // 长按弹出删除选项
                    showDeleteDialog(view);
                    return false;
                }
            });
        }

    }
    //图片分片上传，计算文件总分片数
    private int calculateTotalChunks(File file) {
        // 计算分片数的逻辑，根据文件大小和分片大小计算
        return (int) Math.ceil((double) file.length() / (1024 * 1024));

    }
    private void uploadComplete() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent); // 设置上传完成的结果码
        finish(); // 结束上传页面
    }
    private void showDeleteDialog(View view) {
        // 弹出对话框
        new AlertDialog.Builder(this)
                .setTitle("删除")
                .setMessage("确定删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 点击确定按钮
                        //在页面上删除这个控件
                        imgLinerLayout.removeView(view);
                        //在file列表中删除这个控件对应的文件
                        for(File file : fileList){
                            if(file.getName().equals(view.getTag().toString())){
                                file.delete();
                                break;
                            }
                        }
                        // 关闭对话框
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(
                        "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 点击取消按钮
                                // 关闭对话框
                                dialogInterface.dismiss();
                            }
                        }
                ).show();
    }
    public void initView(){
        imgLinerLayout = findViewById(R.id.imageContainer);
        uploadImage = findViewById(R.id.input_image);
        title = findViewById(R.id.input_title);
        content = findViewById(R.id.input_content);
        back = findViewById(R.id.post_back_btn);
        upload = findViewById(R.id.post_upload_btn);

        //自适应底部工具栏
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        bottomToolbar = findViewById(R.id.bottom_toolbar);
        strBtn1=findViewById(R.id.str_btn1);
        strBtn2=findViewById(R.id.str_btn2);
        strBtn3=findViewById(R.id.str_btn3);
    }
    private int convertDpToPixel(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    private void showPopupWindow() {
        View popView = View.inflate(this, R.layout.popupwindow_camera_need, null);
        Button bt_album = popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancel = popView.findViewById(R.id.btn_pop_cancel);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1 / 3;
        final PopupWindow popupWindow = new PopupWindow(popView, width, height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //用户点击从相册选择
        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
                popupWindow.dismiss();
            }
        });
        //用户选择拍照上传
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCamera(RESULT_CAMERA_IMAGE);
                popupWindow.dismiss();
            }
        });
        //用户选择取消
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 50);
    }
    //打开文件选择器
    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGES);
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
    //生成文件名
    private String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "JPEG_" + timeStamp + "_";
    }
    //处理所获得的图片（拍照和选相册择都在这）
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGES && data != null) {
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    int count = clipData.getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri selectedImage = clipData.getItemAt(i).getUri();
                        //生成文件唯一标识
                        File file = getFileFromUri(selectedImage);
                        //获取文件名
                        String fileName1 = file.getName();
                        fileList.add(file);
                        displaySelectedImage(selectedImage,fileName1);
                    }
                } else if(data.getData() != null) {
                    Uri selectedImage = data.getData();
                    //生成文件唯一标识
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
    } //展示所选择的图片
    private void displaySelectedImage(Uri selectedImage,String fileName) {
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(selectedImage);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                convertDpToPixel(150), // 宽度 150dp 转换为像素
                convertDpToPixel(150) // 高度 150dp 转换为像素
        );
        layoutParams.setMargins(0, 0, 0, 16);
        imageView.setLayoutParams(layoutParams);
        //设置Tag
        imageView.setTag(fileName);
        imgLinerLayout.addView(imageView);
        imgLinerLayout.removeView(uploadImage);
        imgLinerLayout.addView(uploadImage);
        viewList.add(imageView);
        setListener();
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
                convertDpToPixel(150), // 宽度 150dp 转换为像素
                convertDpToPixel(150) // 高度 150dp 转换为像素
        );
        layoutParams.setMargins(0, 0, 0, 16);
        imageView.setLayoutParams(layoutParams);
        imageView.setTag(file.getName());
        imgLinerLayout.addView(imageView);
        imgLinerLayout.removeView(uploadImage);
        imgLinerLayout.addView(uploadImage);
        viewList.add(imageView);
        setListener();
    }
    private String generateUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }
    //通过uri获取文件
    private File getFileFromUri(Uri uri) {
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
    /**
     * 将dp值转换为px值
     * @param context 上下文对象
     * @param dp dp值
     * @return px值
     */
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * 将px值转换为dp值
     * @param context 上下文对象
     * @param pxValue px值
     * @return dp值
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) Math.round(pxValue / scale);
    }

    /**
     * 将选中的文本替换为有序表
     * @param content 文本框
     * @param mode 行首模式 0: ● 圆点序列 1: 1.数字序列 2: a.字母序列
     */
    public void replaceString(EditText content, int mode){
        //根据模式使用不同的序号
        char dot = '●';
        int num = 1;
        int letter = 97; // 'a' -> 97(ASCII)

        //获取选择的文本内容
        int startSelection = content.getSelectionStart();
        int endSelection = content.getSelectionEnd();

        Editable editable = content.getText();
        Layout layout = content.getLayout();
        Paint edtPaint = content.getPaint();
        float edtWidth = content.getWidth() - content.getPaddingLeft() - content.getPaddingRight(); //控件可用宽度
        // 判断起始位置和结束位置是否有效
        if(startSelection==endSelection) {
            Toast.makeText(PostEditActivity.this, "请先选择文本内容", Toast.LENGTH_SHORT).show();
        } else if (startSelection >= 0 && endSelection <= editable.length() && startSelection <= endSelection) {
            int startLine = layout.getLineForOffset(startSelection); // 获取起始位置所在行
            int endLine = layout.getLineForOffset(endSelection); // 获取结束位置所在行
            StringBuilder selectedLines = new StringBuilder();

            String[] selectedTexts = content.getText().subSequence(startSelection, endSelection).toString().split("\n");
//            Toast.makeText(UploadPostActivity.this, "选中的文本内容：" + content.getText().subSequence(startSelection, endSelection).toString(), Toast.LENGTH_SHORT).show();
            String lineText = "";
            for(String selectedText : selectedTexts){
                switch (mode){
                    case 0:
                        lineText = dot + " " + selectedText + "\n";
                        break;
                    case 1:
                        lineText = num + ". " + selectedText + "\n";
                        num++;
                        break;
                    case 2:
                        lineText = (char)letter + ". " + selectedText + "\n";
                        letter++;
                        break;
                }
                selectedLines.append(lineText);
            }
            //selectedText 中包含了逐行获取的选中文本内容
            String processedText = selectedLines.toString();
            // 替换选中的文本为处理后的文本
            editable.replace(startSelection, endSelection, processedText);
        }
    }
}