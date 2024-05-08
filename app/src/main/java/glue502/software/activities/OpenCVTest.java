package glue502.software.activities;

import static glue502.software.activities.MainActivity.ip;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import glue502.software.R;
import glue502.software.utils.ImageUtils;
import glue502.software.utils.Shape;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenCVTest extends AppCompatActivity {
    private Bitmap bitmap;
    private Bitmap bitmap2;
    private Button back,save,lianHuanHua,fuDiao,huaiJiu,gaoSi;
    private ImageView imgOrg,imgCha;
    private TextView tvShow;
    private String saveUrl="http://"+ip+"/travel/travel/";
    private ProgressBar progressBar; // 添加这一行
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opencv_test);
        initOpenCVll();
        initView();

        // 获取传递的URL
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String imagePlaceId=getIntent().getStringExtra("imagePlaceId");

        // 使用URL加载图像到Bitmap中
        loadBitmapFromUrl(imageUrl);

        lianHuanHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // 显示进度条

                // 在后台线程中执行耗时操作
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        // 在后台执行图像处理操作
                        return LianHuanHua(bitmap);
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        // 在UI线程中更新UI，设置处理后的图像到ImageView，并隐藏进度条
                        imgCha.setImageBitmap(result);
                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                        bitmap2=result;
                    }
                }.execute();
            }
        });
        fuDiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // 显示进度条

                // 在后台线程中执行耗时操作
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        // 在后台执行浮雕滤镜操作
                        return FuDiao(bitmap);
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        // 在UI线程中更新UI，设置处理后的图像到ImageView，并隐藏进度条
                        imgCha.setImageBitmap(result);
                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                        bitmap2=result;
                    }
                }.execute();
            }
        });

        huaiJiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // 显示进度条

                // 在后台线程中执行耗时操作
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        // 在后台执行怀旧色滤镜操作
                        return HuaiJiu(bitmap);
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        // 在UI线程中更新UI，设置处理后的图像到ImageView，并隐藏进度条
                        imgCha.setImageBitmap(result);
                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                        bitmap2=result;
                    }
                }.execute();
            }
        });

        gaoSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // 显示进度条

                // 在后台线程中执行耗时操作1
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        // 在后台执行高斯模糊操作
                        return gaoSi(bitmap);
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        // 在UI线程中更新UI，设置处理后的图像到ImageView，并隐藏进度条
                        imgCha.setImageBitmap(result);
                        // 隐藏进度条
                        progressBar.setVisibility(View.GONE);
                        bitmap2=result;

                    }
                }.execute();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2024/5/7 把bitmap2和placeId传到服务器，相应方法
                if (bitmap2 != null && imagePlaceId != null) {
                    String filename = UUID.randomUUID().toString();
                    // 构建Multipart请求体
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("placeId", imagePlaceId)
                            .addFormDataPart("file", filename+".jpg", RequestBody.create(MediaType.parse("image/*"), bitmapToBytes(bitmap2)))
                            .build();

// 构建POST请求
                    Request request = new Request.Builder()
                            .url(saveUrl + "uploadphoto")
                            .post(requestBody)
                            .build();

// 发送请求
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Response response = client.newCall(request).execute();
                                final String responseData = response.body().string();

                                // 处理服务器响应，更新UI或执行其他操作
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 处理响应数据
                                        // 这里可以根据服务器的响应结果进行操作，例如显示消息、更新UI等

                                        System.out.println(responseData);

                                        // 示例：显示成功消息并结束当前Activity
                                        Toast.makeText(OpenCVTest.this, responseData, Toast.LENGTH_SHORT).show();

                                        // 结束当前Activity
                                        finish();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }
    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
    private void loadBitmapFromUrl(String imageUrl) {
        // 在这里加载图像到Bitmap中，您可以使用任何加载图像的库或方法
        // 例如，您可以使用Picasso、Glide或直接使用HttpURLConnection加载图像

        // 这里是一个示例，您需要根据您的需求实现加载图像的逻辑
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);

                // 加载完图像后更新UI，如果需要的话
                runOnUiThread(() -> {
                    // 在这里更新UI，例如显示图像到ImageView
                    imgOrg.setImageBitmap(bitmap);

                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void initView() {
        imgOrg=findViewById(R.id.img_org);
        imgCha=findViewById(R.id.img_cha);
        progressBar = findViewById(R.id.progressBar);
        back=findViewById(R.id.btn_back);
        save=findViewById(R.id.btn_save);
        lianHuanHua=findViewById(R.id.btn_lianhuanhua);
        fuDiao=findViewById(R.id.btn_fudiao);
        huaiJiu=findViewById(R.id.btn_huaijiu);
        gaoSi=findViewById(R.id.btn_gaosi);
    }



    private void initOpenCVll() {
        if(OpenCVLoader.initDebug()){
            Toast.makeText(this,"成功",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"失败",Toast.LENGTH_LONG).show();
        }
    }
    //浮雕滤镜
    Bitmap FuDiao(Bitmap photo){
        Bitmap bingdong  = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 1;i<photo.getWidth()-1;i++){
            for( int j = 1;j<photo.getHeight()-1;j++){
                int A = photo.getPixel(i-1,j-1);
                int B = photo.getPixel(i+1,j+1);
                int AR =Color.red(B)-Color.red(A)+128;
                int AG =Color.green(B)-Color.green(A)+128;
                int AB =Color.blue(B)-Color.blue(A)+128;
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                bingdong.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return bingdong;
    }
    Bitmap LianHuanHua(Bitmap photo){
        Bitmap lianhuanhua = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 0;i<photo.getWidth();i++){
            for( int j = 0;j<photo.getHeight();j++){
                int A = photo.getPixel(i,j);
                int AR =Math.abs(Color.red(A) - Color.blue(A) + Color.green(A)+ Color.green(A)  ) * Color.red(A) / 256;
                int AG =Math.abs(Color.red(A) - Color.green(A) + Color.blue(A) + Color.blue(A)) * Color.red(A) / 256;
                int AB =Math.abs(Color.red(A) - Color.blue(A) + Color.blue(A) + Color.blue(A)) * Color.green(A) / 256;
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                lianhuanhua.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return lianhuanhua;
    }
    //怀旧色滤镜
    Bitmap HuaiJiu(Bitmap photo){
        Bitmap huaijiu = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 0;i<photo.getWidth();i++){
            for( int j = 0;j<photo.getHeight();j++){
                int A = photo.getPixel(i,j);
                int AR =(int)(0.393*Color.red(A) + 0.769*Color.green(A) + 0.189*Color.blue(A));
                int AG =(int)(0.349*Color.red(A) + 0.686*Color.green(A) + 0.168*Color.blue(A));
                int AB =(int)(0.272*Color.red(A) + 0.534*Color.green(A) + 0.131*Color.blue(A));
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                huaijiu.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return huaijiu;
    }
    Bitmap gaoSi(Bitmap photo){

        Mat mat=new Mat(photo.getWidth(), photo.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(photo,mat);
        Imgproc.blur(mat,mat,new Size(30,30));
        Bitmap gaosi=Bitmap.createBitmap(mat.cols(),mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat,gaosi);
        return gaosi;
    }
    // 光照滤镜
//     Bitmap lightFilter(Bitmap photo) {
//        Mat src = new Mat();
//        Mat dst = new Mat();
//        Bitmap resultBitmap;
//
//        // 将Bitmap转换为Mat
//        Utils.bitmapToMat(photo, src);
//
//        // 直方图均衡化，增强图像整体亮度和对比度
//        Imgproc.equalizeHist(src, dst);
//
//        // 将处理后的Mat转换回Bitmap
//        resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(dst, resultBitmap);
//
//        return resultBitmap;
//    }
    //素描滤镜
    Bitmap SuMiao(Bitmap photo){
        Mat SM = new Mat();
        Mat SM1 = new Mat();
        Bitmap sumiaoMap = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap SMB = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap SMB1 = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.bitmapToMat(photo, SM);
        //灰度化
        Imgproc.cvtColor(SM, SM, Imgproc.COLOR_RGB2GRAY);
        //颜色取反
        Core.bitwise_not(SM,SM1);
        //高斯模糊
        Imgproc.GaussianBlur(SM1,SM1,new Size(13,13),0,0);
        Utils.matToBitmap(SM, SMB);
        Utils.matToBitmap(SM1, SMB1);
        for(int i = 0;i<SMB.getWidth();i++){
            for( int j = 0;j<SMB.getHeight();j++){
                int A = SMB.getPixel(i,j);
                int B = SMB1.getPixel(i,j);
                int CR = colordodge(Color.red(A),Color.red(B));
                int CG = colordodge(Color.green(A),Color.red(B));
                int CB = colordodge(Color.blue(A),Color.blue(B));
                sumiaoMap.setPixel(i,j,Color.rgb(CR,CG,CB));
            }
        }
        return sumiaoMap;
    }
    private int colordodge(int colorA, int colorB) {
        float valueA = colorA / 255.0f; // 将颜色分量转换为浮点数（范围：0.0 ~ 1.0）
        float valueB = colorB / 255.0f;

        float result;
        if (valueA == 1.0f || valueB == 0.0f) {
            result = valueA; // 当颜色A完全不透明或颜色B完全透明时，结果为颜色A
        } else {
            result = Math.min(1.0f, valueA / (1.0f - valueB)); // 根据颜色 dodge 公式计算混合结果
        }

        return Math.round(result * 255.0f); // 将结果转换回整数（范围：0 ~ 255）
    }

}
