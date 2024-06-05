package glue502.software.activities.travelRecord;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import glue502.software.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyExchangeActivity extends AppCompatActivity {

    private EditText inputAmount;
    private Spinner spinnerFromCurrency, spinnerToCurrency;
    private TextView textResult;
    private Button buttonConvert;

    private static final String API_URL = "http://op.juhe.cn/onebox/exchange/currency";
    private static final String API_KEY = "a322d7b2b417667c2d94738e89463c4a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_exchange);

        // 初始化UI组件
        inputAmount = findViewById(R.id.input_amount);
        spinnerFromCurrency = findViewById(R.id.spinner_from_currency);
        spinnerToCurrency = findViewById(R.id.spinner_to_currency);
        textResult = findViewById(R.id.text_result);
        buttonConvert = findViewById(R.id.button_convert);

        // 设置Spinner的适配器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFromCurrency.setAdapter(adapter);
        spinnerToCurrency.setAdapter(adapter);

        // 设置按钮点击事件
        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
            }
        });
    }

    private void convertCurrency() {
        String fromCurrency = spinnerFromCurrency.getSelectedItem().toString();
        String toCurrency = spinnerToCurrency.getSelectedItem().toString();
        String amountStr = inputAmount.getText().toString();

        if (!amountStr.isEmpty()) {
            double amount = Double.parseDouble(amountStr);

            // 获取货币代码
            String fromCurrencyCode = getResources().getStringArray(R.array.currency_codes)[spinnerFromCurrency.getSelectedItemPosition()];
            String toCurrencyCode = getResources().getStringArray(R.array.currency_codes)[spinnerToCurrency.getSelectedItemPosition()];

            // 调用实际的货币转换API来获取汇率
            getExchangeRate(fromCurrencyCode, toCurrencyCode, amount);
        } else {
            textResult.setText("请输入金额");
        }
    }

    private void getExchangeRate(String fromCurrencyCode, String toCurrencyCode, double amount) {
        OkHttpClient client = new OkHttpClient();

        String url = API_URL + "?from=" + fromCurrencyCode + "&to=" + toCurrencyCode + "&version=2&key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> textResult.setText("请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray resultArray = jsonObject.getJSONArray("result");

                        // 假设我们要找的是从 fromCurrencyCode 到 toCurrencyCode 的汇率
                        double exchangeRate = 0.0;
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject exchangeObject = resultArray.getJSONObject(i);
                            if (exchangeObject.getString("currencyF").equals(fromCurrencyCode) &&
                                    exchangeObject.getString("currencyT").equals(toCurrencyCode)) {
                                exchangeRate = exchangeObject.getDouble("exchange");
                                break;
                            }
                        }

                        double result = amount * exchangeRate;

                        runOnUiThread(() -> textResult.setText(String.format("%.2f %s", result, toCurrencyCode)));
                    } catch (JSONException e) {
                        runOnUiThread(() -> textResult.setText("这个暂时不支持"));
                    }
                } else {
                    runOnUiThread(() -> textResult.setText("无法获取汇率"));
                }
            }
        });
    }
}
