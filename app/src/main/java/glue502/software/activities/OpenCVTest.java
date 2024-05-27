package glue502.software.activities;

import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ImageReader;
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
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import glue502.software.R;
import glue502.software.activities.travelRecord.TravelAlbumActivity;
import glue502.software.activities.travelRecord.TravelPicturesActivity;
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
    private Button lianHuanHua,fuDiao,huaiJiu,gaoSi,ink,watercolor,skinsmoothing;
    private ImageView back,save;
    private ImageView imgOrg,imgCha;
    private TextView tvShow;
    private String result="0000";
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

        initBitmap();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.equals("1111")) {
                    // 示例：显示成功消息并结束当前Activity
                    setResult(Activity.RESULT_OK); // 设置结果码为成功
                    finish(); // 结束当前Activity
                } else {
                    finish();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 把bitmap2和placeId传到服务器，相应方法
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
                                System.out.println(responseData+"dsadasdad");
                                // 处理服务器响应，更新UI或执行其他操作
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        result=responseData;
                                        // 处理响应数据
                                        // 这里可以根据服务器的响应结果进行操作，例如显示消息、更新UI等
                                        if(responseData.equals("1111")) {
                                            // 示例：显示成功消息并结束当前Activity
                                            Toast.makeText(OpenCVTest.this, "成功", Toast.LENGTH_SHORT).show();

                                        }else{
                                            Toast.makeText(OpenCVTest.this, "失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(OpenCVTest.this, "图片滤镜添加未完成", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void initBitmap() {
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
                        return applySmokeEffect(bitmap);
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

        ink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // 显示进度条

                // 在后台线程中执行耗时操作1
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        // 在后台执行高斯模糊操作
                        return applyInkEffect(bitmap);
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
        watercolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // 显示进度条

                // 在后台线程中执行耗时操作1
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        // 在后台执行高斯模糊操作
                        return applyWatercolorFilter(bitmap);
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
        skinsmoothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // 显示进度条

                // 在后台线程中执行耗时操作1
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        // 在后台执行高斯模糊操作
                        return applyBrightnessFilter(bitmap,2);
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
    }

    private void updateActivity() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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
        back=findViewById(R.id.img_back);
        save=findViewById(R.id.img_save);
        lianHuanHua=findViewById(R.id.btn_lianhuanhua);
        fuDiao=findViewById(R.id.btn_fudiao);
        huaiJiu=findViewById(R.id.btn_huaijiu);
        gaoSi=findViewById(R.id.btn_gaosi);
        ink=findViewById(R.id.btn_ink);
        watercolor=findViewById(R.id.btn_watercolor);
        skinsmoothing=findViewById(R.id.btn_skinsmoothing);
    }

    private void initOpenCVll() {
        if(OpenCVLoader.initDebug()){
            Toast.makeText(this,"功能初始化成功",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"功能初始化失败",Toast.LENGTH_LONG).show();
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
    public Bitmap changeColour(Bitmap photo){
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(photo, inputMat);

        // 定义双边滤波的参数
        int diameter = 9; // 过滤器的直径
        double sigmaColor = 75; // 颜色空间的标准偏差
        double sigmaSpace = 75; // 坐标空间的标准偏差

        // 应用双边滤波
        Mat filteredMat = new Mat();
        Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2RGB); // 转换颜色空间
        Imgproc.bilateralFilter(inputMat, filteredMat, diameter, sigmaColor, sigmaSpace);

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(filteredMat.cols(), filteredMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(filteredMat, outputBitmap);

        return outputBitmap;
    }
    // 霓虹灯滤镜方法
    public static Bitmap  applyNeonFilter(Bitmap inputBitmap) {
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        // 高斯模糊
        Imgproc.GaussianBlur(inputMat, inputMat, new Size(7, 7), 0);

        // 边缘检测
        Mat edgesMat = new Mat();
        Imgproc.Canny(inputMat, edgesMat, 10, 100);

        // 确保edgesMat的尺寸与inputMat相同
        Mat resizedEdgesMat = new Mat();
        Imgproc.resize(edgesMat, resizedEdgesMat, inputMat.size());

        // 霓虹灯效果：颜色映射和亮度调整
        Mat neonMat = new Mat();
        Core.add(inputMat, new Scalar(100, 100, 100), neonMat);
        Core.subtract(neonMat, resizedEdgesMat, neonMat);

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(neonMat.cols(), neonMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(neonMat, outputBitmap);

        return outputBitmap;
    }
    // 卡通效果滤镜方法
    public static Bitmap applyCartoonEffect(Bitmap inputBitmap) {
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        // 创建空白的输出图像
        Mat outputMat = new Mat(inputMat.size(), CvType.CV_8UC3);

        // 应用双边滤波以平滑图像并保留边缘
        Mat smoothMat = new Mat();
        Imgproc.cvtColor(inputMat, smoothMat, Imgproc.COLOR_RGBA2RGB);
        Imgproc.bilateralFilter(smoothMat, outputMat, 9, 75, 75);

        // 将图像转换为灰度图像
        Mat grayMat = new Mat();
        Imgproc.cvtColor(outputMat, grayMat, Imgproc.COLOR_RGB2GRAY);

        // 检测图像的边缘
        Mat edgesMat = new Mat();
        Imgproc.adaptiveThreshold(grayMat, edgesMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 2);

        // 通过膨胀边缘图像来增强边缘
        Imgproc.cvtColor(edgesMat, edgesMat, Imgproc.COLOR_GRAY2RGB);
        Mat dilatedEdgesMat = new Mat();
        Imgproc.cvtColor(edgesMat, edgesMat, Imgproc.COLOR_RGB2RGBA);
        Imgproc.medianBlur(edgesMat, dilatedEdgesMat, 7);

        // 合并原始图像和边缘图像以产生卡通效果
        Mat cartoonMat = new Mat();
        Core.addWeighted(inputMat, 0.9, dilatedEdgesMat, 0.1, 0, cartoonMat);

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(cartoonMat.cols(), cartoonMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cartoonMat, outputBitmap);

        return outputBitmap;
    }
    // 水墨效果滤镜方法
    public static Bitmap applyInkEffect(Bitmap inputBitmap) {
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        // 创建空白的输出图像
        Mat outputMat = new Mat(inputMat.size(), inputMat.type());

        // 将图像转换为灰度图像
        Mat grayMat = new Mat();
        Imgproc.cvtColor(inputMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        // 应用自适应阈值化以获取水墨效果
        Imgproc.adaptiveThreshold(grayMat, outputMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 4);

        // 应用中值模糊以模糊水墨效果
        Imgproc.medianBlur(outputMat, outputMat, 7);

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(outputMat.cols(), outputMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, outputBitmap);

        return outputBitmap;
    }
    // 烟雾效果滤镜方法
    public static Bitmap applySmokeEffect(Bitmap inputBitmap) {
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        // 创建空白的输出图像
        Mat outputMat = new Mat(inputMat.size(), inputMat.type());

        // 应用高斯模糊以模糊图像
        Imgproc.GaussianBlur(inputMat, outputMat, new Size(35, 35), 0);

        // 调整透明度以模拟烟雾效果
        for (int y = 0; y < outputMat.rows(); y++) {
            for (int x = 0; x < outputMat.cols(); x++) {
                double[] pixel = outputMat.get(y, x);
                pixel[3] = 150; // 设置 alpha 通道值
                outputMat.put(y, x, pixel);
            }
        }

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(outputMat.cols(), outputMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, outputBitmap);

        return outputBitmap;
    }
    // 水彩效果滤镜方法
    public static Bitmap applyWatercolorFilter(Bitmap inputBitmap) {
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        // 创建空白的输出图像
        Mat outputMat = Mat.zeros(inputMat.size(), CvType.CV_8UC3);

        // 模糊处理
        Imgproc.GaussianBlur(inputMat, outputMat, new org.opencv.core.Size(9, 9), 0);

        // 增加颜色偏移
        for (int y = 0; y < inputMat.rows(); y++) {
            for (int x = 0; x < inputMat.cols(); x++) {
                double[] pixel = inputMat.get(y, x);
                double[] outputPixel = outputMat.get(y, x);
                for (int c = 0; c < inputMat.channels(); c++) {
                    outputPixel[c] += (Math.random() - 0.5) * 50; // 随机增加颜色偏移
                    outputPixel[c] = Math.max(0, Math.min(255, outputPixel[c])); // 确保颜色值在0到255之间
                }
                outputMat.put(y, x, outputPixel);
            }
        }

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(outputMat.cols(), outputMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, outputBitmap);

        return outputBitmap;
    }
    // 磨皮滤镜方法
    public static Bitmap applySkinSmoothingFilter(Bitmap inputBitmap) {
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        // 将图像转换为BGR格式（如果输入图像是ARGB格式）
        if (inputMat.channels() == 4) {
            Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_RGBA2BGR);
        }

        // 高斯模糊
        Imgproc.GaussianBlur(inputMat, inputMat, new Size(5, 5), 0);

        // 双边滤波
        Mat bilateralFilteredMat = new Mat();
        Imgproc.bilateralFilter(inputMat, bilateralFilteredMat, 15, 80, 80);

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(bilateralFilteredMat.cols(), bilateralFilteredMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(bilateralFilteredMat, outputBitmap);

        return outputBitmap;
    }
    // 光照滤镜方法
    public static Bitmap applyBrightnessFilter(Bitmap inputBitmap, float brightnessFactor) {
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);

        // 增加亮度
        inputMat.convertTo(inputMat, -1, brightnessFactor, 0);

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(inputMat.cols(), inputMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(inputMat, outputBitmap);

        return outputBitmap;
    }
    public Bitmap doubleLJ(Bitmap photo) {
        // 将Android Bitmap对象转换为OpenCV Mat对象
        Mat inputMat = new Mat();
        Utils.bitmapToMat(photo, inputMat);

        // 定义双边滤波的参数
        int diameter = 9; // 过滤器的直径
        double sigmaColor = 75; // 颜色空间的标准偏差
        double sigmaSpace = 75; // 坐标空间的标准偏差

        // 应用双边滤波
        Mat filteredMat = new Mat();
        Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2RGB); // 转换颜色空间
        Imgproc.bilateralFilter(inputMat, filteredMat, diameter, sigmaColor, sigmaSpace);

        // 将处理后的Mat对象转换回Android Bitmap对象
        Bitmap outputBitmap = Bitmap.createBitmap(filteredMat.cols(), filteredMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(filteredMat, outputBitmap);

        return outputBitmap;
    }

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
