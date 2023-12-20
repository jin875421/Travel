package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;
import glue502.software.models.ReturnCommentRespond;

public class RespondDetailAdapter extends BaseAdapter {
    private Context context;
    private int respond_detail_layout_id;
    private List<ReturnCommentRespond> returnCommentResponds;
    private String url = "http://"+ip+"/travel/";

    public RespondDetailAdapter(Context context, int respond_detail_layout_id, List<ReturnCommentRespond> returnCommentResponds){
        this.context = context;
        this.respond_detail_layout_id = respond_detail_layout_id;
        this.returnCommentResponds = returnCommentResponds;
    }

    @Override
    public int getCount() {
        return returnCommentResponds.size();
    }

    @Override
    public Object getItem(int position) {
        return returnCommentResponds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, respond_detail_layout_id, null);
        //获取控件对象
        ImageView avatar = view.findViewById(R.id.avatar);
        TextView username = view.findViewById(R.id.username);
        TextView time = view.findViewById(R.id.time);
        TextView text = view.findViewById(R.id.text);
        //获取当前要显示的对象
        ReturnCommentRespond respond = returnCommentResponds.get(position);
        //显示头像
        Glide.with(context)
                .load(url + respond.getAvatar())
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .fallback(R.mipmap.blank)
                .circleCrop()
                .into(avatar);
        username.setText(respond.getUserName());
        time.setText(respond.getTime());
        text.setText(respond.getText());

        return view;
    }
}
