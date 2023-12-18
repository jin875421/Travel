package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;
import glue502.software.models.TravelReview;

public class TravelReviewAdapter extends BaseAdapter {
    private Context context;
    private int layoutId;
    private List<TravelReview> travelReview;
    private String url = "http://"+ip+"/travel";

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
        TextView travelName = v.findViewById(R.id.travel_name);
        //给滚动图添加图片
        if (travelReview.get(i).getImages().size() > 0) {
            LinearLayout imagecontainer = v.findViewById(R.id.imageContainer);
            for (String path : travelReview.get(i).getImages()) {
                ImageView imageView = new ImageView(context);
                // 通过服务器地址设置图片
                Glide.with(context).load(url + path).into(imageView);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                imageView.setLayoutParams(params);
                imagecontainer.addView(imageView);
            }
        }
        return v;
    }
}
