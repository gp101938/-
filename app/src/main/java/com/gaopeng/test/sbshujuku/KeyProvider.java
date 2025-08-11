package com.gaopeng.test.sbshujuku;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class KeyProvider extends ContentProvider {
    private KeyDbHelper dbHelper;

    // URI匹配器
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int RECORDS = 100;
    private static final int RECORD_ID = 101;
    private static final int RECORD_BY_PKG = 102;


    static {
        sUriMatcher.addURI(KeyContract.CONTENT_AUTHORITY, KeyContract.PATH_RECORDS, RECORDS);
        sUriMatcher.addURI(KeyContract.CONTENT_AUTHORITY, KeyContract.PATH_RECORDS + "/#", RECORD_ID);
        sUriMatcher.addURI(KeyContract.CONTENT_AUTHORITY,KeyContract.PATH_RECORD_BY_PKG + "/*",RECORD_BY_PKG);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new KeyDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECORDS:
                cursor = database.query(KeyContract.KeyEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case RECORD_ID:
                selection = KeyContract.KeyEntry._ID + "=?";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(KeyContract.KeyEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case RECORD_BY_PKG:
                // 从URI中获取包名
                String pkg = uri.getLastPathSegment();
                if (pkg != null) {
                    pkg = Uri.decode(pkg); // 解码特殊字符
                }

                selection = KeyContract.KeyEntry.COLUMN_PKG + "=?";
                selectionArgs = new String[]{pkg};

                cursor = database.query(KeyContract.KeyEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // 设置通知URI
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        for (String key : values.keySet()) {
            Log.d("DB_INSERT", key + " = " + values.get(key));
        }
        if (match != RECORDS) {
            throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String pkg = values.getAsString(KeyContract.KeyEntry.COLUMN_PKG);
        Log.d("shabi","插入包名->"+pkg);
        if (pkg == null || pkg.isEmpty()) {
            throw new IllegalArgumentException("Package name must be provided");
        }
        Cursor cursor = database.query(
                KeyContract.KeyEntry.TABLE_NAME,
                new String[]{KeyContract.KeyEntry._ID},
                KeyContract.KeyEntry.COLUMN_PKG + " = ?",
                new String[]{pkg},
                null, null, null
        );
        Uri resultUri;
        if (cursor != null && cursor.moveToFirst()) {
            // 包名已存在，执行更新
            long existingId = cursor.getLong(cursor.getColumnIndexOrThrow(KeyContract.KeyEntry._ID));
            resultUri = ContentUris.withAppendedId(uri, existingId);

            // 更新现有记录
            database.update(
                    KeyContract.KeyEntry.TABLE_NAME,
                    values,
                    KeyContract.KeyEntry._ID + " = ?",
                    new String[]{String.valueOf(existingId)}
            );
        } else{
            long id = database.insert(KeyContract.KeyEntry.TABLE_NAME, null, values);

        if (id == -1) {
            throw new SQLException("Failed to insert row into " + uri);
            }
            resultUri = ContentUris.withAppendedId(uri, id);
        }
        if (cursor != null) {
            cursor.close();
        }

        Uri pkgUri = Uri.withAppendedPath(
                KeyContract.KeyEntry.CONTENT_URI_BY_PKG,
                Uri.encode(pkg) // 对包名进行编码，处理特殊字符
        );

        // 通知数据变更
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(pkgUri, null);

        return pkgUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int rowsUpdated;

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        switch (match) {
            case RECORDS:
                rowsUpdated = database.update(KeyContract.KeyEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case RECORD_ID:
                selection = KeyContract.KeyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(KeyContract.KeyEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case RECORD_BY_PKG:
                String pkg = uri.getLastPathSegment();
                if (pkg != null) {
                    pkg = Uri.decode(pkg);
                }
                selection = KeyContract.KeyEntry.COLUMN_PKG + "=?";
                selectionArgs = new String[]{pkg};
                rowsUpdated = database.update(KeyContract.KeyEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int rowsDeleted;

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        switch (match) {
            case RECORDS:
                rowsDeleted = database.delete(KeyContract.KeyEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case RECORD_ID:
                selection = KeyContract.KeyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(KeyContract.KeyEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case RECORD_BY_PKG:
                String pkg = uri.getLastPathSegment();
                if (pkg != null) {
                    pkg = Uri.decode(pkg);
                }
                selection = KeyContract.KeyEntry.COLUMN_PKG + "=?";
                selectionArgs = new String[]{pkg};
                rowsDeleted = database.delete(KeyContract.KeyEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECORDS:
                return KeyContract.KeyEntry.CONTENT_TYPE;
            case RECORD_ID:
                return KeyContract.KeyEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri);
        }
    }
}
