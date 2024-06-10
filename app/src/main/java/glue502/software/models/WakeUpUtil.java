package glue502.software.models;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import glue502.software.R;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class WakeUpUtil {
    //   private static AutoTouch autoTouch = new AutoTouch();//自动点击屏幕
    /**
     * 唤醒的回调
     */
    public abstract void wakeUp(String resultString);

    // Log标签
    private static final String TAG = "WakeUpUtil";

    // 上下文
    private static Context mContext;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;

    //唤醒门限值
    //门限值越高，则要求匹配度越高，才能唤醒
    //值范围：[0，3000]
    //默认值：1450
    private static int curThresh = 1450;

    public WakeUpUtil(Context context) {
        initKedaXun(context);

        mContext = context;
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(context, null);
        Log.d("initLogData", "===进入唤醒工具类====");

    }

    /**
     * 获取唤醒词功能
     *
     * @return 返回文件位置
     */
    private static String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + "16621d18" + ".jet");
        return resPath;
    }

    /**
     * 唤醒
     */
    public void wake() {
        Log.d("initLogData", "===进入唤醒工具类====");
        // 非空判断，防止因空指针使程序崩溃
        VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            // textView.setText(resultString);
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 设置唤醒资源路径
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            mIvw.startListening(mWakeuperListener);
            Log.d("initLogData", "====唤醒====");
        } else {
            Log.d("initLogData", "===唤醒未初始化11====");
//            Toast.makeText(mContext, "唤醒未初始化1", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopWake() {
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.stopListening();
        } else {
            Log.d("initLogData", "===唤醒未初始化222====");
//            Toast.makeText(mContext, "唤醒未初始化2", Toast.LENGTH_SHORT).show();
        }
    }

    String resultString = "";
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            try {

                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();
                stopWake();
                // autoTouch.autoClickPos( 0.1, 0.1);

                wakeUp(resultString);
//                MyEventManager.postMsg("" + resultString, "voicesWakeListener");

            } catch (JSONException e) {
//                MyEventManager.postMsg("" + "结果解析出错", "voicesWakeListener");
                resultString = "结果解析出错";
                wakeUp(resultString);
                e.printStackTrace();
            }

//            Logger.d("===开始说话==="+resultString);
        }

        @Override
        public void onError(SpeechError error) {

            // MyEventManager.postMsg("" + "唤醒出错", "voicesWakeListener");
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d("initLogData", "===唤醒onBeginOfSpeech====");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
//            Log.d("initLogData", "===唤醒onEvent===" + eventType);
        }

        @Override
        public void onVolumeChanged(int i) {
//            Log.d("initLogData", "===开始说话==="+i);
        }
    };

    /**
     * 科大讯飞
     * 语音sdk
     * 初始化
     */
    public void initKedaXun(Context context) {

        // 初始化参数构建
        StringBuffer param = new StringBuffer();
        //IflytekAPP_id为我们申请的Appid
        param.append("appid=" + "16621d18");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(context, param.toString());
        Log.d("initLogData", "===在appacation中初始化=====");
    }

}
