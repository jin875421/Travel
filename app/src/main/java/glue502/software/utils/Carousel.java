package glue502.software.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.BannerPagerAdapter;
import glue502.software.models.RecoAttraction;

public class Carousel {
    private Context mContext;
    private ViewPager2 viewPager2;
    private LinearLayout dotLinerLayout;
    private List<ImageView> mDotVIewList = new ArrayList<>();
    private List<String> originalImages = new ArrayList<>();
    private boolean isFiestDot = true;
    private String extraPath;
    private Handler mHandler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //获得轮播图当前的位置
            int currentPosition = viewPager2.getCurrentItem();
            currentPosition++;
            viewPager2.setCurrentItem(currentPosition,true);
            mHandler.postDelayed(runnable,5000);//延时5秒，自动轮播图片
        }
    };

    public Carousel(Context mContext, LinearLayout dotLinerLayout, ViewPager2 viewPager2,String extraPath) {
        this.mContext = mContext;
        this.dotLinerLayout = dotLinerLayout;
        this.viewPager2 = viewPager2;
        this.extraPath = extraPath;
    }

    public void initViews(List<String> imagePaths) {
        //加载绑定轮播图
        for (String path : imagePaths) {
//                    originalImages.add(path);
            //制作标志点的ImageView，并初始化第一张图片标志点
            ImageView dotImageView = new ImageView(mContext);
            if (isFiestDot) {
                dotImageView.setImageResource(R.drawable.dot_red);
                isFiestDot = false;
            } else {
                dotImageView.setImageResource(R.drawable.dot_white);
            }
            LinearLayout.LayoutParams dotImageLayoutParams = new LinearLayout.LayoutParams(10, 10);
            dotImageLayoutParams.setMargins(5, 0, 5, 0);
            dotImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            dotLinerLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            dotImageView.setLayoutParams(dotImageLayoutParams);
            mDotVIewList.add(dotImageView);
            dotLinerLayout.addView(dotImageView);
        }
        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(imagePaths, viewPager2,extraPath);
        viewPager2.setAdapter(bannerPagerAdapter);
        //设置当前项为第一个元素，使其为轮播图的开始
        viewPager2.setCurrentItem(imagePaths.size() * 10000, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //z
                int current = position % imagePaths.size();
                for (int i = 0; i < mDotVIewList.size(); i++) {
                    mDotVIewList.get(i).setImageResource(R.drawable.dot_white);
                }
                mDotVIewList.get(current).setImageResource(R.drawable.dot_red);

            }

        });

    }

    public void initViews1(String recoAttractionsStr) {
        Type RecoAttractionListType = new TypeToken<List<RecoAttraction>>(){}.getType();
        List<RecoAttraction> recoAttractions = new Gson().fromJson(recoAttractionsStr,RecoAttractionListType);
        //加载绑定轮播图
        for (RecoAttraction r : recoAttractions) {
            //制作标志点的ImageView，并初始化第一张图片标志点
            ImageView dotImageView = new ImageView(mContext);
            if (isFiestDot) {
                dotImageView.setImageResource(R.drawable.dot_red);
                isFiestDot = false;
            } else {
                dotImageView.setImageResource(R.drawable.dot_white);
            }
            LinearLayout.LayoutParams dotImageLayoutParams = new LinearLayout.LayoutParams(10, 10);
            dotImageLayoutParams.setMargins(5, 0, 5, 0);
            dotImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            dotLinerLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            dotImageView.setLayoutParams(dotImageLayoutParams);
            mDotVIewList.add(dotImageView);
            dotLinerLayout.addView(dotImageView);
        }
        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(recoAttractions,recoAttractionsStr, viewPager2,1);
        viewPager2.setAdapter(bannerPagerAdapter);
        //设置当前项为第一个元素，使其为轮播图的开始
        viewPager2.setCurrentItem((recoAttractions.size() * 10000)+1, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int current = position % recoAttractions.size();
                for (int i = 0; i < mDotVIewList.size(); i++) {
                    mDotVIewList.get(i).setImageResource(R.drawable.dot_white);
                }
                mDotVIewList.get(current).setImageResource(R.drawable.dot_red);
            }

//            @Override
//            public void onPageScrollStateChanged(int state) {
//                super.onPageScrollStateChanged(state);
//                if (state == viewPager2.SCROLL_STATE_IDLE) {
//                    mHandler.postDelayed(runnable,5000);//延时5秒，自动轮播图片
//                }
//            }
        });
        mHandler.postDelayed(runnable,3000);
    }
}
