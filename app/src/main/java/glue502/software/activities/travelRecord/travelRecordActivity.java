package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import glue502.software.R;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.fragments.RecommendFragment;
import glue502.software.models.UserInfo;
import glue502.software.models.travelRecord;
import glue502.software.utils.MyViewUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class travelRecordActivity extends Activity {

    private String url = "http://"+ip+"/travel/travel/createTravelRecoed";
    private static Map<String, String> uriIdentifierMap = new HashMap<>();
    private UserInfo  userInfo= new UserInfo();
    private String travelId = generateUUID();
    private String userId ;
    private String city = "北京市";
    private ListView mSugListView;
    // 在 PostActivity 中定义一个 SharedPreferences 的实例变量,持久化保存
    private SharedPreferences sharedPreferences;

    private int value=0;
    private int which=10000;
    // 外围的LinearLayout容器
    private LinearLayout llContentView;
    //添加点击按钮
    private EditText etContent1,etTravelName;
    private Button btnReturn,btnSubmit;
    // “+”按钮控件List
    private LinkedList<ImageButton> listIBTNAdd;
    // “+”按钮ID索引
    private int btnIDIndex = 1000;
    // “-”按钮控件List
    private LinkedList<ImageButton> listIBTNDel;
    private LinkedList<ImageButton> listPhotoAdd;
    private LinkedList<ImageButton> listPhotoAlbum;
    private SuggestionSearch mSuggestionSearch = null;
    //路径
    private String mCurrentPhotoPath;
    private int iETContentHeight = 0;   // EditText控件高度
    private float fDimRatio = 1.0f; // 尺寸比例（实际尺寸/xml文件里尺寸）
    private final int RESULT_LOAD_IMAGES = 1, RESULT_CAMERA_IMAGE = 2;
    private String selectedKey;
    private String selectedCity;
    private String selectedDistrict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始百度地图
        LocationClient.setAgreePrivacy(true);
        SDKInitializer.setAgreePrivacy(this.getApplicationContext(), true);
        SDKInitializer.initialize(this.getApplicationContext());
        mSuggestionSearch = SuggestionSearch.newInstance();
        setContentView(R.layout.activity_content);
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(),false);
        // 检查是否已经授予了所需的权限
        Log.d("PostActivity", "onCreate() called");
        initCtrl();
        // 获取 SharedPreferences 实例
        sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String TravelName = sharedPreferences.getString("TravelName", "请输入");
        etTravelName.setText(TravelName);
        int numberOfControls = sharedPreferences.getInt("numberOfControls", 0);
        // 如果之前有保存的控件数量，则重新创建控件
        if (numberOfControls > 0) {

            for (int i = numberOfControls-1; i > 0; i--) {
                addContentWithTag(i);
            }
        }
        // 加载保存的用户输入内容
        loadSavedContent();
        PoiSugSearch();
        setListener();
    }
    private void setValue(int value) {
        this.value = value;
    }

    // 另一个方法用于获取参数值
    private int getValue() {
        return this.value;
    }
    private void setWhich(int which) {
        this.which = which;
    }
    // 另一个方法用于获取参数值
    private int getWhich() {
        return this.which;
    }
    //TODO 保存List到SharedPreferences,通过tag区分
    private void saveListToSharedPreferences(List<String> nestedList, int tag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        List<List<String>> list =getListFromSharedPreferences();
        if (tag >= 0 && tag < list.size()) {
            list.remove(tag);
            list.add(tag,nestedList);
            String json = gson.toJson(list);
            editor.putString("myList", json);
            editor.apply();
        }else {
            list.add(tag,nestedList);
            String json = gson.toJson(list);
            editor.putString("myList", json);
            editor.apply();
        }

    }
    private void saveListStringToSharedPreferences(List<List<String>> nestedList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(nestedList);
        editor.putString("myList", json);
        editor.apply();
    }

    // 从SharedPreferences中获取List
    private List<List<String>> getListFromSharedPreferences() {
        String json = sharedPreferences.getString("myList", "");
        if (json.equals("")) {
            return new ArrayList<>();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<List<String>>>() {}.getType();
            return gson.fromJson(json, type);
        }
    }
    private void setListener() {
        etContent1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString()) // 关键字
                        .city(city)); // 城市
            }
        });
        etContent1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(getApplicationContext(), "focus", Toast.LENGTH_LONG).show();
                    mSugListView.setVisibility(View.VISIBLE);
                } else {
                    mSugListView.setVisibility(View.GONE);
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 提交的代码 逻辑如下 通过sharp得到总数 依次上传
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            int numberOfControls = sharedPreferences.getInt("numberOfControls", 0);
                            for (int j = numberOfControls-1; j >= 0; j--) {
                                 List<File> fileList = new ArrayList<>();
                               List<List<String>> list = getListFromSharedPreferences();
                               List<String> path  = list.get(j);
                                for (String URI : path) {
                                    try {
                                        FileInputStream localStream =openFileInput(generateIdentifierFromUri(URI));
                                        Bitmap bitmap = BitmapFactory.decodeStream(localStream);
                                        System.out.println(bitmap);
                                        savefile(fileList,bitmap);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }

                                travelRecord travelrecord = new travelRecord();
                                travelrecord.setPlaceName(
                                        sharedPreferences.getString("userTitle" + j,""));
                                travelrecord.setContent(sharedPreferences.getString("userContent" + j,""));
                                travelrecord.setUserId(userId);
                                travelrecord.setTravelName(etTravelName.getText().toString());
                                travelrecord.setTravelId(travelId);
                                Date currentTime = new Date();
                                // 定义日期时间格式
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                // 格式化当前时间
                                String formattedTime = sdf.format(currentTime);
                                travelrecord.setCreateTime(formattedTime);
                                travelrecord.setPictureNumber(fileList.size());
                                OkHttpClient client = new OkHttpClient();
                                Gson gson = new Gson();
                                String json = gson.toJson(travelrecord);
                                MultipartBody.Builder builder = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("travelrecord", json, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json));
                                //循环处理图片
                                for (int i = 0; i < fileList.size(); i++){
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
                                Request request = new Request.Builder()
                                        .url(url)
                                        .post(requestBody)
                                        .build();
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
                                uploadComplete();
                            }
                        }
                    }).start();
                }
            }
            //TODO 在这里要删除页面的content和照片
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(travelRecordActivity.this, travelRecordEdit.class);

            }
        });
    }

    private void showPopupWindow(){
        {
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

    }
    private String generateUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private int calculateTotalChunks(File file) {
        // 计算分片数的逻辑，根据文件大小和分片大小计算
        return (int) Math.ceil((double) file.length() / (1024 * 1024));

    }
    //TODO 哈希函数

    // 使用 MD5 哈希函数生成唯一标识符
    public static String generateIdentifierFromUri(String uri) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(uri.getBytes());
            byte[] messageDigest = digest.digest();

            // 将 byte 数组转换成十六进制的字符串表示
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    //启动相机
    private void takeCamera(int num) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
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
    //打开文件选择器
    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGES);
    }

    //通过uri获取文件
    private File getFileFromBitmap(Bitmap bitmap) {
        try {
            String displayName = bitmap+".png"; // 设置文件名（可以根据需要修改）
            File file = new File(getCacheDir(), displayName);
            FileOutputStream outputStream = new FileOutputStream(file);

            // 将 Bitmap 写入文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void savefile(List<File> file,Bitmap bitmap){
        file.add(getFileFromBitmap(bitmap));
    }
    private int convertDpToPixel(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

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
                        if(getWhich()==10000){
                            //则通过循环11保存一个uri，12保存一个uri，13保存一个uri，以此类推
                            savePicture(selectedImage,10000,10000);
                            //下面是展示图片
                            putPicture(selectedImage,10000,null,10000);
                        }
                        else {

                            //则通过循环11保存一个uri，12保存一个uri，13保存一个uri，以此类推
                            savePicture(selectedImage,10000,getWhich());
                            //下面是展示图片
                            putPicture(selectedImage,10000,null,getWhich());
                        }
                    }

                } else if(data.getData() != null) {
                    Uri selectedImage = data.getData();
                    if(getWhich()==10000){
                        savePicture(selectedImage,10000,10000);
// 假设您想获取第一个LinearLayout中的ImageView，可以通过以下代码获取
                        putPicture(selectedImage,10000,null,10000);
                    }
                    else {
                        savePicture(selectedImage,10000,getWhich());
// 假设您想获取第一个LinearLayout中的ImageView，可以通过以下代码获取
                        putPicture(selectedImage,10000,null,getWhich());
                    }
                }
            } else if (requestCode == RESULT_CAMERA_IMAGE) {
            }
        }
    }
    //TODO 下面是保存图片，使用哈希函数把他uri的特殊标识符作为名字存储在本地中。若要读取则需要通过特殊标识符得到uri
    private void savePicture(Uri uri,int tag,int n) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(tag==10000){
            tag = getValue();
            List<List<String>>  lujing=  getListFromSharedPreferences();
            if (tag >= 0 && tag < lujing.size()) { // 确保 i 在列表范围内
                List<String> path = lujing.get(tag);
                if (path != null && !path.isEmpty()) {
                    String URI =  uri.toString();
                    if(n==10000){
                        path.add(URI);
                    }else{
                        path.add(n,URI);
                    }

                    saveListToSharedPreferences(path,tag);
                    try {
                        // 通过 URI 获取 Bitmap 对象
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        // 将 Bitmap 对象保存到内部存储
                        FileOutputStream fileOutputStream = openFileOutput(generateIdentifierFromUri(URI), 0);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    }
                } else {
                    List<String> path = new ArrayList<>();
                    String URI =  uri.toString();
                    if(n==10000){
                        path.add(URI);
                    }else{
                        path.add(n,URI);
                    }

                    saveListToSharedPreferences(path,tag);
                try {
                    // 通过 URI 获取 Bitmap 对象
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // 将 Bitmap 对象保存到内部存储
                    FileOutputStream fileOutputStream = openFileOutput(generateIdentifierFromUri(URI), 0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }

        } else {
            List<List<String>>  lujing=  getListFromSharedPreferences();
            List<String> path = lujing.get(tag);
            String URI =  uri.toString();
            if(n==10000){
                path.add(URI);
            }else{
                path.add(n-2,URI);
            }
            saveListToSharedPreferences(path,tag);
            try {

                // 通过 URI 获取 Bitmap 对象
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                // 将 Bitmap 对象保存到内部存储
                FileOutputStream fileOutputStream = openFileOutput(generateIdentifierFromUri(URI), 0);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        editor.apply();

    }

    //TODO 展示图片
    private void putPicture(Uri selectedImage,int a,Bitmap bitmap,int n){
        //n=10000说明不是从中间加的
        //关于a，如果a=10000就说明他是新加的页面我们需要去获得看是那个页面在新加
        if(bitmap == null) {
            if(a==10000){
                LinearLayout firstLayout = (LinearLayout) llContentView.getChildAt(getValue()); // 获取第一个LinearLayout
                // 获取第一个LinearLayout}
                HorizontalScrollView scrollView = (HorizontalScrollView) firstLayout.getChildAt(0); // 获取第一个LinearLayout中的HorizontalScrollView
                LinearLayout innerLayout = (LinearLayout) scrollView.getChildAt(0); // 获取HorizontalScrollView中的LinearLayout
                innerLayout.setTag(getValue());
                ImageView imageView1 = new ImageView(this);
                Glide.with(this)
                        .load(selectedImage)
                        .into(imageView1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        convertDpToPixel(150), // 宽度 150dp 转换为像素
                        convertDpToPixel(150) // 高度 150dp 转换为像素
                );
                imageView1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        loadSave();
                        int index = innerLayout.indexOfChild(v);
                        int c = (int)innerLayout.getTag();
                        setValue(c);
                        System.out.println(c+"ccccccccccccc");
                        setWhich(index);
                        showPopupWindow();
                    }
                });
                imageView1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDeleteDialog(new DeleteConfirmationListener() {
                            @Override
                            public void onConfirmDelete() {
                                loadSave();
                                int index = innerLayout.indexOfChild(v); // 获取点击的 ImageView 在 innerLayout 中的索引位置
                                int c = (int)innerLayout.getTag();
                                List<List<String>> list = getListFromSharedPreferences();
                                List<String> path= list.get(c);
                                path.remove(index-1);
                                saveListToSharedPreferences(path, c);
                                // 用户确认删除的处理逻辑，可以在这里执行删除操作
                                innerLayout.removeView(v);
                            }

                            @Override
                            public void onCancelDelete() {
                            }
                        });
                        return true;
                    }
                });
                layoutParams.setMargins(0, 0, 0, 16);
                imageView1.setLayoutParams(layoutParams);
// 添加新的imageView1 到 innerLayout
                if(n==10000){
                    innerLayout.addView(imageView1);
                }
                else{
                    innerLayout.addView(imageView1,getWhich()+1);
                }

//
            }
            else {
                LinearLayout firstLayout = (LinearLayout) llContentView.getChildAt(a);
                // 获取第一个LinearLayout}
                HorizontalScrollView scrollView = (HorizontalScrollView) firstLayout.getChildAt(0); // 获取第一个LinearLayout中的HorizontalScrollView
                LinearLayout innerLayout = (LinearLayout) scrollView.getChildAt(0); // 获取HorizontalScrollView中的LinearLayout
                innerLayout.setTag(a);
                ImageView imageView1 = new ImageView(this);
                Glide.with(this)
                        .load(selectedImage)
                        .into(imageView1);
                imageView1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        loadSave();
                        int index = innerLayout.indexOfChild(v);
                        int a = (int)innerLayout.getTag();
                        System.out.println(a+"11111111111AAAAAAAAAAAAAAA");
                        setWhich(index);
                        setValue(a);
                        showPopupWindow();
                    }
                });
                imageView1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDeleteDialog(new DeleteConfirmationListener() {
                            @Override
                            public void onConfirmDelete() {
                                loadSave();
                                int index = innerLayout.indexOfChild(v); // 获取点击的 ImageView 在 innerLayout 中的索引位置
                                int c = (int)innerLayout.getTag();
                                List<List<String>> list = getListFromSharedPreferences();
                                List<String> path= list.get(c);
                                path.remove(index-1);
                                saveListToSharedPreferences(path, c);
                                // 用户确认删除的处理逻辑，可以在这里执行删除操作
                                innerLayout.removeView(v);
                            }

                            @Override
                            public void onCancelDelete() {
                            }
                        });
                        return true;
                    }
                });
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        convertDpToPixel(150), // 宽度 150dp 转换为像素
                        convertDpToPixel(150) // 高度 150dp 转换为像素
                );
                layoutParams.setMargins(0, 0, 0, 16);
                imageView1.setLayoutParams(layoutParams);
// 添加新的imageView1 到 innerLayout
                if(n==10000){
                    innerLayout.addView(imageView1);
                }
                else{
                    innerLayout.addView(imageView1,getWhich()+1);
                }
            }
        }else {
            LinearLayout firstLayout = (LinearLayout) llContentView.getChildAt(a);
            // 获取第一个LinearLayout}
            HorizontalScrollView scrollView = (HorizontalScrollView) firstLayout.getChildAt(0); // 获取第一个LinearLayout中的HorizontalScrollView
            LinearLayout innerLayout = (LinearLayout) scrollView.getChildAt(0); // 获取HorizontalScrollView中的LinearLayout
            innerLayout.setTag(a);
            ImageView imageView1 = new ImageView(this);
            Glide.with(this)
                    .load(bitmap)
                    .into(imageView1);
            imageView1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    loadSave();
                    int index = innerLayout.indexOfChild(v);
                    int a = (int)innerLayout.getTag();
                    System.out.println(a+"2222222222AAAAAAAAAAAAAAA");
                    setWhich(index);
                    setValue(a);
                    showPopupWindow();
                }
            });
            imageView1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteDialog(new DeleteConfirmationListener() {
                        @Override
                        public void onConfirmDelete() {
                            // 用户确认删除的处理逻辑，可以在这里执行删除操作
                            int index = innerLayout.indexOfChild(v); // 获取点击的 ImageView 在 innerLayout 中的索引位置
                            int a = (int)innerLayout.getTag();
                            List<List<String>> list = getListFromSharedPreferences();
                            List<String> path = list.get(a);
                            path.remove(index-1);
                            saveListToSharedPreferences(path, a);
                            innerLayout.removeView(v);
                        }
                        @Override
                        public void onCancelDelete() {
                        }
                    });
                    return true;
                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    convertDpToPixel(150), // 宽度 150dp 转换为像素
                    convertDpToPixel(150) // 高度 150dp 转换为像素
            );
            layoutParams.setMargins(0, 0, 0, 16);
            imageView1.setLayoutParams(layoutParams);
// 添加新的imageView1 到 innerLayout
            if(n==10000){
                innerLayout.addView(imageView1);
            }
            else{
                innerLayout.addView(imageView1,getWhich()+1);
            }
        }
    }
    // TODO 下面是新建的控件
    private void addContentWithTag(int i) {
        // 1.创建外围LinearLayout控件
        LinearLayout layout = new LinearLayout(travelRecordActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.border_backgrounddjp);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
        layout.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));

//TODO 以下是图片的新加

// 1. 创建外围 HorizontalScrollView 控件
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(150)); // 150dp高度
        scrollView.setLayoutParams(scrollParams);

// 创建内部 LinearLayout
        LinearLayout innerLayout = new LinearLayout(this);
        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        innerLayout.setLayoutParams(innerParams);
        innerLayout.setTag(i);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8)); // 设置内边距
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                8,
                8);
        imageView.setLayoutParams(imageParams);
        int a = llContentView.getChildCount();
        imageView.setTag(a);
//// 将 ImageView 添加到内部 LinearLayout
        innerLayout.addView(imageView,0);

// 将内部 LinearLayout 添加到 HorizontalScrollView
        scrollView.addView(innerLayout);

// 添加到您的布局容器中（假设容器是 llContentView）
        layout.addView(scrollView);

// 创建 EditText1
        EditText etContent1 = new EditText(this);
        LinearLayout.LayoutParams etParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(25));
        etContent1.setLayoutParams(etParams1);
        etContent1.setId(View.generateViewId());
        etContent1.setGravity(Gravity.LEFT);
        etContent1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etContent1.setPadding(dpToPx(5), 0, 0, 0);
        etContent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        etContent1.setHint("标题");
        etContent1.setTag(i);
        layout.addView(etContent1);

// 创建 EditText2
        EditText etContent2 = new EditText(this);
        LinearLayout.LayoutParams etParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,// 将高度设置为 WRAP_CONTENT
                dpToPx(50));
        etContent2.setLayoutParams(etParams2);
        etContent2.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        etContent2.setGravity(Gravity.LEFT);
        etContent2.setPadding(dpToPx(5), 0, 0, 0);
        etContent2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        etContent2.setHint("输入你的内容");
        etContent2.setSingleLine(false);
        etContent2.setLines(25); // 设置初始行数为5行
        etContent2.setMinLines(12); // 设置最大行数为5行
        etContent2.setTag(i);
        layout.addView(etContent2);

        // 3.创建“+”和“-”按钮外围控件RelativeLayout
        RelativeLayout rlBtn = new RelativeLayout(travelRecordActivity.this);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rlBtn.setLayoutParams(rlParam);

// 创建第一个按钮
        ImageButton btnAdd = new ImageButton(travelRecordActivity.this);
        RelativeLayout.LayoutParams btnAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        btnAddParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        btnAdd.setLayoutParams(btnAddParam);
        btnAdd.setBackgroundResource(R.drawable.ic_add);
        btnAdd.setId(View.generateViewId());
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSave();
                int a = (int)innerLayout.getTag()+1; // 索引值从已有子控件的数量开始
                int b =llContentView.getChildCount();
                if(a!=b){
                    // 显示短暂的消息提示
                    Toast.makeText(getApplicationContext(), "这不是最后一个，要从最后一个开始添加哦", Toast.LENGTH_SHORT).show();
                }else {
                    addContent(v);
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        listIBTNAdd.add(1,btnAdd);
        rlBtn.addView(btnAdd);

// 创建第二个按钮
        ImageButton btnDelete = new ImageButton(travelRecordActivity.this);
        RelativeLayout.LayoutParams btnDeleteParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        btnDeleteParam.addRule(RelativeLayout.RIGHT_OF, btnAdd.getId());
        btnDeleteParam.leftMargin = (int) getResources().getDimension(R.dimen.margin_between_buttons);
        btnDelete.setLayoutParams(btnDeleteParam);
        btnDelete.setBackgroundResource(R.drawable.ic_delete);
        btnDelete.setId(View.generateViewId());
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSave();
                int a = (int)innerLayout.getTag()+1; // 索引值从已有子控件的数量开始
                int b =llContentView.getChildCount();
                if(a!=b){
                    // 显示短暂的消息提示
                    Toast.makeText(getApplicationContext(), "这不是最后一个，要从最后一个开始删除哦", Toast.LENGTH_SHORT).show();
                }else {
                    deleteContent(v);
                    Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                }

            }
        });
        listIBTNDel.add(1,btnDelete);
        rlBtn.addView(btnDelete);
// 创建第三个按钮
        ImageButton photoAdd = new ImageButton(travelRecordActivity.this);
        RelativeLayout.LayoutParams photoAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        photoAddParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // 将第三个按钮置于最右侧
        photoAdd.setLayoutParams(photoAddParam);
        photoAdd.setBackgroundResource(R.drawable.cameraline);
        photoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContent(v);
            }
        });
        photoAdd.setId(View.generateViewId());
        rlBtn.addView(photoAdd);

// 创建第四个按钮
        ImageButton photoAlbum = new ImageButton(travelRecordActivity.this);
        RelativeLayout.LayoutParams photoAlbumParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        photoAlbumParam.addRule(RelativeLayout.LEFT_OF, photoAdd.getId()); // 放置在第三个按钮的左边
        photoAlbumParam.rightMargin = (int) getResources().getDimension(R.dimen.margin_between_buttons);
        photoAlbum.setId(View.generateViewId());
        photoAlbum.setLayoutParams(photoAlbumParam);
        photoAlbum.setBackgroundResource(R.drawable.imageadd);
        photoAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = (int)etContent2.getTag(); // 索引值从已有子控件的数量开始
                int index = innerLayout.indexOfChild(v);
                setValue(a);
                setWhich(10000);
                openFilePicker();
            }
        });
        listPhotoAlbum.add(1,photoAlbum);
        rlBtn.addView(photoAlbum);
        layout.addView(rlBtn);
        // 7.将layout同它内部的所有控件加到最外围的llContentView容器里
        llContentView.addView(layout, 1);

    }


    // Method to convert dp to pixels
    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    /**
     * 初始化控件
     */
    private void initCtrl() {
        llContentView = (LinearLayout) this.findViewById(R.id.content_view);
        etContent1 = (EditText) this.findViewById(R.id.et_content1);
        etTravelName = (EditText) this.findViewById(R.id.Ed_place);
        btnReturn =findViewById(R.id.btn_return);
        btnSubmit =findViewById(R.id.btn_submit);
        mSugListView = findViewById(R.id.sug_list);
        listIBTNAdd = new LinkedList<ImageButton>();
        listIBTNDel = new LinkedList<   ImageButton>();
//        listPhotoAdd = new LinkedList<ImageButton>();
        listPhotoAlbum = new LinkedList<ImageButton>();


        // “+”按钮（第一个）
        ImageButton ibtnAdd1 = (ImageButton) this.findViewById(R.id.ibn_add1);
        ImageButton ibtnDelete = (ImageButton) this.findViewById(R.id.ibn_delete);
//        ImageButton ibtnPhotoAdd = (ImageButton) this.findViewById(R.id.ibn_add1);
        ImageButton ibtnPhotoAlbum = (ImageButton) this.findViewById(R.id.ibn_PhotoAlbum);
        ibtnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取尺寸变化比例
                iETContentHeight = etContent1.getHeight();
                fDimRatio = iETContentHeight / 80;
                loadSave();
                int b =llContentView.getChildCount();
                if(b!=1){
                    // 显示短暂的消息提示
                    Toast.makeText(getApplicationContext(), "这不是最后一个，要从最后一个开始添加哦", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    addContent(v);
                }
                // 检查输入框内容是否为空
//                if (etContent1.getText().toString().isEmpty()) {
//                    // 如果输入框内容为空，显示提示或者打印消息
//                    Toast.makeText(travelRecordActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
//                    // 或者打印消息到控制台
//                    Log.d("PostActivity", "输入框内容为空");
//                } else {
//                    // 如果输入框不为空，则执行添加控件的操作
//                    addContent(v);
//                }
            }
        });
        ibtnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //TODO 清空操作
                    loadSave();
                    int b =llContentView.getChildCount();
                    if(b!=1){
                        // 显示短暂的消息提示
                        Toast.makeText(getApplicationContext(), "这不是最后一个，要从最后一个开始删除哦", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        ibtnPhotoAlbum.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setValue(0);
                //TODO 从相册选择
                openFilePicker();

            }
        });
        listIBTNAdd.add(ibtnAdd1);
        listPhotoAlbum.add(ibtnPhotoAlbum);
        listIBTNDel.add(null);  // 第一组隐藏了“-”按钮，所以为null
    }

    /**
     * 添加一组新控件
     *
     * @param v 事件触发控件，其实就是触发添加事件对应的“+”按钮
     */
    //TODO 生成页面
    private void addContent(View v) {
        if (v == null) {
            return;
        }
        int iIndex = -1;
        for (int i = 0; i < listIBTNAdd.size(); i++) {
            if (listIBTNAdd.get(i) == v) {
                iIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
// 控件实际添加位置为当前触发位置点下一位
            iIndex += 1;
// 1.创建外围LinearLayout控件
            LinearLayout layout = new LinearLayout(travelRecordActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setBackgroundResource(R.drawable.border_backgrounddjp);
            layout.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
            layout.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
//TODO 以下是图片的新加

// 1. 创建外围 HorizontalScrollView 控件
            HorizontalScrollView scrollView = new HorizontalScrollView(this);
            LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(150)); // 150dp高度
            scrollView.setLayoutParams(scrollParams);

// 创建内部 LinearLayout 这是一个很小的点，没有任何用
            LinearLayout innerLayout = new LinearLayout(this);
            LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            innerLayout.setLayoutParams(innerParams);
            int newIndex = llContentView.getChildCount(); // 索引值从已有子控件的数量开始

            innerLayout.setTag(newIndex);
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8)); // 设置内边距

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    8,
                    8);
            imageView.setLayoutParams(imageParams);
            int a = llContentView.getChildCount();
            imageView.setTag(a);
// 将 ImageView 添加到内部 LinearLayout
            innerLayout.addView(imageView);

// 将内部 LinearLayout 添加到 HorizontalScrollView
            scrollView.addView(innerLayout);

// 添加到您的布局容器中（假设容器是 llContentView）
            layout.addView(scrollView);

            ListView listView = new ListView(this);

// 创建 EditText1
            AutoCompleteTextView etContent1 = new AutoCompleteTextView(this);
            LinearLayout.LayoutParams etParams1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(25));
            PoiSugSearch(etContent1,listView);
            etContent1.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable arg0) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (cs.length() <= 0) {
                        return;
                    }
                    // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(cs.toString()) // 关键字
                            .city(city)); // 城市
                }
            });
            etContent1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Toast.makeText(getApplicationContext(), "focus", Toast.LENGTH_LONG).show();
                        listView.setVisibility(View.VISIBLE);
                    } else {
                        listView.setVisibility(View.GONE);
                    }
                }
            });
            etContent1.setLayoutParams(etParams1);
            etContent1.setId(View.generateViewId());
            etContent1.setGravity(Gravity.LEFT);
            etContent1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            etContent1.setPadding(dpToPx(5), 0, 0, 0);
            etContent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            etContent1.setHint("标题");
            etContent1.setTag(newIndex);
            layout.addView(etContent1);

// 设置 ListView 的布局参数
            LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.list_height) // 这里的 R.dimen.list_height 是在 dimens.xml 文件中定义的高度，可以根据实际需求进行修改
            );
            listView.setLayoutParams(layoutParam);
            listView.setVisibility(View.GONE); // 设置初始可见性为 GONE
// 给 ListView 设置 ID
            listView.setId(View.generateViewId()); // 为了确保唯一性，可以使用 generateViewId() 为 ListView 生成一个唯一的 ID
            layout.addView(listView);

// 创建 EditText2
            EditText etContent2 = new EditText(this);
            LinearLayout.LayoutParams etParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,// 将高度设置为 WRAP_CONTENT
                    dpToPx(50));
            etContent2.setLayoutParams(etParams2);
            etContent2.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
            etContent2.setGravity(Gravity.LEFT);
            etContent2.setPadding(dpToPx(5), 0, 0, 0);
            etContent2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            etContent2.setSingleLine(false);
            etContent2.setLines(25); // 设置初始行数为5行
            etContent2.setMinLines(12); // 设置最大行数为5行
            etContent2.setHint("输入你的内容");
            etContent2.setTag(newIndex);
            layout.addView(etContent2);

// 将动态生成的 LinearLayout 添加到 llContentView 容器中
            llContentView.addView(layout);

            // 3.创建“+”和“-”按钮外围控件RelativeLayout
            RelativeLayout rlBtn = new RelativeLayout(travelRecordActivity.this);
            RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rlBtn.setLayoutParams(rlParam);

// 创建第一个按钮
            ImageButton btnAdd = new ImageButton(travelRecordActivity.this);
            RelativeLayout.LayoutParams btnAddParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            btnAddParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            btnAdd.setLayoutParams(btnAddParam);
            btnAdd.setBackgroundResource(R.drawable.ic_add);
            btnAdd.setId(View.generateViewId());
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        loadSave();
                        int a = (int)innerLayout.getTag()+1; // 索引值从已有子控件的数量开始
                        int b =llContentView.getChildCount();
                        if(a!=b){
                            // 显示短暂的消息提示
                            Toast.makeText(getApplicationContext(), "这不是最后一个，要从最后一个开始添加哦", Toast.LENGTH_SHORT).show();
                        }else {
                            addContent(v);
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        }
                }
            });
            listIBTNAdd.add(iIndex,btnAdd);
            rlBtn.addView(btnAdd);

// 创建第二个按钮
            ImageButton btnDelete = new ImageButton(travelRecordActivity.this);
            RelativeLayout.LayoutParams btnDeleteParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            btnDeleteParam.addRule(RelativeLayout.RIGHT_OF, btnAdd.getId());
            btnDeleteParam.leftMargin = (int) getResources().getDimension(R.dimen.margin_between_buttons);
            btnDelete.setLayoutParams(btnDeleteParam);
            btnDelete.setBackgroundResource(R.drawable.ic_delete);
            btnDelete.setId(View.generateViewId());
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadSave();
                    int a = (int)innerLayout.getTag()+1; // 索引值从已有子控件的数量开始
                    int b =llContentView.getChildCount();
                    if(a!=b){
                        // 显示短暂的消息提示
                        Toast.makeText(getApplicationContext(), "这不是最后一个，要从最后一个开始删除哦", Toast.LENGTH_SHORT).show();
                    }else {
                        deleteContent(v);
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            listIBTNDel.add(iIndex,btnDelete);
            rlBtn.addView(btnDelete);
// 创建第三个按钮
            ImageButton photoAdd = new ImageButton(travelRecordActivity.this);
            RelativeLayout.LayoutParams photoAddParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            photoAddParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // 将第三个按钮置于最右侧
            photoAdd.setLayoutParams(photoAddParam);
            photoAdd.setBackgroundResource(R.drawable.cameraline);
            photoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteContent(v);
                }
            });
            photoAdd.setId(View.generateViewId());
            rlBtn.addView(photoAdd);

// 创建第四个按钮
            ImageButton photoAlbum = new ImageButton(travelRecordActivity.this);
            RelativeLayout.LayoutParams photoAlbumParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            photoAlbumParam.addRule(RelativeLayout.LEFT_OF, photoAdd.getId()); // 放置在第三个按钮的左边
            photoAlbumParam.rightMargin = (int) getResources().getDimension(R.dimen.margin_between_buttons);
            photoAlbum.setId(View.generateViewId());
            photoAlbum.setLayoutParams(photoAlbumParam);
            photoAlbum.setBackgroundResource(R.drawable.imageadd);
            photoAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int a = (int)etContent2.getTag(); // 索引值从已有子控件的数量开始
                    setValue(a);
                    int index = innerLayout.indexOfChild(v);
                    //TODO 执行点击操作
                    setWhich(10000);
                    openFilePicker();
                }
            });
            listPhotoAlbum.add(iIndex,photoAlbum);
            rlBtn.addView(photoAlbum);
            layout.addView(rlBtn);

            // 7.将layout同它内部的所有控件加到最外围的llContentView容器里
//            llContentView.addView(layout, 1);

            btnIDIndex++;
        }
    }
    /**
     * 删除一组控件
     *
     * @param v 事件触发控件，其实就是触发删除事件对应的“-”按钮
     */
    private void deleteContent(View v) {
        if (v == null) {
            return;
        }

        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNDel.size(); i++) {
            if (listIBTNDel.get(i) == v) {
                iIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            listIBTNAdd.remove(iIndex);
            listIBTNDel.remove(iIndex);
            // 从外围llContentView容器里删除第iIndex控件
            llContentView.removeViewAt(iIndex);
            removeFromSharedPreferences(iIndex);
        }
    }
    // 保存内容到 SharedPreferences
    private void saveContentToSharedPreferences() {
        String TravelName = etTravelName.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TravelName", TravelName);
        for (int i = 0; i < llContentView.getChildCount(); i++) {
            //TODO 在这里执行关闭保存 通过i先读取，读取出总数，得到总数，清空总数，循环count保存，读取出uri再保存
            List<List<String>> list = getListFromSharedPreferences();
            if (i >= 0 && i < list.size()) { // 确保 i 在列表范围内
                List<String> path = list.get(i);
                if (path != null && !path.isEmpty()) {
                    // 这里是当 path 不为空时执行的操作
                    // 例如，可以遍历 path 中的元素或者执行其他操作
                    saveListToSharedPreferences(path,i);
                } else {
                    // 这里是当 path 为空时执行的操作
                }
            } else {
                // 处理索引超出范围的情况
            }

            View view = llContentView.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;
                int editTextCount = 0;
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View childView = linearLayout.getChildAt(j);
                    // Ensure childView is an instance of EditText
                    if (childView instanceof EditText) {
                        EditText editText = (EditText) childView;

                        int index;
                        if (editText.getTag() != null && editText.getTag() instanceof Integer) {
                            index = (int) editText.getTag();
                        } else {
                            index = 0; // Use default index if tag is not an Integer or null
                        }
                        String title = editText.getText().toString();
                        String content = editText.getText().toString();
                        if (editTextCount == 0) {
                            // First EditText - Assume it as title EditText
                            editor.putString("userTitle" + index, title);
                        } else if (editTextCount == 1) {
                            // Second EditText - Assume it as content EditText
                            editor.putString("userContent" + index, content);
                        }
                        editTextCount++; // Increment EditText counter
                    }
                }
            }
        }
        editor.apply();
        Log.d("PostActivity", "saveContentToSharedPreferences() called");
    }
    //打开时执行加载的代码，把上一次保存的东西加载进来
    //TODO 在这里进行读取操作，得到uri，进行展示
    //innerLayout 是按顺序保存的
    private void loadSave() {
        for (int i = 0; i < llContentView.getChildCount(); i++) {
            LinearLayout firstLayout = (LinearLayout) llContentView.getChildAt(i);
            // 获取第一个LinearLayout}
            HorizontalScrollView scrollView = (HorizontalScrollView) firstLayout.getChildAt(0); // 获取第一个LinearLayout中的HorizontalScrollView
            LinearLayout innerLayout = (LinearLayout) scrollView.getChildAt(0); // 获取HorizontalScrollView中的LinearLayout
            innerLayout.setTag(i);
        }
    }
    private void loadSavedContent() {
        for (int i = 0; i < llContentView.getChildCount(); i++) {
            View view = llContentView.getChildAt(i);
            List<List<String>> list = getListFromSharedPreferences();
            if (i >= 0 && i < list.size()) { // 确保 i 在列表范围内
                List<String> path = list.get(i);
                if (path != null && !path.isEmpty()) {
                    // 如果列表不为空，则执行操作
                    // 在这里执行您想要执行的操作，例如遍历列表、获取列表的大小等
                    for (String URI : path) {
                        Uri uri = Uri.parse(URI);
                        try {
                            FileInputStream localStream =openFileInput(generateIdentifierFromUri(URI));
                            Bitmap bitmap = BitmapFactory.decodeStream(localStream);
                            putPicture(uri,i,bitmap,10000);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // 这里是当 path 为空时执行的操作
                }
            } else {
                // 处理索引超出范围的情况
            }
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;
                EditText etTitle = null;
                EditText etContent = null;
                // 遍历 LinearLayout 中的子视图
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View childView = linearLayout.getChildAt(j);
                    if (childView instanceof EditText) {
                        EditText editText = (EditText) childView;
                        if (etTitle == null) {
                            // 在 LinearLayout 中找到第一个 EditText，假设为标题 EditText
                            etTitle = editText;
                        } else {
                            // 在 LinearLayout 中找到第二个 EditText，假设为内容 EditText
                            etContent = editText;
                            break;
                        }
                    }
                }
                if (etTitle != null && etContent != null) {
                    Object tagObject = etTitle.getTag();
                    int savedIndex;
                    if (tagObject instanceof Integer) {
                        //(Integer) tagObject;
                        savedIndex = (Integer) tagObject;
                    } else {
                        savedIndex = 0; // 如果标签不是 Integer 类型或为空，则使用默认索引
                    }
                    String savedTitle = sharedPreferences.getString("userTitle" + savedIndex, "");
                    String savedContent = sharedPreferences.getString("userContent" + savedIndex, "");
                    etTitle.setText(savedTitle);
                    etContent.setText(savedContent);
                }
            }
        }
        Log.d("PostActivity", "loadSavedContent() called");
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    //关闭时执行的代码
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PostActivity", "onPause() called");
        saveContentToSharedPreferences();
        // 保存已添加的控件数量到 SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("numberOfControls", 0);
        editor.putInt("numberOfControls", llContentView.getChildCount());
        editor.apply();
    }
    //下面是按钮删除的操作在储存里也删除了
    //TODO 不知图片是否要再删除
    private void removeFromSharedPreferences(int index) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userContent" + index);
        editor.remove("userTitle" + index);
        String a =String.valueOf(index);
        List<List<String>> list = getListFromSharedPreferences();
        if (index >= 0 && index < list.size()) {
            list.remove(index);
        }else {
        }
        saveListStringToSharedPreferences(list);
        editor.remove(a);
        editor.apply();
        //TODO 循环所有的控件去重新给
    }
    private void uploadComplete() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent); // 设置上传完成的结果码
        finish(); // 结束上传页面
    }
    private void showDeleteDialog(final DeleteConfirmationListener listener) {
        new AlertDialog.Builder(this)
                .setTitle("删除")
                .setMessage("确定删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        listener.onConfirmDelete(); // 用户确认删除的处理逻辑
                    }
                })
                .setNegativeButton(
                        "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                listener.onCancelDelete(); // 用户取消删除的处理逻辑
                            }
                        }
                )
                .show();
    }
    private static String convertFileToBinaryString(File file) throws IOException {
        StringBuilder binaryContent = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream(file);
        int byteRead;

        while ((byteRead = fileInputStream.read()) != -1) {
            // 将字节转换为8位二进制字符串并追加到StringBuilder中
            String binary = String.format("%8s", Integer.toBinaryString(byteRead)).replace(' ', '0');
            binaryContent.append(binary);
        }

        fileInputStream.close();
        return binaryContent.toString();
    }
    private String generateUUID() {
        return UUID.randomUUID().toString();
    }
    private void PoiSugSearch(final AutoCompleteTextView autoCompleteTextView,final ListView listView){
        OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
            /**
             * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
             *
             * @param suggestionResult    Sug检索结果
             */
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }

                List<HashMap<String, String>> suggest = new ArrayList<>();
                for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                    if (info.getKey() != null && info.getDistrict() != null && info.getCity() != null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("key", info.getKey());
                        map.put("city", info.getCity());
                        map.put("dis", info.getDistrict());
                        System.out.println(map);
                    }
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                        suggest,
                        R.layout.item_layout,
                        new String[]{"key", "city", "dis"},
                        new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis}
                );
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(simpleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // 获取点击的建议项的数据
                        HashMap<String, String> selectedItem = suggest.get(position);
                        selectedKey = selectedItem.get("key");
                        selectedCity = selectedItem.get("city");
                        selectedDistrict = selectedItem.get("dis");
                        Log.v("AddLabelActivity", "lzx key"+selectedKey);
                        Log.v("AddLabelActivity", "lzx city"+selectedCity);
                        Log.v("AddLabelActivity", "lzx dis"+selectedDistrict);
                        System.out.println("===key" +  selectedKey + "city" + selectedCity + "dis" + selectedDistrict + "===");

                        // 使用地理编码服务获取经纬度坐标
                        GeoCoder geoCoder = GeoCoder.newInstance();
                        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                            @Override
                            public void onGetGeoCodeResult(GeoCodeResult result) {
                                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                    // 没有检索到结果，处理错误
                                    Log.v("AddLabelActivity", "lzx没东西");
                                } else {
                                    // 获取坐标信息
                                    LatLng location = result.getLocation();

                                    // 打印输出经纬度信息
                                    Log.v("AddLabelActivity", "lzx Latitude: " + location.latitude);
                                    Log.v("AddLabelActivity", "lzx Longitude: " + location.longitude);

                                    // 在这里你可以将坐标信息存储到成员变量中，或者进行其他操作
                                    LatLng point = location;

                                    //创建marker
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                                            .fromResource(R.drawable.ic_marker);
                                    //构件MarkerOption，用于在地图上添加Marker
                                    OverlayOptions option = new MarkerOptions()
                                            .position(point)
                                            .icon(bitmap)
                                            .draggable(true)
                                            .zIndex(1);

                                    // 设置文本框的值
                                    autoCompleteTextView.setText(selectedKey);

                                    // 隐藏建议列表
                                    listView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                                // 反地理编码的结果，如果需要的话可以处理
                            }
                        });

                        // 设置地理编码检索参数
                        GeoCodeOption geoCodeOption = new GeoCodeOption()
                                .city(selectedCity)
                                .address(selectedDistrict + selectedKey);
                        geoCoder.geocode(geoCodeOption);

                        // 释放地理编码检索实例
                        geoCoder.destroy();

                        if (selectedKey != null) {
                            autoCompleteTextView.setText(selectedKey);
                        }

                        // 隐藏建议列表（假设 mSugListView 是你的建议列表）
                        listView.setVisibility(View.GONE);
                    }
                });
                simpleAdapter.notifyDataSetChanged();
            }

        };
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);

    }
    private void  PoiSugSearch(){
        OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
            /**
             * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
             *
             * @param suggestionResult    Sug检索结果
             */
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }

                List<HashMap<String, String>> suggest = new ArrayList<>();
                for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                    if (info.getKey() != null && info.getDistrict() != null && info.getCity() != null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("key", info.getKey());
                        map.put("city", info.getCity());
                        map.put("dis", info.getDistrict());
                        suggest.add(map);
                        System.out.println(map);
                    }
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                        suggest,
                        R.layout.item_layout,
                        new String[]{"key", "city", "dis"},
                        new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis}
                );
                mSugListView.setVisibility(View.VISIBLE);
                mSugListView.setAdapter(simpleAdapter);
                mSugListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // 获取点击的建议项的数据
                        HashMap<String, String> selectedItem = suggest.get(position);

                        selectedKey = selectedItem.get("key");
                        selectedCity = selectedItem.get("city");
                        selectedDistrict = selectedItem.get("dis");
                        Log.v("AddLabelActivity", "lzx key"+selectedKey);
                        Log.v("AddLabelActivity", "lzx city"+selectedCity);
                        Log.v("AddLabelActivity", "lzx dis"+selectedDistrict);
                        System.out.println("===key" +  selectedKey + "city" + selectedCity + "dis" + selectedDistrict + "===");

                        // 使用地理编码服务获取经纬度坐标
                        GeoCoder geoCoder = GeoCoder.newInstance();
                        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                            @Override
                            public void onGetGeoCodeResult(GeoCodeResult result) {
                                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                    // 没有检索到结果，处理错误
                                    Log.v("AddLabelActivity", "lzx没东西");
                                } else {
                                    // 获取坐标信息
                                    LatLng location = result.getLocation();

                                    // 打印输出经纬度信息
                                    Log.v("AddLabelActivity", "lzx Latitude: " + location.latitude);
                                    Log.v("AddLabelActivity", "lzx Longitude: " + location.longitude);

                                    // 在这里你可以将坐标信息存储到成员变量中，或者进行其他操作
                                    LatLng point = location;

                                    //创建marker
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                                            .fromResource(R.drawable.ic_marker);
                                    //构件MarkerOption，用于在地图上添加Marker
                                    OverlayOptions option = new MarkerOptions()
                                            .position(point)
                                            .icon(bitmap)
                                            .draggable(true)
                                            .zIndex(1);

                                    // 设置文本框的值
                                    etContent1.setText(selectedKey);

                                    // 隐藏建议列表
                                    mSugListView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                                // 反地理编码的结果，如果需要的话可以处理
                            }
                        });

                        // 设置地理编码检索参数
                        GeoCodeOption geoCodeOption = new GeoCodeOption()
                                .city(selectedCity)
                                .address(selectedDistrict + selectedKey);
                        geoCoder.geocode(geoCodeOption);

                        // 释放地理编码检索实例
                        geoCoder.destroy();

                        if (selectedKey != null) {
                            etContent1.setText(selectedKey);
                        }

                        // 隐藏建议列表（假设 mSugListView 是你的建议列表）
                        mSugListView.setVisibility(View.GONE);
                    }
                });
                simpleAdapter.notifyDataSetChanged();
            }

        };
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);

    }
    //TODO 上古遗留bugcontent和title并不是List<list<String>>保存删除中间后面的会消失 以后再改 ,要求用户按顺序添加
}