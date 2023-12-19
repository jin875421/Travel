package glue502.software.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;

import glue502.software.R;
import glue502.software.models.ShowPicture;

//TODO 这个类现在可能没有什么用了，只是放在这里看一些代码作为参考
public class TravelPictureAdapter extends BaseAdapter {

    //首先是数据源
    //这个数据源是自定义的实体类的list
    private List<ShowPicture> list;
    private Context context;//上下文环境
    private int travel_pictures_layout_id;

    //绑定控件对象
    private LinearLayout llPictures;

    public TravelPictureAdapter(List<ShowPicture> list,Context context,int travel_pictures_layout_id){
        this.travel_pictures_layout_id = travel_pictures_layout_id;
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

    //在这里进行填充到item的操作，这里需要注意，在进行填充的时候，这个adapter自身照片的填充也需要用到一个adapter
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
            //获取布局文件对象
            view = LayoutInflater.from(context).inflate(travel_pictures_layout_id, null);
            llPictures = view.findViewById(R.id.ll_pictures_showed);
            //获取控件对象
            TextView tvDate = view.findViewById(R.id.tv_travel_date);
            //获取第i个对象
            ShowPicture showPicture = list.get(i);
            //然后将数据添加到控件之中
            //将Date类型转化成String类型
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String strDate1 = sdf1.format(showPicture.getTravelDate());
            List<String> picturePath = showPicture.getPicturePath();
            tvDate.setText(strDate1);


            //在这里先试验，添加一个按钮控件
//            Button button = new Button(context);
//            LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            button.setLayoutParams(btnLayoutParams);
//            button.setText("测试动态添加的组件");
//            llPictures.addView(button);

//            Button button = new Button(context);
//            button.setText("测试用的按钮");
//            llPictures.addView(button);


            //在这里遍历照片数组，每遍历到一个照片，就在页面中添加一个照片控件
            for(String picture : picturePath){
                addPicture(picture);
            }

        return view;
    }

    //这个方法用于添加照片控件
    private void addPicture(String picture) {
        ImageView imageView = new ImageView(context);
        //设置照片属性
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                convertDpToPixel(150), // 宽度 150dp 转换为像素
                convertDpToPixel(150) // 高度 150dp 转换为像素
        );
        imageView.setLayoutParams(imageLayoutParams);
        // 可根据需要设置ImageView的其他属性，例如网络加载图片、调整图片大小等。
        llPictures.addView(imageView);
        //将照片数据添加到控件中
        Glide.with(context)
                .load(picture)
                .into(imageView);
        // 将ImageView添加到LinearLayout中
//        llPictures.addView(imageView);
    }

    private int convertDpToPixel(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}













