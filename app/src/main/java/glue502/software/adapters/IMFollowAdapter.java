package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import glue502.software.R;
import glue502.software.models.UserInfo;

public class IMFollowAdapter extends RecyclerView.Adapter<IMFollowAdapter.IMFollowViewHolder> {
    private List<UserInfo> userInfoList;
    private Context context;
    private OnItemClickListener listener; // 添加点击事件监听器接口

    public IMFollowAdapter(Context context, List<UserInfo> userInfoList) {
        this.context = context;
        this.userInfoList = userInfoList;
    }

    @NonNull
    @Override
    public IMFollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_im_follow_adapter, parent,false);
        return new IMFollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IMFollowViewHolder holder, int position) {
        UserInfo userInfo = userInfoList.get(position);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(context)
                .load("http://"+ip+"/travel/"+userInfo.getAvatar())
                .apply(requestOptions)
                .into(holder.avatar);
        holder.name.setText(userInfo.getUserName());
        holder.message.setText(userInfo.getStatus());
        // 绑定点击事件到 ViewHolder
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(userInfo, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }

    static class IMFollowViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView message;
        public IMFollowViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UserInfo userInfo, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
