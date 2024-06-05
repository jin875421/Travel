package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.PERMISSION_REQUEST_CODE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import glue502.software.R;
import glue502.software.activities.AI.ChatActivity;
import glue502.software.activities.AI.ImageRecognitionActivityA;
import glue502.software.activities.AI.MainWenXinActivity;
import glue502.software.activities.AI.SpeechTest;
import glue502.software.activities.photoMeld.PhotoMeld;
import glue502.software.activities.posts.UploadPostActivity;
import glue502.software.utils.MyViewUtils;

public class FunctionActivity extends AppCompatActivity {
    private RelativeLayout todolist,expenserecord, AI_1, AI_5, speechBtn, wenXinBtn,photoMeld;
    private ImageView back;
    private RelativeLayout top;
    private String mCurrentPhotoPath;
    private final int RESULT_LOAD_IMAGES = 1, RESULT_CAMERA_IMAGE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        init();
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        setListener();

    }
    private void init(){
        todolist = findViewById(R.id.todolist);
        back = findViewById(R.id.back);
        top = findViewById(R.id.top);
        expenserecord = findViewById(R.id.expense_record);
        AI_1 = findViewById(R.id.AI_1);
        AI_5 = findViewById(R.id.AI_5);
        currancyexchange = findViewById(R.id.currancy_exchange);
        translate = findViewById(R.id.translate);
        speechBtn = findViewById(R.id.speechBtn);
        wenXinBtn = findViewById(R.id.wenxin);
        photoMeld = findViewById(R.id.rltl_photo_meld);
    }
    private void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        photoMeld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        todolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, TodolistActivity.class);
                startActivity(intent);
            }
        });
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, TranslateActivity.class);
                startActivity(intent);
            }
        });
        currancyexchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, CurrencyExchangeActivity.class);
                startActivity(intent);
            }
        });
        expenserecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, ExpenseRecordActivity.class);
                startActivity(intent);
            }
        });
        AI_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                Intent intent = new Intent(FunctionActivity.this, MainWenXinActivity.class);

                int sign = 1;
                intent.putExtra("sign",sign);

                startActivity(intent);
            }
        });
        AI_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                Intent intent = new Intent(FunctionActivity.this, ImageRecognitionActivityA.class);

                int sign = 5;
                intent.putExtra("sign",sign);

                startActivity(intent);
            }
        });
        speechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, SpeechTest.class);
                startActivity(intent);
            }
        });
        wenXinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        photoMeld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FunctionActivity.this, PhotoMeld.class);
                startActivity(intent);
            }
        });
    }

}