package glue502.software.adapters;


import static glue502.software.activities.MainActivity.ip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.models.ImageEntity;
import glue502.software.utils.bigImgUtils.MyImageLoader;

public class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.ImageViewHolder> {
    private Context context;
    private List<String> images;
    private List<String> imagepath;
    private ViewPager2 viewPager2;
    private List<ImageEntity> datas = new ArrayList<>();
    private String url = "http://" + ip + "/travel/";

    public BannerPagerAdapter(List<String> images, List<String> imagepath, ViewPager2 viewPager2) {
        this.imagepath = imagepath;
        this.images = images;
        this.viewPager2 = viewPager2;
        for(String imageUrl:imagepath){
            datas.add(new ImageEntity(url+imageUrl,url+imageUrl,null,null,null, 0));
        }
    }
    public BannerPagerAdapter(List<String> imagepath, ViewPager2 viewPager2) {
        this.imagepath = imagepath;
        this.viewPager2 = viewPager2;
        for(String imageUrl:imagepath){
            datas.add(new ImageEntity(url+imageUrl,url+imageUrl,null,null,null, 0));
        }
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
        MyImageLoader.getInstance().load(holder.imageView, datas.get(position % imagepath.size()).getCoverImageUrl(), R.mipmap.loading, R.mipmap.blank);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //图片全屏显示
//j
//                Intent intent = new Intent(context, FullScreenDisplayActivity.class);
//                intent.putStringArrayListExtra("images", (ArrayList<String>) imagepath);
//                intent.putExtra("position",position+"");
//                context.startActivity(intent);

                //z
                holder.imageView.setOnClickListener(v -> {
                    OpenImage.with(context).setClickViewPager2(viewPager2, new SourceImageViewIdGet() {
                                @Override
                                public int getImageViewId(OpenImageUrl data, int position) {
                                    return R.id.imageView;
                                }
                            })
                            .setAutoScrollScanPosition(true)
                            .setImageUrlList(datas)
                            .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                            .addPageTransformer(new ScaleInTransformer())
                            .setClickPosition(position % imagepath.size(),position)
                            .setOnItemLongClickListener(new OnItemLongClickListener() {
                                @Override
                                public void onItemLongClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
                                    Toast.makeText(context,"长按图片",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setShowDownload()
                            .addMoreView(R.layout.big_img_layout,
                                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT),
                                    MoreViewShowType.BOTH,
                                    new OnLoadViewFinishListener() {
                                        @Override
                                        public void onLoadViewFinish(View view) {
                                            TextView tv = view.findViewById(R.id.big_img_tv);
                                            tv.setText("我是猫不是狗");
                                        }
                                    })
                            .show();
                });

            }
        });
//        Glide.with(context).load(url + images.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
//        return imagepath != null ? imagepath.size() : 0;
        return Integer.MAX_VALUE;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

//    private void loadNetworkImageAndShowFullScreen(String imageUrl) {
//        Glide.with(context)
//                .asBitmap()
//                .load(imageUrl)
//                .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用缓存，可根据需求调整
//                .skipMemoryCache(true) // 禁用内存缓存，可根据需求调整
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        // 图片加载完成后显示全屏
//                        showFullScreenImage(resource);
//                    }
//                });
//
//    }
//    private void showFullScreenImage(Bitmap bitmap) {
//        if (context instanceof Activity) {
//            final Dialog dialog = new Dialog(context);
//            ImageView image = new ImageView(context);
//            image.setImageBitmap(bitmap);
//            dialog.setContentView(image);
//            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            dialog.show();
//            // 点击图片取消
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.cancel();
//                }
//            });
//        }
//    }
}
