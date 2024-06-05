package glue502.software.fragments;

import static android.content.Context.MODE_PRIVATE;

import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.login.LoginActivity;
import glue502.software.activities.posts.PostDisplayActivity;
import glue502.software.activities.posts.PostSearchActivity;
import glue502.software.activities.posts.UploadPostActivity;
import glue502.software.activities.travelRecord.SearchActivity;
import glue502.software.activities.travelRecord.TodolistActivity;
import glue502.software.adapters.PageAdapter;
import glue502.software.adapters.PostListAdapter;
import glue502.software.models.Post;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class CommunityFragment extends Fragment {
    private String url="http://"+ip+"/travel/posts/getpostlist";
    private String searchUrl="http://"+ip+"/travel/posts/search";
    private ListView listView;
    private Toolbar toolbar;
    private ImageView img;
    private TextView txtSs;
    private AppBarLayout appBarLayout;
    private RelativeLayout lsda;
    private Button uploadBtn;
    private List<Post> posts = new ArrayList<>();
    private List<UserInfo> userInfos = new ArrayList<>();
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String status;
    private RefreshLayout refreshLayout;
    private ImageButton searchBtn;
    private View view;
    private int page = 0;
    private PostListAdapter postAdapter;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private PageAdapter adapter;
    private List<Fragment> fragments;
    private final Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_community,container,false);
//        listView = view.findViewById(R.id.post_display);
        searchBtn = view.findViewById(R.id.search_btn);
        uploadBtn = view.findViewById(R.id.floating_button);
//        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
//        postAdapter = new PostListAdapter(getActivity(),R.layout.post_item,posts,userInfos);
        lsda=view.findViewById(R.id.community_top);
        tabLayout = view.findViewById(R.id.tbl);
        viewPager2 = view.findViewById(R.id.vp2);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        status = sharedPreferences.getString("status","");
        setListener();
//        initData();
        initPage();
        //添加沉浸式状态栏
        MyViewUtils.setISBarWithoutView(getActivity(),true);
        adapter = new PageAdapter(fragments,getActivity());

        viewPager2.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {

                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:
                                tab.setText("推荐");
                                break;
                            case 1:
                                tab.setText("关注");
                                break;
                            default:
                                break;
                        }
                    }
                }
        );
        mediator.attach();
        return view;
    }

    private void initPage() {
        fragments = new ArrayList<>();
        fragments.add(new PostsFragment());
        fragments.add(new FollowFragment());
    }


    public void setListener(){
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转搜索页
                Intent intent = new Intent(getActivity(), PostSearchActivity.class);
                startActivity(intent);
            }
        });
//        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
//        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                page = 0;
//                posts = new ArrayList<>();
//                userInfos = new ArrayList<>();
//                initData();
//                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
//            }
//        });
//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//                page++;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        OkHttpClient client = new OkHttpClient();
//                        //创建请求获取Post类
//                        Request request = new Request.Builder()
//                                .url(url+"?page="+ page)
//                                .build();
//                        try {
//                            //发起请求并获取响应
//                            Response response = client.newCall(request).execute();
//                            //检测响应是否成功
//                            if (response.isSuccessful()){
//                                //获取响应数据
//                                ResponseBody responseBody = response.body();
//                                if (responseBody!=null){
//                                    //处理数据
//                                    String responseData = responseBody.string();
//                                    Gson gson = new Gson();
//                                    List<PostWithUserInfo> postWithUserInfoList = gson.fromJson(responseData,new TypeToken<List<PostWithUserInfo>>(){}.getType());
//                                    for (PostWithUserInfo postWithUserInfo: postWithUserInfoList){
//                                        posts.add(postWithUserInfo.getPost());
//                                        userInfos.add(postWithUserInfo.getUserInfo());
//                                    }
//                                    handler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (posts !=null&&userInfos!=null){
//                                                postAdapter.notifyDataSetChanged();
//                                            }else {
//
//                                            }
//
//                                        }
//                                    });
//
//                                }else {
//                                    //处理空数据
//                                }
//                            }
//                        }catch (IOException e){
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
//            }
//        });
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
//                PostListAdapter postListAdapter = (PostListAdapter) parent.getAdapter();
//                //获取点击项数据对象
//                PostWithUserInfo clickItem = (PostWithUserInfo) postListAdapter.getItem(i);
//                Intent intent = new Intent(getActivity(), PostDisplayActivity.class);
//                intent.putExtra("postwithuserinfo", clickItem);
//                startActivityForResult(intent,1);
//            }
//        });

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
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1) { // 检查请求码是否与上传页面的请求码一致
//            if (resultCode == Activity.RESULT_OK) {
//                // 检查是否上传完成
//                // 进行刷新操作，重新加载数据
//                page = 0;
//                posts = new ArrayList<>();
//                userInfos = new ArrayList<>();
//                initData();
//            }
//        }
//    }

}