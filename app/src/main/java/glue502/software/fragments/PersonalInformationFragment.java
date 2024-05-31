package glue502.software.fragments;

import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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
import glue502.software.activities.personal.SettingActivity;
import glue502.software.activities.personal.UpdatePersonalInformationActivity;
import glue502.software.activities.travelRecord.TravelPicturesActivity;
import glue502.software.adapters.PageAdapter;
import glue502.software.models.LoginResult;
import glue502.software.models.Personal;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PersonalInformationFragment extends Fragment {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private List<Fragment> fragments;
    private TextView txtName;
    private TextView txtUserId;
    private LinearLayout linearSetting;
    private LinearLayout linearTitle;
    private LinearLayout linearCustomerService;
    private ImageView imgAvatar,imgBackground;
    private String mCurrentPhotoPath;
    private View view;
    private float startX;
    private PageAdapter adapter;
    private final int RESULT_LOAD_IMAGES = 1, RESULT_CAMERA_IMAGE = 2;
    private String urlAvatar="http://"+ip+"/travel/user/getAvatar?userId=";
    private String urlBackground="http://"+ip+"/travel/personal/getBackground?userId=";
    private String urlLoadImage="http://"+ip+"/travel/";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private StarFragment starFragment = new StarFragment();
    private MyPostFragment myPostFragment = new MyPostFragment();
    private boolean firstLoad = true;

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
        txtUserId=view.findViewById(R.id.txt_userId);
        linearSetting=view.findViewById(R.id.linear_setting);
        linearTitle=view.findViewById(R.id.linear_title);
        linearCustomerService=view.findViewById(R.id.linear_customer_service);
        //头像及背景
        imgAvatar=view.findViewById(R.id.img_avatar);
        imgBackground=view.findViewById(R.id.img_personal);
        //收藏和发布
        tabLayout = view.findViewById(R.id.tbl);
        viewPager2 = view.findViewById(R.id.vp2);

        setViewPager2ScrollSensitivity(9);
        initFragment();
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.personal_top),true);
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
        String status=sharedPreferences.getString("status","");
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.personal_top),true);
        if("".equals(status)){
            txtName.setText("请登录");
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
            txtName.setText(userName);
            txtUserId.setText("账号: "+userId);
            loadUserAvatar(false);
            remindBind();
        }
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
        imgBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建一个 PopupMenu
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                // 从资源文件中填充菜单
                popupMenu.getMenu().add("从相册选择");
                popupMenu.getMenu().add("从相机拍照");

                // 为菜单项设置点击监听器
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 处理菜单项点击事件
                        switch (item.getTitle().toString()) {
                            case "从相册选择":
                                openFilePicker();
                                return true;
                            case "从相机拍照":
                                takeCamera(RESULT_CAMERA_IMAGE);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                // 显示 PopupMenu
                popupMenu.show();
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
                startActivityForResult(intent, 2);
            }
        });
        linearCustomerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("联系客服");
                builder.setMessage("请拨打1008611或发送邮件到2391835196@qq.com");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 在这里执行确定按钮被点击后的操作
                        dialogInterface.dismiss(); // 关闭对话框
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 在这里执行取消按钮被点击后的操作
                        dialogInterface.dismiss(); // 关闭对话框
                    }
                });
                // 创建并显示AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return view;

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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
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
            startActivityForResult(intent, 1);
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
                String backgroundUrl=personal.getBackground();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(requireContext())
                                .load(backgroundUrl)
                                .placeholder(R.drawable.headimg)
                                .into(imgBackground);

                    }
                });
            }
        });
    }
    private void uploadBackground(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
    }
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
                            txtUserId.setText("账号: "+userId);
                        }
                    }
                });
            }
        });
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { // 检查请求码是否与上传页面的请求码一致
            if (resultCode == Activity.RESULT_OK) { // 检查是否上传完成
                // 进行刷新操作，重新加载数据
                    loadUserAvatar(true);
            }
        }
        if(requestCode==2){
            if (resultCode == Activity.RESULT_OK){
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
                String status=sharedPreferences.getString("status","");
                if("".equals(status)){
                    txtName.setText("请登录");
                    txtUserId.setText("");
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new CircleCrop());
                    Glide.with(requireContext())
                            .load(R.drawable.headimg )
                            .apply(requestOptions)// 设置签名
                            .into(imgAvatar);
                    //清空star和likefragment
                    viewPager2.setAdapter(adapter);
                    TabLayoutMediator mediator = new TabLayoutMediator(
                            tabLayout,
                            viewPager2,
                            new TabLayoutMediator.TabConfigurationStrategy() {

                                @Override
                                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                    switch (position){
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

                }else{
                    String userName=sharedPreferences.getString("userName","");
                    String userId=sharedPreferences.getString("userId","");
                    txtName.setText(userName);
                    txtUserId.setText("账号: "+userId);
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