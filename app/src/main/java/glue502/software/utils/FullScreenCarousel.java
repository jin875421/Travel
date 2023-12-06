package glue502.software.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.BannerPagerAdapter;
import glue502.software.adapters.FullScreenDisplayAdapter;

public class FullScreenCarousel {
    private Context mContext;
    private ViewPager2 viewPager2;
    private LinearLayout dotLinerLayout;
    private List<ImageView> mDotVIewList = new ArrayList<>();
    private List<String> originalImages = new ArrayList<>();
    public FullScreenCarousel(Context mContext,LinearLayout dotLinerLayout,ViewPager2 viewPager2){
        this.mContext = mContext;
        this.dotLinerLayout = dotLinerLayout;
        this.viewPager2 = viewPager2;
    }
    public void initViews(List<String> imagePaths, int imgPosition){
        //加载绑定轮播图
        for (String path:imagePaths){
            originalImages.add(path);
            //制作标志点的ImageView，并初始化第一张图片标志点
            ImageView dotImageView = new ImageView(mContext);
            if (originalImages.size()==imgPosition){
                dotImageView.setImageResource(R.drawable.dot_red);
            }else {
                dotImageView.setImageResource(R.drawable.dot_white);
            }
            LinearLayout.LayoutParams dotImageLayoutParams = new LinearLayout.LayoutParams(10,10);
            dotImageLayoutParams.setMargins(5,0,5,0);
            dotImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            dotLinerLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            dotImageView.setLayoutParams(dotImageLayoutParams);
            mDotVIewList.add(dotImageView);
            dotLinerLayout.addView(dotImageView);
        }


        originalImages.add(0,originalImages.get(originalImages.size()-1));//将originalImages最后一张插入到开头
        originalImages.add(originalImages.get(1));//将第二张插入到结尾
        FullScreenDisplayAdapter fullScreenDisplayAdapter = new FullScreenDisplayAdapter(originalImages);
        viewPager2.setAdapter(fullScreenDisplayAdapter);
        //设置当前项为第一个元素，使其为轮播图的开始
        viewPager2.setCurrentItem(imgPosition,false);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for(int i= 0;i<mDotVIewList.size();i++){
                    if (i == position-1){
                        mDotVIewList.get(i).setImageResource(R.drawable.dot_red);
                    }else {
                        mDotVIewList.get(i).setImageResource(R.drawable.dot_white);
                    }
                }
                if (position == originalImages.size()-1){
                    viewPager2.setCurrentItem(1,false);
                } else if (position==0) {
                    viewPager2.setCurrentItem(originalImages.size()-2,false);
                }
            }

            //            @Override
            //            public void onPageScrollStateChanged(int state) {
            //                super.onPageScrollStateChanged(state);
            //
            //                //
            //            }
        });

    }
}
