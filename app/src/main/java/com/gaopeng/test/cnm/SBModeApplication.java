package com.gaopeng.test.cnm;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.gaopeng.test.sbshujuku.KeyContract;
import com.gaopeng.test.sbshujuku.KeyDatabaseHelper;
import com.gaopeng.test.sbshujuku.KeyRecordHelper;

public class SBModeApplication extends AppCompatActivity {

    private DragContainerView dragContainer;
    private TextView tvArea1, tvArea2;
    private SharedPreferences prefs;
    private KeyDatabaseHelper dbHelper;
    private KeyRecordHelper keyRecordHelper;
    private Uri newRecordUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_view);

        // 初始化视图
        dragContainer = findViewById(R.id.dragContainer);
        tvArea1 = findViewById(R.id.tvArea1);
        tvArea2 = findViewById(R.id.tvArea2);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnReset = findViewById(R.id.btnReset);
        Button btnLoad = findViewById(R.id.btnLoad);
        //dbHelper = new KeyDatabaseHelper(this);
        keyRecordHelper = new KeyRecordHelper(getContentResolver());
        // 初始化SharedPreferences
        prefs = getSharedPreferences("DragPositions", MODE_PRIVATE);

        // 设置位置变化监听器
        dragContainer.setOnPositionChangeListener((id, x, y) -> {
            if (id == 1) {
                tvArea1.setText(String.format("区域1: (%d, %d)", x, y));
            } else if (id == 2) {
                tvArea2.setText(String.format("区域2: (%d, %d)", x, y));
            }
        });

        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> savePositions());

        // 重置按钮点击事件
        btnReset.setOnClickListener(v -> resetPositions());

        // 加载按钮点击事件
        btnLoad.setOnClickListener(v -> loadPositions());
        savePositions();
        // 初始加载保存的位置
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadPositions(); // 延迟加载数据
        }, 1000);
    }

    private void savePositions() {
        Point pos1 = dragContainer.getViewPosition(1);
        Point pos2 = dragContainer.getViewPosition(2);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("area1_x", pos1.x);
        editor.putInt("area1_y", pos1.y);
        editor.putInt("area2_x", pos2.x);
        editor.putInt("area2_y", pos2.y);
        editor.apply();
        String packageName = getApplicationContext().getPackageName();
        Log.d("cnm","packageName->"+packageName);

        Point singleA = new Point(pos1.x, pos1.y);
        Point singleB = new Point(pos2.x, pos2.y);
        Point doubleADown = new Point(50, 300);
        Point doubleAUp = new Point(50, 350);
        Point doubleBDown = new Point(200, 300);
        Point doubleBUp = new Point(200, 350);
//        KeyRecord record1 = new KeyRecord(packageName,
//                singleA, singleB,
//                doubleADown, doubleAUp,
//                doubleBDown, doubleBUp);
//        KeyRecord fetched = dbHelper.getKeyRecord(packageName);
//        if(fetched == null){
//            dbHelper.addKeyRecord(record1);
//        }else {
//            dbHelper.updateKeyRecord(record1);
//        }
        newRecordUri = keyRecordHelper.addKeyRecord(
                packageName,
                singleA, singleB,
                doubleADown, doubleAUp,
                doubleBDown, doubleBUp
        );
        Log.d("shabi","newRecordUri->"+newRecordUri);
        Cursor cursor = keyRecordHelper.getKeyRecordByPkg(packageName);
        if(cursor == null){
            Log.d("shabi","cursor第一次进来 空，插入数据");
            newRecordUri = keyRecordHelper.addKeyRecord(
                    packageName,
                    singleA, singleB,
                    doubleADown, doubleAUp,
                    doubleBDown, doubleBUp
            );
        }else{
            Log.d("shabi","cursor不是第一次进来 不为空，更新数据");
            int updatedRows = keyRecordHelper.updateKeyRecord(
                    newRecordUri,
                    singleA, singleB,
                    doubleADown, doubleAUp,
                    doubleBDown, doubleBUp
            );
        }
        Toast.makeText(this, "坐标已保存!", Toast.LENGTH_SHORT).show();
    }

    private void loadPositions() {
        int area1_x = prefs.getInt("area1_x", -1);
        int area1_y = prefs.getInt("area1_y", -1);
        int area2_x = prefs.getInt("area2_x", -1);
        int area2_y = prefs.getInt("area2_y", -1);

        Point fetchedSingleA = new Point(500,200);
        Point fetchedSingleB = new Point(200,200);

        Point pos1 = dragContainer.getViewPosition(1);
        Point pos2 = dragContainer.getViewPosition(2);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("area1_x", pos1.x);
        editor.putInt("area1_y", pos1.y);
        editor.putInt("area2_x", pos2.x);
        editor.putInt("area2_y", pos2.y);
        editor.apply();
        String packageName = getApplicationContext().getPackageName();
        Log.d("shabi","packageName->"+packageName);

//        KeyRecord fetched = dbHelper.getKeyRecord(packageName);
//        if (fetched != null) {
//            Log.d("shabi", "Fetched SingleB: (" +
//                    fetched.getSingleB().x + ", " + fetched.getSingleB().y + ")");
//        }
        Cursor cursor = keyRecordHelper.getKeyRecordByPkg(packageName);
        if (cursor != null && cursor.moveToFirst()) {
            fetchedSingleA = keyRecordHelper.getPointFromCursor(
                    cursor,
                    KeyContract.KeyEntry.COLUMN_SINGLE_A_X,
                    KeyContract.KeyEntry.COLUMN_SINGLE_A_Y
            );
            fetchedSingleB = keyRecordHelper.getPointFromCursor(
                    cursor,
                    KeyContract.KeyEntry.COLUMN_SINGLE_B_X,
                    KeyContract.KeyEntry.COLUMN_SINGLE_B_Y
            );
        }

        if (area1_x != -1 && area1_y != -1) {
            dragContainer.setViewPositions(1, fetchedSingleA.x, fetchedSingleA.y);
            tvArea1.setText(String.format("区域1: (%d, %d)", area1_x, area1_y));
        }

        if (area2_x != -1 && area2_y != -1) {
            dragContainer.setViewPositions(2, fetchedSingleB.x, fetchedSingleB.y);
            tvArea2.setText(String.format("区域2: (%d, %d)", area2_x, area2_y));
        }

        if (area1_x != -1 || area2_x != -1) {
            Toast.makeText(this, "坐标已加载!", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPositions(){
        dragContainer.resetPositions();
        String packageName = getApplicationContext().getPackageName();
        Log.d("shabi","packageName->"+packageName);

        Point singleA = new Point(124, 340);
        Point singleB = new Point(374, 568);
        Point doubleADown = new Point(50, 300);
        Point doubleAUp = new Point(50, 350);
        Point doubleBDown = new Point(200, 300);
        Point doubleBUp = new Point(200, 350);
        newRecordUri = keyRecordHelper.addKeyRecord(
                packageName,
                singleA, singleB,
                doubleADown, doubleAUp,
                doubleBDown, doubleBUp
        );
        int updatedRows = keyRecordHelper.updateKeyRecord(
                newRecordUri,
                singleA, singleB,
                doubleADown, doubleAUp,
                doubleBDown, doubleBUp
        );
    }


    @Override
    protected void onPause() {
        super.onPause();
        // 保存当前位置
        savePositions();
    }
}