package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import glue502.software.R;
import glue502.software.utils.MyViewUtils;

public class FunctionActivity extends AppCompatActivity {
    private RelativeLayout todolist,expenserecord;
    private ImageView back;
    private RelativeLayout top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        init();
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        setListener();
    }
    private void init(){
        todolist = findViewById(R.id.todolist);
        back = findViewById(R.id.back);
        top = findViewById(R.id.top);
        expenserecord = findViewById(R.id.expense_record);
    }
    private void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        todolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, TodolistActivity.class);
                startActivity(intent);
            }
        });
        expenserecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, ExpenseRecordActivity.class);
                startActivity(intent);
            }
        });
    }
}