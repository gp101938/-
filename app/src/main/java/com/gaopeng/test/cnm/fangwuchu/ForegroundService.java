package com.gaopeng.test.cnm.fangwuchu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.gaopeng.test.cnm.R;
import com.gaopeng.test.cnm.SBModeApplication;

public class ForegroundService extends Service {
   private static final int NOTIFICATION_ID = 1001;
   private static final String CHANNEL_ID = "resident_service_channel";

   @Override
   public void onCreate() {
      super.onCreate();
      createNotificationChannel();
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      Notification notification = buildNotification();
      startForeground(NOTIFICATION_ID, notification);
      return START_STICKY;
   }

   private void createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel channel = new NotificationChannel(
                 CHANNEL_ID,
                 "常驻服务通知",
                 NotificationManager.IMPORTANCE_LOW
         );
         channel.setDescription("显示后台服务运行状态");
         //channel.setBlockableSystem(true);
         channel.setBypassDnd(true);
         NotificationManager manager = getSystemService(NotificationManager.class);

         manager.createNotificationChannel(channel);
      }
   }

   private Notification buildNotification() {
      // 创建停止服务的Intent
      Intent stopIntent = new Intent(this, StopServiceReceiver.class);
      PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
              this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
      );

      // 创建打开应用的Intent
      Intent appIntent = new Intent(this, SBModeApplication.class);
      PendingIntent appPendingIntent = PendingIntent.getActivity(
              this, 0, appIntent, PendingIntent.FLAG_IMMUTABLE
      );

      return new NotificationCompat.Builder(this, CHANNEL_ID)
              .setContentTitle("肩键功能已打开，防止误触")
              .setContentText("此通知无法手动关闭")
              .setSmallIcon(R.drawable.ic_service)
              .setContentIntent(appPendingIntent)
              .setOngoing(true) // 关键：使通知持续显示
              .setAutoCancel(false) // 关键：禁用自动取消
              .setShowWhen(false) // 隐藏时间
              .setPriority(NotificationCompat.PRIORITY_MIN) // 降低优先级但保持可见
              .setCategory(NotificationCompat.CATEGORY_SERVICE)
              .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
              .build();
   }

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      // 服务停止时取消通知
      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.cancel(NOTIFICATION_ID);
   }
}
