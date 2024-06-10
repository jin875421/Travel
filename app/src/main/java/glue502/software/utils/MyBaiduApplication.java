package glue502.software.utils;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class MyBaiduApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //设置百度地图隐私政策
        SDKInitializer.setAgreePrivacy(this,true);
        //sdk初始化
        SDKInitializer.initialize(this);
        initKedaXun();
    }
    /**
     * 科大讯飞
     * 语音sdk
     * 初始化
     */
    private void initKedaXun() {

        // 初始化参数构建

        StringBuffer param = new StringBuffer();
        //IflytekAPP_id为我们申请的Appid
        param.append("appid=" + "IflytekAPP_id");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(MyBaiduApplication.this, param.toString());

    }


}

