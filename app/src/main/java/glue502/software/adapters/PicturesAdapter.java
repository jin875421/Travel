package glue502.software.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;

public class PicturesAdapter extends BaseAdapter {

    //数据源
    private List<String> list;
    private Context context;
    private int layout;

    public PicturesAdapter() {
    }

    public PicturesAdapter(List<String> list, Context context, int layout) {
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

        //这里将图片数据显示到对应的控件当中
        String picture = list.get(i);

        //获取控件对象
        ImageView imPicture = view.findViewById(R.id.imageView);

        Glide.with(context)
                .load(picture)
                .into(imPicture);


        //不是，这就完了？？？？？？？？？？？？？？？？？


        return view;
    }
}
