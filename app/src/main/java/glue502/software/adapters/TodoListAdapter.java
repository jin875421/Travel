package glue502.software.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import glue502.software.R;
import glue502.software.models.TodoItem;
import glue502.software.utils.TodoDatabaseHelper;

public class TodoListAdapter extends ArrayAdapter<TodoItem> {
    private TodoDatabaseHelper dbHelper;
    private Context mContext;
    private List<TodoItem> mTodos;

    public TodoListAdapter(Context context, List<TodoItem> todos, TodoDatabaseHelper dbHelper) {
        super(context, 0, todos);
        this.mContext = context;
        this.dbHelper = dbHelper;
        this.mTodos = todos; // 创建一个新的ArrayList来存储传入的todos，避免直接修改传入的列表
        for (int i = 0; i < mTodos.size(); i++) {
            mTodos.get(i).setOriginalIndex(i);
        }
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
                dbHelper.updateTodo(todo);
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
                                dbHelper.deleteTodo(todo.getId());
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
    // 更新列表并根据完成状态进行排序
    private void updateListAfterCheckChange(TodoItem todo, boolean isChecked) {
        todo.setCompleted(isChecked);
        dbHelper.updateTodo(todo);
        sortTodos1();
    }

    // ViewHolder类用于缓存视图组件
    private static class ViewHolder {
        TextView tvTitle;
        CheckBox cbComplete;
    }
    // 分离已完成和未完成任务，未完成任务在前面
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


}
