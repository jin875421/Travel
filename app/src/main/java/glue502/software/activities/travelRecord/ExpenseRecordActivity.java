package glue502.software.activities.travelRecord;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import glue502.software.R;
import glue502.software.adapters.ExpenseAdapter;
import glue502.software.models.Expense;
import glue502.software.utils.MyViewUtils;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExpenseRecordActivity extends AppCompatActivity {

    private Button addExpenseButton;
    private Button clearExpensesButton;
    private ListView expenseListView;
    private TextView totalExpenseTextView;
    private List<Expense> expenses;
    private double totalExpense = 0.0;
    private String url = "http://"+ip + "/travel/expense/";
    private String userId;
    private ExpenseAdapter expenseAdapter;
    private OkHttpClient client;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_record);

        //状态栏
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);
        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        addExpenseButton = findViewById(R.id.addExpenseButton);
        clearExpensesButton = findViewById(R.id.clearExpensesButton);
        expenseListView = findViewById(R.id.expenseListView);
        totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
        back = findViewById(R.id.back);
        expenses = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(this, R.layout.expense_item, expenses);
        expenseListView.setAdapter(expenseAdapter);

        client = new OkHttpClient();

        loadExpenses();

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        expenseListView.setOnItemLongClickListener((parent, view, position, id) -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExpenseRecordActivity.this);
            builder.setTitle("删除")
                    .setMessage("确定要删除吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Expense expense = expenses.get(position);
                            totalExpense -= expense.getPrice();
                            expenses.remove(position);
                            expenseAdapter.notifyDataSetChanged();
                            totalExpenseTextView.setText("总支出: ¥" + String.format("%.2f", totalExpense));
                            deleteExpense(expense.getId());
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();

            return true;
        });

        clearExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExpenseRecordActivity.this);
                builder.setTitle("清空")
                        .setMessage("确定要清空吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                totalExpense = 0.00;
                                expenses.clear();
                                expenseAdapter.notifyDataSetChanged();
                                totalExpenseTextView.setText("总支出: ¥" + String.format("%.2f", totalExpense));
                                clearExpense();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            }
        });
    }

    private void clearExpense() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String clearUrl = url + "clear?userId="+userId;

                Request request = new Request.Builder()
                        .url(clearUrl)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadExpenses();
                                Toast.makeText(ExpenseRecordActivity.this, "支出清空成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e("ExpenseRecordActivity", "Error clearing expenses", e);
                }
            }
        }).start();
    }

    private void deleteExpense(String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String deleteUrl = url + "delete?id="+id;
                Request request = new Request.Builder()
                        .url(deleteUrl)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadExpenses();
                                Toast.makeText(ExpenseRecordActivity.this, "支出删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e("ExpenseRecordActivity", "Error deleting expense", e);
                }
            }
        }).start();
    }

    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_expense, null);
        builder.setView(dialogView);

        EditText expenseAmountEditText = dialogView.findViewById(R.id.dialogExpenseAmountEditText);
        EditText expenseDescriptionEditText = dialogView.findViewById(R.id.dialogExpenseDescriptionEditText);

        builder.setTitle("添加支出")
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amountStr = expenseAmountEditText.getText().toString();
                        String description = expenseDescriptionEditText.getText().toString();
                        if (!amountStr.isEmpty() && !description.isEmpty()) {
                            double amount = Double.parseDouble(amountStr);
                            Expense expense = new Expense();
                            expense.setDescribe(description);
                            expense.setPrice(amount);
                            expense.setId(UUID.randomUUID().toString());
                            expense.setUserId(userId);
                            saveExpenses(expense);
                        } else {
                            Toast.makeText(ExpenseRecordActivity.this, "请输入支出金额和描述", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void saveExpenses(Expense expense) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String saveUrl = url + "save";
                Gson gson = new Gson();
                String json = gson.toJson(expense);

                RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url(saveUrl)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.d("ExpenseRecordActivity", "Response: " + responseData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadExpenses();
                            }
                        });
                    } else {
                        Log.e("ExpenseRecordActivity", "Error: " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("ExpenseRecordActivity", "Error saving expense", e);
                }
            }
        }).start();
    }



    private void loadExpenses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String loadUrl = url + "load?userId"+"="+userId;
                Request request = new Request.Builder()
                        .url(loadUrl)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();
                        Gson gson = new Gson();
                        List<Expense> expenseList = gson.fromJson(jsonResponse, new TypeToken<List<Expense>>(){}.getType());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                expenses.clear();
                                totalExpense = 0.0;
                                for (Expense expense : expenseList) {
                                    expenses.add(expense);
                                    totalExpense += expense.getPrice();
                                }
                                expenseAdapter.notifyDataSetChanged();
                                totalExpenseTextView.setText("总支出: ¥" + String.format("%.2f", totalExpense));
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e("ExpenseRecordActivity", "Error loading expenses", e);
                }
            }
        }).start();
    }
}

