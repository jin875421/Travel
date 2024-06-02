package glue502.software.activities.Widget;

import static glue502.software.activities.MainActivity.ip;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import glue502.software.R;
import glue502.software.activities.MainActivity;
import glue502.software.models.ShowPicture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WidgetProvider extends AppWidgetProvider {

    private static List<String> imageUrls = new ArrayList<>();
    private static Handler handler = new Handler();
    private static Runnable runnable;
    private String url = "http://"+ip+"/travel/travel/";
    private String userId;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
// 从SharedPreferences加载userid
        SharedPreferences sharedPreferences = context.getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        // 假设你从服务器获取的图片URL列表
        imageUrls = fetchImageUrlsFromServer();

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        startImageRotation(context, appWidgetManager, appWidgetIds);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        if (!imageUrls.isEmpty()) {
            // 将图片添加到ViewFlipper中
            views.removeAllViews(R.id.view_flipper);
            for (String url : imageUrls) {
                RemoteViews imageView = new RemoteViews(context.getPackageName(), R.layout.widget_image_item);
                Picasso.get().load(url).into(imageView, R.id.widget_image_item, new int[]{appWidgetId});
                views.addView(R.id.view_flipper, imageView);
            }
        }

        views.setTextViewText(R.id.widget_title, "旅游App");
        views.setTextViewText(R.id.widget_subtitle, "点击查看");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.view_flipper, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void startImageRotation(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
                handler.postDelayed(this, 5000); // 每5秒更新一次图片
            }
        };

        handler.post(runnable);
    }

    private List<String> fetchImageUrlsFromServer() {
        // 模拟从服务器获取图片URL列表
        List<String> urls = new ArrayList<>();
        urls=getTravelPictureList(userId);
        return urls;
    }

    private List<String> getTravelPictureList(String userId) {
        List<String> photoDate = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        // 定义媒体类型
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(url+"showPictures")
                .post(requestBody)
                .build();

        // 异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    // 解析JSON
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ShowPicture>>(){}.getType();
                    List<ShowPicture> showPictureList = gson.fromJson(responseData, listType);
                    for (ShowPicture showPicture : showPictureList) {
                        photoDate.add(showPicture.getTravelDate());
                    }
                    Collections.sort(photoDate, new Comparator<String>() {
                        @Override
                        public int compare(String date1, String date2) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            try {
                                return sdf.parse(date1).compareTo(sdf.parse(date2));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });

                    // 更新UI或处理数据
                    handler.post(() -> {
                        // 在主线程中更新UI或处理数据
                    });
                }
            }
        });

        return photoDate;
    }



    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        handler.removeCallbacks(runnable);
    }
}