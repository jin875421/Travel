package glue502.software.activities.photoMeld;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;

import java.util.List;

import glue502.software.R;

public class ShowPictureResultAdapter extends RecyclerView.Adapter<ShowPictureResultAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageUrls;

    public ShowPictureResultAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.showpicture_result_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.loading)
                .into(holder.imageView);
        // 为ImageView设置点击监听器
        holder.imageView.setOnClickListener(v -> {
            // 使用OpenImage库打开大图
            OpenImage.with(context) // 确保传入正确的Context，对于Activity使用Activity.this，对于Fragment使用requireContext()
                    .setClickImageView(holder.imageView) // 设置点击的ImageView，用于动画效果
                    .setAutoScrollScanPosition(true)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                    .setShowDownload()
                    .addPageTransformer(new ScaleInTransformer()) // 添加页面转换动画
                    .setImageUrl(imageUrl, com.flyjingfish.openimagelib.enums.MediaType.IMAGE) // 设置图片URL
                    .show(); // 显示图片
        });

    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

