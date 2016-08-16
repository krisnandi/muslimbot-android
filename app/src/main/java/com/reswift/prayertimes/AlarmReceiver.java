package com.reswift.prayertimes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class AlarmReceiver extends BroadcastReceiver{

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION_TITLE = "notification-title";
    public static String NOTIFICATION_INFO = "notification-info";
    public static String NOTIFICATION_TICK = "notification-tick";


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent = new Intent(context, SplashScreen.class);

        String id = intent.getStringExtra(NOTIFICATION_ID);
        String title = intent.getStringExtra(NOTIFICATION_TITLE);
        String info = intent.getStringExtra(NOTIFICATION_INFO);
        String tick = intent.getStringExtra(NOTIFICATION_TICK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SplashScreen.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle(title)
                .setContentText(info)
                .setTicker(tick)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setGroup(info)
                .setAutoCancel(true)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Random random = new Random();
        //int m = random.nextInt(9999 - 1000) + 1000;
        //notificationManager.notify(m, notification);
        notificationManager.notify(0, notification);

    }
}