package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import glue502.software.R;
import glue502.software.models.Post;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.UserInfo;
public class PostListAdapter extends BaseAdapter {
    private Context context;
    private int layoutId;
    private List<Post> posts;
    private List<UserInfo> userInfos;
    private String url = "http://"+ip+"/travel/";
    public PostListAdapter(Context context, int layoutId, List<Post> posts, List<UserInfo> userInfos){
        this.posts = posts;
        this.layoutId = layoutId;
        this.context = context;
        this.userInfos = userInfos;
    }
    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return new PostWithUserInfo(posts.get(i),userInfos.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(layoutId,null);
        ImageView useravatar = v.findViewById(R.id.imageViewUserAvatar);
        TextView username = v.findViewById(R.id.textViewUsername);
        TextView title = v.findViewById(R.id.textViewTitle);
        TextView content = v.findViewById(R.id.textViewContentPreview);
        LinearLayout iamges = v.findViewById(R.id.image_container);
//        ImageView view1 = v.findViewById(R.id.image1);
//        ImageView view2 = v.findViewById(R.id.image2);
//        ImageView view3 = v.findViewById(R.id.image3);

        Post post1 = posts.get(i);
        title.setText(post1.getPostTitle());
        if (post1.getPostContent().length()<20){
            content.setText(post1.getPostContent().replace("\n"," "));
        }else {
            content.setText((post1.getPostContent().substring(0,20)+"...").replace("\n"," "));
        }

        username.setText(userInfos.get(i).getUserName());
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(context)
                .load(url+ userInfos.get(i).getAvatar())
                .apply(requestOptions)
                .into(useravatar);
        //分离地址并获取
        //最多展示三张图片
//            if (post1.getPicturePath().size()>0) {
//                //执行展示代码，将图片展示到页面上
//                Glide.with(context)
//                        .load(url + post1.getPicturePath().get(0))
//                        .override(convertDpToPixel(125), convertDpToPixel(125))
//                        .into(view1);
//            }
//            if (post1.getPicturePath().size()>1) {
//                Glide.with(context)
//                        .load(url + post1.getPicturePath().get(1))
//                        .override(convertDpToPixel(125), convertDpToPixel(125))
//                        .into(view2);
//            }
//            if (post1.getPicturePath().size()>2) {
//                Glide.with(context)
//                        .load(url + post1.getPicturePath().get(2))
//                        .override(convertDpToPixel(125), convertDpToPixel(125))
//                        .into(view3);
//            }
        for (String path:post1.getPicturePath()){
                ImageView imageView = new ImageView(context);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(convertDpToPixel(125),convertDpToPixel(125)));
                Glide.with(context)
                       .load(url+path)
                       .into(imageView);
                iamges.addView(imageView);
        }
        return v;
    }
    private int convertDpToPixel(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
