package glue502.software.fragments;

import static glue502.software.activities.MainActivity.ip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;

import glue502.software.R;
import glue502.software.activities.CodeLoginActivity;
import glue502.software.activities.SettingActivity;
import glue502.software.activities.UpdatePersonalInformationActivity;
import glue502.software.models.LoginResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PersonalInformationFragment extends Fragment {
    private TextView txtUserNickname;
    private TextView txtUserId;
    private ImageView imgSetting;
    private LinearLayout linearTitle;
    private LinearLayoutManager layoutManager;
    private ImageView imgAvatar;
    private RecyclerView recyclerView;
    private String url="http://"+ip+"/test/user/updateNickname";
    private String urlAvatar="http://"+ip+"/test/user/getAvatar?userId=";
    private String urlLoadImage="http://"+ip+"/test/";
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
        txtUserNickname=view.findViewById(R.id.txt_userNickname);
        txtUserId=view.findViewById(R.id.txt_userId);
        imgSetting=view.findViewById(R.id.img_setting);
        linearTitle=view.findViewById(R.id.linear_title);
        recyclerView = view.findViewById(R.id.recyclerView);
        imgAvatar=view.findViewById(R.id.img_avatar);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


//        RecyclerbviewAdapter adapter = new RecyclerbviewAdapter(requireContext(),data);
//        recyclerView.setAdapter(adapter);


        linearTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoginStatus();
            }
        });


        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
//        fetchDataFromBackend();
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
            startActivity(intent);
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
    @Override
    public void onResume() {
        super.onResume();
        //加载数据
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        String status=sharedPreferences.getString("status","");
        if("".equals(status)){
            txtUserNickname.setText("请登录");
            txtUserId.setText("");
        }else{
            String userNickname=sharedPreferences.getString("userName","");
            String userId=sharedPreferences.getString("userId","");
            txtUserNickname.setText(userNickname);
            txtUserId.setText(userId);

        }
        loadUserAvatar();

    }
    private void loadUserAvatar() {
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
                final String avatarUrl = response.body().string();

                // 在 UI 线程中更新 ImageView
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用 Glide 加载用户头像
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            loadImageWithGlide(avatarUrl);
                        } else {
                            // 处理返回的不是有效地址的情况
                            // 可以设置默认头像或给用户提示
                            Glide.with(requireContext())
                                    .load(R.drawable.ic_launcher_background)
                                    .into(imgAvatar);
                        }
                    }
                });
            }
        });
    }


    private void loadImageWithGlide(String avatarUrl) {
        // 使用 Glide 加载用户头像
        Glide.with(requireContext())
                .load(urlLoadImage + avatarUrl)
                .placeholder(R.drawable.ic_launcher_background)  // 设置占位图
                .into(imgAvatar);
    }
}