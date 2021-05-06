package uk.ac.tees.aad.w9491709.Utills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLiteHelpers extends SQLiteOpenHelper {


    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "mydb";
    private static final String TABLE_Users = "Alarm";
    private static final String KEY_ID = "id";

    Context context;

    public SQLiteHelpers(Context context1) {
        super(context1, DB_NAME, null, DB_VERSION);
        context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "("
                + "mYear" + " INT,"
                + "mMonth" + " INT, "
                + "mDay" + " INT,"
                + "mHours" + " INT,"
                + "mMinutes" + " INT" + ")";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Users);
        onCreate(db);
    }





    public void insertData(int year, int month, int day, int hours, int mins) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mYear", year); // Contact Name
        values.put("mMonth", month); // Contact Name
        values.put("mDay", day); // Contact Name
        values.put("mHours", hours); // Contact Name
        values.put("mMinutes", mins); // Contact Name

        long insert = db.insert(TABLE_Users, null, values);
        Log.d("hdhdhdhdhhd", "insertData: " + insert);

        db.close(); // Closing database connection
    }


}
