package glue502.software.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.travelRecord.PictureShowActivity;
import glue502.software.activities.travelRecord.travelPictureActivity;
import glue502.software.models.ShowPicture;


//这个适配器用于相册功能的看到所有旅游地点的照片
public class PictureAlbumAdapter extends BaseAdapter {

    private List<ShowPicture> list;//数据源
    private Context context;//上下文环境
    private int layout;//要放控件的布局

    public PictureAlbumAdapter() {
    }

    public PictureAlbumAdapter(List<ShowPicture> list, Context context, int layout) {
        this.list = list;
        this.context = context;
        this.layout = layout;
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

        //获取布局文件对象
        view = LayoutInflater.from(context).inflate(layout, null);
        //这个时候图片已经被分好类了，现在只需要将数据源放到控件中就行了
        ImageView ivTest = view.findViewById(R.id.iv_test);
        TextView tvPicture = view.findViewById(R.id.tv_picture_text);

        ShowPicture showPicture = list.get(i);

        tvPicture.setText(showPicture.getPlaceName());

        //将照片数据添加到控件中
        Glide.with(context)
                .load(showPicture.getPicturePath().get(1))
                .into(ivTest);

        ivTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里写点击之后跳转到展示照片页面
                //在跳转的时候，将ShowPicture类型的对象作为参数传递过去，在这里要传递这个对象数据的时候需要先序列化
                Intent intent = new Intent(context,PictureShowActivity.class);
                //试试将数据放到SharedPreferences文件中，而不是当作参数传递过去

//                Gson gson = new Gson();
//                String json = gson.toJson(showPicture.getPicturePath());
//                SharedPreferences sharedPreferences = context.getSharedPreferences("Pictures", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("pictures", json);
//                editor.commit();

                Intent pictures = intent.putStringArrayListExtra("pictures", (ArrayList<String>) showPicture.getPicturePath());
                context.startActivity(intent);
            }
        });

        return view;
    }

}












