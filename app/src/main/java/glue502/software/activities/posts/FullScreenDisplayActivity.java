package glue502.software.activities.posts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.List;

import glue502.software.R;
import glue502.software.utils.FullScreenCarousel;

public class FullScreenDisplayActivity extends AppCompatActivity {
    private LinearLayout dotLinerLayout;
    private ViewPager2 image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_display);
        image = findViewById(R.id.post_image);
        dotLinerLayout = findViewById(R.id.index_dot);
        List<String> images = getIntent().getStringArrayListExtra("images");
        int position = Integer.parseInt(getIntent().getStringExtra("position"));
        FullScreenCarousel carousel = new FullScreenCarousel(this.getApplicationContext(),dotLinerLayout,image);
        carousel.initViews(images,position);
    }
}