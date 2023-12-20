package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.PicturesAdapter;


//这个界面用于相册功能，用于显示一个地点的所有照片
public class PictureShowActivity extends AppCompatActivity {

    GridView gvPictureShow;
    //把数据源中的照片都展示出来，为此要再写一个适配器
    Intent intent = getIntent();
    List<String> pictures = intent.getStringArrayListExtra("pictures");//这里报错了，显示空指针


//    SharedPreferences sharedPreferences = getSharedPreferences("Pictures", MODE_PRIVATE);
//    String json = sharedPreferences.getString("pictures", "");
//    Gson gson = new Gson();
//    Type type = new TypeToken<List<String>>() {}.getType();
//    List<String> pictures = gson.fromJson(json, type);

    private PicturesAdapter picturesAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_show);

        //绑定控件对象
        gvPictureShow = findViewById(R.id.gv_show_picture);

        //在这里通过适配器将图片数据显示在对应控件中
        picturesAdapter = new PicturesAdapter(pictures,this,R.layout.picture_show);

        gvPictureShow.setAdapter(picturesAdapter);


        //这就没了？？？？？？？？？？？？？？


    }
}