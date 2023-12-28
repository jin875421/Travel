package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import glue502.software.R;
import glue502.software.models.ReturnStrategy;

public class RecyclerViewStrategyAdapter extends RecyclerView.Adapter<RecyclerViewStrategyAdapter.MyViewHolder> {
    private Context context;
    private List<ReturnStrategy> strategyList;

    public RecyclerViewStrategyAdapter(Context context,List<ReturnStrategy> strategyList) {
        this.context = context;
        this.strategyList = strategyList;
    }

    public interface onRespondClickListener {
        void onRespondClick(int i);
    }
    private onRespondClickListener listener;

    public void setOnRespondClickListener(onRespondClickListener listener) {
        this.listener = listener;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.activity_strategy_adapter, parent, false);
//        return new MyViewHolder(view);
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_strategy_adapter, parent, false));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReturnStrategy returnStrategy = strategyList.get(position);
        // 计算像素值
        int widthInDp = 172;
        int heightInDp = 240;

        int widthInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                widthInDp,
                context.getResources().getDisplayMetrics()
        );

        int heightInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                heightInDp,
                context.getResources().getDisplayMetrics()
        );
        //显示封面
        Glide.with(context)
                .load("http://"+ip+"/travel/"+returnStrategy.getImageFromImages(0).getPicturePath())
                .override(widthInPixels, heightInPixels) // 设置加载图片时的大小
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .fallback(R.mipmap.blank)
                .into(holder.cover);
        holder.desc.setText(returnStrategy.getDescribe());
        holder.title.setText(returnStrategy.getTitle());
        holder.name.setText(returnStrategy.getUserName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRespondClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return strategyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        TextView name;
        ImageView cover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取控件对象
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            name = itemView.findViewById(R.id.name);
            cover = itemView.findViewById(R.id.cover);
        }
    }
}
