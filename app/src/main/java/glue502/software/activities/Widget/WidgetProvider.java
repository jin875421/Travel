package glue502.software.activities.Widget;

import static glue502.software.activities.MainActivity.ip;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import glue502.software.R;
import glue502.software.activities.MainActivity;
import glue502.software.models.ShowPicture;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WidgetProvider extends AppWidgetProvider {

    private static List<List<String>> travelPictureList = new ArrayList<>();
    private static Handler handler = new Handler();
    private static Runnable runnable;
    private String url = "http://" + ip + "/travel/travel/";
    private String urlLoadImage = "http://" + ip + "/travel/";
    private String userId;
    private static final String PREFS_NAME = "TravelAppPrefs";
    private static final String KEY_CURRENT_INDEX = "currentIndex";
    private static SharedPreferences sharedPreferences1,sharedPreferences;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // 从SharedPreferences加载userid
         sharedPreferences = context.getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        sharedPreferences1 = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CURRENT_INDEX, 0);
        editor.apply();

        // 获取服务器上的图片URL列表
        getTravelPictureList(userId, context, appWidgetManager, appWidgetIds);
        // 注册更新任务
        registerUpdateWork(context);
    }
    public static void registerUpdateWork(Context context) {
        PeriodicWorkRequest updateWorkRequest =
                new PeriodicWorkRequest.Builder(UpdateWorker.class, 60, TimeUnit.SECONDS)
                        .build();
        WorkManager.getInstance(context).enqueue(updateWorkRequest);
    }
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, List<String> imageUrls) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if(sharedPreferences.getString("status","").equals("1")){
            views.setViewVisibility(R.id.progress_bar, View.VISIBLE);
            views.setViewVisibility(R.id.view_flipper, View.GONE);
            // 清空视图
            views.removeAllViews(R.id.view_flipper);

            if (imageUrls != null && !imageUrls.isEmpty()) {
                // 创建一个 LoadImageTask 实例
                LoadImageTask loadImageTask = new LoadImageTask(context, views, appWidgetId, imageUrls, appWidgetManager);

                // 执行任务
                loadImageTask.execute();
            }

            views.setTextViewText(R.id.widget_subtitle, "点击查看");

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.view_flipper, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }else {
            views.setTextViewText(R.id.widget_subtitle, "请登录");
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.view_flipper, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }


    static void startImageRotation(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        runnable = new Runnable() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void run() {
                List<String> currentDayPictures = getNextDayPictures();
                for(int appWidgetId:appWidgetIds)
                    updateAppWidget(context, appWidgetManager, appWidgetId, currentDayPictures);
                handler.postDelayed(this, 60000); // 每分钟更新一次
            }
        };

        handler.post(runnable);
    }


    private void getTravelPictureList(String userId, final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        OkHttpClient client = new OkHttpClient();

        // 定义媒体类型
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(url + "showPictures")  // 替换为你的服务器URL
                .post(requestBody)
                .build();

        // 异步请求
        client.newCall(request).enqueue(new okhttp3.Callback() {
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
                    Type listType = new TypeToken<List<ShowPicture>>() {
                    }.getType();
                    List<ShowPicture> showPictureList = gson.fromJson(responseData, listType);

                    // 按日期排序
                    Collections.sort(showPictureList, new Comparator<ShowPicture>() {
                        @Override
                        public int compare(ShowPicture sp1, ShowPicture sp2) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            try {
                                return sdf.parse(sp2.getTravelDate()).compareTo(sdf.parse(sp1.getTravelDate()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });

                    // 提取排序后的路径列表
                    travelPictureList.clear();
                    for (ShowPicture showPicture : showPictureList) {
                        List<String> modifiedUrls = new ArrayList<>();
                        for (String string : showPicture.getPicturePath()) {
                            // 将每个图片路径前面加上 urlLoadImage
                            String modifiedUrl = urlLoadImage + string;
                            modifiedUrls.add(modifiedUrl);
                        }
                        travelPictureList.add(modifiedUrls);
                    }

                    handler.post(() -> {
                        startImageRotation(context, appWidgetManager, appWidgetIds);
                    });
                }
            }
        });
    }

    public static List<String> getNextDayPictures() {
        if (travelPictureList == null || travelPictureList.isEmpty()) {
            // 列表为空或未初始化，处理错误
            return new ArrayList<>();
        }

        // 获取当前索引
        int currentIndex = sharedPreferences1.getInt(KEY_CURRENT_INDEX,0);

        // 获取当前索引对应的图片列表
        List<String> currentDayPictures=new ArrayList<>();
        currentDayPictures = travelPictureList.get(currentIndex);

        // 计算下一个索引，循环至开头
        int nextIndex = (currentIndex + 1) % travelPictureList.size();

        // 存储更新后的索引
        sharedPreferences1.edit().putInt(KEY_CURRENT_INDEX, nextIndex).apply();
        return currentDayPictures;
    }
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // 注册更新任务
        registerUpdateWork(context);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
//        registerUpdateWork(context);
    }
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        WorkManager.getInstance(context).cancelAllWork();
        handler.removeCallbacks(runnable);
    }
    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        startImageRotation(context, appWidgetManager, appWidgetIds);
    }

    private static class LoadImageTask extends AsyncTask<Void, Void, List<Bitmap>> {
        private Context context;
        private RemoteViews views;
        private int appWidgetId;
        private List<String> imageUrls;
        private AppWidgetManager appWidgetManager;

        public LoadImageTask(Context context, RemoteViews views, int appWidgetId, List<String> imageUrls, AppWidgetManager appWidgetManager) {
            this.context = context;
            this.views = views;
            this.appWidgetId = appWidgetId;
            this.imageUrls = imageUrls;
            this.appWidgetManager = appWidgetManager;
        }

        @Override
        protected List<Bitmap> doInBackground(Void... voids) {
            List<Bitmap> bitmaps = new ArrayList<>();
            try {
                for (String imageUrl : imageUrls) {
                    Bitmap bitmap = Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .placeholder(R.drawable.personal_bg001)
                            .submit(250, 250)
                            .get();
                    bitmaps.add(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmaps;
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            if (bitmaps != null && !bitmaps.isEmpty()) {
                for (Bitmap bitmap : bitmaps) {
                    if (bitmap != null) {
                        RemoteViews imageView = new RemoteViews(context.getPackageName(), R.layout.widget_image_item);
                        imageView.setImageViewBitmap(R.id.widget_image_item, bitmap);
                        views.addView(R.id.view_flipper, imageView);
                    }
                }
                views.setViewVisibility(R.id.progress_bar, View.GONE);
                views.setViewVisibility(R.id.view_flipper, View.VISIBLE);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

}
