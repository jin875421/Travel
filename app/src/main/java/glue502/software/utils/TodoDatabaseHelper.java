package glue502.software.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import glue502.software.models.TodoItem;

public class TodoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TodoList.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TODO = "todo";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_COMPLETED = "completed";

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_COMPLETED + " INTEGER)";
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    // 添加TodoItem
    public long insertTodo(TodoItem todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, todo.getTitle());
        contentValues.put(COLUMN_COMPLETED, todo.isCompleted());

        long todoId = db.insert(TABLE_TODO, null, contentValues);
        db.close();
        return todoId;
    }

    // 更新TodoItem
    public int updateTodo(TodoItem todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, todo.getTitle());
        contentValues.put(COLUMN_COMPLETED, todo.isCompleted());

        int rowsAffected = db.update(TABLE_TODO, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(todo.getId())});
        db.close();
        return rowsAffected;
    }

    // 删除TodoItem
    public void deleteTodo(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 查询所有TodoItem
    public List<TodoItem> getAllTodos() {
        List<TodoItem> todos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODO, new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_COMPLETED}, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                TodoItem todo = new TodoItem();
                todo.setId(cursor.getLong(0));
                todo.setTitle(cursor.getString(1));
                todo.setCompleted(cursor.getInt(2) == 1);
                todos.add(todo);
            }
        }
        cursor.close();
        db.close();
        return todos;
    }
    //清空列表
    public void deleteAllTodos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, null, null);
        db.close();
    }
}


