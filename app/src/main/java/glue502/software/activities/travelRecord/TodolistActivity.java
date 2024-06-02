package glue502.software.activities.travelRecord;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import glue502.software.R;
import glue502.software.adapters.TodoListAdapter;
import glue502.software.models.TodoItem;
import glue502.software.utils.MyViewUtils;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static glue502.software.activities.MainActivity.ip;

public class TodolistActivity extends AppCompatActivity {
    private ListView listView;
    private TodoListAdapter adapter;
    private ImageView back;
    private List<TodoItem> todoItems;
    private OkHttpClient client;
    private String url = "http://" + ip + "/travel/todo/";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(this, getWindow().getDecorView(), true);

        SharedPreferences sharedPreferences = getSharedPreferences("userName_and_userId", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        listView = findViewById(R.id.listView);
        back = findViewById(R.id.back);
        todoItems = new ArrayList<>();
        adapter = new TodoListAdapter(this, todoItems);
        listView.setAdapter(adapter);
        client = new OkHttpClient();

        loadTodos();

        // 添加按钮点击事件处理
        Button addButton = findViewById(R.id.addButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        addButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TodolistActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_todo, null);
            builder.setView(dialogView);

            EditText etTitle = dialogView.findViewById(R.id.etTitle);

            builder.setTitle("添加计划")
                    .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String title = etTitle.getText().toString().trim();
                            if (!title.isEmpty()) {
                                TodoItem todo = new TodoItem();
                                todo.setTitle(title);
                                todo.setCompleted(false);
                                todo.setId(UUID.randomUUID().toString());
                                todo.setUserId(userId);
                                saveTodo(todo);
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
                            clearTodos();
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

    private void saveTodo(TodoItem todo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String saveUrl = url + "save";
                Gson gson = new Gson();
                String json = gson.toJson(todo);

                RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url(saveUrl)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadTodos();
                            }
                        });
                    } else {
                        Log.e("TodolistActivity", "Error: " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("TodolistActivity", "Error saving todo", e);
                }
            }
        }).start();
    }

    private void clearTodos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String clearUrl = url + "clear?userId=" + userId;

                Request request = new Request.Builder()
                        .url(clearUrl)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadTodos();
                                Toast.makeText(TodolistActivity.this, "清空成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e("TodolistActivity", "Error clearing todos", e);
                }
            }
        }).start();
    }

    private void loadTodos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String loadUrl = url + "load?userId=" + userId;
                Request request = new Request.Builder()
                        .url(loadUrl)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();
                        Gson gson = new Gson();
                        List<TodoItem> todoList = gson.fromJson(jsonResponse, new TypeToken<List<TodoItem>>(){}.getType());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                todoItems.clear();
                                for (TodoItem todo : todoList) {
                                    todoItems.add(todo);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e("TodolistActivity", "Error loading todos", e);
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodos();
    }
}
