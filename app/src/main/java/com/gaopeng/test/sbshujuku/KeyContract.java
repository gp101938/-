package com.gaopeng.test.sbshujuku;

import android.net.Uri;
import android.provider.BaseColumns;

public final class KeyContract {
    private KeyContract() {}

    public static final String CONTENT_AUTHORITY = "com.gaopeng.test.cnm.keyprovider";

    public static final String PATH_RECORD_BY_PKG = "record_by_pkg";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECORDS = "records";

    public static class KeyEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.gaopeng.test.cnm.keyprovider/records");
                //Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECORDS);
                public static final Uri CONTENT_URI_BY_PKG =
                        Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECORD_BY_PKG);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "." + PATH_RECORDS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "." + PATH_RECORDS;

        // 表名
        public static final String TABLE_NAME = "key_records";

        // 列名
        public static final String COLUMN_PKG = "pkg";
        public static final String COLUMN_SINGLE_A_X = "single_a_x";
        public static final String COLUMN_SINGLE_A_Y = "single_a_y";
        public static final String COLUMN_SINGLE_B_X = "single_b_x";
        public static final String COLUMN_SINGLE_B_Y = "single_b_y";
        public static final String COLUMN_DOUBLE_A_DOWN_X = "double_a_down_x";
        public static final String COLUMN_DOUBLE_A_DOWN_Y = "double_a_down_y";
        public static final String COLUMN_DOUBLE_A_UP_X = "double_a_up_x";
        public static final String COLUMN_DOUBLE_A_UP_Y = "double_a_up_y";
        public static final String COLUMN_DOUBLE_B_DOWN_X = "double_b_down_x";
        public static final String COLUMN_DOUBLE_B_DOWN_Y = "double_b_down_y";
        public static final String COLUMN_DOUBLE_B_UP_X = "double_b_up_x";
        public static final String COLUMN_DOUBLE_B_UP_Y = "double_b_up_y";
    }
}
