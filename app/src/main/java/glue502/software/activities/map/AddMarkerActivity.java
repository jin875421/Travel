package glue502.software.activities.map;

import androidx.appcompat.app.AppCompatActivity;
import glue502.software.R;
import glue502.software.models.MarkerIntentInfo;

import android.os.Bundle;
import android.util.Log;

public class AddMarkerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        initData();

    }

    private void initData() {
        //获取MarkerIntentInfo
        if (getIntent().getExtras()!= null) {
            MarkerIntentInfo markerIntentInfo = getIntent().getExtras().getParcelable("MarkerIntentInfo");
            if (markerIntentInfo!= null) {
                Log.v("AddMarkerActivity", "initData: " + markerIntentInfo.toString());

            }
        }
    }
}