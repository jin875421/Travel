package glue502.software.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.models.TodoItem;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static glue502.software.activities.MainActivity.ip;

public class TodoListAdapter extends ArrayAdapter<TodoItem> {
    private Context mContext;
    private List<TodoItem> mTodos;
    private OkHttpClient client;
    private String url = "http://" + ip + "/travel/todo/";

    public TodoListAdapter(Context context, List<TodoItem> todos) {
        super(context, 0, todos);
        this.mContext = context;
        this.mTodos = todos;
        this.client = new OkHttpClient();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_todo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
            viewHolder.cbComplete = convertView.findViewById(R.id.cbComplete);
            convertView.setTag(viewHolder);

            viewHolder.cbComplete.setOnClickListener(v -> {
                CheckBox checkBox = (CheckBox) v;
                TodoItem todo = getItem(position);
                boolean isChecked = checkBox.isChecked();

                todo.setCompleted(isChecked);
                updateTodoOnServer(todo);
                updateListAfterCheckChange(todo, isChecked); // 更新列表并排序
            });
            convertView.setOnLongClickListener(v -> {
                // 创建一个AlertDialog.Builder对象
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("确认删除？")
                        .setMessage("您确定要删除此项吗？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户点击是，执行删除操作
                                TodoItem todo = getItem(position);
                                deleteTodoOnServer(todo.getId());
                                mTodos.remove(todo);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("否", null)
                        .create()
                        .show();
                return true; // 表示长按已被消耗
            });

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        sortTodos1();
        TodoItem todo = getItem(position);
        viewHolder.tvTitle.setText(todo.getTitle());
        viewHolder.cbComplete.setChecked(todo.isCompleted());
        viewHolder.tvTitle.setTextColor(todo.isCompleted() ? Color.GRAY : Color.BLACK);
        viewHolder.tvTitle.setPaintFlags(todo.isCompleted() ?
                viewHolder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                viewHolder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        return convertView;
    }

    public void addNewTodoItem(TodoItem newItem) {
        mTodos.add(0, newItem); // 将新项目插入到列表的开头位置
        notifyDataSetChanged(); // 通知适配器数据集已更改
    }

    private void updateListAfterCheckChange(TodoItem todo, boolean isChecked) {
        todo.setCompleted(isChecked);
        updateTodoOnServer(todo);
        sortTodos1();
    }

    private static class ViewHolder {
        TextView tvTitle;
        CheckBox cbComplete;
    }

    private void sortTodos1() {
        List<TodoItem> completedTodos = new ArrayList<>();
        List<TodoItem> uncompletedTodos = new ArrayList<>(mTodos.size());

        for (TodoItem todo : mTodos) {
            if (todo.isCompleted()) {
                completedTodos.add(todo);
            } else {
                uncompletedTodos.add(todo);
            }
        }

        uncompletedTodos.addAll(completedTodos);
        mTodos.clear();
        mTodos.addAll(uncompletedTodos);
        notifyDataSetChanged();
    }

    private void updateTodoOnServer(TodoItem todo) {
        new Thread(() -> {
            String updateUrl = url + "update";
            Gson gson = new Gson();
            String json = gson.toJson(todo);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(updateUrl)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e("TodoListAdapter", "Error: " + response.code());
                }
            } catch (IOException e) {
                Log.e("TodoListAdapter", "Error updating todo", e);
            }
        }).start();
    }

    private void deleteTodoOnServer(String todoId) {
        new Thread(() -> {
            String deleteUrl = url + "delete?todoId=" + todoId;
            Request request = new Request.Builder()
                    .url(deleteUrl)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e("TodoListAdapter", "Error: " + response.code());
                }
            } catch (IOException e) {
                Log.e("TodoListAdapter", "Error deleting todo", e);
            }
        }).start();
    }
}
