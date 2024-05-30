package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.flyjingfish.openimagelib.BaseInnerFragment;
import com.flyjingfish.openimagelib.OpenImage;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.listener.OnItemLongClickListener;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet;
import com.flyjingfish.openimagelib.transformers.ScaleInTransformer;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import glue502.software.R;
import glue502.software.activities.OpenCVTest;
import glue502.software.activities.personal.UpdatePersonalInformationActivity;
import glue502.software.adapters.ImagePagerAdapter;
import glue502.software.adapters.TravelPicturesAdapter;
import glue502.software.models.ImageEntity;
import glue502.software.models.TravelPicture;
import glue502.software.models.UserInfo;
import glue502.software.utils.MyViewUtils;
import glue502.software.utils.bigImgUtils.MyImageLoader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//这个页面是一个用于展示具体地点照片的页面
public class TravelPicturesActivity extends AppCompatActivity {

    //数据源
    private List<String> list1= new ArrayList<>();
    private List<TravelPicture> pictureList = new ArrayList<>();
    // 在 TravelPicturesActivity 中启动 OpenCVTest 活动
    private static final int REQUEST_CODE_OPENCV_TEST = 1;
    private RecyclerView recyclerView;
    private String url = "http://"+ip+"/travel/";
    private String placeName ;
    private TextView name;
    private ImageView back;
    private String placeId;
    private String result="0000";
    private String saveUrl="http://"+ip+"/travel/travel/";
//    private GridView gvPictures;

    private TravelPicturesAdapter travelPicturesAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_pictures);
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        placeName = getIntent().getStringExtra("placeName");

        //获取传递过来的参数
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            placeId=extras.getString("placeId");
        }
        getPictureList(placeId);
        name = findViewById(R.id.place_name);
        name.setText(placeName);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getPictureList(String placeId) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url+"travel/pictures").newBuilder();
        urlBuilder.addQueryParameter("placeId", placeId);
        String url = urlBuilder.build().toString();

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发起异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 处理请求失败情况
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 处理服务器响应
                    String responseData = response.body().string();
                    try {
                        // 解析JSON数据
                        pictureList.clear();
                        JSONArray jsonArray = new JSONArray(responseData);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TravelPicture travelPicture = new TravelPicture();
                            travelPicture.setPictureId(jsonObject.getString("pictureId"));
                            travelPicture.setPicturePath(jsonObject.getString("picturePath"));
                            travelPicture.setPlaceId(jsonObject.getString("placeId"));
                            pictureList.add(travelPicture);
                        }

                        // 在数据准备好后更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                list1.clear();
                                for (TravelPicture picture : pictureList) {
                                    list1.add(picture.getPicturePath());
                                }
                                initpage();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 处理服务器响应失败情况
                    // 可以根据不同的响应码进行不同的处理
                }
            }
        });
    }


    private void initpage() {
        List<ImageEntity> datas = new ArrayList<>();
        for(String s:list1){
            datas.add(new ImageEntity(url+s,url+s,null,null,null,0,s));
        }
        recyclerView = findViewById(R.id.picture_rcv);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        TravelPicturesActivity.RvAdapter myAdapter = new RvAdapter(datas);
        recyclerView.setAdapter(myAdapter);


    }

    private class RvAdapter extends RecyclerView.Adapter<TravelPicturesActivity.RvAdapter.MyHolder> {
        List<ImageEntity> datas;

        public RvAdapter(List<ImageEntity> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public TravelPicturesActivity.RvAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            //设置布局
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_picture_try, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TravelPicturesActivity.RvAdapter.MyHolder holder, int position) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            //图片(后面的两张图片：加载时的占位图，加载失败时的占位图。可自行设置)
            MyImageLoader.getInstance().load(holder.ivImage, datas.get(position).getCoverImageUrl(), R.mipmap.loading, R.mipmap.blank);
            String clickedImageUrl = datas.get(position).getCoverImageUrl();
            String path=datas.get(position).getPath();
            //设置点击图片后打开大图的监听器
            holder.ivImage.setOnClickListener(v -> {
                OpenImage.with(TravelPicturesActivity.this)
                        .setClickRecyclerView(recyclerView, new SourceImageViewIdGet() {
                            @Override
                            public int getImageViewId(OpenImageUrl data, int position) {
                                return R.id.imageView;
                            }
                        })
                        .setAutoScrollScanPosition(true)
                        //RecyclerView的数据
                        .setImageUrlList(datas)
                        //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
                        .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true)
                        .addPageTransformer(new ScaleInTransformer())
                        .setClickPosition(position)
                        .setShowDownload()
                        .addMoreView(R.layout.activity_photo_edit,
                                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT),
                                MoreViewShowType.BOTH,
                                new OnLoadViewFinishListener() {
                                    @Override
                                    public void onLoadViewFinish(View moreView) {
                                        TextView txtEdit = moreView.findViewById(R.id.txtEdit);
                                        ImageView imgDelete=moreView.findViewById(R.id.img_delete);
                                        imgDelete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deletePicture(path);
                                            }
                                        });
                                        txtEdit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // 创建一个 PopupMenu
                                                PopupMenu popupMenu = new PopupMenu(TravelPicturesActivity.this, v);
                                                // 从资源文件中填充菜单
                                                popupMenu.getMenu().add("滤镜");
                                                popupMenu.getMenu().add("剪切");

                                                // 为菜单项设置点击监听器
                                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                    @Override
                                                    public boolean onMenuItemClick(MenuItem item) {
                                                        // 处理菜单项点击事件
                                                        switch (item.getTitle().toString()) {
                                                            case "滤镜":
                                                                // 启动 OpenCVTest 活动并传递必要的数据
                                                                Intent intentFilter = new Intent(TravelPicturesActivity.this, OpenCVTest.class);
                                                                intentFilter.putExtra("imageUrl", clickedImageUrl);
                                                                intentFilter.putExtra("imagePlaceId", placeId);
                                                                intentFilter.putExtra("operation", "filter");
                                                                startActivityForResult(intentFilter, REQUEST_CODE_OPENCV_TEST);
                                                                return true;
                                                            case "剪切":
                                                                Uri Uri1 = Uri.fromFile(new File(getCacheDir(), "SampleCropImage.jpeg"));
                                                                //开始
                                                                UCrop uCrop = UCrop.of(Uri.parse(clickedImageUrl), Uri1);
                                                                uCrop.start(TravelPicturesActivity.this);
                                                                return true;
                                                            default:
                                                                return false;
                                                        }
                                                    }
                                                });

                                                // 显示 PopupMenu
                                                popupMenu.show();
                                            }
                                        });
                                    }
                                })
                        .show();
            });
        }
        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            public MyHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }
    }

    private void deletePicture(String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), path);
                    Request request = new Request.Builder()
                            .url(url + "travel/deletePicture") //***.***.**.***为本机IP，xxxx为端口，/  /  为访问的接口后缀
                            .post(requestBody)
                            .build(); //创建Http请求
                    Response response = client.newCall(request).execute();
                    final String responseData = response.body().string();

                    // 处理服务器响应，更新UI或执行其他操作
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TravelPicturesActivity.this, responseData, Toast.LENGTH_SHORT).show();
                            if (responseData.equals("图片已成功删除")) {
                                getPictureList(placeId);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPENCV_TEST && resultCode == RESULT_OK) {
            // 从 OpenCVTest 返回并且结果码为 RESULT_OK 时，更新图片列表并刷新 UI
            getPictureList(placeId);
        }
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                uploadCroppedImage(resultUri);
                Handler handler = new Handler(Looper.getMainLooper());
                // 延迟执行的代码
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getPictureList(placeId);
                    }
                }, 500);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            // 处理裁剪错误
        }
    }

    private byte[] fileToBytes(File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int len;
            while ((len = in.read(buffer, 0, bufSize)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bos.close();
        }
    }
    private void uploadPhoto(byte[] photo) {
        String filename = UUID.randomUUID().toString();
        // 构建Multipart请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("placeId", placeId)
                .addFormDataPart("file", filename+".jpg", RequestBody.create(MediaType.parse("image/*"),photo ))
                .build();

// 构建POST请求
        Request request = new Request.Builder()
                .url(saveUrl + "uploadphoto")
                .post(requestBody)
                .build();

// 发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();
                    final String responseData = response.body().string();
                    System.out.println(responseData+"dsadasdad");
                    // 处理服务器响应，更新UI或执行其他操作
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            result=responseData;
                            // 处理响应数据
                            // 这里可以根据服务器的响应结果进行操作，例如显示消息、更新UI等
                            if(responseData.equals("1111")) {
                                // 示例：显示成功消息并结束当前Activity
                                Toast.makeText(TravelPicturesActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(TravelPicturesActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void uploadCroppedImage(Uri resultUri) {
        File file = new File(resultUri.getPath());
        try {
            byte[] photoBytes = fileToBytes(file);
            uploadPhoto(photoBytes);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "文件转换失败", Toast.LENGTH_SHORT).show();
        }
    }

}