package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import glue502.software.R;
import glue502.software.activities.personal.MyFollowActivity;
import glue502.software.models.Follow;
import glue502.software.models.UserInfo;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FollowListAdapter extends BaseAdapter {
    public static final int NORMAL_TYPE = 0;
    public static final int UNFOLLOW_TYPE = 1;
    private Context context;
    private int adapter_fellow_item;
    // 关注用户信息
    private List<UserInfo> userInfoList;
    // 关注信息(分组信息)
    private int[] isFollow;
    // 批量取关列表
    private List<String> unfollowIdList;
    private String userId;
    private int type;
    private Gson gson = new Gson();
    private String url = "http://" + ip + "/travel/";
    private String urlLoadImage = "http://" + ip + "/travel/";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Handler adapterHandler;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getUnfollowIdList() {
        return unfollowIdList;
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public int[] getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int[] isFollow) {
        this.isFollow = isFollow;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    /**
     * @param context             上下文
     * @param adapter_fellow_item item布局
     * @param userInfoList        关注用户信息
     * @param userId              用户id
     * @param type                NORMAL_TYPE:常规关注列表 UNFOLLOW_TYPE:批量取关列表
     * @param adapterHandler      adapterHandler 用于消息回传
     */
    public FollowListAdapter(Context context, int adapter_fellow_item, List<UserInfo> userInfoList, String userId, int type, Handler adapterHandler) {
        this.context = context;
        this.userInfoList = userInfoList;
        this.adapter_fellow_item = adapter_fellow_item;
        this.userId = userId;
        this.type = type;
        this.adapterHandler = adapterHandler;
        isFollow = new int[userInfoList.size()];
        // 全部置为1（已关注）
        for (int i = 0; i < userInfoList.size(); i++) {
            isFollow[i] = 1;
        }
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, adapter_fellow_item, null);
            holder = new ViewHolder();
            holder.itemRootLayout = convertView.findViewById(R.id.follow_item_root);
            holder.followAvatar = convertView.findViewById(R.id.follow_item_avatar);
            holder.followName = convertView.findViewById(R.id.follow_item_name);
            holder.followState = convertView.findViewById(R.id.follow_state);
            holder.place = convertView.findViewById(R.id.follow_item_place);
            holder.more = convertView.findViewById(R.id.follow_item_more);
            holder.checkBox = convertView.findViewById(R.id.follow_item_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 从列表中获取当前用户的信息
        UserInfo currentUserInfo = userInfoList.get(position);
        setData(holder, currentUserInfo);
        switch (type) {
            case NORMAL_TYPE:
                // 设置关注状态 1: 已关注 0: 未关注
                if (isFollow[position]==1) {
                    setFollowStateUI(holder, 1,position);
                } else {
                    setFollowStateUI(holder, 0,position);
                }
                holder.place.setVisibility(View.INVISIBLE);
                holder.checkBox.setVisibility(View.INVISIBLE);
                holder.more.setVisibility(View.VISIBLE);
                holder.followState.setVisibility(View.VISIBLE);
                // 重置复选框状态
                holder.checkBox.setChecked(false);
                // 设置监听
                setListener(holder, currentUserInfo, position);
                break;
            case UNFOLLOW_TYPE:
                // 先判断当前关注状态 0-未关注 1-已关注
//                int state = (int) holder.followState.getTag();
                int state = isFollow[position];
                if (state == 0) {
                    holder.followState.setText("已取关");
                    holder.more.setVisibility(View.GONE);
                    holder.checkBox.setClickable(false);
                } else {
                    holder.checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 记录选中状态
                            if (holder.checkBox.isChecked()) {
                                // 选中
                                if (unfollowIdList == null) {
                                    unfollowIdList = new ArrayList<>();
                                }
                                unfollowIdList.add(currentUserInfo.getUserId());
                            } else {
                                // 取消选中
                                if (unfollowIdList != null) {
                                    unfollowIdList.remove(currentUserInfo.getUserId());
                                }
                            }
                        }
                    });
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.more.setVisibility(View.INVISIBLE);
                    holder.followState.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
        return convertView;
    }

    private void setListener(ViewHolder holder, UserInfo userInfo, int position) {
        holder.followState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先判断当前关注状态 0-未关注 1-已关注
                int state = isFollow[position];
                switch (state) {
                    case 0:
                        // 用户想要重新关注
                        followUser(holder, userInfo,position);
                        break;
                    case 1:
                        // 用户想要取消关注 Dialog中包含了取关方法
                        showDialog(holder, userInfo,position);
                        break;
                    default:
                        break;
                }

            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 回传用户信息并在activity中弹出抽屉
                Message message = handler.obtainMessage();
                message.what = 0;
                message.arg1 = position;
                message.obj = userInfo;
                adapterHandler.sendMessage(message);
            }
        });

    }

    private void showDialog(ViewHolder holder, UserInfo userInfo,int position) {
        // 创建Dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_layout);

        // 获取Dialog中的控件
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView sure = dialog.findViewById(R.id.dialog_button_sure);
        TextView cancel = dialog.findViewById(R.id.dialog_button_cancel);
        dialogTitle.setText("不再关注此用户？");
        // 设置按钮点击事件
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unFollowUser(holder, userInfo,position);
                // 关闭Dialog
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭Dialog
                dialog.dismiss();
            }
        });
        // 显示Dialog
        dialog.show();
    }

    private void setData(ViewHolder holder, UserInfo userInfo) {
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
                .into(holder.followAvatar);

        // 设置用户名
        holder.followName.setText(userInfo.getUserName());

    }

    private void followUser(ViewHolder holder, UserInfo userInfo,int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //执行关注
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("userId", userId)
                        .addFormDataPart("followId", userInfo.getUserId())
                        .build();
                Request request = new Request.Builder()
                        .url(url + "/follow/addFollow")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                switch (responseData) {
                                    case "success":
                                        setFollowStateUI(holder, 1,position);
                                        Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "fail":
                                        Toast.makeText(context, "关注失败", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void unFollowUser(ViewHolder holder, UserInfo followUser,int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //执行取消关注
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("userId", userId)
                        .addFormDataPart("followId", followUser.getUserId())
                        .build();
                Request request = new Request.Builder()
                        .url(url + "/follow/deleteFollow")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //获取返回消息
                                switch (responseData) {
                                    case "success":
                                        //取消关注成功
                                        setFollowStateUI(holder, 0,position);
                                        Toast.makeText(context, "取关成功", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "fail":
                                        //取消关注失败
                                        Toast.makeText(context, "取关失败", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        //未知错误
                                        break;
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * 根据传入的状态更新关注状态的UI显示。
     *
     * @param state 状态值，用于决定UI的显示状态。0代表未关注状态，1代表已关注状态。
     */
    private void setFollowStateUI(ViewHolder holder, int state, int position) {
        Drawable drawable;
        switch (state) {
            case 0:
                // 设置UI为未关注状态
                holder.followState.setText("+关注");
                holder.followState.setTextColor(Color.parseColor("#FFFFFF"));
                // #03A9F4
                drawable = ContextCompat.getDrawable(context, R.drawable.round_button_unfollowed_background);
                holder.followState.setBackground(drawable);
                isFollow[position] = 0;
                break;
            case 1:
                // 设置UI为已关注状态
                holder.followState.setText("已关注");
                holder.followState.setTextColor(Color.parseColor("#181A23"));
                drawable = ContextCompat.getDrawable(context, R.drawable.round_button_followed_background);
                holder.followState.setBackground(drawable);
                isFollow[position] = 1;
                break;
            default:
                // 对于其他状态不做处理
                break;
        }
    }

    // ViewHolder类
    private static class ViewHolder {
        LinearLayout itemRootLayout;
        ImageView followAvatar;
        TextView followName;
        TextView followState;
        ImageView more;
        CheckBox checkBox;
        View place;
    }
}