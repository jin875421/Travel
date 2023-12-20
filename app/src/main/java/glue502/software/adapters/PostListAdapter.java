package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import java.io.IOException;
import java.util.List;

import glue502.software.R;
import glue502.software.models.Post;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.UserInfo;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        LinearLayout images = v.findViewById(R.id.image_container);
        TextView likeCount = v.findViewById(R.id.like_count);
        TextView commentCount = v.findViewById(R.id.comment_count);
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
        int x = 0;
        for (String path : post1.getPicturePath()) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(convertDpToPixel(110), convertDpToPixel(110));
            layoutParams.setMargins(convertDpToPixel(4), 0, 0, 0); // 左边距为4dp
            imageView.setLayoutParams(layoutParams);
            Glide.with(context)
                    .load(url + path)
                    .into(imageView);
            images.addView(imageView);
            x++;
            if(x==3){
                break;
            }
        }

        //从服务器查询点赞和评论数
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url+"posts/getLikeCount?postId="+post1.getPostId())
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String responseData = response.body().string();
                        //获取点赞数
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (responseData.equals("0")) {
                                    likeCount.setText("0");
                                } else {
                                    likeCount.setText(responseData);
                                }
                            }
                        });
                    }

                } catch (IOException e) {
                    Log.e("NetworkError", "Error: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url+"comment/getCommentCount?postId="+post1.getPostId())
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String responseData = response.body().string();
                        //给控件设置评论数
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (responseData.equals("0")) {
                                    commentCount.setText("0");
                                } else {
                                    commentCount.setText(responseData);
                                }
                            }
                        });

                    }

                } catch (IOException e) {
                    Log.e("NetworkError", "Error: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }).start();

        return v;
    }
    private int convertDpToPixel(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
