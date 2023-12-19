package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.PictureAlbumAdapter;
import glue502.software.adapters.TravelPictureAdapter;
import glue502.software.adapters.TryAdapter;
import glue502.software.models.ShowPicture;

public class travelPictureActivity extends AppCompatActivity {
    private List<ShowPicture> pictureShowed;
    //目前模拟的数据源 TODO 还没有填充数据
//    List<String> pictures1,pictures2,pictures3,pictures4;

    List<ShowPicture> pictures1,pictures2,pictures3,pictures4;

    //声明适配器对象
//    private TravelPictureAdapter travelPictureAdapter;
    private PictureAlbumAdapter pictureAlbumAdapter;
    private GridView gridView1,gridView2,gridView3,gridView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_picture);

        //在这个类中，要初始化数据，初始化的数据是客户端在跳转的时候向服务端发送请求，然后服务端向客户端返回的信息

        //获取控件对象
        getViews();
        //首先初始化数据源
        initDate();

        //这里初始化所有的适配器对象
        PictureAlbumAdapter pictureAlbumAdapter1 = new PictureAlbumAdapter(pictures1,this,R.layout.just_have_a_try);
        PictureAlbumAdapter pictureAlbumAdapter2 = new PictureAlbumAdapter(pictures2,this,R.layout.just_have_a_try);
        PictureAlbumAdapter pictureAlbumAdapter3 = new PictureAlbumAdapter(pictures3,this,R.layout.just_have_a_try);
        PictureAlbumAdapter pictureAlbumAdapter4 = new PictureAlbumAdapter(pictures4,this,R.layout.just_have_a_try);

        gridView1.setAdapter(pictureAlbumAdapter1);
        gridView2.setAdapter(pictureAlbumAdapter2);
        gridView3.setAdapter(pictureAlbumAdapter3);
        gridView4.setAdapter(pictureAlbumAdapter4);

        //然后将数据填充到布局控件中
        //在这里，将每个gridView中都用adapter来填充数据
//        travelPictureAdapter = new TravelPictureAdapter(pictureShowed,travelPictureActivity.this,R.layout.travel_pictures_item);

        //在这里为四个GridView添加数据
//        TryAdapter tryAdapter1 = new TryAdapter(pictures1,this,R.layout.just_have_a_try);
//        TryAdapter tryAdapter2 = new TryAdapter(pictures2,this,R.layout.just_have_a_try);
//        TryAdapter tryAdapter3 = new TryAdapter(pictures3,this,R.layout.just_have_a_try);
//        TryAdapter tryAdapter4 = new TryAdapter(pictures4,this,R.layout.just_have_a_try);
//        gridView1.setAdapter(tryAdapter1);
//        gridView2.setAdapter(tryAdapter2);
//        gridView3.setAdapter(tryAdapter3);
//        gridView4.setAdapter(tryAdapter4);

    }

    private void getViews() {
        gridView1 = findViewById(R.id.gv_view1);
        gridView2 = findViewById(R.id.gv_view2);
        gridView3 = findViewById(R.id.gv_view3);
        gridView4 = findViewById(R.id.gv_view4);
    }

    private void initDate() {
//        pictureShowed = new ArrayList<>();
//        ShowPicture showPicture1 = new ShowPicture();
//        Date currentDate = new Date();
//        showPicture1.setTravelDate(currentDate);
//        List<String> path1 = new ArrayList<>();
//        path1.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
//        path1.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
//        showPicture1.setPicturePath(path1);
//
//        ShowPicture showPicture2 = new ShowPicture();
//        showPicture2.setTravelDate(new Date());
//        List<String> path2 = new ArrayList<>();
//        path2.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");
//        showPicture2.setPicturePath(path2);
//        pictureShowed.add(showPicture1);
//        pictureShowed.add(showPicture2);


        //在这里为四个GridView中的数据初始化数据源
        pictures1 = new ArrayList<>();
        pictures2 = new ArrayList<>();
        pictures3 = new ArrayList<>();
        pictures4 = new ArrayList<>();

        ShowPicture showPicture = new ShowPicture();
        showPicture.setPlaceName("石家庄");
        Date date1 = new Date();
        List<String> picturesPath = new ArrayList<>();
        picturesPath.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
        picturesPath.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
        picturesPath.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");
        showPicture.setTravelDate(date1);
        showPicture.setPicturePath(picturesPath);
        pictures1.add(showPicture);
        pictures2.add(showPicture);
        pictures3.add(showPicture);
        pictures4.add(showPicture);


    }
}




















