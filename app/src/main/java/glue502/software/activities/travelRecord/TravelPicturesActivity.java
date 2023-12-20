package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.GridView;

import glue502.software.R;


//这个页面是一个用于展示具体地点照片的页面
public class TravelPicturesActivity extends AppCompatActivity {

    private GridView gvPictures;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_pictures);

        //获取控件对象
        gvPictures = findViewById(R.id.gv_pictures);



    }
}