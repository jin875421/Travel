package glue502.software.utils;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;


public class MyBaiduApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //设置百度地图隐私政策
        SDKInitializer.setAgreePrivacy(this,true);
        //sdk初始化
        SDKInitializer.initialize(this);
    }
}
