package com.gaopeng.test.sbshujuku;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class KeyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KeyRecordDB";
    private static final int DATABASE_VERSION = 1;

    // 表名和列名
    public static final String TABLE_NAME = "key_records";
    public static final String COL_PKG = "pkg";
    public static final String COL_SINGLE_A_X = "single_a_x";
    public static final String COL_SINGLE_A_Y = "single_a_y";
    public static final String COL_SINGLE_B_X = "single_b_x";
    public static final String COL_SINGLE_B_Y = "single_b_y";
    public static final String COL_DOUBLE_A_DOWN_X = "double_a_down_x";
    public static final String COL_DOUBLE_A_DOWN_Y = "double_a_down_y";
    public static final String COL_DOUBLE_A_UP_X = "double_a_up_x";
    public static final String COL_DOUBLE_A_UP_Y = "double_a_up_y";
    public static final String COL_DOUBLE_B_DOWN_X = "double_b_down_x";
    public static final String COL_DOUBLE_B_DOWN_Y = "double_b_down_y";
    public static final String COL_DOUBLE_B_UP_X = "double_b_up_x";
    public static final String COL_DOUBLE_B_UP_Y = "double_b_up_y";

    // 创建表SQL
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COL_PKG + " TEXT PRIMARY KEY," +
                    COL_SINGLE_A_X + " INTEGER," +
                    COL_SINGLE_A_Y + " INTEGER," +
                    COL_SINGLE_B_X + " INTEGER," +
                    COL_SINGLE_B_Y + " INTEGER," +
                    COL_DOUBLE_A_DOWN_X + " INTEGER," +
                    COL_DOUBLE_A_DOWN_Y + " INTEGER," +
                    COL_DOUBLE_A_UP_X + " INTEGER," +
                    COL_DOUBLE_A_UP_Y + " INTEGER," +
                    COL_DOUBLE_B_DOWN_X + " INTEGER," +
                    COL_DOUBLE_B_DOWN_Y + " INTEGER," +
                    COL_DOUBLE_B_UP_X + " INTEGER," +
                    COL_DOUBLE_B_UP_Y + " INTEGER" +
                    ")";

    public KeyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 添加记录
    public void addKeyRecord(KeyRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PKG, record.getPkg());
        putPoint(values, COL_SINGLE_A_X, COL_SINGLE_A_Y, record.getSingleA());
        putPoint(values, COL_SINGLE_B_X, COL_SINGLE_B_Y, record.getSingleB());
        putPoint(values, COL_DOUBLE_A_DOWN_X, COL_DOUBLE_A_DOWN_Y, record.getDoubleADown());
        putPoint(values, COL_DOUBLE_A_UP_X, COL_DOUBLE_A_UP_Y, record.getDoubleAUp());
        putPoint(values, COL_DOUBLE_B_DOWN_X, COL_DOUBLE_B_DOWN_Y, record.getDoubleBDown());
        putPoint(values, COL_DOUBLE_B_UP_X, COL_DOUBLE_B_UP_Y, record.getDoubleBUp());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // 辅助方法：存储坐标点到ContentValues
    private void putPoint(ContentValues values, String colX, String colY, Point point) {
        values.put(colX, point.x);
        values.put(colY, point.y);
    }

    // 获取所有记录
    public List<KeyRecord> getAllKeyRecords() {
        List<KeyRecord> records = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                KeyRecord record = cursorToKeyRecord(cursor);
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    // 根据pkg获取记录
    public KeyRecord getKeyRecord(String pkg) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null,
                COL_PKG + "=?",
                new String[]{pkg},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            KeyRecord record = cursorToKeyRecord(cursor);
            cursor.close();
            db.close();
            return record;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // 更新记录
    public int updateKeyRecord(KeyRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        putPoint(values, COL_SINGLE_A_X, COL_SINGLE_A_Y, record.getSingleA());
        putPoint(values, COL_SINGLE_B_X, COL_SINGLE_B_Y, record.getSingleB());
        putPoint(values, COL_DOUBLE_A_DOWN_X, COL_DOUBLE_A_DOWN_Y, record.getDoubleADown());
        putPoint(values, COL_DOUBLE_A_UP_X, COL_DOUBLE_A_UP_Y, record.getDoubleAUp());
        putPoint(values, COL_DOUBLE_B_DOWN_X, COL_DOUBLE_B_DOWN_Y, record.getDoubleBDown());
        putPoint(values, COL_DOUBLE_B_UP_X, COL_DOUBLE_B_UP_Y, record.getDoubleBUp());

        return db.update(TABLE_NAME, values,
                COL_PKG + "=?",
                new String[]{record.getPkg()});
    }

    // 删除记录
    public void deleteKeyRecord(String pkg) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_PKG + "=?", new String[]{pkg});
        db.close();
    }

    // 辅助方法：从Cursor创建KeyRecord对象
    @SuppressLint("Range")
    private KeyRecord cursorToKeyRecord(Cursor cursor) {
        return new KeyRecord(
                cursor.getString(cursor.getColumnIndex(COL_PKG)),
                new Point(
                        cursor.getInt(cursor.getColumnIndex(COL_SINGLE_A_X)),
                        cursor.getInt(cursor.getColumnIndex(COL_SINGLE_A_Y))
                ),
                new Point(
                        cursor.getInt(cursor.getColumnIndex(COL_SINGLE_B_X)),
                        cursor.getInt(cursor.getColumnIndex(COL_SINGLE_B_Y))
                ),
                new Point(
                        cursor.getInt(cursor.getColumnIndex(COL_DOUBLE_A_DOWN_X)),
                        cursor.getInt(cursor.getColumnIndex(COL_DOUBLE_A_DOWN_Y))
                ),
                new Point(
                        cursor.getInt(cursor.getColumnIndex(COL_DOUBLE_A_UP_X)),
                        cursor.getInt(cursor.getColumnIndex(COL_DOUBLE_A_UP_Y))
                ),
                new Point(
                        cursor.getInt(cursor.getColumnIndex(COL_DOUBLE_B_DOWN_X)),
                        cursor.getInt(cursor.getColumnIndex(COL_DOUBLE_B_DOWN_Y))
                ),
                new Point(
                        cursor.getInt(cursor.getColumnIndex(COL_DOUBLE_B_UP_X)),
                        cursor.getInt(cursor.getColumnIndex(COL_DOUBLE_B_UP_Y))
                )
        );
    }
}
