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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
    private String urlLoadImage="http://"+ip+"/travel/";
    private String userId;
    private static final String PREFS_NAME = "TravelAppPrefs";
    private static final String KEY_CURRENT_INDEX = "currentIndex";
    private SharedPreferences sharedPreferences1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // 从SharedPreferences加载userid
        SharedPreferences sharedPreferences = context.getSharedPreferences("userName_and_userId", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        sharedPreferences1 = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 获取服务器上的图片URL列表
        getTravelPictureList(userId, context, appWidgetManager, appWidgetIds);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        List<String> currentDayPictures = getNextDayPictures();
        if (!currentDayPictures.isEmpty()) {
            // 将图片添加到ViewFlipper中
            views.removeAllViews(R.id.view_flipper);
            for (String url : currentDayPictures) {
                System.out.println(url+"dsadasd");
                RemoteViews imageView = new RemoteViews(context.getPackageName(), R.layout.widget_image_item);
                Picasso.get().load(url).into(imageView, R.id.widget_image_item, new int[]{appWidgetId}, new Callback() {
                    @Override
                    public void onSuccess() {
                        // 图片加载成功
                    }

                    @Override
                    public void onError(Exception e) {
                        // 图片加载失败，处理错误
                        e.printStackTrace();
                    }
                });
                views.addView(R.id.view_flipper, imageView);
            }
        }

        views.setTextViewText(R.id.widget_title, "旅游App");
        views.setTextViewText(R.id.widget_subtitle, "点击查看");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
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

    private void getTravelPictureList(String userId, final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        List<String> modifiedUrls = new ArrayList<>();
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
                                return sdf.parse(sp1.getTravelDate()).compareTo(sdf.parse(sp2.getTravelDate()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });

                    // 提取排序后的路径列表
                    travelPictureList.clear();
                    for (ShowPicture showPicture : showPictureList) {
                        for(String string : showPicture.getPicturePath()){
                            // 将每个图片路径前面加上 urlLoadImage
                            String modifiedUrl = urlLoadImage + string;
                            modifiedUrls.add(modifiedUrl);
                        }
                        travelPictureList.add(modifiedUrls);
                        modifiedUrls.clear();
                    }


                    handler.post(() -> {
                        for (int appWidgetId : appWidgetIds) {
                            updateAppWidget(context, appWidgetManager, appWidgetId);
                        }
                        startImageRotation(context, appWidgetManager, appWidgetIds);
                    });
                }
            }
        });
    }

    public List<String> getNextDayPictures() {
        if (travelPictureList == null || travelPictureList.isEmpty()) {
            // 列表为空或未初始化，处理错误
            return new ArrayList<>();
        }

        // 获取当前索引
        int currentIndex = sharedPreferences1.getInt(KEY_CURRENT_INDEX, 0);

        // 获取当前索引对应的图片列表
        List<String> currentDayPictures = travelPictureList.get(currentIndex);
        System.out.println(currentDayPictures+"dsadasd");

        // 计算下一个索引，循环至开头
        int nextIndex = (currentIndex + 1) % travelPictureList.size();

        // 存储更新后的索引
        sharedPreferences1.edit().putInt(KEY_CURRENT_INDEX, nextIndex).apply();

        return currentDayPictures;
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        handler.removeCallbacks(runnable);
    }
}
