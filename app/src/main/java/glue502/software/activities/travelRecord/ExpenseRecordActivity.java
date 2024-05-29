package glue502.software.activities.travelRecord;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import glue502.software.R;
import glue502.software.utils.MyViewUtils;

public class ExpenseRecordActivity extends AppCompatActivity {

    private Button addExpenseButton;
    private Button clearExpensesButton;
    private ListView expenseListView;
    private TextView totalExpenseTextView;
    private ArrayList<String> expenses;
    private ArrayAdapter<String> expenseAdapter;
    private double totalExpense = 0.0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_record);

        //状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);

        addExpenseButton = findViewById(R.id.addExpenseButton);
        clearExpensesButton = findViewById(R.id.clearExpensesButton);
        expenseListView = findViewById(R.id.expenseListView);
        totalExpenseTextView = findViewById(R.id.totalExpenseTextView);

        expenses = new ArrayList<>();
        expenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenses);
        expenseListView.setAdapter(expenseAdapter);

        sharedPreferences = getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE);
        loadExpenses();

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });

        expenseListView.setOnItemLongClickListener((parent, view, position, id) -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExpenseRecordActivity.this);
            builder.setTitle("删除")
                    .setMessage("确定要删除吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String expense = expenses.get(position);
                            String[] parts = expense.split(": ¥");
                            if (parts.length == 2) {
                                double amount = Double.parseDouble(parts[1]);
                                totalExpense -= amount;
                                expenses.remove(position);
                                expenseAdapter.notifyDataSetChanged();
                                totalExpenseTextView.setText("总支出: ¥" + String.format("%.2f", totalExpense));
                                saveExpenses();
                            } else {
                                Toast.makeText(ExpenseRecordActivity.this, "解析错误，无法删除项目", Toast.LENGTH_SHORT).show();
                            }
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
                                saveExpenses();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            }
        });

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
                            totalExpense += amount;
                            expenses.add(description + ": ¥" + String.format("%.2f", amount));
                            expenseAdapter.notifyDataSetChanged();
                            totalExpenseTextView.setText("总支出: ¥" + String.format("%.2f", totalExpense));
                            saveExpenses();
                        } else {
                            Toast.makeText(ExpenseRecordActivity.this, "请输入支出金额和描述", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void saveExpenses() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> expenseSet = new HashSet<>(expenses);
        editor.putStringSet("expenses", expenseSet);
        editor.putFloat("totalExpense", (float) totalExpense);
        editor.apply();
    }

    private void loadExpenses() {
        Set<String> expenseSet = sharedPreferences.getStringSet("expenses", new HashSet<>());
        expenses.clear();
        expenses.addAll(expenseSet);
        expenseAdapter.notifyDataSetChanged();
        totalExpense = sharedPreferences.getFloat("totalExpense", 0);

        totalExpenseTextView.setText("总支出: ¥" + String.format("%.2f", totalExpense));
    }
}
