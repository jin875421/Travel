package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.travelRecord.PlaceDetailActivity;
import glue502.software.activities.travelRecord.TravelDetailActivity;
import glue502.software.models.travelRecord;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TravelDetailAdapter extends BaseAdapter {
    private Context context;
    private List<travelRecord> travelRecords;
    private String url = "http://"+ip+"/travel/";
    private int layoutId;
    public TravelDetailAdapter(Context context, List<travelRecord> travelRecords, int layoutId) {
        this.context = context;
        this.travelRecords = travelRecords;
        this.layoutId = layoutId;
    }
    @Override
    public int getCount() {
        return travelRecords.size();
    }

    @Override
    public Object getItem(int i) {
        return travelRecords.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(layoutId,null);
        TextView travelName = v.findViewById(R.id.travel_place);
        LinearLayout imageContainer = v.findViewById(R.id.image_container);
        HorizontalScrollView horizontalScrollView = v.findViewById(R.id.scrollView);
        v.setBackgroundResource(R.drawable.border_backgrounddjpjpjp);
        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            // 记录按下的位置和时间戳
            float startX, startY;
            long downTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // 记录按下时的位置和时间戳
                        startX = event.getX();
                        startY = event.getY();
                        downTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 在移动事件中可能进行一些判断，但不做处理
                        float moveX = event.getX();
                        float moveY = event.getY();
                        float deltaX = startX - moveX;
                        float deltaY = startY - moveY;

                        // 判断是水平滚动还是垂直滚动
                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            // 水平滚动
                            horizontalScrollView.smoothScrollBy((int) deltaX, 0);
                        } else {
                            // 垂直滚动
                            // 这里可以根据需要处理垂直滚动的逻辑
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // 计算时间和位置的差异
                        float endX = event.getX();
                        float endY = event.getY();
                        float distanceX = Math.abs(endX - startX);
                        float distanceY = Math.abs(endY - startY);
                        long upTime = System.currentTimeMillis();
                        long clickDuration = upTime - downTime;

                        // 设置阈值来判断点击或滚动
                        float touchSlop = 10; // 可以根据实际情况调整阈值
                        long clickThreshold = 100; // 点击时间阈值

                        if (distanceX < touchSlop && distanceY < touchSlop && clickDuration < clickThreshold) {
                            // 执行跳转至详情页面
                            Intent intent = new Intent(context, PlaceDetailActivity .class);
                            //传递travelRecord
                            intent.putExtra("travelRecord", travelRecords.get(i));
                            context.startActivity(intent);
                        }
                        break;
                }
                return true; // 返回true以消费触摸事件
            }
        });
        TextView content = v.findViewById(R.id.content);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行跳转至详情页面
                Intent intent = new Intent(context, PlaceDetailActivity .class);
                //传递travelRecord
                intent.putExtra("travelRecord", travelRecords.get(i));
                context.startActivity(intent);
            }
        });
        travelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行跳转至详情页面
                Intent intent = new Intent(context, PlaceDetailActivity.class);
                //传递travelRecord
                intent.putExtra("travelRecord", travelRecords.get(i));
                context.startActivity(intent);
            }
        });
        travelName.setText(travelRecords.get(i).getPlaceName());
        content.setText(travelRecords.get(i).getContent());
        for(String path:travelRecords.get(i).getImage()){
            if(path!=null){
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(convertDpToPixel(120), convertDpToPixel(120));
                layoutParams.setMargins(convertDpToPixel(4), 0, 0, 0); // 左边距为4dp
                imageView.setLayoutParams(layoutParams);
                MultiTransformation mation5 = new MultiTransformation(
                        new CenterCrop(),
                        new RoundedCornersTransformation(20,0,RoundedCornersTransformation.CornerType.ALL)
                );
                Glide.with(context)
                        .load(url + path)
                        .apply(RequestOptions.bitmapTransform(mation5))
                        .into(imageView);
                imageContainer.addView(imageView);
            }
        }
        return v;
    }
    private int convertDpToPixel(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
