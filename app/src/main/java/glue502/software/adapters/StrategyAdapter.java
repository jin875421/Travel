package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;

import glue502.software.R;
import glue502.software.models.ReturnStrategy;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class StrategyAdapter extends BaseAdapter {
    private Context context;
    private int strategy_layout_id;
    private List<ReturnStrategy> strategyList;
    private TextView title;
    private TextView desc;
    private TextView name;
    private ImageView cover;
    public StrategyAdapter(Context context, int strategy_layout_id, List<ReturnStrategy> strategyList) {
        this.context = context;
        this.strategy_layout_id = strategy_layout_id;
        this.strategyList = strategyList;
    }
    @Override
    public int getCount() {
        return strategyList.size();
    }

    @Override
    public Object getItem(int position) {
        return strategyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, strategy_layout_id, null);
        //获取控件对象
        title = view.findViewById(R.id.title);
        desc = view.findViewById(R.id.desc);
        name = view.findViewById(R.id.name);
        cover = view.findViewById(R.id.cover);
        //获取当前要显示的对象
        ReturnStrategy returnStrategy = new ReturnStrategy();
        returnStrategy = strategyList.get(position);
        //显示封面
        Glide.with(context)
                .load("http://"+ip+"/travel/"+returnStrategy.getImageFromImages(0).getPicturePath())
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .fallback(R.mipmap.blank)
                .into(cover);
        desc.setText(returnStrategy.getDescribe());
        title.setText(returnStrategy.getTitle());
        name.setText(returnStrategy.getUserName());

        return view;
    }
}