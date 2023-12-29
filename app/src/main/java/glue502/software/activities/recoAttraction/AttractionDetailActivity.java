package glue502.software.activities.recoAttraction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.MainActivity;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.models.RecoAttraction;
import glue502.software.utils.Carousel;
import glue502.software.utils.MyViewUtils;

public class AttractionDetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txAttractionName, txAttractionAddress, txAttractionDesc;
    ViewPager2 vp2;
    LinearLayout dotLinerLayout;
    RecoAttraction attraction;
    String attractionStr;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);
        //添加沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);
        attractionStr = getIntent().getStringExtra("attractionStr");
        attraction = new Gson().fromJson(attractionStr, RecoAttraction.class);
        getViews();
        setListener();
        initData();
    }

    private void initData() {
        Carousel carousel = new Carousel(AttractionDetailActivity.this, dotLinerLayout, vp2, "");
        carousel.initViews(attraction.getImgUrls());
        txAttractionName.setText(attraction.getAttractionName());
        txAttractionAddress.setText(attraction.getCountry() + " " + attraction.getProvince() + " " + attraction.getCity() + " " + attraction.getAddress());
        txAttractionDesc.setText(attraction.getAttractionDesc());
    }

    private void getViews() {
        txAttractionName = findViewById(R.id.attraction_name);
        txAttractionAddress = findViewById(R.id.attraction_address);
        txAttractionDesc = findViewById(R.id.attraction_desc);
        dotLinerLayout = findViewById(R.id.index_dot);
        vp2 = findViewById(R.id.vp2);
        back = findViewById(R.id.ad_back);
    }

    private void setListener() {
        back.setOnClickListener(this);
        txAttractionAddress.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ad_back:
                finish();
                break;
            case R.id.attraction_address:
                Intent toMap = new Intent(AttractionDetailActivity.this, MainActivity.class);
                toMap.putExtra("lat", attraction.getLatitude());
                toMap.putExtra("lng", attraction.getLongitude());
                toMap.putExtra("id",2);
                startActivity(toMap);
        }
    }
}