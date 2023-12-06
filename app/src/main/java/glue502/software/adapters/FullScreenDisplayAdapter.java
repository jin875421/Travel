package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;

public class FullScreenDisplayAdapter extends RecyclerView.Adapter<FullScreenDisplayAdapter.ImageViewHolder> {
    private Context context;
    private List<String> images;
    private String url = "http://" +ip +"/test/";
    public FullScreenDisplayAdapter(List<String> images){
        this.images = images;
    }


    @NonNull
    @Override
    public FullScreenDisplayAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.full_screen_item,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FullScreenDisplayAdapter.ImageViewHolder holder, int position) {
        Glide.with(context).load(url + images.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
