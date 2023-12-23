package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.TravelAlbumAdapter;
import glue502.software.models.ShowPicture;
import glue502.software.utils.MyViewUtils;

public class TravelAlbumActivity extends AppCompatActivity {

    private List<ShowPicture> list;
    private List<ShowPicture> list1,list2,list3,list4;

    private GridView gridView1,gridView2,gridView3,gridView4;

    TravelAlbumAdapter t1,t2,t3,t4;

    //这是一个用于测试的数据源，只有图片，看能不能在相册页面中显示出来
    private List<String> list10 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_album);

        //在这个页面显示很多组照片
        //首先准备数据源,然后将数据源放到适配器当中

        findViews();
        //沉浸式状态栏
        MyViewUtils.setISBarWithoutView(this,true);

        //初始化数据源
        initData();

        //现在分配适配器
        t1 = new TravelAlbumAdapter(list1,this,R.layout.travel_album);
        t2 = new TravelAlbumAdapter(list2,this,R.layout.travel_album);
        t3 = new TravelAlbumAdapter(list3,this,R.layout.travel_album);
        t4 = new TravelAlbumAdapter(list4,this,R.layout.travel_album);
        gridView1.setAdapter(t1);
        gridView2.setAdapter(t2);
        gridView3.setAdapter(t3);
        gridView4.setAdapter(t4);






//        t1 = new TravelAlbumAdapter(list10,this,R.layout.travel_album);
//        t2 = new TravelAlbumAdapter(list10,this,R.layout.travel_album);
//        t3 = new TravelAlbumAdapter(list10,this,R.layout.travel_album);
//        t4 = new TravelAlbumAdapter(list10,this,R.layout.travel_album);
//
//        gridView1.setAdapter(t1);
//        gridView2.setAdapter(t2);
//        gridView3.setAdapter(t3);
//        gridView4.setAdapter(t4);

    }

    private void initData() {

        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();

        ShowPicture sp = new ShowPicture();
        Date date = new Date();
        sp.setTravelDate(date);
        sp.setPlaceName("河北师范大学");
        List<String> li = new ArrayList<>();
        li.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
        li.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
        li.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");
        li.add("https://picdm.sunbangyan.cn/2023/12/20/fc8965d2da51b0a191386036b824d942.jpeg");
        li.add("https://picst.sunbangyan.cn/2023/12/20/fefbfc6cbd0e10c7cb11727dc5f2cfae.jpeg");
        li.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
        li.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
        li.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");
        sp.setPicturePath(li);
        list1.add(sp);
        list2.add(sp);
        list3.add(sp);
        list4.add(sp);


        //在这里初始化测试用的数据源
//        list10.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
//        list10.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
//        list10.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");
//        list10.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
//        list10.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
//        list10.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");
//        list10.add("https://picst.sunbangyan.cn/2023/12/15/819f5c5edca5d199f65abab3194973cf.jpeg");
//        list10.add("https://picdl.sunbangyan.cn/2023/12/15/24f98c8fc99caba5490846430185efce.jpeg");
//        list10.add("https://picdl.sunbangyan.cn/2023/12/15/16ddc0e539b47227bd082bd59d0f0faf.jpeg");

    }

    private void findViews() {
        gridView1 = findViewById(R.id.gv_view1);
        gridView2 = findViewById(R.id.gv_view2);
        gridView3 = findViewById(R.id.gv_view3);
        gridView4 = findViewById(R.id.gv_view4);
    }
}