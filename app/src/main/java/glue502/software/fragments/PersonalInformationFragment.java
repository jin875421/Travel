package glue502.software.fragments;

import static glue502.software.activities.MainActivity.ip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import glue502.software.R;
import glue502.software.activities.login.CodeLoginActivity;
import glue502.software.activities.login.LoginActivity;
import glue502.software.activities.personal.MineStarActivity;
import glue502.software.activities.personal.SettingActivity;
import glue502.software.activities.personal.UpdatePersonalInformationActivity;
import glue502.software.activities.posts.UploadPostActivity;
import glue502.software.models.LoginResult;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PersonalInformationFragment extends Fragment {
    private TextView txtName;
    private TextView txtUserId;
    private TextView txtCollect;
    private TextView txtNews;
    private TextView txtPublish;
    private TextView txtHistory;
    private TextView txtPersonal;
    private LinearLayout linearSetting;
    private LinearLayout linearTitle;
    private LinearLayout linearCustomerService;
    private ImageView imgAvatar;
    private String urlAvatar="http://"+ip+"/travel/user/getAvatar?userId=";
    private String urlLoadImage="http://"+ip+"/travel/";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    Gson gson = new Gson();
                    LoginResult loginResult = gson.fromJson(result, LoginResult.class);
                    int resultCode = loginResult.getResultCode();
                    String message = loginResult.getMsg();

                    // 根据 resultCode 判断登录是否成功
                    if (resultCode == 1) {
                        // 上传成功
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    } else {
                        // 上传失败
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_personal_information, container, false);
        txtName=view.findViewById(R.id.txt_name);
        txtUserId=view.findViewById(R.id.txt_userId);
        linearSetting=view.findViewById(R.id.linear_setting);
        linearTitle=view.findViewById(R.id.linear_title);
        linearCustomerService=view.findViewById(R.id.linear_customer_service);
        imgAvatar=view.findViewById(R.id.img_avatar);
        txtCollect=view.findViewById(R.id.txt_collect);
        txtNews=view.findViewById(R.id.txt_news);
        txtPublish=view.findViewById(R.id.txt_publish);
        txtHistory=view.findViewById(R.id.txt_history);
        txtPersonal=view.findViewById(R.id.txt_personal);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String status=sharedPreferences.getString("status","");
        if("".equals(status)){
            txtName.setText("请登录");
            txtUserId.setText("");
            RequestOptions requestOptions = new RequestOptions()
                    .transform(new CircleCrop());
            Glide.with(requireContext())
                    .load(R.drawable.ic_launcher_background )
                    .apply(requestOptions)// 设置签名
                    .into(imgAvatar);
        }else{
            String userName=sharedPreferences.getString("userName","");
            String userId=sharedPreferences.getString("userId","");
            txtName.setText(userName);
            txtUserId.setText("账号: "+userId);
            loadUserAvatar(false);

        }
        txtCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(status)){
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
                    Intent intent = new Intent(getActivity(), MineStarActivity.class);
                    startActivity(intent);
                }
            }
        });
        imgAvatar.setOnClickListener(v->{
            OpenImage.with(getContext()).setClickImageView(imgAvatar)
                    .setAutoScrollScanPosition(true)
                    .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP,true)
                    .addPageTransformer(new ScaleInTransformer())
                    .setImageUrlList(Collections.singletonList(urlAvatar+sharedPreferences.getString("userId", "")), MediaType.IMAGE)
//                    .setOnItemLongClickListener(new OnItemLongClickListener() {
//                        @Override
//                        public void onItemLongClick(BaseInnerFragment fragment, OpenImageUrl openImageUrl, int position) {
//                            Toast.makeText(getContext(),"长按图片",Toast.LENGTH_LONG).show();
//                        }
//                    })
                    .show();
        });
        txtNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        txtPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        txtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        txtPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdatePersonalInformationActivity.class);
                startActivity(intent);
            }
        });
        linearTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoginStatus();
            }
        });


        linearSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        linearCustomerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("联系客服");
                builder.setMessage("请拨打1008611或发送邮件到2391835196@qq.com");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 在这里执行确定按钮被点击后的操作
                        dialogInterface.dismiss(); // 关闭对话框
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 在这里执行取消按钮被点击后的操作
                        dialogInterface.dismiss(); // 关闭对话框
                    }
                });
                // 创建并显示AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return view;

    }



    private void checkLoginStatus() {
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String status=sharedPreferences.getString("status","");
        if ("".equals(status)) {
            // 用户未登录，弹出提示框
            showLoginAlertDialog();
        }else{
            Intent intent = new Intent(getActivity(), UpdatePersonalInformationActivity.class);
            startActivityForResult(intent, 1);
        }
    }



    private void showLoginAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("您未登录，是否登录？");
        builder.setPositiveButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 跳转到登录页面
                Intent intent = new Intent(getActivity(), CodeLoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 取消操作
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void loadUserAvatar(boolean a) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        // 创建 OkHttp 客户端
        OkHttpClient client = new OkHttpClient();

        // 构建请求
        Request request = new Request.Builder()
                .url(urlAvatar + userId)  // 替换为你的后端 API 地址
                .build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 处理请求失败的情况
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                Gson gson=new Gson();
                // 获取 avatarUrl 和 userNickname
                System.out.println(responseData);
                UserInfo userInfo = gson.fromJson(responseData,UserInfo.class);
                String avatarUrl=userInfo.getAvatar();
                String userName =userInfo.getUserName();
                // 在 UI 线程中更新 ImageView
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用 Glide 加载用户头像
                        if (avatarUrl!=null&&!avatarUrl.isEmpty()) {
                            loadImageWithGlide(avatarUrl,a);
                        } else {
                            // 处理返回的不是有效地址的情况
                            // 可以设置默认头像或给用户提示
                            RequestOptions requestOptions = new RequestOptions()
                                    .transform(new CircleCrop());
                            Glide.with(requireContext())
                                    .load(R.drawable.ic_launcher_background)
                                    .apply(requestOptions)
                                    .into(imgAvatar);
                        }
                        if(!userName.isEmpty()){
                            txtName.setText(userName);
                            txtUserId.setText("账号: "+userId);
                        }
                    }
                });
            }
        });
    }

    private void loadImageWithGlide(String avatarUrl, boolean forceRefresh) {
        // 使用 Glide 加载用户头像，并进行圆形裁剪
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        if(forceRefresh==false){
            // 如果需要强制刷新缓存，生成一个动态签名
            Glide.get(requireContext()).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(requireContext()).clearDiskCache();
                }
            }).start();
            Glide.with(requireContext())
                    .load(urlLoadImage + avatarUrl)
                    .skipMemoryCache(false)  //允许内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 使用磁盘缓存
                    .placeholder(R.drawable.ic_launcher_background)  // 设置占位图
                    .apply(requestOptions)
                    .signature(new ObjectKey(userId))  // 设置签名
                    .into(imgAvatar);
        }else{
            Glide.get(requireContext()).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(requireContext()).clearDiskCache();
                }
            }).start();
            Glide.with(requireContext())
                    .load(urlLoadImage + avatarUrl)
                    .skipMemoryCache(true)  //允许内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // 不使用磁盘缓存
                    .placeholder(R.drawable.ic_launcher_background)  // 设置占位图
                    .apply(requestOptions)
                    .signature(new ObjectKey(userId))  // 设置签名
                    .into(imgAvatar);


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { // 检查请求码是否与上传页面的请求码一致
            if (resultCode == Activity.RESULT_OK) { // 检查是否上传完成
                // 进行刷新操作，重新加载数据
                    loadUserAvatar(true);
            }
        }
        if(requestCode==2){
            if (resultCode == Activity.RESULT_OK){
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
                String status=sharedPreferences.getString("status","");
                if("".equals(status)){
                    txtName.setText("请登录");
                    txtUserId.setText("");
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new CircleCrop());
                    Glide.with(requireContext())
                            .load(R.drawable.ic_launcher_background )
                            .apply(requestOptions)// 设置签名
                            .into(imgAvatar);
                }else{
                    String userName=sharedPreferences.getString("userName","");
                    String userId=sharedPreferences.getString("userId","");
                    txtName.setText(userName);
                    txtUserId.setText("账号: "+userId);
                    loadUserAvatar(false);

                }
            }
        }
    }

}