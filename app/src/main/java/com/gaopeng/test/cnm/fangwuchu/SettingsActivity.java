package com.gaopeng.test.cnm.fangwuchu;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.gaopeng.test.cnm.R;

public class SettingsActivity extends AppCompatActivity {

    private final String NOTIFICATION_ENABLED = "notification_enabled";
    private SwitchCompat switchNotification;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("通知设置");

        switchNotification = findViewById(R.id.switch_notification);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // 读取保存的状态
//        boolean isEnabled = prefs.getBoolean(NOTIFICATION_ENABLED, false);
//        switchNotification.setChecked(isEnabled);

        // 设置开关监听
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateNotificationSetting(isChecked);
        });

        // 添加厂商设置引导
        Button guideBtn = findViewById(R.id.btn_guide);
        guideBtn.setOnClickListener(v -> showVendorGuideDialog());
    }

    private void updateNotificationSetting(boolean enable) {
        // 保存状态
        prefs.edit().putBoolean(NOTIFICATION_ENABLED, enable).apply();

        // 控制服务启停
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        if (enable) {
            // 开启服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            Toast.makeText(this, "已开启常驻通知", Toast.LENGTH_SHORT).show();
        } else {
            // 关闭服务
            stopService(serviceIntent);
            Toast.makeText(this, "已关闭常驻通知", Toast.LENGTH_SHORT).show();
        }
    }

    private void showVendorGuideDialog() {
        new AlertDialog.Builder(this)
                .setTitle("后台权限设置指南")
                .setMessage("如果通知无法常驻，请按以下步骤设置：\n\n" +
                        "1. 进入手机设置 → 应用管理\n" +
                        "2. 找到本应用并进入\n" +
                        "3. 开启『自启动』权限\n" +
                        "4. 关闭『电池优化』\n" +
                        "5. 锁定应用到后台")
                .setPositiveButton("确定", null)
                .show();
    }
}
