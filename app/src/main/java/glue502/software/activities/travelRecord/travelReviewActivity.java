package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

import glue502.software.R;

public class travelReviewActivity extends AppCompatActivity {
    private MapView mvMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_review);
        SharedPreferences sharedPreferences = getSharedPreferences("userId",MODE_PRIVATE);
        SDKInitializer.initialize(getApplicationContext());
        mvMap = findViewById(R.id.bmapView);
        // 获取地图控件引用

    }
    @Override
    protected void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvMap.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mvMap.onPause();
    }

}