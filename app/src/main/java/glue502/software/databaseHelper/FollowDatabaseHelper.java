package glue502.software.databaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FollowDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_database.db";
    private static final int DATABASE_VERSION = 1;

    // User Exact Info table
    private static final String TABLE_USER_EXACT_INFO = "user_exact_info";
    private static final String CREATE_TABLE_USER_EXACT_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_EXACT_INFO + " (" +
            "user_id VARCHAR(100) PRIMARY KEY, " +
            "follow_group TEXT" +
            ");";

    public FollowDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER_EXACT_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_EXACT_INFO);
        onCreate(db);
    }
}

