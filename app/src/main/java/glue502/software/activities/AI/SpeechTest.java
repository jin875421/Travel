package glue502.software.activities.AI;

import static glue502.software.models.XunFeiUtil.initXunFei;
import static glue502.software.models.XunFeiUtil.parseIatResult;
import static glue502.software.models.XunFeiUtil.startVoice;

import androidx.appcompat.app.AppCompatActivity;

import glue502.software.R;
import glue502.software.activities.MainActivity;
import glue502.software.models.XunFeiCallbackListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.iflytek.cloud.RecognizerResult;

public class SpeechTest extends AppCompatActivity {
    private Button btn_click;
    private EditText mResultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_test);
        initXunFei(this);
        Intent intent = getIntent();

        btn_click = (Button) findViewById(R.id.btn_click);
        mResultText = ((EditText) findViewById(R.id.result));
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoice(SpeechTest.this, new XunFeiCallbackListener() {
                    @Override
                    public void onFinish(RecognizerResult results) {
                        String text = parseIatResult(results.getResultString());
                        // 自动填写地址
                        mResultText.setText(text);
                    }
                });
            }
        });
    }
}