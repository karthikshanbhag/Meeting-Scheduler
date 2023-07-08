package com.example.madminiproject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class StopAlarmReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Stop the alarm tone

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Cancel the notification
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        //notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID);
    }
}

