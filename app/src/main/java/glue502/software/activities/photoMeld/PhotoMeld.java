package glue502.software.activities.photoMeld;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import glue502.software.R;
import glue502.software.utils.bigImgUtils.FileUtils;

public class PhotoMeld extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE1 = 1;
    private static final int REQUEST_CODE_PICK_IMAGE2 = 2;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageViewBlended;

    private Uri imageUri1;
    private Uri imageUri2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_meld);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageViewBlended = findViewById(R.id.imageViewBlended);

        Button buttonSelectImage1 = findViewById(R.id.buttonSelectImage1);
        Button buttonSelectImage2 = findViewById(R.id.buttonSelectImage2);
        Button buttonBlendImages = findViewById(R.id.buttonBlendImages);

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
        initOpenCVll();
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
        String outputImagePath = getExternalFilesDir(null) + "/" + UUID.randomUUID().toString() + ".jpg";

        // 融合图片
        blendImages(image1Path, image2Path, outputImagePath);

        // 显示融合后的图片
        imageViewBlended.setImageURI(Uri.fromFile(new File(outputImagePath)));

//        // 上传融合后的图片到服务器
//        uploadImageToServer(outputImagePath);
    }

    private String getPathFromUri(Uri uri) {
        // 获取文件路径的方法，这里可以根据具体实现进行调整
        return FileUtils.getPath(this, uri);
    }

    private void uploadImageToServer(String imagePath) {
        // 实现图片上传到服务器的方法
        // 这里可以使用任何网络库，例如 Retrofit、OkHttp 等
        // 伪代码示例：
        // uploadFile(new File(imagePath));
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

        // 初始化结果矩阵
        Mat blendedImage = new Mat(size, image1.type());

        // 使用alpha值进行融合，这里假设alpha=0.5，你可以根据需要调整
        double alpha = 0.5;
        double beta = 1 - alpha;

        // 将两张图片融合
        Core.addWeighted(image1, alpha, image2, beta, 0, blendedImage);

        // 保存融合后的图片
        Imgcodecs.imwrite(outputImagePath, blendedImage);

        Log.d("PhotoMeld", "Blended image saved to: " + outputImagePath);
    }
}
