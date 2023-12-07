package glue502.software.utils.bigImgUtils;

import android.app.Application;

import com.flyjingfish.openimagelib.OpenImageConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {
    public static MyApplication mInstance;
    public static ExecutorService cThreadPool = Executors.newFixedThreadPool(5);
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        OpenImageConfig.getInstance().setBigImageHelper(new BigImageHelperImpl());
    }
}

