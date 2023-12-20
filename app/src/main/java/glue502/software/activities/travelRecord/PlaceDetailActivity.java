package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import glue502.software.R;
import glue502.software.models.travelRecord;
import glue502.software.utils.Carousel;
import glue502.software.utils.MyViewUtils;

public class PlaceDetailActivity extends AppCompatActivity {
    private travelRecord travelRecord;
    private TextView title,content;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView());
        initView();
        setlistener();
        initData();
    }
    public void initView() {
        back = findViewById(R.id.btn_back);
        //设置标题
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
    }
    public void initData() {
        //获取传递过来的TravelRecord
        if (getIntent().getSerializableExtra("travelRecord")!= null) {
            travelRecord = (travelRecord) getIntent().getSerializableExtra("travelRecord");
            //设置图片
            Carousel carousel = new Carousel(this, findViewById(R.id.index_dot), findViewById(R.id.post_image));
            carousel.initViews(travelRecord.getImage());
            title.setText(travelRecord.getPlaceName());
            content.setText(travelRecord.getContent());
        }
    }
    public void setlistener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}