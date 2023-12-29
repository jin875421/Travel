package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.travelRecord.TravelPicturesActivity;
import glue502.software.activities.travelRecord.TravelReviewActivity;
import glue502.software.models.ShowPicture;

public class TravelAlbumAdapter extends BaseAdapter {

    private List<ShowPicture> list;//数据源

    //用于测试的数据源
//    private List<String> list;
    private Context context;//上下文环境
    private int layout;//要填充的页面布局
    private String url = "http://"+ip+"/travel/";
    public TravelAlbumAdapter() {
    }

    public TravelAlbumAdapter(List<ShowPicture> list, Context context, int layout) {
        this.list = list;
        this.context = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        //首先获取页面布局
        view = LayoutInflater.from(context).inflate(layout,null);

        //获取控件对象
        ImageView ivAlbum = view.findViewById(R.id.iv_album);

        ShowPicture sp = list.get(i);
        String picturePath = sp.getPicturePath().get(0);
//        String picturePath = list.get(i);
        //将地点写上去
        TextView tvPlace = view.findViewById(R.id.tv_picture);
        //在这里用于改变字体格式
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/幼圆.TTF");
//        tvPlace.setTypeface(typeface);
        //在这里用于改变字体背景，让字体背景变透明
//        tvPlace.getBackground().setAlpha(10);
        tvPlace.setText(sp.getPlaceName());
//        System.out.println(picturePath);
        //显示图片
//        System.out.println(url+picturePath);
        //设定图片宽高
        int width = 110;
        int height = 110;
        Glide.with(context)
                .load(url+picturePath)
                .override(convertDpToPixel(width), convertDpToPixel(height))
                .into(ivAlbum);
        //TODO 设置点击时间监听器

        //现在，开始设置点击事件监听器
        ivAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到另一个页面，并且将参数传递过去
                Intent intent = new Intent(context, TravelPicturesActivity.class);

                Bundle extras = new Bundle();
                extras.putStringArrayList("parameter_list_key", (ArrayList<String>) list.get(i).getPicturePath());
                extras.putString("placeName",list.get(i).getPlaceName());
                intent.putExtras(extras);

                context.startActivity(intent);
            }
        });
        return view;
    }
    private int convertDpToPixel(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}









