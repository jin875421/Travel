package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.recoAttraction.AttractionDetailActivity;
import glue502.software.models.RecoAttraction;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class AttractionAdapter extends BaseAdapter {
    List<RecoAttraction> attractionList = new ArrayList<>();
    Context context;
    private int layoutId;
    private String url = "http://"+ip+"/travel/";


    public AttractionAdapter(Context context, List<RecoAttraction> attractionList, int layoutId) {
        this.context = context;
        this.attractionList = attractionList;
        this.layoutId = layoutId;
    }
    @Override
    public int getCount() {
        return attractionList.size();
    }

    @Override
    public Object getItem(int position) {
        return attractionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(layoutId,null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetail = new Intent(context, AttractionDetailActivity.class);
                String attractionStr = new Gson().toJson(attractionList.get(position % attractionList.size()));
                toDetail.putExtra("type", 1);
                toDetail.putExtra("attractionStr", attractionStr);
                context.startActivity(toDetail);
            }
        });
        TextView tv = v.findViewById(R.id.textViewTitle);
        LinearLayout l = v.findViewById(R.id.image_container);
        tv.setText(attractionList.get(position).getAttractionName());
        if(attractionList.get(position).getImgUrls().size() > 0){
            List<String> imagePaths = attractionList.get(position).getImgUrls();
            //分离地址并获取
            int x = 0;
            for (String path : imagePaths) {
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //设定宽度为屏幕大小的1/3
                int width = (int) ((context.getResources().getDisplayMetrics().widthPixels - convertDpToPixel(32) - convertDpToPixel(12)) / 3);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
                layoutParams.setMargins(0, 0, convertDpToPixel(4), 0); // 右边距为4dp
                RequestOptions requestOption = new RequestOptions();
                requestOption.placeholder(R.mipmap.loading);
                requestOption.circleCropTransform();
                requestOption.transform(new RoundedCorners(30));
                MultiTransformation mation5 = new MultiTransformation(
                        new CenterCrop(),
                        new RoundedCornersTransformation(20,0,RoundedCornersTransformation.CornerType.ALL)
                );
                imageView.setLayoutParams(layoutParams);
                Glide.with(context)
                        .load(url + path)
                        .apply(requestOption)
                        .apply(RequestOptions.bitmapTransform(mation5))
                        .into(imageView);
                l.addView(imageView);
                x++;
                if(x==3){
                    break;
                }
            }
        }
        return v;
    }
    private int convertDpToPixel(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
