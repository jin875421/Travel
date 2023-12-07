package glue502.software.fragments;

import static android.content.Context.MODE_PRIVATE;

import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.LoginActivity;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.activities.posts.UploadPostActivity;
import glue502.software.adapters.PostListAdapter;
import glue502.software.models.Post;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.UserInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class CommunityFragment extends Fragment {
    private final static String url="http://"+ip+"/test/posts/getpostlist";
    private ListView listView;
    private Button uploadBtn;
    private String userID;
    private List<Post> posts;
    private List<UserInfo> userInfos;
    private String status;
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this.getContext();

        View view = inflater.inflate(R.layout.fragment_community,container,false);
        listView = view.findViewById(R.id.post_display);
        uploadBtn = view.findViewById(R.id.upload_post_btn);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        status = sharedPreferences.getString("status","");
        setListener();
        initData();


        return view;

    }

    public void initData(){
        posts = new ArrayList<>();
        userInfos = new ArrayList<>();
        //开启线程接收帖子数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                //创建请求获取Post类
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    //发起请求并获取响应
                    Response response = client.newCall(request).execute();
                    //检测响应是否成功
                    if (response.isSuccessful()){
                        //获取响应数据
                        ResponseBody responseBody = response.body();
                        if (responseBody!=null){
                            //处理数据
                            String responseData = responseBody.string();
                            Gson gson = new Gson();
                            List<PostWithUserInfo> postWithUserInfoList = gson.fromJson(responseData,new TypeToken<List<PostWithUserInfo>>(){}.getType());
                            posts = new ArrayList<>();
                            userInfos = new ArrayList<>();
                            for (PostWithUserInfo postWithUserInfo: postWithUserInfoList){
                                posts.add(postWithUserInfo.getPost());
                                userInfos.add(postWithUserInfo.getUserInfo());
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (posts !=null&&userInfos!=null){
                                        PostListAdapter postAdapter = new PostListAdapter(getActivity(),R.layout.post_item,posts,userInfos);
                                        listView.setAdapter(postAdapter);

                                    }else {

                                    }

                                }
                            });

                        }else {
                            //处理空数据
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();


    }
    public void setListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                PostListAdapter postListAdapter = (PostListAdapter) parent.getAdapter();
                //获取点击项数据对象
                PostWithUserInfo clickItem = (PostWithUserInfo) postListAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), PostDisplayActivity.class);
                intent.putExtra("postwithuserinfo", clickItem);
                System.out.println(clickItem.getUserInfo().getAvatar());
                startActivity(intent);

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==""){
                    // 创建AlertDialog构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("账号未登录！")
                            .setMessage("是否前往登录账号")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确定”按钮后的操作
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“取消”按钮后的操作
                                    dialog.dismiss(); // 关闭对话框
                                }
                            });

                    // 创建并显示对话框
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{
                    Intent intent = new Intent(getActivity(), UploadPostActivity.class);
                    startActivityForResult(intent,1);
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // 检查请求码是否与上传页面的请求码一致
            if (resultCode == Activity.RESULT_OK) {
                // 检查是否上传完成
                // 进行刷新操作，重新加载数据
                initData();
            }
        }
    }

}