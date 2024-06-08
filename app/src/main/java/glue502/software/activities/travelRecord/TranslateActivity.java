package glue502.software.activities.travelRecord;
import android.Manifest;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iflytek.cloud.record.PcmRecorder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import glue502.software.R;
import glue502.software.utils.AudioRecorder;
import glue502.software.utils.AuthUtils;
import glue502.software.utils.MyViewUtils;
import glue502.software.utils.PcmToWav;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class TranslateActivity extends AppCompatActivity {
    private static String requestUrl = "wss://ws-api.xf-yun.com/v1/private/simult_interpretation";
    //控制台获取以下信息
    private static String APPID = "16621d18";
    private static String apiSecret = "YjgzZTEyMWZmNTM4OWRlNzlhMTlmYzZi";
    private static String apiKey = "90a080097af977eea95f000b84e500b3";

    private static final String domain = "ist_ed_open";
    private static final String language = "zh_cn";
    private static final String accent = "mandarin";
    // 翻译所需参数，从中文-英文
    private static final String from = "cn"; // 源文件的语言类型
    private static final String to = "en"; // 目标文件的语言类型
    // 发声发音人
    private static final String vcn = "x2_catherine";
    // 输出音频编码方式 PCM
    private static final String encoding = "raw";
    private ImageView back;

    // 输入的源音频文件

    // 输出的音频与文本文件
    private String outPutPcm = "/trans.pcm";
    private String outPutWav = "/trans.wav";
    private String asr_result = "/asr.txt";
    private String trans_result = "/trans.txt";

    public static final int StatusFirstFrame = 0;
    public static final int StatusContinueFrame = 1;
    public static final int StatusLastFrame = 2;
    public static final Gson gson = new Gson();
    private static BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static boolean overFlag = false;

    private static final String TAG = "TranslateActivity";
    private static final String API_KEY = "90a080097af977eea95f000b84e500b3";
    private static final String API_SECRET = "YjgzZTEyMWZmNTM4OWRlNzlhMTlmYzZi";
    private static final String APP_ID = "16621d18";
    private static final String URL = "wss://ws-api.xf-yun.com/v1/private/simult_interpretation";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private boolean permissionToRecordAccepted = false;
    private String fileName = null;
    private AudioRecorder recorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        // 使用 getExternalFilesDir 获取可靠的路径
        fileName = getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/audiorecordtest.pcm";
        //状态栏
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);

        outPutPcm = getExternalFilesDir(Environment.DIRECTORY_MUSIC) +outPutPcm;
        outPutWav = getExternalFilesDir(Environment.DIRECTORY_MUSIC) +outPutWav;
        asr_result = getExternalFilesDir(Environment.DIRECTORY_MUSIC) +asr_result;
        trans_result = getExternalFilesDir(Environment.DIRECTORY_MUSIC) +trans_result;

        recorder = new AudioRecorder(fileName);
        //删除缓存文件
        if (getExternalCacheDir().exists()) {
            getExternalCacheDir().delete();
        }
        //获取文件地址

        back = findViewById(R.id.back);
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button translateButton = findViewById(R.id.translateButton);

        startButton.setOnClickListener(v -> {
            recorder.startRecording();

        });
        stopButton.setOnClickListener(v -> {
            recorder.stopRecording();

        });
        back.setOnClickListener(v -> {
            finish();
        });

        translateButton.setOnClickListener(v -> {
            //先执行文件删除
            File file1 = new File(outPutPcm);
            File file2 = new File(outPutWav);
            File file3 = new File(asr_result);
            File file4 = new File(trans_result);

            if (file1.exists()) {
                file1.delete();
            }
            if (file2.exists()) {
                file2.delete();
            }
            if (file3.exists()) {
                file3.delete();
            }
            if (file4.exists()) {
                file4.delete();
            }
            //完成删除后重新创建
            if(!file1.exists()){
                try {
                    file1.createNewFile();
                } catch (IOException e) {
                   e.printStackTrace();
                }
            }
            if(!file2.exists()){
                try {
                    file2.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(!file3.exists()){
                try {
                    file3.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(!file4.exists()){
                try {
                    file4.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                startTranslation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void startTranslation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String authUrl = AuthUtils.assembleRequestUrl(requestUrl, apiKey, apiSecret);
                OkHttpClient client = new OkHttpClient.Builder().build();
                Request request = new Request.Builder().url(authUrl).build();
                WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                        sendAudioData(webSocket);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        super.onMessage(webSocket, text);

                        handleWebSocketMessage(text);
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                        super.onFailure(webSocket, t, response);
                        Log.e(TAG, "WebSocket connection failed", t);
                    }
                });
            }
        }).start();

        new Thread(new videoPlayer()).start();
    }

    private void sendAudioData(WebSocket webSocket) {
        try {
            //打开音频文件
            int frameSize = 1280; // 每一帧音频的大小 1280/40ms
            int interval = 40;
            int status = 0;  // 音频的状态
            int count = 0;
            try (FileInputStream fs = new FileInputStream(fileName)) {
                byte[] buffer = new byte[frameSize];
                // 发送音频
                end:
                while (true) {
                    int len = fs.read(buffer);
                    if (len < frameSize) {
                        status = StatusLastFrame;  //文件读完，改变status 为 2
                    }
                    switch (status) {
                        case StatusFirstFrame:   // 第一帧音频status = 0
                            JsonObject frame = new JsonObject();
                            JsonObject header = new JsonObject();  //第一帧必须发送
                            JsonObject parameter = new JsonObject();
                            JsonObject ist = new JsonObject();
                            JsonObject streamtrans = new JsonObject();
                            JsonObject tts = new JsonObject();
                            JsonObject tts_results = new JsonObject();
                            JsonObject payload = new JsonObject();
                            JsonObject data = new JsonObject();
                            // 填充header
                            header.addProperty("app_id", APPID);//appid 必须带上，只需第一帧发送
                            header.addProperty("status",0);
                            // 填充parameter
                            // ist参数填充
                            ist.addProperty("eos", 600000);
                            ist.addProperty("vto", 15000);
                            ist.addProperty("accent", accent);
                            ist.addProperty("language", language);
                            ist.addProperty("language_type", 1);
                            ist.addProperty("domain", domain);
                            // streamtrans参数填充
                            streamtrans.addProperty("from", from);
                            streamtrans.addProperty("to", to);
                            // tts参数填充
                            tts.addProperty("vcn",vcn);
                            tts_results.addProperty("encoding","raw");
                            tts_results.addProperty("sample_rate",16000);
                            tts_results.addProperty("channels",1);
                            tts_results.addProperty("bit_depth",16);
                            tts.add("tts_results",tts_results);
                            parameter.add("ist",ist);
                            parameter.add("streamtrans",streamtrans);
                            parameter.add("tts",tts);
                            //填充payload
                            data.addProperty("audio", Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)));
                            data.addProperty("encoding",encoding);
                            data.addProperty("sample_rate",16000);
                            data.addProperty("status",status);
                            data.addProperty("seq",count);
                            payload.add("data",data);
                            //填充frame
                            frame.add("header", header);
                            frame.add("parameter", parameter);
                            frame.add("payload", payload);

                            webSocket.send(frame.toString());
                            status = StatusContinueFrame;  // 发送完第一帧改变status 为 1
                            System.out.println("send first");
                            break;
                        case StatusContinueFrame:  //中间帧status = 1
                            JsonObject contineuFrame = new JsonObject();
                            JsonObject header1 = new JsonObject();
                            JsonObject payload1 = new JsonObject();
                            JsonObject data1 = new JsonObject();
                            // 填充head
                            header1.addProperty("status",1);
                            header1.addProperty("app_id", APPID);
                            //填充payload
                            data1.addProperty("audio", Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)));
                            data1.addProperty("encoding",encoding);
                            data1.addProperty("sample_rate",16000);
                            data1.addProperty("status",status);
                            data1.addProperty("seq",count);
                            payload1.add("data",data1);
                            contineuFrame.add("header", header1);
                            contineuFrame.add("payload", payload1);
                            webSocket.send(contineuFrame.toString());
                            // System.out.println("send continue");
                            break;
                        case StatusLastFrame:    // 最后一帧音频status = 2 ，标志音频发送结束
                            String audio = "";
                            if(len>0){
                                audio = Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len));
                            }
                            JsonObject lastFrame = new JsonObject();
                            JsonObject header2 = new JsonObject();
                            JsonObject payload2 = new JsonObject();
                            JsonObject data2 = new JsonObject();
                            // 填充head
                            header2.addProperty("status",2);
                            header2.addProperty("app_id", APPID);
                            //填充payload
                            data2.addProperty("audio", audio);
                            data2.addProperty("encoding",encoding);
                            data2.addProperty("sample_rate",16000);
                            data2.addProperty("status",status);
                            data2.addProperty("seq",count);
                            payload2.add("data",data2);
                            lastFrame.add("header", header2);
                            lastFrame.add("payload", payload2);
                            webSocket.send(lastFrame.toString());
                            System.out.println("send last");
                            break end;
                    }
                    count++;
                    Thread.sleep(interval); //模拟音频采样延时
                }
                System.out.println("all data is send");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
    }

    private void handleWebSocketMessage(String text) {
        ResponseData resp = gson.fromJson(text, ResponseData.class);
        System.out.println("Received message: " + text);
        if (resp != null) {
            if (resp.header.code != 0) {
                System.out.println("error=>" + resp.header.message + " sid=" + resp.header.sid+" 错误码="+resp.header.code);
                return;
            }
            if (resp.header != null) {
                if (resp.header.code == 0) {
                    // System.out.println(text);
                    if(resp.payload!=null){
                        // 接收到的识别结果写到文本
                        if(resp.payload.recognition_results!=null){
                            String s1 = resp.payload.recognition_results.text;
                            byte[] trans1 = Base64.getDecoder().decode(s1);
                            String res1 = new String(trans1);
                            try {
                                writeStringToFile(res1,asr_result);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // 接收到的翻译结果写到文本
                        if(resp.payload.streamtrans_results!=null){
                            String s2 = resp.payload.streamtrans_results.text;
                            byte[] trans = Base64.getDecoder().decode(s2);
                            String res = new String(trans);
                            try {
                                writeStringToFile(res,trans_result);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // 把接收到的音频流合成PCM
                        if(resp.payload.tts_results!=null){
                            String s = resp.payload.tts_results.audio;
                            queue.add(s);
                            try {
                                writeBytesToFile(Base64.getDecoder().decode(s),outPutPcm);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (resp.header.status == 2) {
                    // todo  resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                    System.out.println("session end ");
                    System.out.println("本次请求的sid==》 "+resp.header.sid);
                    System.out.println("数据处理完毕，等待实时转译结束！");
                    overFlag = true;
                    try {
                        // 流程完毕后，输出音频文件，把PCM转换为WAV
                        PcmToWav.convertAudioFiles(outPutPcm,outPutWav);
                        //播放音频
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(outPutWav);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    webSocket.close(1000,"");
                    if(queue.size() == 0){
                        System.exit(0);
                    }
                } else {
                    // todo 根据返回的数据处理
                }
            }
        }
    }

    static class videoPlayer implements Runnable {
        @Override
        public void run() {

//            MediaPlayer mediaPlayer = new MediaPlayer();
//            while (true) {
//                if (overFlag && queue.isEmpty()) {
//                    break;
//                }
//                if (!queue.isEmpty()) {
//                    String poll = queue.poll();
//                    try {
//                        // Decode Base64 to byte array
//                        byte[] audioData = Base64.getDecoder().decode(poll);
//                        // Write audio data to a temporary file
//                        File audioFile = new File("output/audio/temp_audio.wav");
//                        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
//                            fos.write(audioData);
//                        }
//                        // Reset and set up the MediaPlayer
//                        mediaPlayer.reset();
//                        mediaPlayer.setDataSource(audioFile.getAbsolutePath());
//                        mediaPlayer.prepare();
//                        mediaPlayer.start();
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error playing audio", e);
//                    }
//                }
//            }
            Log.d(TAG, "Real-time translation ended.");
//            mediaPlayer.release();
        }
    }


    public static void writeBytesToFile(byte[] bs,String path) throws IOException{
        OutputStream out = new FileOutputStream(path,true);
        InputStream is = new ByteArrayInputStream(bs);
        byte[] buff = new byte[1024];
        int len = 0;
        while((len=is.read(buff))!=-1){
            out.write(buff, 0, len);
        }
        is.close();
        out.close();
    }

    public static void writeStringToFile(String content,String path) throws IOException{
        OutputStream out = new FileOutputStream(path,true);
        out.write(content.getBytes());
        out.close();
    }

    public static class ResponseData {
        header header;
        payload payload;
    }

    public static class payload{
        streamtrans_results streamtrans_results;
        recognition_results recognition_results;
        tts_results tts_results;
        @Override
        public String toString() {
            return "payload{" +
                    "streamtrans_results=" + streamtrans_results +
                    ", recognition_results=" + recognition_results +
                    ", tts_results=" + tts_results +
                    '}';
        }
    }

    public static class header{
        int code;
        String message;
        String sid;
        int status;
    }
    public static class recognition_results{
        String encoding;
        String format;
        String text;
        int status;
        @Override
        public String toString() {
            return "recognition_results{" +
                    "encoding='" + encoding + '\'' +
                    ", format='" + format + '\'' +
                    ", text='" + text + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
    public static class streamtrans_results{
        String encoding;
        String format;
        String text;
        int status;
        @Override
        public String toString() {
            return "streamtrans_results{" +
                    "encoding='" + encoding + '\'' +
                    ", format='" + format + '\'' +
                    ", text='" + text + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
    public static class tts_results{
        String encoding;
        String audio;
        int sample_rate;
        int channels;
        int bit_depth;
        int status;
        int seq;
        int frame_size;
        @Override
        public String toString() {
            return "tts_results{" +
                    "encoding='" + encoding + '\'' +
                    ", audio='" + audio + '\'' +
                    ", sample_rate=" + sample_rate +
                    ", channels=" + channels +
                    ", bit_depth=" + bit_depth +
                    ", status=" + status +
                    ", seq=" + seq +
                    ", frame_size=" + frame_size +
                    '}';
        }
    }



}
