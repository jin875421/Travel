package glue502.software.activities.photoMeld;

import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;

import java.io.IOException;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.OpenCVTest;
import glue502.software.activities.travelRecord.TravelPicturesActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowPictureResultAdapter extends RecyclerView.Adapter<ShowPictureResultAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageUrls;
    private String loadUrl="http://"+ip+"/travel/pictureEdit/deletePicture";


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
//                    .addMoreView(R.layout.showpicture_bigshow,
//                            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT),
//                            MoreViewShowType.BOTH,
//                            new OnLoadViewFinishListener() {
//                                @Override
//                                public void onLoadViewFinish(View moreView) {
//                                    ImageView imgDelete=moreView.findViewById(R.id.img_delete);
//                                    imgDelete.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            deletePicture(imageUrl);
//                                        }
//                                    });
//
//
//                                }
//                            })
                    .show(); // 显示图片
        });

    }
    private void deletePicture(final String path) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), path);
                                Request request = new Request.Builder()
                                        .url(loadUrl) //***.***.**.***为本机IP，xxxx为端口，/  /  为访问的接口后缀
                                        .post(requestBody)
                                        .build(); //创建Http请求
                                Response response = client.newCall(request).execute();
                                final String responseData = response.body().string();

                                // 处理服务器响应，更新UI或执行其他操作
                                if (responseData.equals("图片已成功删除")) {
                                    // 在这里执行删除图片后的UI更新
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 例如：从列表中移除图片并通知适配器
                                            imageUrls.remove(path);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "图片已删除", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        } else {
            throw new IllegalStateException("Context must be an instance of Activity for using runOnUiThread.");
        }
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

