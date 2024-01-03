package glue502.software.activities.map;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import glue502.software.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import glue502.software.utils.MyViewUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieResult;
import com.airbnb.lottie.LottieTask;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddLabelActivity extends AppCompatActivity {

    Button toMap;
    MapView mMapView;
    BaiduMap mBaiduMap;
    EditText editText1;
    AutoCompleteTextView editText2;
    //POI搜索
    private SuggestionSearch mSuggestionSearch = null;
    // 搜索关键字输入窗口
    private ListView mSugListView;
    private LocationClient mLocationClient;
    private double latitude, longitude;
    private double mlatitude, mlongitude;
    private String city = "北京市";
    //逆地理位置编码
    private GeoCoder mCoder = GeoCoder.newInstance();
    private String selectedKey;
    private String selectedCity = "北京市";
    private String selectedDistrict;

    //==========

    private String url="http://"+ip+"/travel/strategy";
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<View> viewList = new ArrayList<>();
    private  List<File> fileList = new ArrayList<>();
    private final int RESULT_LOAD_IMAGES = 1, RESULT_CAMERA_IMAGE = 2;
    private String mCurrentPhotoPath;
    private ImageView upload;
    private ImageView back;
    private EditText totalEditText;
    private EditText disEditText;
    private LinearLayout imgLinerLayout;
    private ImageView uploadImage;
    private String strategyId;
    private OkHttpClient client;
    private String userId;
    private BottomSheetBehavior<View> behavior;
    private View bottomSheet;
    RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationClient.setAgreePrivacy(true);
        SDKInitializer.setAgreePrivacy(this.getApplicationContext(), true);
        SDKInitializer.initialize(this.getApplicationContext());

        setContentView(R.layout.activity_add_label);
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
//            startLocation();
        }
        //绑定地图控件
        initView();
        //获取地理位置
        startLocation();
        //Poi搜索
        PoiSugSearch();
        //绑定监听器
        setListener();
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void initView() {
        toMap = findViewById(R.id.but_toMap);
        editText1 = findViewById(R.id.main_edt_city);
        editText2 = findViewById(R.id.main_edt_poi);
        mMapView = findViewById(R.id.add_label_bmapView);
        mBaiduMap = mMapView.getMap();
        mSugListView = findViewById(R.id.sug_list);
        editText2.setThreshold(1);
        mSuggestionSearch = SuggestionSearch.newInstance();
        //抽屉内容
        upload = findViewById(R.id.btn_upload);
        back = findViewById(R.id.btn_back);
        totalEditText = findViewById(R.id.total_edit_text);
        disEditText = findViewById(R.id.dis_edit_text);
        imgLinerLayout = findViewById(R.id.imageContainer);
        uploadImage = findViewById(R.id.input_image);
        //底部抽屉
        mRecyclerView = findViewById(R.id.recyclerview);
        bottomSheet =  findViewById(R.id.bottom_sheet);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        behavior = BottomSheetBehavior.from(bottomSheet);
    }

    public void startLocation() {
        mBaiduMap.clear();
        // 初始化定位客户端
        try {
            Log.v("AddLabelActivity", "lzxAddLabelActivity页面开启");
            mLocationClient = new LocationClient(getApplicationContext());
            // 配置定位选项
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(0); // 设置扫描时间
            option.setIsNeedAddress(true); // 设置需要获取地址信息
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation == null || mMapView == null) {
                        return;
                    }
                    latitude = bdLocation.getLatitude();
                    longitude = bdLocation.getLongitude();
                    city = bdLocation.getCity(); // 获取详细地址信息
                    editText1.setText(city);

                    // 在这里处理获取到的经纬度信息
                    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                    mBaiduMap.animateMapStatus(mapStatusUpdate);
                    LatLng latLng = new LatLng(latitude, longitude);
                    //构件MarkerOption，用于在地图上添加Marker
                    MarkerOptions option = new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.new_marker))
                            .animateType(MarkerOptions.MarkerAnimateType.grow)
                            .draggable(true)
                            .zIndex(1);

                    //在地图上添加并显示
                    Log.v("AddLabelActivity", "lzx 初始定位添加marker");
                    mBaiduMap.addOverlay(option);

                    //将地图中心移动到当前位置
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
                    //将地图比例尺缩放到合适的级别
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));
                }
            });
            // 开始定位
            mLocationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        //长按删除图片
        // 循环遍历控件列表
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
        //点击百度地图添加marker点
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.clear();
                Log.v("AddLabelActivity", "lzx AddLabelActivity页面点击百度地图添加marker点"+latLng.toString());
                //构件MarkerOption，用于在地图上添加Marker
                MarkerOptions option = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.new_marker))
                        .animateType(MarkerOptions.MarkerAnimateType.grow)
                        .draggable(true)
                        .zIndex(1);
                mCoder.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(latLng)
                        // 设置是否返回新数据 默认值0不返回，1返回
                        .newVersion(1)
                        // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                        .radius(500));
                //在地图上添加并显示
                mBaiduMap.addOverlay(option);

                //将地图中心移动到当前位置
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
                //将地图比例尺缩放到合适的级别
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });
        // 当输入关键字变化时，动态更新建议列表
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (arg0.length() <= 0) {
                    mSugListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (arg0.length() <= 0) {
                    mSugListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    mSugListView.setVisibility(View.GONE);
                }
                // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString()) // 关键字
                        .city(city)); // 城市
            }
        });

        // 设置触摸事件监听器
        findViewById(R.id.add_label_main_content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断点击的位置是否在输入框外
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isPointInsideView(event.getRawX(), event.getRawY(), editText2)) {
                        // 输入框外部被点击，让输入框失去焦点
                        editText2.clearFocus();
                    }
                }
                return false;
            }
        });
        //触摸事件
        BaiduMap.OnMapTouchListener listener = new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                // 判断点击的位置是否在输入框外
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isPointInsideView(motionEvent.getRawX(), motionEvent.getRawY(), editText2)) {
                        // 输入框外部被点击，让输入框失去焦点
                        editText2.clearFocus();
                    }
                }
                //点击地图时收回底部抽屉
                //如果抽屉全展开，将抽屉调至隐藏状态
                if ((behavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) != (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                //触摸地图时收回键盘
                InputMethodManager inputMethodManager = (InputMethodManager) AddLabelActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        };
        //设置触摸地图事件监听者
        mBaiduMap.setOnMapTouchListener(listener);


        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(getApplicationContext(), "focus", Toast.LENGTH_LONG).show();
                    mSugListView.setVisibility(View.GONE);
                } else {
                    mSugListView.setVisibility(View.GONE);
                }
            }
        });

        //设置marker可拖拽
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
                Toast.makeText(getApplicationContext(), "拖拽了marker", Toast.LENGTH_LONG).show();
                Log.v("AddLabelActivity", "lzx拖拽了marker");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(getApplicationContext(), "拖拽结束了marker", Toast.LENGTH_LONG).show();
                Log.v("AddLabelActivity", "lzx拖拽结束了marker");
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });

        //绑定marker点击事件监听器
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //获取marker点的经纬度
                mlatitude = marker.getPosition().latitude;
                mlongitude = marker.getPosition().longitude;
                Log.v("AddLabelActivity", "lzx点击了marker"+"mlatitude"+mlatitude+"mlongitude"+mlongitude);
                //召唤底部抽屉
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                return true;
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow();
            }
        });
        //点击上传
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取信息
                if(totalEditText.getText().toString().equals("") || disEditText.getText().toString().equals("")){
                    Toast.makeText(AddLabelActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                }else if(1==1){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //生成strategy的UUID
                            UUID uuid = UUID.randomUUID();
                            strategyId = uuid.toString();
                            //生成时间
                            Date date = new Date();
                            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                            String uploadTime = dateFormat.format(date);
                            int pictureNum = fileList.size();
                            Gson gson = new Gson();
                            String json = gson.toJson(pictureNum);
                            SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
                            userId = sharedPreferences.getString("userId","");
                            Log.v("AddLabelActivity", "lzx 要上传的信息"+selectedCity+"mlatitude"+mlatitude+"mlongitude"+mlongitude);
                            //构建Multipart请求体
                            MultipartBody.Builder builder = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("pictureNum", json, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                                    .addFormDataPart("strategyId", strategyId)
                                    .addFormDataPart("userId", userId)
                                    .addFormDataPart("title", totalEditText.getText().toString())
                                    .addFormDataPart("describe", disEditText.getText().toString())
                                    .addFormDataPart("latitude", String.valueOf(mlatitude))
                                    .addFormDataPart("longitude", String.valueOf(mlongitude))
                                    .addFormDataPart("selectedCity", selectedCity)
                                    .addFormDataPart("time", uploadTime);
                            //循环处理图片
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
                            Request request = new Request.Builder()
                                    .url(url+"/addStrategy")
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
                    }).start();
                }
                if(1==1){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int pictureNum = fileList.size();
                            client = new OkHttpClient();
                            Gson gson = new Gson();
                            String json = gson.toJson(pictureNum);
                            MultipartBody.Builder builder = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("pictureNum", json, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json));
                            //循环处理图片
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
                    }).start();
                }
            }
        });
        //页面返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGES && data != null) {
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    int count = clipData.getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri selectedImage = clipData.getItemAt(i).getUri();
                        //生成文件唯一标识
                        String fileName = generateUniqueIdentifier();
                        File file = getFileFromUri(selectedImage);
                        //获取文件名
                        String fileName1 = file.getName();
                        fileList.add(file);
                        displaySelectedImage(selectedImage,fileName1);
                    }
                } else if(data.getData() != null) {
                    Uri selectedImage = data.getData();
                    //生成文件唯一标识
                    String fileName = generateUniqueIdentifier();
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
        layoutParams.setMargins(0, 0, 5, 16);
        imageView.setLayoutParams(layoutParams);
        imageView.setTag(file.getName());
        imgLinerLayout.addView(imageView);
        imgLinerLayout.removeView(uploadImage);
        imgLinerLayout.addView(uploadImage);
        viewList.add(imageView);
        setListener();
    }
    private int convertDpToPixel(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    private void displaySelectedImage(Uri selectedImage,String fileName) {
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
        imgLinerLayout.addView(uploadImage);
        viewList.add(imageView);
        setListener();
    }
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
                    //给文件命名

                    return file;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String generateUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGES);
    }

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
    private String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "JPEG_" + timeStamp + "_";
    }
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

    //供用户选择拍照或从相册选择
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

    //开始POI搜索，此下位POI搜索方法
    //POI
    private void PoiSugSearch() {
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
                        mBaiduMap.clear();
                        // 获取点击的建议项的数据
                        HashMap<String, String> selectedItem = suggest.get(position);

                        selectedKey = selectedItem.get("key");
                        selectedCity = selectedItem.get("city");
                        selectedDistrict = selectedItem.get("dis");
                        Log.v("AddLabelActivity", "lzx key"+selectedKey);
                        Log.v("AddLabelActivity", "lzx city"+selectedCity);
                        Log.v("AddLabelActivity", "lzx dis"+selectedDistrict);
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
                                            .fromResource(R.drawable.new_marker);
                                    //构件MarkerOption，用于在地图上添加Marker
                                    OverlayOptions option = new MarkerOptions()
                                           .position(point)
                                           .icon(bitmap)
                                            .draggable(true)
                                           .zIndex(1);

                                    //在地图上添加并显示
                                    mBaiduMap.addOverlay(option);

                                    //将地图中心移动到当前位置
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
                                    //将地图比例尺缩放到合适的级别
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));

                                    // 设置文本框的值
                                    editText2.setText(selectedKey);

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
                            editText2.setText(selectedKey);
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

    // 判断点击的位置是否在 View 的范围内
    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        return (x > viewX && x < (viewX + viewWidth) && y > viewY && y < (viewY + viewHeight));
    }

    /*生命周期管理*/
    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if(mLocationClient!=null) {
            if (!mLocationClient.isStarted()) {
                mLocationClient.start();
            }
        }
    }
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mLocationClient!=null) {
            mLocationClient.stop();
        }
    }
}