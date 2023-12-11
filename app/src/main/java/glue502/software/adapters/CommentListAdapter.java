package glue502.software.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;
import glue502.software.models.Comment;

public class CommentListAdapter extends BaseAdapter {
    private Context context;//上下文环境
    private int comment_layout_id;
    private List<Comment> commentList;
    private String URL = "192.168.29.92";

    public CommentListAdapter(Context context, int comment_layout_id, List<Comment> commentList){
        this.context = context;
        this.comment_layout_id = comment_layout_id;
        this.commentList = commentList;
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
        //获取当前要显示的对象
        Comment comment = commentList.get(position);
        //显示头像
        System.out.println(comment.getAvatar());
        Glide.with(context)
                .load("http://"+ URL +":8080/test/" + comment.getAvatar())
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .fallback(R.mipmap.blank)
                .circleCrop()
                .into(avatar);

        username.setText(comment.getUsername());
        time.setText(comment.getUploadTime());
        text.setText(comment.getComment());

        return commentView;
    }
}