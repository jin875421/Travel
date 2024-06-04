package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.FollowGroupListAdapter;
import glue502.software.adapters.FollowListAdapter;
import glue502.software.models.Follow;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyFollowActivity extends AppCompatActivity implements View.OnClickListener {
    private String url = "http://" + ip + "/travel";
    private String userId;
    private RelativeLayout title;
    private ImageView back, titleMenu;
    private TextView tvTitle;
    private TextView unfollowCancel, unfollowSure, selectGroup;
    private LinearLayout groupLayout;
    private CoordinatorLayout rootLayout;
    private SmartRefreshLayout refreshLayout;
    private PopupWindow groupPopupWindow;
    private Handler handler;
    private ListView followListView, followGroupListView;
    private List<UserInfo> followUserInfoList;
    private List<String> followGroupList;
    private List<Follow> followList;
    private View bottomSheet, coverView,groupSetting;
    // 抽屉布局
    private RelativeLayout myFollowDrawer;
    private RelativeLayout rlUserInfo;
    private TextView tvUserName;
    private TextView tvUserId;
    private TextView tvClose;
    private LinearLayout llOption;
    private LinearLayout llSetGroup, llSeeMore;
    private LinearLayout llUnfollow;
    private TextView tvUnfollow;
    // 抽屉布局对应的userInfo
    private UserInfo drawerUserInfo;
    private int position;

    private BottomSheetBehavior<View> behavior, behavior2;
    private FollowListAdapter followListAdapter;
    private FollowGroupListAdapter followGroupListAdapter;
    private Handler adapterHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);

        SharedPreferences sharedPreferences = this.getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        // 初始化布局中的组件
        initViews();

        //
        setHandler();

        //初始化数据
        initData();

        //设置沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this, title, true);

        // 设置点击事件
        setClickListeners();

        // 设置ListView的Adapter
        setListAdapter();

        // 设置SmartRefreshLayout的刷新和加载更多监听
        setRefreshLayoutListener();
    }

    private void initViews() {
        rootLayout = findViewById(R.id.root_layout);
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

        coverView = findViewById(R.id.cover_view);
        // 用户信息抽屉布局
        bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        // 分组信息布局
        groupSetting = findViewById(R.id.group_setting);
        behavior2 = BottomSheetBehavior.from(groupSetting);
        followGroupListView = findViewById(R.id.group_setting_list);

        // 用户信息抽屉布局的控件
        myFollowDrawer = findViewById(R.id.my_follow_drawer);
        rlUserInfo = findViewById(R.id.rl_userInfo);
        tvUserName = findViewById(R.id.tv_userName);
        tvUserId = findViewById(R.id.tv_userId);
        tvClose = findViewById(R.id.tv_close);
        llOption = findViewById(R.id.ll_option);
        llSetGroup = findViewById(R.id.ll_set_group);
        llSeeMore = findViewById(R.id.ll_see_more);
        llUnfollow = findViewById(R.id.ll_unfollow);
        tvUnfollow = findViewById(R.id.tv_unfollow);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickListeners() {
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NotNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if(behavior2.getState()==BottomSheetBehavior.STATE_HIDDEN){
                            coverView.setVisibility(View.GONE);
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        behavior2.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NotNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        coverView.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        coverView.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // 触摸到屏幕时，隐藏底部抽屉
                            // 获取触摸点的坐标
                            float x = event.getX();
                            float y = event.getY();
                            // 判断触摸点是否在抽屉1区域内
                            if(behavior.getState()!=BottomSheetBehavior.STATE_HIDDEN){
                                if (!isPointInsideView(bottomSheet, x, y)) {
                                    if (behavior != null) {
                                        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                        coverView.setVisibility(View.GONE);
                                    }
                                }
                            }
                            if (behavior2.getState()!=BottomSheetBehavior.STATE_HIDDEN){
                                if (!isPointInsideView(groupSetting, x, y)) {
                                    if (behavior2 != null) {
                                        behavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                                        coverView.setVisibility(View.GONE);
                                    }
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            // 松开手指时的逻辑处理
                            break;
                        case MotionEvent.ACTION_MOVE:
                            // 手指移动时的逻辑处理
                            break;
                    }
                    return true;
                });
        llSetGroup.setOnClickListener(v -> {
            if (followGroupList.size() == 0) {
                Toast.makeText(MyFollowActivity.this, "您还没有分组", Toast.LENGTH_SHORT).show();
            } else {
                followGroupListAdapter = new FollowGroupListAdapter(getApplicationContext(),
                        R.layout.adapter_follow_group_item,
                        followGroupList,
                        followList,
                        drawerUserInfo.getUserId());
                followGroupListView.setAdapter(followGroupListAdapter);
                // 收起用户信息抽屉
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                // 弹出分组信息抽屉
                behavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                // 覆盖层
                coverView.setVisibility(View.VISIBLE);
            }

        });
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 收起用户信息抽屉
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                // 覆盖层
                coverView.setVisibility(View.GONE);
            }
        });
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
                if (unfollowIdList == null || unfollowIdList.size() == 0) {
                    Toast.makeText(MyFollowActivity.this, "请选择用户", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("MyFollowActivity", unfollowIdList.toString());
                showUnfollowDialog(unfollowIdList);
            }
        });
        // 这个绑定的是activity实现的点击事件
        selectGroup.setOnClickListener(this);
        llUnfollow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Activity实现View.OnClickListener接口设置监听
        switch (v.getId()) {
            case R.id.select_group:
                showPopupWindow();
                break;
            case R.id.ll_unfollow:
                int isFollow[] = followListAdapter.getIsFollow();
                isFollow[position] = 0;
                List<String> unfollowIdList = new ArrayList<>();
                unfollowIdList.add(drawerUserInfo.getUserId());
                unFollowUsers(unfollowIdList);
                followListAdapter.setIsFollow(isFollow);
                followListAdapter.notifyDataSetChanged();
            default:
                break;
        }
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
//                                        initData();
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
        // 创建并设置关注列表展示的Adapter
        followListAdapter = new FollowListAdapter(getApplicationContext(),
                R.layout.adapter_follow_item,
                followUserInfoList,
                userId,
                FollowListAdapter.NORMAL_TYPE,
                adapterHandler);
        followListView.setAdapter(followListAdapter);
    }

    private void setHandler() {
        handler = new Handler(Looper.getMainLooper());
        adapterHandler = new Handler(new Handler.Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        drawerUserInfo = (UserInfo) msg.obj;
                        if (drawerUserInfo != null) {
                            //获取用户信息成功
                            tvUserName.setText(drawerUserInfo.getUserName());
                            tvUserId.setText("万里录ID: "+drawerUserInfo.getUserId());
                        } else {
                            //获取用户信息失败
                            Toast.makeText(MyFollowActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                        }
                        position = msg.arg1;
                        //召唤底部抽屉
                        coverView.setVisibility(View.VISIBLE);
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        return true;
                    default:
                        return false;
                }
            }
        });
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
        //隐藏底部抽屉
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        behavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
        int height = getResources().getDisplayMetrics().heightPixels;
        behavior.setExpandedOffset(height - dpToPx(this,280));
        behavior2.setExpandedOffset(height - dpToPx(this,400));

        followUserInfoList = new ArrayList<>();
        followGroupList = new ArrayList<>();
        followList = new ArrayList<>();
        //开启线程获取关注列表
        new Thread(new Runnable() {
            public void run() {
                //获取关注列表
                OkHttpClient client = new OkHttpClient();

                Request getFollowUserInfoList = new Request.Builder()
                        .url(url + "/follow/getFollowUserInfoList?userId=" + userId)
                        .build();
                Request getFollowList = new Request.Builder()
                        .url(url + "/follow/getFollowList?userId=" + userId)
                        .build();
                Request getGroupList = new Request.Builder()
                        .url(url + "/follow/getGroupInfo?userId=" + userId)
                        .build();
                try {
                    //发起请求并获取响应
                    Response followUserInfoListResponse = client.newCall(getFollowUserInfoList).execute();
                    Response followListResponse = client.newCall(getFollowList).execute();
                    Response groupListResponse = client.newCall(getGroupList).execute();
                    //检测响应是否成功
                    if (followUserInfoListResponse.isSuccessful() && followListResponse.isSuccessful()) {
                        //获取响应数据
                        ResponseBody responseBody = followUserInfoListResponse.body();
                        ResponseBody responseBody1 = followListResponse.body();
                        if (responseBody != null) {
                            //处理数据
                            String responseData = responseBody.string();
                            String responseData1 = responseBody1.string();
                            Gson gson = new Gson();
                            followUserInfoList = gson.fromJson(responseData, new TypeToken<List<UserInfo>>() {
                            }.getType());
                            followList = gson.fromJson(responseData1, new TypeToken<List<Follow>>() {
                            }.getType());
                            Log.i("MyFollowActivity", followList.toString());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (followUserInfoList != null) {
                                        followListAdapter = new FollowListAdapter(MyFollowActivity.this,
                                                R.layout.adapter_follow_item,
                                                followUserInfoList, userId,
                                                FollowListAdapter.NORMAL_TYPE,
                                                adapterHandler);
                                        followListView.setAdapter(followListAdapter);
                                    } else {
                                    }
                                }
                            });

                        } else {
                            //处理空数据
                            Log.e("MyFollowActivity", "获取关注列表失败");
                        }
                    } else {
                        Log.e("MyFollowActivity", "获取关注列表失败");
                    }
                    if (groupListResponse.isSuccessful()) {
                        //获取响应数据
                        ResponseBody responseBody = groupListResponse.body();
                        if (responseBody != null) {
                            //处理数据
                            String responseData = responseBody.string();
                            Gson gson = new Gson();
                            followGroupList = gson.fromJson(responseData, new TypeToken<List<String>>() {
                            }.getType());
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

    private void showPopupWindow() {
        // 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_popup_item, followGroupList);

        // 设置下拉菜单的布局
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_list_view, null, false);
        ListView listView = view.findViewById(R.id.popupwindow_list_view);

        // 创建 PopupWindow 对象
        groupPopupWindow = new PopupWindow(view);
        listView.setAdapter(adapter);

        // 设置背景并使其可点击
        groupPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        groupPopupWindow.setOutsideTouchable(true);

        // 在背景点击时关闭 PopupWindow
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (groupPopupWindow != null && groupPopupWindow.isShowing()) {
                    groupPopupWindow.dismiss();
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
                List<String> filteredIdList = new ArrayList<>();
                Gson gson = new Gson();
                for (Follow follow : followList) {
                    List<String> groups = gson.fromJson(follow.getGroupOf(), new TypeToken<List<String>>() {
                    }.getType());
                    if (groups.contains(selectGroupName)) {
                        filteredIdList.add(follow.getFollowId());
                    }
                }
                List<UserInfo> filteredUserInfoList = new ArrayList<>();
                for (UserInfo userInfo : followUserInfoList) {
                    if (filteredIdList.contains(userInfo.getUserId())) {
                        filteredUserInfoList.add(userInfo);
                    }
                }
                followListAdapter.setUserInfoList(filteredUserInfoList);
                followListAdapter.notifyDataSetChanged();
                // 点击后关闭下拉菜单
                groupPopupWindow.dismiss();
            }
        });
        // 设置下拉菜单的宽度和高度
        groupPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        groupPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // 显示下拉菜单
        View anchorView = findViewById(R.id.select_group); // 这里的 anchor_view 是你想要下拉菜单显示在哪个 View 附近的 id
        int offsetX = 0; // X 轴偏移量
        int offsetY = 0; // Y 轴偏移量
        groupPopupWindow.showAsDropDown(anchorView, offsetX, offsetY);
    }

    // 判断坐标点是否在指定视图内部的方法
    private boolean isPointInsideView(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        // 判断触摸点是否在指定视图内部
        return (x > viewX && x < (viewX + view.getWidth()) && y > viewY && y < (viewY + view.getHeight()));
    }

    // 将dp值转换为像素值
    public static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // 将像素值转换为dp值
    public static float pxToDp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (groupPopupWindow != null && groupPopupWindow.isShowing()){
            groupPopupWindow.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (groupPopupWindow != null && groupPopupWindow.isShowing()){
            groupPopupWindow.dismiss();
        }
    }
}