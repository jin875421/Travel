package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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
import glue502.software.adapters.ImagePagerAdapter;
import glue502.software.adapters.TravelPicturesAdapter;
import glue502.software.models.ImageEntity;
import glue502.software.utils.MyViewUtils;
import glue502.software.utils.bigImgUtils.MyImageLoader;


//这个页面是一个用于展示具体地点照片的页面
public class TravelPicturesActivity extends AppCompatActivity {

    //数据源
    private List<String> list1;
    private RecyclerView recyclerView;
    private String url = "http://"+ip+"/travel/";
    private String placeName ;
    private TextView name;
    private ImageView back;

    private TravelPicturesAdapter travelPicturesAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_pictures);
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        placeName = getIntent().getStringExtra("placeName");

        //获取传递过来的参数
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             list1 = extras.getStringArrayList("parameter_list_key");
        }


        name = findViewById(R.id.place_name);
        name.setText(placeName);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        List<ImageEntity> datas = new ArrayList<>();
        for(String s:list1){
            datas.add(new ImageEntity(url+s,url+s,null,null,null,0));
        }
        recyclerView = findViewById(R.id.picture_rcv);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        TravelPicturesActivity.RvAdapter myAdapter = new RvAdapter(datas);
        recyclerView.setAdapter(myAdapter);

    }

    private class RvAdapter extends RecyclerView.Adapter<TravelPicturesActivity.RvAdapter.MyHolder> {
        List<ImageEntity> datas;

        public RvAdapter(List<ImageEntity> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public TravelPicturesActivity.RvAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            //设置布局
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_picture_try, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TravelPicturesActivity.RvAdapter.MyHolder holder, int position) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

            //图片(后面的两张图片：加载时的占位图，加载失败时的占位图。可自行设置)
            MyImageLoader.getInstance().load(holder.ivImage, datas.get(position).getCoverImageUrl(), R.mipmap.loading, R.mipmap.blank);
            //设置点击图片后打开大图的监听器
            holder.ivImage.setOnClickListener(v -> {
                OpenImage.with(TravelPicturesActivity.this).setClickRecyclerView(recyclerView, new SourceImageViewIdGet() {
                            @Override
                            public int getImageViewId(OpenImageUrl data, int position) {
                                return R.id.imageView;
                            }
                        })
                        .setAutoScrollScanPosition(true)
                        //RecyclerView的数据
                        .setImageUrlList(datas)
                        //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .addPageTransformer(new ScaleInTransformer())
                        .setClickPosition(position)
                        .setShowDownload()
                        .show();
            });
        }
        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            public MyHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }
    }
}