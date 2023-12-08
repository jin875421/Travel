package glue502.software.adapters;


import static glue502.software.activities.MainActivity.ip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.posts.FullScreenDisplayActivity;

public class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.ImageViewHolder> {
    private Context context;
    private List<String> images;
    private List<String> imagepath;
    private String url = "http://" + ip + "/travel/";

    public BannerPagerAdapter(List<String> images,List<String> imagepath) {
        this.imagepath = imagepath;
        this.images = images;
    }

    @NonNull
    @Override
    public BannerPagerAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerPagerAdapter.ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //图片全屏显示

                Intent intent = new Intent(context, FullScreenDisplayActivity.class);
                intent.putStringArrayListExtra("images", (ArrayList<String>) imagepath);
                intent.putExtra("position",position+"");
                context.startActivity(intent);

            }
        });
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

    private void loadNetworkImageAndShowFullScreen(String imageUrl) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用缓存，可根据需求调整
                .skipMemoryCache(true) // 禁用内存缓存，可根据需求调整
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // 图片加载完成后显示全屏
                        showFullScreenImage(resource);
                    }
                });

    }
    private void showFullScreenImage(Bitmap bitmap) {
        if (context instanceof Activity) {
            final Dialog dialog = new Dialog(context);
            ImageView image = new ImageView(context);
            image.setImageBitmap(bitmap);
            dialog.setContentView(image);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            // 点击图片取消
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }
}
