package glue502.software.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MyViewUtils {
    /**
     * 这个方法用于设置沉浸式状态栏,通过设置控件的内边距（设置外边距的方法难以覆盖背景，必要时再补充）来替代原来状态栏的高度，保证设置沉浸之后原有控件不会被遮挡
     * tips:如果要被设置的控件在布局文件中没有id，可以通过其子控件的getRootView()方法获得
     * 代码解释：(大可不看)
     * clearFlags:清除了两个标志位，FLAG_TRANSLUCENT_STATUS 和 FLAG_TRANSLUCENT_NAVIGATION。这两个标志位用于启用透明状态栏和透明导航栏。通过清除这些标志位，我们告诉系统我们要自定义状态栏和导航栏的外观，而不使用默认的透明效果
     * getDecorView:设置系统 UI 的可见性,使用了三个标志位：
     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN: 表示布局时考虑全屏，即内容布局将延伸到状态栏的区域。
     * SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION: 表示布局时考虑隐藏导航栏，即内容布局将延伸到导航栏的区域。
     * SYSTEM_UI_FLAG_LAYOUT_STABLE: 保持稳定的布局，防止系统栏隐藏时界面整体上下抖动。
     * 这三个标志位的组合使得应用的布局会显示在整个窗口之下，包括状态栏和导航栏的区域
     * addFlags:添加了一个标志位 FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS，表示窗口将绘制系统栏的背景,让应用的布局能够覆盖状态栏和导航栏
     * setStatusBarColor:将状态栏的颜色设置为透明,状态栏将不再有实际的背景颜色，让应用的内容能够显示在状态栏的区域
     * setNavigationBarColor:将导航栏的颜色设置为透明，使得应用的内容能够显示在导航栏的区域
     * @param activity 传入调用该方法的Activity对象
     * @param view     需要设置的View
     */
    public static void setImmersiveStatusBar(Activity activity, View view, boolean isDark) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        int statusBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        if (view != null) {
            view.setPadding(
                    view.getPaddingLeft(),
                    statusBarHeight,
                    view.getPaddingRight(),
                    view.getPaddingBottom()
            );
        }
        if(isDark){
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        }
    }

    public static void setISBarWithoutView(Activity activity,boolean isDark) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        if(isDark){
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        }
    }
}
