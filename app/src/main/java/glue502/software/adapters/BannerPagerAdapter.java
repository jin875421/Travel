package glue502.software.adapters;


import static glue502.software.activities.MainActivity.ip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.recoAttraction.AttractionDetailActivity;
import glue502.software.models.ImageEntity;
import glue502.software.models.RecoAttraction;
import glue502.software.utils.bigImgUtils.MyImageLoader;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.ImageViewHolder> {
    private Context context;
    private List<String> imagepath;
    private ViewPager2 viewPager2;
    private List<ImageEntity> datas = new ArrayList<>();
    private List<RecoAttraction> recoAttractions;
    String recoAttractionsStr;
    private String url = "http://" + ip + "/travel/";
    private int type = 0; //轮播图类型 0为普通轮播图：点击图片放大；  1为页面转跳的轮播图 默认为0

    public BannerPagerAdapter(List<RecoAttraction> recoAttractions, String recoAttractionsStr, ViewPager2 viewPager2, int type) {
        this.type = type;
        this.viewPager2 = viewPager2;
        this.recoAttractions = recoAttractions;
        this.recoAttractionsStr = recoAttractionsStr;
        for (RecoAttraction r : recoAttractions) {
            String imageUrl = url + r.getImgUrls().get(0);
//            http://localhost:8080/travel/ruoyi/uploadPath/upload/2023/12/22/%E3%80%90%E5%AE%A1%E7%BE%8E%E7%A7%AF%E7%B4%AF%E3%80%91%E5%90%91%E5%B1%B1%E8%80%8C%E5%8E%BB%EF%BD%9CJustin%20Wirtalla_8_%E5%AE%89%E7%9A%84%E8%A7%86%E8%A7%89%E5%AE%B6_%E6%9D%A5%E8%87%AA%E5%B0%8F%E7%BA%A2%E4%B9%A6%E7%BD%91%E9%A1%B5%E7%89%88_20231222170753A004.jpg
            datas.add(new ImageEntity(imageUrl, imageUrl, null, null, null, 0));
        }
    }

    private int getHeightInPixels(int heightInDp){
        int heightInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                heightInDp,
                context.getResources().getDisplayMetrics()
        );
        return heightInPixels;
    }
    public BannerPagerAdapter(List<String> imagepath, ViewPager2 viewPager2, String extraPath) {
        this.imagepath = imagepath;
        this.viewPager2 = viewPager2;
        for (String imageUrl : imagepath) {
            datas.add(new ImageEntity(url + extraPath + imageUrl, url + extraPath + imageUrl, null, null, null, 0));
        }
    }

    @NonNull
    @Override
    public BannerPagerAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view;
        if(type == 1){
            view = LayoutInflater.from(context).inflate(R.layout.image_item_type1, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        }
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerPagerAdapter.ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (type == 0) {
            MyImageLoader.getInstance().load(holder.imageView, datas.get(position % imagepath.size()).getCoverImageUrl(), R.mipmap.loading, R.mipmap.blank);
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
                        .setClickPosition(position % imagepath.size(), position)
                        .setOnItemLongClickListener(new OnItemLongClickListener() {
                            @Override
                            public void onItemLongClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
                                Toast.makeText(context, "长按图片", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setShowDownload()
//                        .addMoreView(R.layout.big_img_layout,
//                                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT),
//                                MoreViewShowType.BOTH,
//                                new OnLoadViewFinishListener() {
//                                    @Override
//                                    public void onLoadViewFinish(View view) {
//                                        TextView tv = view.findViewById(R.id.big_img_tv);
//                                        tv.setText("");
//                                    }
//                                })
                        .show();
            });
        }
        if (type == 1) {
            //TODO

//            MyImageLoader.getInstance().load(holder.imageView, datas.get(position % recoAttractions.size()).getCoverImageUrl(), R.mipmap.loading, R.mipmap.blank);

            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.loading)
                    .error(R.mipmap.blank);
            MultiTransformation mation5 = new MultiTransformation(
                    new CenterCrop(),
                    new RoundedCornersTransformation(30,0,RoundedCornersTransformation.CornerType.ALL)
            );
            Glide.with(context)
                    .load(datas.get(position % recoAttractions.size()).getCoverImageUrl())
                    .apply(options)
                    .apply(RequestOptions.bitmapTransform(mation5))
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDetail = new Intent(context, AttractionDetailActivity.class);
                    String attractionStr = new Gson().toJson(recoAttractions.get(position % recoAttractions.size()));
                    toDetail.putExtra("attractionStr", attractionStr);
                    context.startActivity(toDetail);
                }
            });
        }

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
