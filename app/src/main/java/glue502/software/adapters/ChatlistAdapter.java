package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import glue502.software.R;

import glue502.software.models.Chatlist;
import glue502.software.models.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatlistAdapter extends RecyclerView.Adapter
        <ChatlistAdapter.MyViewHolder> {
    private List<Chatlist> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    private String userId;
    private String urlAvatar="http://"+ip+"/travel/user/getAvatar?userId=";
    TextView rc_tv_speakername;
    TextView rc_tv_speakcontent;
    ImageView rc_iv_portrait;
    View v;

    public ChatlistAdapter(Context context, List<Chatlist> datas){
        this. mContext=context;
        this. mDatas=datas;
        inflater=LayoutInflater. from(mContext);
    }

    public void ResetChatlistAdapter(List<Chatlist> datas){
        this. mDatas=datas;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);
            rc_tv_speakername=(TextView) view.findViewById(R.id.rc_tv_speakername);
            rc_tv_speakcontent=(TextView) view.findViewById(R.id.rc_tv_speakcontent);
            rc_iv_portrait=view.findViewById(R.id.rc_iv_portrait);
            v=view;

        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Chatlist da=mDatas.get(position);
        rc_tv_speakername.setText(da.getSpeakerName());
        rc_tv_speakcontent.setText(da.getSpeakContent());

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");
        Log.v("ChatlistAdapter", "lzx chatlist userId"+userId);
        if(da.getSpeakerName().equals("ERNIE")){//如果是文心一言，重新设置头像
            rc_iv_portrait.setImageResource(R.drawable.ai_logo);
        }
        if(!da.getSpeakerName().equals("ERNIE")){//如果不是文心一言，则获取头像
            rc_iv_portrait.setImageResource(R.drawable.ai_avatar);
            rc_tv_speakername.setText("I");
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //这里边还可以对RecycleView里的独立Item进行操作(简单起见这次没有这样弄)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rc_chatlist_layout,parent,false);
        MyViewHolder holder= new MyViewHolder(view);

        return holder;
    }
}
