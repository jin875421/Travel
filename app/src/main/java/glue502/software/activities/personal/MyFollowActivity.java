package glue502.software.activities.personal;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
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
import glue502.software.models.UserExtraInfo;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyFollowActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "MyFollowActivity";
    private String url = "http://" + ip + "/travel";
    Gson gson = new Gson();
    private String userId;
    private RelativeLayout title;
    private ImageView back, titleMenu,toAddFollow;
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
    private View bottomSheet, coverView, groupSetting;
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
    private UserInfo selectUserInfo;
    private int position;

    // 群组设置布局
    private TextView groupSettingCancel, groupSettingAdd;
    private Button groupSettingSave;

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

        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);

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
        toAddFollow = findViewById(R.id.to_add_follow);
        unfollowCancel = findViewById(R.id.unfollow_cancel);
        unfollowSure = findViewById(R.id.unfollow_sure);

        selectGroup = findViewById(R.id.select_group);
        groupLayout = findViewById(R.id.group_layout);

        coverView = findViewById(R.id.cover_view);
        // 用户信息抽屉布局
        bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

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


        // 分组信息布局
        groupSetting = findViewById(R.id.group_setting);
        behavior2 = BottomSheetBehavior.from(groupSetting);
        followGroupListView = findViewById(R.id.group_setting_list);
        groupSettingCancel = findViewById(R.id.group_setting_cancel);
        groupSettingAdd = findViewById(R.id.group_setting_add);
        groupSettingSave = findViewById(R.id.group_setting_save);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickListeners() {
        // 主界面
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NotNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if (behavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
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
                    if (behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                        if (!isPointInsideView(bottomSheet, x, y)) {
                            if (behavior != null) {
                                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                coverView.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (behavior2.getState() != BottomSheetBehavior.STATE_HIDDEN) {
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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回上一个Activity
            }
        });
        toAddFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyFollowActivity.this, FollowSearchActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
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

        followListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 转跳到用户信息界面
                Intent intent = new Intent(MyFollowActivity.this, UserInfoActivity.class);
//                Toast.makeText(MyFollowActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();
                intent.putExtra("AuthorId", followList.get(position).getFollowId());
                //跳转
                startActivity(intent);

            }
        });

        // 用户信息界面
        llSetGroup.setOnClickListener(v -> {
            if (followGroupList.size() == 0) {
                Toast.makeText(MyFollowActivity.this, "您还没有分组", Toast.LENGTH_SHORT).show();
            } else {
                followGroupListAdapter = new FollowGroupListAdapter(getApplicationContext(),
                        R.layout.adapter_follow_group_item,
                        followGroupList,
                        followList,
                        selectUserInfo.getUserId(),
                        adapterHandler);
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
        llUnfollow.setOnClickListener(this);
        //分组设置界面
        groupSettingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭分组设置
                behavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                // 覆盖层
                coverView.setVisibility(View.GONE);
            }
        });
        selectGroup.setOnClickListener(this);
        groupSettingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selectGroupList = followGroupListAdapter.getSelectGroupList();
                String newGroupOf = gson.toJson(selectGroupList);
                Follow updateFollow = null;
                for (Follow follow : followList) {
                    if (follow.getFollowId().equals(selectUserInfo.getUserId())) {
                        follow.setGroupOf(newGroupOf);
                        updateFollow = follow;
                        Log.i(TAG, "updateFollow" + updateFollow.toString());
                    }
                }
                // 更新数据到服务器
                updateFollow(updateFollow);
                followGroupListAdapter.notifyDataSetChanged();
                Log.i(TAG, "更新本地分组信息");
                behavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        groupSettingAdd.setOnClickListener(v -> showAddGroupDialog());
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
                unfollowIdList.add(selectUserInfo.getUserId());
                unFollowUsers(unfollowIdList);
                followListAdapter.setIsFollow(isFollow);
                followListAdapter.notifyDataSetChanged();
            default:
                break;
        }
    }

    private void updateFollow(Follow updateFollow) {
        if (updateFollow != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
//                            .addFormDataPart("userId", updateFollow.getUserId())
//                            .addFormDataPart("followId", updateFollow.getFollowId())
//                            .addFormDataPart("groupOf", updateFollow.getGroupOf())
                            .addFormDataPart("followStr", gson.toJson(updateFollow))
                            .build();
                    Request request = new Request.Builder()
                            .url(url + "/follow/updateFollow")
                            .post(requestBody)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        if (responseData.equals("success")) {
                            Log.i(TAG, "更新分组信息成功");
                        } else {
                            Log.i(TAG, "更新分组信息失败");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Log.i(TAG, "updateFollow is null");
        }
    }

    private void showAddGroupDialog() {
        // 创建Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_edit_dialog_layout);

        // 获取Dialog中的控件
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView sure = dialog.findViewById(R.id.dialog_button_sure);
        TextView cancel = dialog.findViewById(R.id.dialog_button_cancel);
        EditText editText = dialog.findViewById(R.id.dialog_input);
        dialogTitle.setText("新建分组");

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    String newGroup = editText.getText().toString();
                    if (followGroupList.contains(newGroup)) {
                        Toast.makeText(MyFollowActivity.this, "该分组已存在", Toast.LENGTH_SHORT).show();
                    } else {
                        followGroupList.add(newGroup);
                        followGroupListAdapter.notifyDataSetChanged();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UserExtraInfo updateUserExtraInfo = new UserExtraInfo(userId, gson.toJson(followGroupList));
                                OkHttpClient client = new OkHttpClient();
                                Log.i(TAG, "updateUserExtraInfoStr" + updateUserExtraInfo.toString());
                                RequestBody requestBody = new MultipartBody.Builder()
                                        .addFormDataPart("updateUserExtraInfoStr", gson.toJson(updateUserExtraInfo))
                                        .build();
                                Request request = new Request.Builder()
                                        .url(url + "/userExtraInfo/update")
                                        .post(requestBody)
                                        .build();
                                try {
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    if (responseData.equals("success")) {
                                        Log.i(TAG, "新建分组：" + newGroup);
                                    } else {
                                        Log.i(TAG, "新建分组失败");
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).start();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(MyFollowActivity.this, "请输入分组名称", Toast.LENGTH_SHORT).show();
                }
            }
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
                        toAddFollow.setVisibility(View.GONE);
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
                        selectUserInfo = (UserInfo) msg.obj;
                        if (selectUserInfo != null) {
                            //获取用户信息成功
                            tvUserName.setText(selectUserInfo.getUserName());
                            tvUserId.setText("万里录ID: " + selectUserInfo.getUserId());
                        } else {
                            //获取用户信息失败
                            Toast.makeText(MyFollowActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                        }
                        position = msg.arg1;
                        //召唤底部抽屉
                        coverView.setVisibility(View.VISIBLE);
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        return true;
                    case 1:
                        String deleteGroup = (String) msg.obj;
                        int deleteGroupPosition = msg.arg1;
                        // 创建Dialog
                        final Dialog dialog = new Dialog(MyFollowActivity.this);
                        dialog.setContentView(R.layout.custom_dialog_layout);
                        // 获取Dialog中的控件
                        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                        TextView sure = dialog.findViewById(R.id.dialog_button_sure);
                        TextView cancel = dialog.findViewById(R.id.dialog_button_cancel);
                        dialogTitle.setText("确定删除该分组吗？");
                        sure.setOnClickListener(v -> {
                            // 删除分组
                            List<String> updateGroupList = followGroupListAdapter.getGroupList();
                            updateGroupList.remove(deleteGroupPosition);
                            UserExtraInfo updateUserExtraInfo = new UserExtraInfo(userId, gson.toJson(updateGroupList));

                            // 记录被修改过的follow
                            List<Follow> updateFollowList = new ArrayList<>();
                            // 遍历followList中的分组信息，如果分组信息的字符串中包含该分组，则删除该字符串
                            for (int i = 0; i < followList.size(); i++) {
                                String followGroupInfo = followList.get(i).getGroupOf();
//                                Log.i(TAG, "修改前followGroupInfo" + followGroupInfo);
                                if (followGroupInfo.contains(deleteGroup)) {
                                    followGroupInfo = followGroupInfo.replace(",\""+deleteGroup+"\"", "");
                                    followList.get(i).setGroupOf(followGroupInfo);
//                                    Log.i(TAG, "修改后followGroupInfo" + followGroupInfo);
                                    updateFollowList.add(followList.get(i));
                                }
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    OkHttpClient client = new OkHttpClient();
                                    Log.i(TAG, "准备上传修改后的分组信息：" + updateUserExtraInfo.toString());
                                    RequestBody userExtraInfoRequestBody = new MultipartBody.Builder()
                                            .addFormDataPart("updateUserExtraInfoStr", gson.toJson(updateUserExtraInfo))
                                            .build();
                                    Request userExtraInfoRequest = new Request.Builder()
                                            .url(url + "/userExtraInfo/update")
                                            .post(userExtraInfoRequestBody)
                                            .build();
                                    Log.i(TAG, "准备上传修改后的关注信息：" + updateFollowList.toString());
                                    RequestBody followListRequestBody = new MultipartBody.Builder()
                                            .addFormDataPart("followsStr", gson.toJson(updateFollowList))
                                            .build();
                                    Request followListRequest = new Request.Builder()
                                            .url(url + "/follow/updateFollows")
                                            .post(followListRequestBody)
                                            .build();
                                    try {
                                        Response userExtraInfoResponse = client.newCall(userExtraInfoRequest).execute();
                                        String userExtraInfoResponseStr = userExtraInfoResponse.body().string();
                                        if (userExtraInfoResponseStr.equals("success")) {
                                            // 删除分组成功
                                            followGroupListAdapter.setGroupList(updateGroupList);
                                            Log.i(TAG, "删除分组"+deleteGroup+"成功");
                                        } else {
                                            // 删除分组失败
                                            Log.i(TAG, "删除分组"+deleteGroup+"失败");
                                        }
                                        Response followListResponse = client.newCall(followListRequest).execute();
                                        String followListResponseStr = followListResponse.body().string();
                                        if (followListResponseStr.equals("success")) {
                                            // 修改关注信息成功
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    followGroupListAdapter.notifyDataSetChanged();
                                                }
                                            });
                                            Log.i(TAG, "修改关注信息成功");
                                        } else {
                                            // 删除关注信息失败
                                            Log.i(TAG, "修改关注信息失败");
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }).start();
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
        behavior.setExpandedOffset(height - dpToPx(this, 280));
        behavior2.setExpandedOffset(height - dpToPx(this, 400));

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
        if (groupPopupWindow != null && groupPopupWindow.isShowing()) {
            groupPopupWindow.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (groupPopupWindow != null && groupPopupWindow.isShowing()) {
            groupPopupWindow.dismiss();
        }
    }
}