package com.example.madminiproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private MediaPlayer mediaPlayer;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("STOP_ALARM")) {
            stopAlarm();
        } else {
            // Handle the alarm logic here
            // Play the alarm tone
            playAlarm(context);

            // Display the notification
            showNotification(context);
        }
    }

    private void playAlarm(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(Context context) {
        // Create a notification channel if running on Android Oreo and above
        createNotificationChannel(context);

        // Create the notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "AlarmChannel")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Meeting Reminder")
                .setContentText("You have a meeting scheduled!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

        // Create an intent for the notification
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        // Create an intent for the stop action
        Intent stopIntent = new Intent(context, AlarmReceiver.class);
        stopIntent.setAction("STOP_ALARM");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, 0);

        // Add the stop action to the notification
        builder.addAction(R.drawable.ic_stop, "Stop", stopPendingIntent);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel("AlarmChannel", "Alarm", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Meeting Reminder");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}


