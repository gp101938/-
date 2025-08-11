package com.gaopeng.test.sbshujuku;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KeyDbHelper extends SQLiteOpenHelper {
   private static final String DATABASE_NAME = "KeyRecordDB";
   private static final int DATABASE_VERSION = 1;

   // 创建表SQL
   private static final String SQL_CREATE_ENTRIES =
           "CREATE TABLE " + KeyContract.KeyEntry.TABLE_NAME + " (" +
                   KeyContract.KeyEntry._ID + " INTEGER PRIMARY KEY," +
                   KeyContract.KeyEntry.COLUMN_PKG + " TEXT UNIQUE," +
                   KeyContract.KeyEntry.COLUMN_SINGLE_A_X + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_SINGLE_A_Y + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_SINGLE_B_X + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_SINGLE_B_Y + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_DOUBLE_A_DOWN_X + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_DOUBLE_A_DOWN_Y + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_DOUBLE_A_UP_X + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_DOUBLE_A_UP_Y + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_DOUBLE_B_DOWN_X + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_DOUBLE_B_DOWN_Y + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_DOUBLE_B_UP_X + " INTEGER," +
                   KeyContract.KeyEntry.COLUMN_DOUBLE_B_UP_Y + " INTEGER" +
                   ")";

   public KeyDbHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      db.execSQL(SQL_CREATE_ENTRIES);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + KeyContract.KeyEntry.TABLE_NAME);
      onCreate(db);
   }
}