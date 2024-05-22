package glue502.software.activities.travelRecord;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import glue502.software.R;
import glue502.software.utils.MyViewUtils;

public class ExpenseRecordActivity extends AppCompatActivity {

    private EditText expenseAmountEditText;
    private EditText expenseDescriptionEditText;
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
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        expenseAmountEditText = findViewById(R.id.expenseAmountEditText);
        expenseDescriptionEditText = findViewById(R.id.expenseDescriptionEditText);
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
                String amountStr = expenseAmountEditText.getText().toString();
                String description = expenseDescriptionEditText.getText().toString();

                if (!amountStr.isEmpty() && !description.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    totalExpense += amount;
                    expenses.add(description + ": ¥" + String.format("%.2f", amount));
                    expenseAdapter.notifyDataSetChanged();
                    updateTotalExpenseTextView();
                    expenseAmountEditText.setText("");
                    expenseDescriptionEditText.setText("");
                    saveExpenses();
                } else {
                    Toast.makeText(ExpenseRecordActivity.this, "请输入支出金额和描述", Toast.LENGTH_SHORT).show();
                }
            }
        });

        expenseListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String expense = expenses.get(position);
            String[] parts = expense.split(": ¥");
            if (parts.length == 2) {
                double amount = Double.parseDouble(parts[1]);
                totalExpense -= amount;
                expenses.remove(position);
                expenseAdapter.notifyDataSetChanged();
                updateTotalExpenseTextView();
                saveExpenses();
            } else {
                Toast.makeText(ExpenseRecordActivity.this, "解析错误，无法删除项目", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        clearExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalExpense = 0.0;
                expenses.clear();
                expenseAdapter.notifyDataSetChanged();
                updateTotalExpenseTextView();
                saveExpenses();
            }
        });
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
        updateTotalExpenseTextView();
    }

    private void updateTotalExpenseTextView() {
        totalExpenseTextView.setText("总支出: ¥" + String.format("%.2f", totalExpense));
    }
}
