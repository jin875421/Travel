package glue502.software.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.login.LoginActivity;
import glue502.software.activities.posts.UploadPostActivity;
import glue502.software.activities.travelRecord.travelRecordActivity;
import glue502.software.adapters.PageAdapter;
import glue502.software.fragments.Add;
import glue502.software.fragments.CommunityFragment;
import glue502.software.fragments.FunctionFragment;
import glue502.software.fragments.PersonalInformationFragment;
import glue502.software.fragments.RecommendFragment;
import glue502.software.utils.MyViewUtils;


public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_REQUEST_CODE = 123; // 定义一个请求码，用于识别权限请求
    //换成自己电脑的ip地址，连接后端需要
    //192.168.43.123 192.168.142.92
    public static final String ip = "192.168.43.123:8080";
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private List<Fragment> fragments;
    private boolean backPressedOnce = false;
    private ImageView start;
    private RelativeLayout buttoncontainer;
    private String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        status = sharedPreferences.getString("status","");
        //沉浸式状态栏
        MyViewUtils.setISBarWithoutView(this,true);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // 如果权限尚未授予，则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
        //动态申请定位权限，拍照权限，读写权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

            // 如果权限尚未授予，则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {

            // 如果权限尚未授予，则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            // 如果权限尚未授予，则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {

            // 如果权限尚未授予，则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_REQUEST_CODE);
        }

        //每次启动软件，先清空glide中缓存的数据
        new ClearGlideCacheTask().execute();
        tabLayout = findViewById(R.id.tbl);
        viewPager2 = findViewById(R.id.vp2);
        buttoncontainer = findViewById(R.id.button_container);

        viewPager2.setUserInputEnabled(false);
        start = findViewById(R.id.start);
        //设置按钮大小，五分之一的屏幕宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        ViewGroup.LayoutParams layoutParam1 = buttoncontainer.getLayoutParams();
        layoutParam1.width = screenWidth/5;
        //设置居中
        buttoncontainer.setLayoutParams(layoutParam1);
        ViewGroup.LayoutParams layoutParams = start.getLayoutParams();
        layoutParams.width = screenWidth/7;
        layoutParams.height = screenWidth/7;
        start.setLayoutParams(layoutParams);

        //从activity中跳转到Fragment
        int id = getIntent().getIntExtra("id", 0);
        if (id == 2) {
            if (id == 2) {
                Fragment fragmen = new FunctionFragment();
                FragmentManager fmanger = getSupportFragmentManager();
                FragmentTransaction transaction = fmanger.beginTransaction();
                transaction.replace(R.id.viewPager2, fragmen);
                transaction.commit();
                viewPager2.setCurrentItem(2);
            }
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断登录状态
                if (status==""){
                    // 创建AlertDialog构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("账号未登录！")
                            .setMessage("是否前往登录账号")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确定”按钮后的操作
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“取消”按钮后的操作
                                    dialog.dismiss(); // 关闭对话框
                                }
                            });

                    // 创建并显示对话框
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{
                    SharedPreferences Preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String travelName = Preferences.getString("TravelName","");
                    if (travelName.equals("")){
                        // 弹出输入框
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog, null);
                        builder.setTitle("为你的旅行命个名吧")
                                .setView(view1)
                                .setNegativeButton("暂时不了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“取消”按钮后的操作
                                        dialog.dismiss(); // 关闭对话框
                                    }
                                })
                                .setPositiveButton("开始旅程", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“确定”按钮后的操作
                                        // 获取输入框中的内容
                                        EditText editText = view1.findViewById(R.id.editText);
                                        //将travelName存入sharedPreferences
                                        String travelName = editText.getText().toString();
                                        SharedPreferences.Editor editor = Preferences.edit();
                                        editor.putString("TravelName",travelName);
                                        editor.apply();
                                        Intent intent = new Intent(MainActivity.this, travelRecordActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        builder.show();
                    }else {
                        Intent intent = new Intent(MainActivity.this, travelRecordActivity.class);
                        startActivity(intent);
                    }
                }

            }
        });
        //禁止viewpager2左右滑动
        viewPager2.setUserInputEnabled(false);
        setViewPager2ScrollSensitivity(10);
        String phone = sharedPreferences.getString("userPhoneNumber", "");
        String email=sharedPreferences.getString("email", "");
        //初始化
        initpages();
        //实例化
        PageAdapter adapter = new PageAdapter(fragments,this);
        //给viewpager2绑定适配器
        viewPager2.setAdapter(adapter);
        // 在 TabLayout 中添加选中监听器
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 选中时的操作
                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .playOn(tab.view);
                tab.setIcon(getSelectedIcon(tab.getPosition()));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 未选中时的操作
                tab.setIcon(getUnselectedIcon(tab.getPosition()));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 重新选中时的操作
                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .playOn(tab.view);
            }
        });
        TabLayoutMediator mediator = new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch(position){
                            case 0:
                                tab.setIcon(R.drawable.tab_home);
                                tab.setText("首页");
                                break;
                            case 1:
                                tab.setIcon(R.drawable.tab_community);
                                tab.setText("社区");
                                break;
                            case 2:
                                tab.setIcon(R.drawable.ic_add);
                                tab.view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this, travelRecordActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 3:
                                tab.setIcon(R.drawable.tab_map);
                                tab.setText("地图");
                                break;
                            case 4:
                                tab.setIcon(R.drawable.tab_user);
                                tab.setText("我的");
                                break;

                        }
                    }
                }
        );
        mediator.attach();
    }
    private void initpages(){
        fragments = new ArrayList<>();
        fragments.add(new RecommendFragment());
        fragments.add(new CommunityFragment());
        fragments.add(new Add());
        fragments.add(new FunctionFragment());
        fragments.add(new PersonalInformationFragment());
    }


    private class ClearGlideCacheTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // 在后台线程清除磁盘缓存
            Glide.get(getApplicationContext()).clearDiskCache();
            return null;
        }

    }
    private int getSelectedIcon(int position) {
        // 返回选中状态的图标资源ID
        // 例如，你可以在这里根据位置返回不同的图标
        switch (position) {
            case 0:
                return R.drawable.tab_home2;
            case 1:
                return R.drawable.tab_community2;
            case 2:
                return R.drawable.ic_add;
            case 3:
                return R.drawable.tab_map2;
            case 4:
                return R.drawable.tab_user2;
            default:
                return R.drawable.tab_home2;
        }
    }
    private int getUnselectedIcon(int position) {
        // 返回未选中状态的图标资源ID
        // 例如，你可以在这里根据位置返回不同的图标
        switch (position) {
            case 0:
                return R.drawable.tab_home;
            case 1:
                return R.drawable.tab_community;
            case 2:
                return R.drawable.ic_add;
            case 3:
                return R.drawable.tab_map;
            case 4:
                return R.drawable.tab_user;
            default:
                return R.drawable.tab_home;
        }
    }
    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            // 用户第二次点击返回按钮，执行返回桌面操作
            super.onBackPressed();
        } else {
            // 用户第一次点击返回按钮，显示提醒
            Toast.makeText(this, "再次点击返回将返回桌面", Toast.LENGTH_SHORT).show();
            backPressedOnce = true;

            // 使用 Handler 在一定时间后重置 backPressedOnce 标志位
            new Handler().postDelayed(() -> backPressedOnce = false, 2000); // 2秒内再次点击返回按钮生效

        }
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

    // ... 其他代码
}