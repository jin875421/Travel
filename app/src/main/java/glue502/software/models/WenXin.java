package glue502.software.models;

import android.content.SharedPreferences;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**主要用于实现对接文心一言API的功能
 */
public class WenXin {

    public String API_KEY = "oQtUEMpGo1M9vsMfxmrwePzF";
    public String SECRET_KEY = "LxfNECAa2nNu5fLQERgNlUyL4W2UW0eX";

    public String AI_Role="";
    private SharedPreferences preferences;
    View view;

    public JSONArray Dialogue_Content;//用来储存对话内容，当然初始是空的

    Gson gson = new Gson();

    /**
     * WenXin类的初始化设置，设置好apikey等参数，以向服务器发送信息，airole参数的作用是自定义ai助手的角色功能类型等
     * @param apikey 这些都需要从用户设置中获得
     * @param secrectkey
     * @param airole
     */
    public WenXin(String apikey, String secrectkey, String airole){
        Dialogue_Content=new JSONArray();
        //初始化
        API_KEY=apikey;
        SECRET_KEY=secrectkey;
        AI_Role=airole;
    }

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
            .writeTimeout(60, TimeUnit.SECONDS) // 设置写超时时间
            .readTimeout(60, TimeUnit.SECONDS) // 设置读超时时间
            .build();

    public void GetAnswer(String user_msg,final ResponseCallback callback) throws IOException, JSONException{

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("role", "user");
        jsonObject.put("content", user_msg);

        // 将JSONObject添加到JSONArray中
        Dialogue_Content.put(jsonObject);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"messages\":" +
                Dialogue_Content.toString() +
                ",\"system\":\"" + AI_Role + "\",\"disable_search\":false,\"enable_citation\":false}");

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" +
                        getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        HTTP_CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response){
                try {
                    if (response.isSuccessful()) {
                        JSONObject json_feedback = new JSONObject(response.body().string());
                        String re = json_feedback.getString("result");
                        //把文心一言的回答加入到Dialogue_Content中
                        JSONObject jsontmp = new JSONObject();
                        jsontmp.put("assistant", re);
                        Dialogue_Content.put(jsontmp);
                        callback.onSuccess(re);
                    } else {
                        callback.onError("服务器返回错误：" + response.code());
                    }
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }
        });

    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    public String getAccessToken() throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }

    // 回调接口
    public interface ResponseCallback {
        void onSuccess(String response);

        void onError(String error);
    }



}
