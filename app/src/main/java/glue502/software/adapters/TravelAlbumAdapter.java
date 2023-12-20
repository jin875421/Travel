package glue502.software.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;
import glue502.software.models.ShowPicture;

public class TravelAlbumAdapter extends BaseAdapter {

//    private List<ShowPicture> list;//数据源

    //用于测试的数据源
    private List<String> list;
    private Context context;//上下文环境
    private int layout;//要填充的页面布局

    public TravelAlbumAdapter() {
    }

    public TravelAlbumAdapter(List<String> list, Context context, int layout) {
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

//        ShowPicture sp = list.get(i);
//        String picturePath = sp.getPicturePath().get(0);

        String picturePath = list.get(i);
        Glide.with(context)
                .load(picturePath)
                .into(ivAlbum);

        //TODO 设置点击时间监听器
        return view;
    }
}



