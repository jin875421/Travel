package glue502.software.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class XunFeiUtil {

    public static String appid = "16621d18";

    public static void initXunFei(Context context){
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"="+appid);
    }



    public static void startVoice(Context context, final XunFeiCallbackListener callbackListener) {
        RecognizerDialog dialog = new RecognizerDialog(context,null);
        dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        dialog.setParameter(SpeechConstant.ASR_PTT, "0");
        System.out.println("==="+dialog.toString());
        dialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                callbackListener.onFinish(recognizerResult);
                Log.v("XunFeiUtil", "lzx ===");
            }
            @Override
            public void onError(SpeechError speechError) {
                Log.v("XunFeiUtil", "lzx ==error==");
            }
        });
        dialog.show();
        //Toast.makeText(this, "请开始说话", Toast.LENGTH_SHORT).show();
    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }
}
