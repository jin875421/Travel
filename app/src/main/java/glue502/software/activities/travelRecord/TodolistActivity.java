package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import glue502.software.R;
import glue502.software.adapters.TodoListAdapter;
import glue502.software.models.TodoItem;
import glue502.software.utils.MyViewUtils;
import glue502.software.utils.TodoDatabaseHelper;

public class TodolistActivity extends AppCompatActivity {
    private ListView listView;
    private TodoListAdapter adapter;
    private TodoDatabaseHelper dbHelper;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this,getWindow().getDecorView(),true);
        listView = findViewById(R.id.listView);
        back = findViewById(R.id.back);
        dbHelper = new TodoDatabaseHelper(this);

        // 初始化ListView的适配器，并加载数据
        List<TodoItem> todoItems = dbHelper.getAllTodos();
        adapter = new TodoListAdapter(this, todoItems, dbHelper);
        listView.setAdapter(adapter);

        // 添加按钮点击事件处理，打开一个新的Activity或对话框来添加Todo
        Button addButton = findViewById(R.id.addButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        addButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TodolistActivity.this);
            builder.setTitle("添加计划")
                    .setView(R.layout.dialog_add_todo)
                    .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText etTitle = ((AlertDialog) dialog).findViewById(R.id.etTitle);
                            String title = etTitle.getText().toString().trim();
                            if (!title.isEmpty()) {
                                TodoItem todo = new TodoItem();
                                todo.setTitle(title);
                                todo.setCompleted(false);
                                long id = dbHelper.insertTodo(todo);
                                if (id > 0) {
                                    todo.setId(id);
                                    adapter.addNewTodoItem(todo); // 调用适配器中的添加方法，将新项目插入到顶部
                                } else {
                                    Toast.makeText(TodolistActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(TodolistActivity.this, "请输入计划", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        });
        //删除按钮
        deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TodolistActivity.this);
            builder.setTitle("清空")
                    .setMessage("确定要清空吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper.deleteAllTodos();
                            // 更新适配器以反映更改
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(TodolistActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        List<TodoItem> updatedTodos = dbHelper.getAllTodos(); // 假设这个方法能获取所有TodoItem，包括它们的最新状态
        adapter.clear(); // 清空适配器现有数据
        adapter.addAll(updatedTodos); // 用最新的数据填充适配器
        adapter.notifyDataSetChanged(); // 通知数据集有变化
    }


}