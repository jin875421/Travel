package glue502.software.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;

import java.util.Collections;
import java.util.List;

import glue502.software.R;


//这个适配器是用于相册功能展示一个地区具体照片的适配器
public class TravelPicturesAdapter extends BaseAdapter {

    //数据源
    private List<String> list;
    private Context context;

    public TravelPicturesAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        view = LayoutInflater.from(context).inflate(R.layout.travel_pictures,null);

        //获取控件对象
        ImageView ivPictures = view.findViewById(R.id.iv_pictures);

        //将数据源中的东西放到控件对象当中
        Glide.with(context)
                .load(list.get(i))
                .into(ivPictures);

        //TODO 设置点击事件监听器，让照片可以点击放大
//        ivPictures.setOnClickListener(v -> {
//            OpenImage.with(context).setClickViewPager2(viewPager2, new SourceImageViewIdGet() {
//                        @Override
//                        public int getImageViewId(OpenImageUrl data, int position) {
//                            return R.id.imageView;
//                        }
//                    })
//                    .setAutoScrollScanPosition(true)
//                    .setImageUrlList(datas)
//                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
//                    .addPageTransformer(new ScaleInTransformer())
//                    .setClickPosition(position % imagepath.size(),position)
//                    .setOnItemLongClickListener(new OnItemLongClickListener() {
//                        @Override
//                        public void onItemLongClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
//                            Toast.makeText(context,"长按图片",Toast.LENGTH_LONG).show();
//                        }
//                    })
//                    .setShowDownload()
//                    .addMoreView(R.layout.big_img_layout,
//                            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT),
//                            MoreViewShowType.BOTH,
//                            new OnLoadViewFinishListener() {
//                                @Override
//                                public void onLoadViewFinish(View view) {
//                                    TextView tv = view.findViewById(R.id.big_img_tv);
//                                    tv.setText("");
//                                }
//                            })
//                    .show();
//        });

        return view;
    }


//    public void onThumbnailClick(View v) {
//// final AlertDialog dialog = new AlertDialog.Builder(this).create();
//// ImageView imgView = getView();
//// dialog.setView(imgView);
//// dialog.show();
//
//
//// 全屏显示的方法
//        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        ImageView imgView = getView();
//        dialog.setContentView(imgView);
//        dialog.show();
//
//
//// 点击图片消失
//        imgView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//// TODO Auto-generated method stub
//                dialog.dismiss();
//            }
//        });
//    }
//    private ImageView getView() {
//        ImageView imgView = new ImageView(this);
//        imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        InputStream is = getResources().openRawResource(R.drawable.thumbnail);
//        Drawable drawable = BitmapDrawable.createFromStream(is, null);
//        imgView.setImageDrawable(drawable);
//        return imgView;
//    }
}
