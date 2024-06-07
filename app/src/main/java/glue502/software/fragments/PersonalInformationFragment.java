package glue502.software.fragments;

import static android.content.Context.MODE_PRIVATE;
import static glue502.software.activities.MainActivity.PERMISSION_REQUEST_CODE;
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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import glue502.software.R;
import glue502.software.activities.OpenCVTest;
import glue502.software.activities.login.CodeLoginActivity;
import glue502.software.activities.personal.AchievementActivity;
import glue502.software.activities.personal.DailyTaskActivity;
import glue502.software.activities.personal.FollowSearchActivity;
import glue502.software.activities.personal.MyFollowActivity;
import glue502.software.activities.personal.SettingActivity;
import glue502.software.activities.personal.UpdatePersonalInformationActivity;
import glue502.software.activities.photoMeld.ShowPictureEdit;
import glue502.software.activities.posts.UploadPostActivity;
import glue502.software.activities.travelRecord.TravelPicturesActivity;
import glue502.software.adapters.PageAdapter;
import glue502.software.models.LoginResult;
import glue502.software.models.Personal;
import glue502.software.models.UserExtraInfo;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PersonalInformationFragment extends Fragment {
    private String url = "http://" + ip + "/travel";
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private List<Fragment> fragments;
    private TextView txtName,topTxtName;
    private TextView txtUserId;
    private LinearLayout linearSetting;
    private LinearLayout linearTitle;
//    private LinearLayout linearCustomerService;
    private ImageView imgAvatar,imgBackground;
    private String mCurrentPhotoPath;
    private LinearLayout follow,myAchievement,taskCenter,lrltPhotoEdit;
    private View view;
    private PageAdapter adapter;
    private static final int RESULT_LOAD_IMAGES = 1;
    private static final int RESULT_TAKE_PHOTO = 2;
    private static final int REQUEST_READ_STORAGE_PERMISSION = 100;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 101;
    private int RESULT_UPDATE = 0;
    private String userId;
    private String urlAvatar="http://"+ip+"/travel/user/getAvatar?userId=";
    private String urlBackground="http://"+ip+"/travel/personal/getBackground?userId=";
    private String uploadBackgroundUrl="http://"+ip+"/travel/personal/uploadbackground";
    private String urlLoadImage="http://"+ip+"/travel/";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private StarFragment starFragment = new StarFragment();
    private MyPostFragment myPostFragment = new MyPostFragment();
    private boolean firstLoad = true;
    private SharedPreferences sharedPreferences1;

    //顶部渐变控件
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    // 用户额外信息
    UserExtraInfo userExtraInfo;
    // 经验条
    TextView tvLevel;
    ProgressBar experienceBar;

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
                        // 上传成功
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    } else {
                        // 上传失败
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_personal_information, container, false);
        txtName=view.findViewById(R.id.txt_name);
        topTxtName=view.findViewById(R.id.top_txt_name);
        txtUserId=view.findViewById(R.id.txt_userId);
        linearSetting=view.findViewById(R.id.linear_setting);
        linearTitle=view.findViewById(R.id.linear_title);
        lrltPhotoEdit=view.findViewById(R.id.lrlt_photo_edit);
        //头像及背景
        imgAvatar=view.findViewById(R.id.img_avatar);
        imgBackground=view.findViewById(R.id.img_personal);
        //收藏和发布
        tabLayout = view.findViewById(R.id.tbl);
        viewPager2 = view.findViewById(R.id.vp2);
        //关注
        follow = view.findViewById(R.id.follow);
        //成就
        myAchievement = view.findViewById(R.id.my_achievement);
        // 任务中心
        taskCenter = view.findViewById(R.id.task_center);
        //顶部渐变控件
        toolbar = view.findViewById(R.id.toolbar);
        appBarLayout=view.findViewById(R.id.appbar);
        //经验条
        tvLevel = view.findViewById(R.id.tv_level);
        experienceBar = view.findViewById(R.id.experienceBar);
        setViewPager2ScrollSensitivity(9);
        initFragment();
        MyViewUtils.setISBarWithoutView(getActivity(),true);
        //绑定监听器
        viewPager2.setAdapter(adapter);

        TabLayoutMediator mediator = new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {

                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:
                                tab.setText("收藏");
                                break;
                            case 1:
                                tab.setText("发布");
                                break;
                            default:
                                break;
                        }
                    }
                }
        );
        mediator.attach();
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        sharedPreferences1=getActivity().getSharedPreferences("personalBackground",MODE_PRIVATE);
        String personalStatu=sharedPreferences1.getString("personalStatu","");
        String status=sharedPreferences.getString("status","");
        if("".equals(status)){
            txtName.setText("请登录");
            topTxtName.setText("");
            txtUserId.setText("");
            RequestOptions requestOptions = new RequestOptions()
                    .transform(new CircleCrop());
            Glide.with(requireContext())
                    .load(R.drawable.headimg )
                    .apply(requestOptions)// 设置签名
                    .into(imgAvatar);
        }else{
            String userName=sharedPreferences.getString("userName","");
            String userId=sharedPreferences.getString("userId","");
//            System.out.println(userId+"sdasdasd");
            txtName.setText(userName);
            topTxtName.setText(userName);
            txtUserId.setText("账号: "+userId);
            loadUserAvatar(false);
            if(personalStatu.equals("1"))
            {
                loadBackground();
           }
            remindBind();
        }
        //我的创作
        lrltPhotoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowPictureEdit.class);
                startActivity(intent);
            }
        });
        // TODO 经验条相关
        loadUserExtraInfo();

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyFollowActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
        myAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 成就展示界面
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
                String status=sharedPreferences.getString("status","");
                if ("".equals(status)) {
                    // 用户未登录，弹出提示框
                    showLoginAlertDialog();
                }else{
                    Intent intent = new Intent(getActivity(), AchievementActivity.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                }
            }
        });
        taskCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 任务中心
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
                String status=sharedPreferences.getString("status","");
                if ("".equals(status)) {
                    // 用户未登录，弹出提示框
                    showLoginAlertDialog();
                }else{
                   Intent intent = new Intent(getActivity(), DailyTaskActivity.class);
                   intent.putExtra("userId",userId);
                   startActivity(intent);
               }
           }
        });
        imgAvatar.setOnClickListener(v->{
            OpenImage.with(getContext()).setClickImageView(imgAvatar)
                    .setAutoScrollScanPosition(true)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .addPageTransformer(new ScaleInTransformer())
                    .setImageUrlList(Collections.singletonList(urlAvatar+sharedPreferences.getString("userId", "")), MediaType.IMAGE)
//                    .setOnItemLongClickListener(new OnItemLongClickListener() {
//                        @Override
//                        public void onItemLongClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
//                            Toast.makeText(getContext(),"长按图片",Toast.LENGTH_LONG).show();
//                        }
//                    })
                    .show();
        });
        imgBackground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               showPopupWindow();
                // 返回 true 表示事件已被处理
                return true;
            }
        });

        linearTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoginStatus();
            }
        });


        linearSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(intent, 3);
            }
        });
//        linearCustomerService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("联系客服");
//                builder.setMessage("请拨打1008611或发送邮件到2391835196@qq.com");
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // 在这里执行确定按钮被点击后的操作
//                        dialogInterface.dismiss(); // 关闭对话框
//                    }
//                });
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // 在这里执行取消按钮被点击后的操作
//                        dialogInterface.dismiss(); // 关闭对话框
//                    }
//                });
//                // 创建并显示AlertDialog
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        });

        // 顶部渐变控件 获取状态栏高度
        int statusBarHeight = getStatusBarHeight();
        // 获取 Toolbar 实例 动态设置 Toolbar 的高度
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height = statusBarHeight + getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        toolbar.setLayoutParams(params);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                toolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.white),Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange()));
                topTxtName.setTextColor(changeAlpha(Color.parseColor("#0c0c0c"),Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange()));
//                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
//                    // 完全折叠，显示顶部用户名
//                    topTxtName.setVisibility(View.VISIBLE);
//
//                } else {
//                    // 非完全折叠，隐藏ImageView
//                    topTxtName.setVisibility(View.INVISIBLE);
//                }
            }
        });
        return view;

    }

    private void loadUserExtraInfo() {
        Log.i("PersonalInformationFragment", "开始获取用户额外数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("userId", userId)
                        .build();
                Request request = new Request.Builder()
                        .url(url+"/userExtraInfo/getUserExtraInfo")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        String responseData = response.body().string();
                        if(responseData.equals("")){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("PersonalInformationFragment", "无数据");
                                }
                            });
                        } else {
                            userExtraInfo = new Gson().fromJson(responseData, UserExtraInfo.class);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("PersonalInformationFragment", "获取用户额外数据成功");
                                    int level = userExtraInfo.getLevel();
                                    // 设置经验和等级
                                    tvLevel.setText("Lv." + level);
                                    // 0->1 :(lv+1)*100 + lv*50=100   1->2 :250   2->3 :400   3->4 :600   4->5 :750
                                    experienceBar.setMax((level+1)*100 + level*50);
                                    experienceBar.setProgress(userExtraInfo.getExperience());
                                }
                            });
                        }
                    } else {
                        Log.i("PersonalInformationFragment", "获取用户额外数据失败");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }).start();
    }

    //顶部渐变控件
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }
    // 获取状态栏高度
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //供用户选择拍照或从相册选择
    private void showPopupWindow() {
        View popView = View.inflate(getActivity(), R.layout.popupwindow_camera_need, null);
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
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {

                    // 如果权限尚未授予，则请求权限
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                }
                //如果权限已经授予
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takeCamera(RESULT_TAKE_PHOTO);
                }

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
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 50);
    }
    //打开文件选择器
    private void openFilePicker() {
        System.out.println("打开文件选择器");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGES);
    }

    //拍摄照片
    private void takeCamera(int num) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        getActivity().getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
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
    private void loadBackground(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        OkHttpClient client =new OkHttpClient();
        Request request=new Request.Builder()
                .url(urlBackground+userId)
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

                Gson gson=new Gson();
                Personal personal = gson.fromJson(responseData,Personal.class);

                if(personal!=null){
                    String backgroundUrl=personal.getBackground();

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(backgroundUrl!=null) {
                            updateBackgroundWithGlide(backgroundUrl,true);
                        }
                    }
                });
            }
        } });

    }
    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(starFragment);
        fragments.add(myPostFragment);
        adapter = new PageAdapter(fragments,getActivity());
    }


    private void checkLoginStatus() {
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String status=sharedPreferences.getString("status","");
        if ("".equals(status)) {
            // 用户未登录，弹出提示框
            showLoginAlertDialog();
        }else{
            Intent intent = new Intent(getActivity(), UpdatePersonalInformationActivity.class);
            startActivityForResult(intent, 4);
        }
    }



    private void showLoginAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("您未登录，是否登录？");
        builder.setPositiveButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 跳转到登录页面
                Intent intent = new Intent(getActivity(), CodeLoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 取消操作
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateBackgroundWithGlide(String avatarUrl, boolean forceRefresh) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        if(forceRefresh==false){
            Glide.with(requireContext())
                    .load(urlLoadImage + avatarUrl)
                    .skipMemoryCache(true)  //允许内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 使用磁盘缓存
                    .placeholder(R.drawable.personal_bg001)  // 设置占位图
                    .signature(new ObjectKey(userId+"1"))  // 设置签名
                    .into(imgBackground);
        }else{
            Glide.get(requireContext()).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(requireContext()).clearDiskCache();
                }
            }).start();
            Glide.with(requireContext())
                    .load(urlLoadImage + avatarUrl)
                    .skipMemoryCache(true)  //允许内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 使用磁盘缓存
                    .placeholder(R.drawable.personal_bg001)  // 设置占位图
                    .signature(new ObjectKey(userId+"1"))  // 设置签名
                    .into(imgBackground);
        }

    }
    private void uploadBackground(File file){
        System.out.println("向服务器发送请求");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
//        File file = new File(imageUri.getPath()); // 获取图片文件路径
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .addFormDataPart("file", userId+".jpg", RequestBody.create(okhttp3.MediaType.parse("image/*"), file))
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(uploadBackgroundUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "上传成功", Toast.LENGTH_SHORT).show();
                             sharedPreferences1 = getActivity().getSharedPreferences("personalBackground", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.putString("personalStatu","1");
                            editor.apply();
                            loadBackground();
                            // 延时2秒后执行
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadUserAvatar(true);
                                }
                            }, 2000);
                        } else {
                            Toast.makeText(requireContext(), "上传失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    //从服务器获取用户头像路径
    private void loadUserAvatar(boolean a) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        // 创建 OkHttp 客户端
        OkHttpClient client = new OkHttpClient();

        // 构建请求
        Request request = new Request.Builder()
                .url(urlAvatar + userId)  // 替换为你的后端 API 地址
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

                Gson gson=new Gson();
                // 获取 avatarUrl 和 userNickname
                UserInfo userInfo = gson.fromJson(responseData,UserInfo.class);
                String avatarUrl=userInfo.getAvatar();
                String userName =userInfo.getUserName();
                // 在 UI 线程中更新 ImageView
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用 Glide 加载用户头像
                        if (avatarUrl!=null&&!avatarUrl.isEmpty()) {
                            loadImageWithGlide(avatarUrl,a);
                        } else {
                            // 处理返回的不是有效地址的情况
                            // 可以设置默认头像或给用户提示
                            RequestOptions requestOptions = new RequestOptions()
                                    .transform(new CircleCrop());
                            Glide.with(requireContext())
                                    .load(R.drawable.headimg)
                                    .apply(requestOptions)
                                    .into(imgAvatar);
                        }
                        if(!userName.isEmpty()){
                            txtName.setText(userName);
                            topTxtName.setText(userName);
                            txtUserId.setText("账号: "+userId);
                        }
                    }
                });
            }
        });
    }
    //加载用户头像
    private void loadImageWithGlide(String avatarUrl, boolean forceRefresh) {
        // 使用 Glide 加载用户头像，并进行圆形裁剪
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        if(forceRefresh==false){
            Glide.with(requireContext())
                    .load(urlLoadImage + avatarUrl)
                    .skipMemoryCache(true)  //允许内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 使用磁盘缓存
                    .placeholder(R.drawable.headimg)  // 设置占位图
                    .apply(requestOptions)
                    .signature(new ObjectKey(userId))  // 设置签名
                    .into(imgAvatar);
        }else{
            Glide.get(requireContext()).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(requireContext()).clearDiskCache();
                }
            }).start();
            Glide.with(requireContext())
                    .load(urlLoadImage + avatarUrl)
                    .skipMemoryCache(true)  //允许内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 使用磁盘缓存
                    .placeholder(R.drawable.headimg)  // 设置占位图
                    .apply(requestOptions)
                    .signature(new ObjectKey(userId))  // 设置签名
                    .into(imgAvatar);
        }

    }
    // 获取相册图片真实路径
    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                file = new File(getActivity().getCacheDir(), "temp_image.jpg");
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
            String status = sharedPreferences.getString("status", "");
               Uri imageUri = null;
               if (requestCode == RESULT_LOAD_IMAGES && data != null) {

                   if (data.getClipData() != null) {
                       Log.println(Log.INFO, "onActivityResult", "333");
                       ClipData clipData = data.getClipData();
                       int count = clipData.getItemCount();
                       for (int i = 0; i < count; i++) {
                           Uri selectedImage = clipData.getItemAt(i).getUri();
                           File file = getFileFromUri(selectedImage);
                           System.out.println(file.getName()+"asdawdawd");
                           uploadBackground(file);
                       }
                   }
               } else if (requestCode == RESULT_TAKE_PHOTO) {
                   File file = new File(mCurrentPhotoPath);
                   imageUri = Uri.fromFile(file);
                   if (imageUri != null) {
                       uploadBackground(file);
                   }

           }
        }
        if (requestCode == 4) { // 检查请求码是否与上传页面的请求码一致
            if (resultCode == Activity.RESULT_OK) { // 检查是否上传完成
                // 进行刷新操作，重新加载数据
                loadUserAvatar(true);
            }
        }

        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
                String status = sharedPreferences.getString("status", "");
                if ("".equals(status)) {
                    txtName.setText("请登录");
                    topTxtName.setText("");
                    txtUserId.setText("");
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new CircleCrop());
                    Glide.with(requireContext())
                            .load(R.drawable.headimg)
                            .apply(requestOptions) // 设置签名
                            .into(imgAvatar);
                    // 清空star和likefragment
                    viewPager2.setAdapter(adapter);
                    TabLayoutMediator mediator = new TabLayoutMediator(
                            tabLayout,
                            viewPager2,
                            new TabLayoutMediator.TabConfigurationStrategy() {

                                @Override
                                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                    switch (position) {
                                        case 0:
                                            tab.setText("我的收藏");
                                            break;
                                        case 1:
                                            tab.setText("我的发布");
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                    );
                    mediator.attach();

                } else {
                    String userName = sharedPreferences.getString("userName", "");
                    String userId = sharedPreferences.getString("userId", "");
                    txtName.setText(userName);
                    topTxtName.setText(userName);
                    txtUserId.setText("账号: " + userId);
                    loadUserAvatar(false);
                    remindBind();
                }
            }
        }
    }

    private void remindBind() {
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userPhoneNumber=sharedPreferences.getString("userPhoneNumber","");
        String email=sharedPreferences.getString("email","");

        checkAndShowReminder(userPhoneNumber, email);
    }

    private void checkAndShowReminder(String userPhoneNumber, String email) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String lastReminderDate = sharedPreferences.getString("lastReminderDate", "");
        if (userPhoneNumber.isEmpty() && email.isEmpty() && !isSameDay(lastReminderDate)) {
            // 提示绑定的逻辑
            showBindingReminderDialog();
            // 更新上次提醒的日期为今天
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lastReminderDate", getCurrentDate());
            editor.apply();
        }
    }

    private boolean isSameDay(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date currentDate = new Date();
            Date storedDate = sdf.parse(date);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(currentDate);
            cal2.setTime(storedDate);

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showBindingReminderDialog() {
        // 创建并显示提醒对话框的逻辑
        // 可以使用 AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("绑定提醒");
        builder.setMessage("请绑定手机号码或邮箱否则无法找回密码！");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 在这里执行取消按钮被点击后的操作
                dialogInterface.dismiss(); // 关闭对话框
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 在这里执行确定按钮被点击后的操作
                Intent intent = new Intent(getActivity(), UpdatePersonalInformationActivity.class);
                startActivity(intent);
            }
        });

        builder.show();
    }
    //fragment活动灵敏度
    private void setViewPager2ScrollSensitivity(int sensitivity) {
        try {
            Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);
            RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(viewPager2);

            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            int touchSlop = (int) touchSlopField.get(recyclerView);

            touchSlopField.set(recyclerView, touchSlop * sensitivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}