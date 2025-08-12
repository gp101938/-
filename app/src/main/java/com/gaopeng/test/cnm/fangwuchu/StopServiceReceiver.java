package com.gaopeng.test.cnm.fangwuchu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class StopServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 停止服务
        context.stopService(new Intent(context, ForegroundService.class));

        // 更新开关状态
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean("notification_enabled", false)
                .apply();

        // 通知UI更新
        Intent updateIntent = new Intent("com.example.app.SERVICE_STATE_CHANGED");
        context.sendBroadcast(updateIntent);
    }
}