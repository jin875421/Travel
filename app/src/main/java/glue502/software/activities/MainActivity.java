package glue502.software.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.PageAdapter;
import glue502.software.fragments.CommunityFragment;
import glue502.software.fragments.FunctionFragment;
import glue502.software.fragments.PersonalInformationFragment;
import glue502.software.fragments.RecommendFragment;

//
public class MainActivity extends AppCompatActivity {
    //换成自己电脑的ip地址，连接后端需要
    public static final String ip = "192.168.88.91:8080";
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private List<Fragment> fragments;
    private boolean backPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //每次启动软件，先清空glide中缓存的数据
        new ClearGlideCacheTask().execute();
        tabLayout = findViewById(R.id.tbl);
        viewPager2 = findViewById(R.id.vp2);
        //初始化
        initpages();
        //实例化
        PageAdapter adapter = new PageAdapter(fragments,this);
        //给viewpager2绑定适配器
        viewPager2.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch(position){
                            case 0:
                                tab.setText("首页");
                                break;
                            case 1:
                                tab.setText("社区");
                                break;
                            case 2:
                                tab.setText("工具");
                                break;
                            case 3:
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
}