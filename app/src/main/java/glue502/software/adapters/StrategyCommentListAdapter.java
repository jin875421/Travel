package glue502.software.adapters;

import static android.app.PendingIntent.getActivity;
import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import glue502.software.R;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.models.Comment;
import glue502.software.models.CommentRespond;
import glue502.software.models.StrategyComment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StrategyCommentListAdapter extends BaseAdapter {
    private Context context;//上下文环境
    private int comment_layout_id;
    private List<StrategyComment> commentList;
    private String url = "http://"+ip+"/travel/";

    public StrategyCommentListAdapter(Context context, int comment_layout_id, List<StrategyComment> strategyComments){
        this.context = context;
        this.comment_layout_id = comment_layout_id;
        this.commentList = strategyComments;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取comment对象
        View commentView = LayoutInflater.from(context).inflate(comment_layout_id, null);
        //获取控件对象
        ImageView avatar = commentView.findViewById(R.id.avatar);
        TextView username = commentView.findViewById(R.id.username);
        TextView time = commentView.findViewById(R.id.time);
        TextView text = commentView.findViewById(R.id.text);
        TextView first_respond = commentView.findViewById(R.id.first_respond);
        TextView respond_num = commentView.findViewById(R.id.respond_num);
        LinearLayout respond = commentView.findViewById(R.id.respond);
        //获取当前要显示的对象
        StrategyComment strategyComment = commentList.get(position);
        Log.v("StrategyCommentListAdapter", "lzx 要显示的StrategyComment" + strategyComment.toString());
        //显示头像
        Glide.with(context)
                .load(url + strategyComment.getAvatar())
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .fallback(R.mipmap.blank)
                .circleCrop()
                .into(avatar);
        username.setText(strategyComment.getUsername());
        time.setText(strategyComment.getUploadTime());
        text.setText(strategyComment.getComment());
        if(strategyComment.getReturnStrategyCommentResponds()!= null){
            if(strategyComment.getReturnStrategyCommentResponds().size() != 0){
                first_respond.setText(strategyComment.getReturnStrategyCommentResponds().get(0).getUserName()
                        + " 回复 "
                        + strategyComment.getUsername()
                        + ":"
                        + strategyComment.getReturnStrategyCommentResponds().get(0).getText());
                respond_num.setText("共计" + strategyComment.getReturnStrategyCommentResponds().size() + "条回复>");
            }else {
                respond.setVisibility(View.GONE);
            }
        }else{
            respond.setVisibility(View.GONE);
        }
        //绑定评论回复点击事件
        respond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRespondClickListener.onRespondClick(position);
            }
        });

        return commentView;
    }

    public interface onRespondClickListener{
        void onRespondClick(int i);
    }

    private onRespondClickListener mOnRespondClickListener;

    public void setOnRespondClickListener(onRespondClickListener listener){
        this.mOnRespondClickListener = listener;
    }

}