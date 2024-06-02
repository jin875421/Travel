package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.travelRecord.TravelDetailActivity;
import glue502.software.adapters.FollowListAdapter;
import glue502.software.adapters.PostListAdapter;
import glue502.software.models.Follow;
import glue502.software.models.PostWithUserInfo;
import glue502.software.models.UserInfo;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyFollowActivity extends AppCompatActivity implements View.OnClickListener {
    private String url = "http://"+ip+"/travel";
    private String userId;
    private RelativeLayout title;
    private ImageView back;
    private ImageView titleMenu;
    private TextView tvTitle;
    private TextView unfollowCancel;
    private TextView unfollowSure;
    private TextView selectGroup;
    private LinearLayout groupLayout;
    private SmartRefreshLayout refreshLayout;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ListView followListView;
    private List<UserInfo> followUserInfoList;
    private List<String> followGroupList;
    private List<Follow> followList;
    private FollowListAdapter followListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);

        SharedPreferences sharedPreferences = this.getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");

        // 初始化布局中的组件
        initViews();

        //初始化数据
        initData();

        //设置沉浸式状态栏


        // 设置标题和返回按钮的点击事件
        setClickListeners();

        // 设置ListView的Adapter
        setListAdapter();

        // 设置SmartRefreshLayout的刷新和加载更多监听
        setRefreshLayoutListener();
    }

    private void initViews() {
        ConstraintLayout rootLayout = findViewById(R.id.root_layout); // 如果有ConstraintLayout的id
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        tvTitle = findViewById(R.id.title_text);
        refreshLayout = findViewById(R.id.refreshLayout);
        followListView = findViewById(R.id.travel_review);
        titleMenu = findViewById(R.id.title_menu);
        unfollowCancel = findViewById(R.id.unfollow_cancel);
        unfollowSure = findViewById(R.id.unfollow_sure);

        selectGroup = findViewById(R.id.select_group);
        groupLayout = findViewById(R.id.group_layout);
    }

    private void setClickListeners() {
        selectGroup.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回上一个Activity
            }
        });
        titleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                showPopupMenu(v);
            }
        });
        unfollowCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取消批量取关
                followListAdapter.setType(FollowListAdapter.NORMAL_TYPE);
                followListAdapter.notifyDataSetChanged();
                unfollowCancel.setVisibility(View.GONE);
                unfollowSure.setVisibility(View.GONE);
                titleMenu.setVisibility(View.VISIBLE);
            }
        });
        unfollowSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹窗确认
                List<String> unfollowIdList = followListAdapter.getUnfollowIdList();
                if(unfollowIdList == null || unfollowIdList.size() == 0){
                    Toast.makeText(MyFollowActivity.this, "请选择用户", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("MyFollowActivity", unfollowIdList.toString());
                showUnfollowDialog(unfollowIdList);
            }
        });
    }

    private void showUnfollowDialog(List<String> unfollowIdList) {
        // 创建Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);

        // 获取Dialog中的控件
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView sure = dialog.findViewById(R.id.dialog_button_sure);
        TextView cancel = dialog.findViewById(R.id.dialog_button_cancel);
        dialogTitle.setText("不再关注这些用户？");
        // 设置按钮点击事件 (尝试新写法)
        sure.setOnClickListener(v -> {
            unFollowUsers(unfollowIdList);
            // 关闭Dialog
            dialog.dismiss();
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭Dialog
                dialog.dismiss();
            }
        });
        // 显示Dialog
        dialog.show();
    }

    private void unFollowUsers(List<String> unfollowIdList) {
        Gson gson = new Gson();
        String unfollowIds = gson.toJson(unfollowIdList);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //执行取消关注
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("userId", userId)
                        .addFormDataPart("unfollowIds", unfollowIds)
                        .build();
                Request request = new Request.Builder()
                        .url(url + "/follow/deleteFollows")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //获取返回消息
                                switch (responseData) {
                                    case "success":
                                        //取消关注成功
                                        initData();
                                        unfollowCancel.setVisibility(View.GONE);
                                        unfollowSure.setVisibility(View.GONE);
                                        titleMenu.setVisibility(View.VISIBLE);
                                        Toast.makeText(MyFollowActivity.this, "取关成功", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "fail":
                                        //取消关注失败
                                        Toast.makeText(MyFollowActivity.this, "请选择用户", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "Unknown records exist":
                                        Toast.makeText(MyFollowActivity.this, "存在未知记录", Toast.LENGTH_SHORT).show();
                                    default:
                                        //未知错误
                                        Toast.makeText(MyFollowActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void showPopupMenu(View v) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(MyFollowActivity.this, v);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.follow_title_menu, popupMenu.getMenu());
        //显示PopupMenu
        popupMenu.show();
        // 添加菜单项点击监听器
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.unfollow:
                        // 进入批量取关
                        followListAdapter.setType(FollowListAdapter.UNFOLLOW_TYPE);
                        followListAdapter.notifyDataSetChanged();
                        unfollowCancel.setVisibility(View.VISIBLE);
                        unfollowSure.setVisibility(View.VISIBLE);
                        titleMenu.setVisibility(View.GONE);
                        break;
                    case R.id.cancel:
                        // 取消
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setListAdapter() {
        // 创建并设置Adapter
        followListAdapter = new FollowListAdapter(getApplicationContext(),R.layout.adapter_fellow_item,followUserInfoList,userId,FollowListAdapter.NORMAL_TYPE);
        followListView.setAdapter(followListAdapter);
    }

    private void setRefreshLayoutListener() {
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    private void initData() {
        followUserInfoList = new ArrayList<>();
        followGroupList = new ArrayList<>();
        followList = new ArrayList<>();
        //开启线程获取关注列表
        new Thread(new Runnable() {
            public void run() {
                //获取关注列表
                OkHttpClient client = new OkHttpClient();

                Request getFollowUserInfoList = new Request.Builder()
                        .url(url+"/follow/getFollowUserInfoList?userId="+userId)
                        .build();
                Request getFollowList = new Request.Builder()
                        .url(url+"/follow/getFollowList?userId="+userId)
                        .build();
                Request getGroupList = new Request.Builder()
                        .url(url+"/follow/getGroupInfo?userId="+userId)
                        .build();
                try {
                    //发起请求并获取响应
                    Response followUserInfoListResponse = client.newCall(getFollowUserInfoList).execute();
                    Response followListResponse = client.newCall(getFollowList).execute();
                    Response groupListResponse = client.newCall(getGroupList).execute();
                    //检测响应是否成功
                    if (followUserInfoListResponse.isSuccessful() && followListResponse.isSuccessful()){
                        //获取响应数据
                        ResponseBody responseBody = followUserInfoListResponse.body();
                        ResponseBody responseBody1 = followListResponse.body();
                        if (responseBody!=null){
                            //处理数据
                            String responseData = responseBody.string();
                            String responseData1 = responseBody1.string();
                            Gson gson = new Gson();
                            followUserInfoList = gson.fromJson(responseData,new TypeToken<List<UserInfo>>(){}.getType());
                            followList = gson.fromJson(responseData1,new TypeToken<List<Follow>>(){}.getType());
                            Log.i("MyFollowActivity",followList.toString());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (followUserInfoList != null){
                                        followListAdapter = new FollowListAdapter(MyFollowActivity.this,R.layout.adapter_fellow_item,followUserInfoList,userId,FollowListAdapter.NORMAL_TYPE);
                                        followListView.setAdapter(followListAdapter);
                                    }else {
                                    }
                                }
                            });

                        }else {
                            //处理空数据
                            Log.e("MyFollowActivity","获取关注列表失败");
                        }
                    } else {
                        Log.e("MyFollowActivity","获取关注列表失败");
                    }
                    if (groupListResponse.isSuccessful()){
                        //获取响应数据
                        ResponseBody responseBody = groupListResponse.body();
                        if (responseBody!=null){
                            //处理数据
                            String responseData = responseBody.string();
                            Gson gson = new Gson();
                            followGroupList = gson.fromJson(responseData,new TypeToken<List<String>>(){}.getType());
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                }
//                            });
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        // Activity实现View.OnClickListener接口设置监听
        switch (v.getId()){
            case R.id.select_group:
                showPopupWindow();
                break;
            default:
                break;
        }
    }

    private void showPopupWindow() {
        // 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_popup_item, followGroupList);

        // 设置下拉菜单的布局
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_list_view, null, false);
        ListView listView = view.findViewById(R.id.popupwindow_list_view);

        // 创建 PopupWindow 对象
        PopupWindow popupWindow = new PopupWindow(view);
        listView.setAdapter(adapter);

        // 设置背景并使其可点击
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        // 在背景点击时关闭 PopupWindow
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectGroupName = followGroupList.get(position);
                selectGroup.setText(selectGroupName);
                // 执行点击列表项后的操作
//                Toast.makeText(MyFollowActivity.this, "你点击了：" + selectedItem, Toast.LENGTH_SHORT).show();
                List<String> filteredIdList = new ArrayList<>();
                Gson gson = new Gson();
                for (Follow follow : followList) {
                    List<String> groups = gson.fromJson(follow.getGroupOf(), new TypeToken<List<String>>() {}.getType());
                    if (groups.contains(selectGroupName)) {
                        filteredIdList.add(follow.getFollowId());
                    }
                }
                List<UserInfo> filteredUserInfoList = new ArrayList<>();
                for(UserInfo userInfo : followUserInfoList){
                    if(filteredIdList.contains(userInfo.getUserId())){
                        filteredUserInfoList.add(userInfo);
                    }
                }
                followListAdapter.setUserInfoList(filteredUserInfoList);
                followListAdapter.notifyDataSetChanged();
                // 点击后关闭下拉菜单
                popupWindow.dismiss();
            }
        });
        // 设置下拉菜单的宽度和高度
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // 显示下拉菜单
        View anchorView = findViewById(R.id.select_group); // 这里的 anchor_view 是你想要下拉菜单显示在哪个 View 附近的 id
        int offsetX = 0; // X 轴偏移量
        int offsetY = 0; // Y 轴偏移量
        popupWindow.showAsDropDown(anchorView, offsetX, offsetY);
    }
}