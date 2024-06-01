package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;

import java.util.List;

import glue502.software.R;
import glue502.software.models.Follow;
import glue502.software.models.UserInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FollowListAdapter extends BaseAdapter {

    private Context context;
    private int adapter_fellow_item;
    private List<UserInfo> userInfoList;
    private String userId;

    private String url = "http://" + ip + "/travel/";
    private String urlLoadImage = "http://" + ip + "/travel/";

    public FollowListAdapter(Context context, int adapter_fellow_item, List<UserInfo> userInfoList,String userId) {
        this.context = context;
        this.userInfoList = userInfoList;
        this.adapter_fellow_item = adapter_fellow_item;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return userInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, adapter_fellow_item, null);
        }
        LinearLayout itemRootLayout = convertView.findViewById(R.id.follow_item_root);
        ImageView followAvatar = convertView.findViewById(R.id.follow_item_avatar);
        TextView followName = convertView.findViewById(R.id.follow_item_name);
        TextView followState = convertView.findViewById(R.id.follow_state);
        ImageView more = convertView.findViewById(R.id.follow_item_more);

        UserInfo userInfo = userInfoList.get(position);

        // 使用 Glide 加载用户头像，并进行圆形裁剪
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(context)
                .load(urlLoadImage + userInfo.getAvatar())
                .skipMemoryCache(true)  //允许内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // 使用磁盘缓存
                .placeholder(R.drawable.headimg)  // 设置占位图
                .apply(requestOptions)
                .signature(new ObjectKey(userInfo.getUserId()))  // 设置签名
                .into(followAvatar);
        followName.setText(userInfo.getUserName());
        followState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 弹窗询问 创建Dialog

            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
            }
        });
        return convertView;
    }
}
