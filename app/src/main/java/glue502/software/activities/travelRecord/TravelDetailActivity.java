package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.posts.PostEditActivity;
import glue502.software.adapters.TravelDetailAdapter;
import glue502.software.models.TravelReview;
import glue502.software.models.travelRecord;
import glue502.software.utils.MyViewUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TravelDetailActivity extends AppCompatActivity {
    private String travelId;
    private TextView travelName;
    private List<travelRecord> travelRecords;
    private ListView travelRecordList;
    private ImageView back;
    private String url = "http://"+ip+"/travel";
    private int checkedItemId = R.id.edit;
    private ImageView menuBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),false);
        //获取上页面传过来的travelId
        travelId = getIntent().getStringExtra("travelId");
        initView();
        setlistener();
        initData();
    }
    public void initView(){
        travelName = findViewById(R.id.travel_title);
        travelRecordList = findViewById(R.id.list_view);
        menuBtn = findViewById(R.id.popupmenu);
        back = findViewById(R.id.btn_back);
    }
    public void initData(){
        //获取数据
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url+"/travel/showATravel?travelId="+travelId).build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    travelRecords = new Gson().fromJson(responseData,new TypeToken<List<travelRecord>>(){}.getType());
                    //打开ui线程
                    runOnUiThread(()->{
                        travelName.setText(travelRecords.get(0).getTravelName());
                        TravelDetailAdapter travelRecordAdapter = new TravelDetailAdapter(TravelDetailActivity.this,travelRecords,R.layout.review_item);
                        travelRecordList.setAdapter(travelRecordAdapter);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void setlistener(){
        back.setOnClickListener(v -> finish());
        //如果为作者，提供修改和删除功能
        //弹出选择框，修改，删除，取消
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }
    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view){
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(TravelDetailActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        //设置选中
        popupMenu.getMenu().findItem(checkedItemId).setChecked(true);
        //使用反射。强制显示菜单图标
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //显示PopupMenu
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.delete:
                        //执行删除操作
                        AlertDialog.Builder builder = new AlertDialog.Builder(TravelDetailActivity.this);
                        builder.setTitle("删除")
                                .setMessage("是否删除该记录？")
                                .setCancelable(true)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“确定”按钮后的操作
                                        // 发送请求
                                        OkHttpClient client = new OkHttpClient();
                                        Request request = new Request.Builder()
                                                .url(url+"/travel/deleteTravel?travelId="+travelId)
                                                .build();
                                        //开启线程发送请求
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    //开启线程
                                                    Response response = client.newCall(request).execute();
                                                    //关闭帖子
                                                    Intent resultIntent = new Intent();
                                                    setResult(Activity.RESULT_OK, resultIntent); // 设置删除完成的结果码
                                                    finish(); // 关闭页面
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“取消”按钮后的操作
                                        dialog.dismiss();
                                    }
                                }).show();
                        break;
                    case R.id.edit:
                        //TODO 调整编辑页面
                        Intent intent = new Intent(TravelDetailActivity.this, travelRecordEdit.class);
                        intent.putExtra("travelId",travelId);
                        startActivity(intent);
                        break;
                    case R.id.cancel:

                }
                return true;
            }
        });
    }
}