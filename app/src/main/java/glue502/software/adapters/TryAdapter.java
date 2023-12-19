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


//TODO 这个类现在已经没啥用了，只是测试用的一个类
public class TryAdapter extends BaseAdapter {
    //这是用于测试的数据源，只有照片的路径信息
    private List<String> list;

    private Context context;//上下文环境
    private int layout;//要添加数据的listview类型控件

    public TryAdapter() {
    }

    public TryAdapter(List<String> list, Context context, int layout) {
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
        //在这里将照片显示出来就行

        //首先获取布局文件对象
        view = LayoutInflater.from(context).inflate(layout, null);
        ImageView ivPicture = view.findViewById(R.id.iv_test);
        String picture = list.get(i);
        Glide.with(context)
                .load(picture)
                .into(ivPicture);
        return view;
    }
}
