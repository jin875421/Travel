package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import glue502.software.R;

public class travelReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_review);
        SharedPreferences sharedPreferences = getSharedPreferences("userId",MODE_PRIVATE);

    }
}