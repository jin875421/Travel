package glue502.software.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import androidx.appcompat.app.AppCompatActivity;
import glue502.software.R;
import glue502.software.utils.MyViewUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_splash);
        LinearLayout rootView = findViewById(R.id.rootView);
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation("data.json");
        lottieAnimationView.playAnimation();
        lottieAnimationView.setRepeatCount(0);
        //添加沉浸式导航栏
        MyViewUtils.setImmersiveStatusBar(this,findViewById(R.id.rootView));
        // 添加动画监听器
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 动画开始时执行的操作
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时执行的操作
                navigateToMainActivityWithAnimation(rootView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // 动画取消时执行的操作
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // 动画重复时执行的操作
            }
        });
    }

    private void navigateToMainActivityWithAnimation(final LinearLayout rootView) {
        // 创建透明度渐变动画
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(rootView, View.ALPHA, 1.0f, 0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500); // 持续时间 500 毫秒

        // 设置动画结束后的操作
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 跳转到 MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // 关闭 SplashActivity
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        // 启动透明度渐变动画
        fadeOut.start();
    }

}