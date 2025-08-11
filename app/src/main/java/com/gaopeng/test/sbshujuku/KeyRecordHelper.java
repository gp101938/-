package com.gaopeng.test.sbshujuku;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;

public class KeyRecordHelper {
    private final ContentResolver contentResolver;

    public KeyRecordHelper(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    // 添加记录
    public Uri addKeyRecord(String pkg, Point singleA, Point singleB,
                            Point doubleADown, Point doubleAUp,
                            Point doubleBDown, Point doubleBUp) {
        ContentValues values = new ContentValues();
        values.put(KeyContract.KeyEntry.COLUMN_PKG, pkg);
        putPoint(values, KeyContract.KeyEntry.COLUMN_SINGLE_A_X, KeyContract.KeyEntry.COLUMN_SINGLE_A_Y, singleA);
        putPoint(values, KeyContract.KeyEntry.COLUMN_SINGLE_B_X, KeyContract.KeyEntry.COLUMN_SINGLE_B_Y, singleB);
        putPoint(values, KeyContract.KeyEntry.COLUMN_DOUBLE_A_DOWN_X, KeyContract.KeyEntry.COLUMN_DOUBLE_A_DOWN_Y, doubleADown);
        putPoint(values, KeyContract.KeyEntry.COLUMN_DOUBLE_A_UP_X, KeyContract.KeyEntry.COLUMN_DOUBLE_A_UP_Y, doubleAUp);
        putPoint(values, KeyContract.KeyEntry.COLUMN_DOUBLE_B_DOWN_X, KeyContract.KeyEntry.COLUMN_DOUBLE_B_DOWN_Y, doubleBDown);
        putPoint(values, KeyContract.KeyEntry.COLUMN_DOUBLE_B_UP_X, KeyContract.KeyEntry.COLUMN_DOUBLE_B_UP_Y, doubleBUp);

        return contentResolver.insert(KeyContract.KeyEntry.CONTENT_URI, values);
    }

    // 获取所有记录
    public Cursor getAllKeyRecords() {
        return contentResolver.query(
                KeyContract.KeyEntry.CONTENT_URI,
                null, null, null, null
        );
    }

    // 根据ID获取记录
    public Cursor getKeyRecordById(Uri uri) {
        return contentResolver.query(
                uri,
                null, null, null, null
        );
    }

    // 根据包名获取记录
    public Cursor getKeyRecordByPkg(String pkg) {
        return contentResolver.query(
                KeyContract.KeyEntry.CONTENT_URI,
                null,
                KeyContract.KeyEntry.COLUMN_PKG + " = ?",
                new String[]{pkg},
                null
        );
    }

    // 更新记录
    public int updateKeyRecord(Uri uri, Point singleA, Point singleB,
                               Point doubleADown, Point doubleAUp,
                               Point doubleBDown, Point doubleBUp) {
        Log.d("shabi","uri->"+uri);
        ContentValues values = new ContentValues();
        putPoint(values, KeyContract.KeyEntry.COLUMN_SINGLE_A_X, KeyContract.KeyEntry.COLUMN_SINGLE_A_Y, singleA);
        putPoint(values, KeyContract.KeyEntry.COLUMN_SINGLE_B_X, KeyContract.KeyEntry.COLUMN_SINGLE_B_Y, singleB);
        putPoint(values, KeyContract.KeyEntry.COLUMN_DOUBLE_A_DOWN_X, KeyContract.KeyEntry.COLUMN_DOUBLE_A_DOWN_Y, doubleADown);
        putPoint(values, KeyContract.KeyEntry.COLUMN_DOUBLE_A_UP_X, KeyContract.KeyEntry.COLUMN_DOUBLE_A_UP_Y, doubleAUp);
        putPoint(values, KeyContract.KeyEntry.COLUMN_DOUBLE_B_DOWN_X, KeyContract.KeyEntry.COLUMN_DOUBLE_B_DOWN_Y, doubleBDown);
        putPoint(values, KeyContract.KeyEntry.COLUMN_DOUBLE_B_UP_X, KeyContract.KeyEntry.COLUMN_DOUBLE_B_UP_Y, doubleBUp);

        return contentResolver.update(uri, values, null, null);
    }

    // 删除记录
    public int deleteKeyRecord(Uri uri) {
        return contentResolver.delete(uri, null, null);
    }

    // 辅助方法：存储坐标点到ContentValues
    private void putPoint(ContentValues values, String colX, String colY, Point point) {
        values.put(colX, point.x);
        values.put(colY, point.y);
    }

    // 从Cursor中解析点对象
    public Point getPointFromCursor(Cursor cursor, String colX, String colY) {
        int x = cursor.getInt(cursor.getColumnIndexOrThrow(colX));
        int y = cursor.getInt(cursor.getColumnIndexOrThrow(colY));
        return new Point(x, y);
    }
}
