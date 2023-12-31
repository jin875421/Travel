package glue502.software.adapters;
import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import glue502.software.R;
import glue502.software.activities.travelRecord.PlaceDetailActivity;
import glue502.software.activities.travelRecord.TravelDetailActivity;
import glue502.software.activities.travelRecord.TravelReviewActivity;
import glue502.software.models.TravelReview;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TravelReviewAdapter extends BaseAdapter {
    private Context context;
    private int layoutId;
    private List<TravelReview> travelReview;
    private String url = "http://"+ip+"/travel/";

    public TravelReviewAdapter(Context context,List<TravelReview> travelReview,int layoutId) {
        this.context = context;
        this.travelReview = travelReview;
        this.layoutId = layoutId;
    }
    @Override
    public int getCount() {
        return travelReview.size();
    }

    @Override
    public Object getItem(int i) {
        return travelReview.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(layoutId, null);

        //这是作为背景的照片控件
        ImageView ivBackground = v.findViewById(R.id.iv_background);

        TextView travelName = v.findViewById(R.id.travel_name);
//        LinearLayout llReview = v.findViewById(R.id.ll_review);
        travelName.setText(travelReview.get(i).getTravelName());
        // 在getView方法中的适配器中
        HorizontalScrollView horizontalScrollView = v.findViewById(R.id.image_scroll);
        // 记录按下的位置和时间戳
        float startX, startY;
        long downTime;

//        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
//            // 记录按下的位置和时间戳
//            float startX, startY;
//            long downTime;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        // 记录按下时的位置和时间戳
//                        startX = event.getX();
//                        startY = event.getY();
//                        downTime = System.currentTimeMillis();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        // 在移动事件中可能进行一些判断，但不做处理
//                        float moveX = event.getX();
//                        float moveY = event.getY();
//                        float deltaX = startX - moveX;
//                        float deltaY = startY - moveY;
//
//                        // 判断是水平滚动还是垂直滚动
//                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
//                            // 水平滚动
//                            horizontalScrollView.smoothScrollBy((int) deltaX, 0);
//                        } else {
//                            // 垂直滚动
//                            // 这里可以根据需要处理垂直滚动的逻辑
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        // 计算时间和位置的差异
//                        float endX = event.getX();
//                        float endY = event.getY();
//                        float distanceX = Math.abs(endX - startX);
//                        float distanceY = Math.abs(endY - startY);
//                        long upTime = System.currentTimeMillis();
//                        long clickDuration = upTime - downTime;
//
//                        // 设置阈值来判断点击或滚动
//                        float touchSlop = 10; // 可以根据实际情况调整阈值
//                        long clickThreshold = 100; // 点击时间阈值
//
//                        if (distanceX < touchSlop && distanceY < touchSlop && clickDuration < clickThreshold) {
//                            // 执行跳转至详情页面
//                            Intent intent = new Intent(context, TravelDetailActivity.class);
//                            intent.putExtra("travelId",travelReview.get(i).getTravelId());
//                            context.startActivity(intent);
//                        }
//                        break;
//                }
//                return true; // 返回true以消费触摸事件
//            }
//        });
        travelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行跳转至详情页面
                Intent intent = new Intent(context, TravelDetailActivity.class);
                intent.putExtra("travelId",travelReview.get(i).getTravelId());
                context.startActivity(intent);
            }
        });
        //给滚动图添加图片
//        if (travelReview.get(i).getImages().size() > 0) {
//            LinearLayout imagecontainer = v.findViewById(R.id.image_container);
//            for (String path : travelReview.get(i).getImages()) {
//                ImageView imageView = new ImageView(context);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                MultiTransformation mation5 = new MultiTransformation(
//                        new CenterCrop(),
//                        new RoundedCornersTransformation(20,0,RoundedCornersTransformation.CornerType.ALL)
//                );
//                // 通过服务器地址设置图片
//                Glide.with(context).load(url + path).apply(RequestOptions.bitmapTransform(mation5)).into(imageView);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(convertDpToPixel(130), convertDpToPixel(130));
//                params.setMargins(0, 0, convertDpToPixel(4), 0);
//                imageView.setLayoutParams(params);
//                imagecontainer.addView(imageView);
//            }
//        }

        //为整个item添加背景
//        Glide.with(context).load(url + travelReview.get(i).getImages().get(0))
////                .apply(RequestOptions.bitmapTransform(mation5))
//                .centerCrop()
////                .fitCenter()
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        llReview.setBackground(resource);
//                    }
//                });

        //为作为背景的图片添加背景
        int width = ivBackground.getWidth(); // 获取控件的宽度
        int height = ivBackground.getHeight(); // 获取控件的高度
        Glide.with(context)
                .load(url + travelReview.get(i).getImages().get(0))
                .transform(new CenterCrop(), new RoundedCorners(20)) // 先截取指定比例再设置圆角
                .override(width, height) // 设置加载图片的尺寸
                .into(ivBackground);

        ivBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行跳转至详情页面
                Intent intent = new Intent(context, TravelDetailActivity.class);
                intent.putExtra("travelId",travelReview.get(i).getTravelId());
                context.startActivity(intent);
            }
        });

        return v;
    }
    private int convertDpToPixel(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
