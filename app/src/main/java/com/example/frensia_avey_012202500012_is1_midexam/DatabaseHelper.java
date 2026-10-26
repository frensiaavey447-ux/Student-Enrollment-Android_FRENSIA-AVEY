package com.example.frensia_avey_012202500012_is1_midexam;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentEnrollment.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "students";
    private static final String COL_ID = "student_id";
    private static final String COL_NAME = "student_name";
    private static final String COL_ENROLLED = "enrolled";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_ENROLLED + " INTEGER NOT NULL DEFAULT 0)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addStudent(String id, String name, boolean enrolled) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_NAME, name);
        values.put(COL_ENROLLED, enrolled ? 1 : 0);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean updateStudent(String id, String name,
                                 boolean enrolled) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_ENROLLED, enrolled ? 1 : 0);

        int result = db.update(
                TABLE_NAME,
                values,
                COL_ID + "=?",
                new String[]{id}
        );

        return result > 0;
    }

    public boolean deleteStudent(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                COL_ID + "=?",
                new String[]{id}
        );

        return result > 0;
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " ASC"
        );
    }

    public Cursor getStudent(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_NAME,
                null,
                COL_ID + "=?",
                new String[]{id},
                null,
                null,
                null
        );
    }
}
