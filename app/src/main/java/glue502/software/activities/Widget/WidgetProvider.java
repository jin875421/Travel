package glue502.software.activities.Widget;

import static glue502.software.activities.MainActivity.ip;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.RemoteViews;

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
    private static SharedPreferences sharedPreferences1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // 从SharedPreferences加载userid
        SharedPreferences sharedPreferences = context.getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
         sharedPreferences1 = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CURRENT_INDEX, 0);
        editor.apply();

        // 获取服务器上的图片URL列表
        getTravelPictureList(userId, context, appWidgetManager, appWidgetIds);
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, List<String> imageUrls) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

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
    }


    private static void startImageRotation(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
//        registerUpdateAlarm(context);
        runnable = new Runnable() {
            @Override
            public void run() {
                List<String> currentDayPictures = getNextDayPictures();
                int appWidgetId=appWidgetIds[0];
                updateAppWidget(context, appWidgetManager, appWidgetId, currentDayPictures);
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

//    @Override
//    public void onEnabled(Context context) {
//        super.onEnabled(context);
//        registerUpdateAlarm(context);
//    }

//    @Override
//    public void onDisabled(Context context) {
//        super.onDisabled(context);
//        unregisterUpdateAlarm(context);
//        handler.removeCallbacks(runnable);
//    }
//    private static void registerUpdateAlarm(Context context) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, UpdateReceiver.class);
//        intent.setAction("glue502.software.UPDATE_WIDGET");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15;
//        long triggerAtMillis = System.currentTimeMillis() + interval;
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, interval, pendingIntent);
//    }

//    private void unregisterUpdateAlarm(Context context) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, UpdateReceiver.class);
//        intent.setAction("glue502.software.UPDATE_WIDGET");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        alarmManager.cancel(pendingIntent);
//    }

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
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

}
