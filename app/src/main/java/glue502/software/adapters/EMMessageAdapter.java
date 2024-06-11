package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import glue502.software.R;
import glue502.software.models.MyEMMessage;

public class EMMessageAdapter extends RecyclerView.Adapter<EMMessageAdapter.EMMessageViewHolder> {
    private List<MyEMMessage> myEMMessages;
    private Context context;

    public EMMessageAdapter(Context context, List<MyEMMessage> myEMMessages) {
        this.context = context;
        this.myEMMessages = myEMMessages;
    }
    @NonNull
    @Override
    public EMMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_em_message_adapter, parent,false);
        return new EMMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EMMessageViewHolder holder, int position) {
        MyEMMessage myEMMessage = myEMMessages.get(position);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(context)
                .load("http://"+ip+"/travel/"+myEMMessage.getAvatar())
                .apply(requestOptions)
                .into(holder.avatar);
        holder.message.setText(myEMMessage.getMessage());
    }

    @Override
    public int getItemCount() {
        return myEMMessages.size();
    }

    static class EMMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView message;

        public EMMessageViewHolder (View itemView){
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            message = itemView.findViewById(R.id.message);
        }
    }
}
