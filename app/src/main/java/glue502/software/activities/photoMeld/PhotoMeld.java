package glue502.software.activities.photoMeld;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import glue502.software.R;
import glue502.software.utils.MyViewUtils;
import glue502.software.utils.bigImgUtils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoMeld extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE1 = 1;
    private static final int REQUEST_CODE_PICK_IMAGE2 = 2;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageViewBlended;
    private ImageView imgBack;
    private String outputImagePath;
    private Uri imageUri1;
    private Uri imageUri2;
    private Button buttonBlendImages, buttonSelectImage1, buttonSelectImage2,btnSave;
    private String uploadUrl="http://"+ip+"/travel/pictureEdit/uploadPicture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_meld);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageViewBlended = findViewById(R.id.imageViewBlended);
        imgBack=findViewById(R.id.img_back);
        buttonSelectImage1 = findViewById(R.id.buttonSelectImage1);
        buttonSelectImage2 = findViewById(R.id.buttonSelectImage2);
        buttonBlendImages = findViewById(R.id.buttonBlendImages);
        btnSave=findViewById(R.id.btn_save);
        MyViewUtils.setImmersiveStatusBar(this,findViewById(R.id.lrlt_photo_meld),true);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputFile = new File(outputImagePath);
                if(outputFile.exists()) {
                    uploadPhoto(outputFile);
                } else {
                    Toast.makeText(PhotoMeld.this, "输出文件不存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonSelectImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(REQUEST_CODE_PICK_IMAGE1);
            }
        });

        buttonSelectImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(REQUEST_CODE_PICK_IMAGE2);
            }
        });

        buttonBlendImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null && imageUri2 != null) {
                    blendAndUploadImages();
                } else {
                    Log.e("PhotoMeld", "Please select both images first");
                }
            }
        });
        imageView1.setOnClickListener(v->{
                OpenImage.with(this).setClickImageView(imageView1)
                        .setAutoScrollScanPosition(true)
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                        .addPageTransformer(new ScaleInTransformer())
                        .setImageUrl(String.valueOf(imageUri1), com.flyjingfish.openimagelib.enums.MediaType.IMAGE)
//                    .setOnItemLongClickListener(new OnItemLongClickListener() {
//                        @Override
//                        public void onItemLongClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
//                            Toast.makeText(getContext(),"长按图片",Toast.LENGTH_LONG).show();
//                        }
//                    })
                        .show();
            });
        imageView2.setOnClickListener(v->{
            OpenImage.with(this).setClickImageView(imageView2)
                    .setAutoScrollScanPosition(true)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .addPageTransformer(new ScaleInTransformer())
                    .setImageUrl(String.valueOf(imageUri2), com.flyjingfish.openimagelib.enums.MediaType.IMAGE)
//                    .setOnItemLongClickListener(new OnItemLongClickListener() {
//                        @Override
//                        public void onItemLongClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
//                            Toast.makeText(getContext(),"长按图片",Toast.LENGTH_LONG).show();
//                        }
//                    })
                    .show();
        });
        imageViewBlended.setOnClickListener(v->{
            OpenImage.with(this).setClickImageView(imageViewBlended)
                    .setAutoScrollScanPosition(true)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .addPageTransformer(new ScaleInTransformer())
                    .setShowDownload()
                    .setImageUrl(String.valueOf(outputImagePath), com.flyjingfish.openimagelib.enums.MediaType.IMAGE)
//                    .setOnItemLongClickListener(new OnItemLongClickListener() {
//                        @Override
//                        public void onItemLongClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
//                            Toast.makeText(getContext(),"长按图片",Toast.LENGTH_LONG).show();
//                        }
//                    })
                    .show();
        });
        initOpenCVll();
    }
    //获取图片路径
    private String createBitmapFilePath(Bitmap bitmap, int type) {
        String imageUrl1 = "";
        // 获取文件输出流
        String fileName = type+".jpg";
        FileOutputStream outStream = null;
        File imageFile = null;
        try {
            // 创建一个临时文件
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            imageFile = new File(storageDir, fileName);

            // 将Bitmap保存为JPEG文件
            outStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

// 如果成功保存，创建URL
        if (imageFile.exists()) {
            Uri imageUri = Uri.fromFile(imageFile);
            imageUrl1 = "file://" + imageUri.toString();
        }
        return imageUrl1;
    }
    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }
    private void initOpenCVll() {
        if(OpenCVLoader.initDebug()){
            Toast.makeText(this,"功能初始化成功",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"功能初始化失败",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                if (requestCode == REQUEST_CODE_PICK_IMAGE1) {
                    imageUri1 = selectedImageUri;
                    imageView1.setImageURI(imageUri1);
                } else if (requestCode == REQUEST_CODE_PICK_IMAGE2) {
                    imageUri2 = selectedImageUri;
                    imageView2.setImageURI(imageUri2);
                }
            }
        }
    }

    private void blendAndUploadImages() {
        // 获取图片路径
        String image1Path = getPathFromUri(imageUri1);
        String image2Path = getPathFromUri(imageUri2);

        // 生成输出路径
        outputImagePath = getExternalFilesDir(null) + "/" + UUID.randomUUID().toString() + ".jpg";

        // 融合图片
        blendImages(image1Path, image2Path, outputImagePath);

        // 显示融合后的图片
        imageViewBlended.setImageURI(Uri.fromFile(new File(outputImagePath)));

//
    }

    private String getPathFromUri(Uri uri) {
        // 获取文件路径的方法，这里可以根据具体实现进行调整
        return FileUtils.getPath(this, uri);
    }


    public void blendImages(String image1Path, String image2Path, String outputImagePath) {
        // 读取第一张图片
        Mat image1 = Imgcodecs.imread(image1Path);
        if (image1.empty()) {
            throw new RuntimeException("Failed to load image: " + image1Path);
        }

        // 读取第二张图片
        Mat image2 = Imgcodecs.imread(image2Path);
        if (image2.empty()) {
            throw new RuntimeException("Failed to load image: " + image2Path);
        }

        // 确保两张图片尺寸相同，如果不相同，可以调整较小图片的大小以匹配较大图片
        Size size = image1.size();
        if (!size.equals(image2.size())) {
            Imgproc.resize(image2, image2, size);
        }

        // 图像对齐（可以使用特征匹配算法，如ORB、SURF等，这里省略具体实现）
        // image1 = alignImages(image1, image2);

        // 初始化结果矩阵
        Mat blendedImage = new Mat(size, image1.type());

        // 使用动态alpha值进行融合，可以根据需要进行调整
        double alpha = 0.3;
        double beta = 1 - alpha;

        // 使用不同的混合模式（这里示例为简单加权）
//        Core.addWeighted(image1, alpha, image2, beta, 0, blendedImage);

        // 可选：使用掩膜进行选择性混合
         Mat mask = createBlendMask(size);
         Core.addWeighted(image1, alpha, image2, beta, 0, blendedImage, mask);

        // 图像增强
        enhanceImage(blendedImage);

        // 保存融合后的图片
        Imgcodecs.imwrite(outputImagePath, blendedImage);

        Log.d("PhotoMeld", "Blended image saved to: " + outputImagePath);
    }

    // 示例：图像增强方法
    private void enhanceImage(Mat image) {
        // 调整亮度和对比度
        image.convertTo(image, -1, 1.2, 20);

    }

    // 示例：创建融合掩膜
    private Mat createBlendMask(Size size) {
        Mat mask = new Mat(size, CvType.CV_8UC1, new Scalar(0));
        Imgproc.rectangle(mask, new Point(50, 50), new Point(size.width - 50, size.height - 50), new Scalar(255), -1);
        return mask;
    }

    private void uploadPhoto(File photo) {
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String filename = UUID.randomUUID().toString();
        // 构建Multipart请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .addFormDataPart("file", filename+".jpg", RequestBody.create(MediaType.parse("image/*"),photo ))
                .build();

        // 构建POST请求
        Request request = new Request.Builder()
                .url(uploadUrl)
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
                            if(response.isSuccessful()) {
                                Toast.makeText(PhotoMeld.this, "保存成功", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(PhotoMeld.this, "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
