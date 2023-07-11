package com.example.madminiproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashMap;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    private TextToSpeech tts;
    private static final String UTTERANCE_ID = "alarmUtterance";
    private static final String CHANNEL_ID = "meeting_reminder_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String agenda = intent.getStringExtra("agenda");
        String time = intent.getStringExtra("time");

        if (!TextUtils.isEmpty(agenda) && !TextUtils.isEmpty(time)) {
            initializeTextToSpeech(context, agenda, time);
        }
    }

    private void initializeTextToSpeech(Context context, String agenda, String time) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Language not supported, handle the error
                    } else {
                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String utteranceId) {
                                // Utterance started
                            }

                            @Override
                            public void onDone(String utteranceId) {
                                // Utterance completed
                                if (utteranceId.equals(UTTERANCE_ID)) {
                                    showNotification(context, agenda, time);
                                }
                            }

                            @Override
                            public void onError(String utteranceId) {
                                // Utterance error occurred
                            }
                        });

                        speakAgenda(agenda, time);
                    }
                } else {
                    // TextToSpeech initialization failed, handle the error
                }
            }
        });
    }

    private void speakAgenda(String agenda, String time) {
        if (tts != null && !TextUtils.isEmpty(agenda) && !TextUtils.isEmpty(time)) {
            // Set the speech rate
            tts.setSpeechRate(0.8f);

            // Build the message to be spoken
            String speechMessage = "There is a meeting at " + time + " regarding " + agenda;

            // Speak the message
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(speechMessage, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID);
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);
                tts.speak(speechMessage, TextToSpeech.QUEUE_FLUSH, params);
            }
        }
    }

    private void showNotification(Context context, String agenda, String time) {
        // Create a notification channel (for devices running Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Meeting Reminder";
            String channelDescription = "Notification Channel for Meeting Reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the intent to launch the app when the notification is clicked
        Intent notificationIntent = new Intent(context, MainActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, flags);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Meeting Reminder")
                .setContentText("There is a meeting at " + time + " regarding " + agenda)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}



//    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        if (action != null && action.equals("STOP_ALARM")) {
//            stopAlarm();
//        } else {
//            // Handle the alarm logic here
//            // Play the alarm tone
//            playAlarm(context);
//
//            // Display the notification
//            showNotification(context);
//        }
//    }
//
//    private void playAlarm(Context context) {
//        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
//        mediaPlayer.setLooping(true);
//        mediaPlayer.start();
//    }
//
//    private void stopAlarm() {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void showNotification(Context context) {
//        // Create a notification channel if running on Android Oreo and above
//        createNotificationChannel(context);
//
//        // Create the notification builder
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "AlarmChannel")
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentTitle("Meeting Reminder")
//                .setContentText("You have a meeting scheduled!")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_ALARM)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setAutoCancel(true);
//
//        // Create an intent for the notification
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//        builder.setContentIntent(pendingIntent);
//
//        // Create an intent for the stop action
//        Intent stopIntent = new Intent(context, AlarmReceiver.class);
//        stopIntent.setAction("STOP_ALARM");
//        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, 0);
//
//        // Add the stop action to the notification
//        builder.addAction(R.drawable.ic_stop, "Stop", stopPendingIntent);
//
//        // Show the notification
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(1, builder.build());
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void createNotificationChannel(Context context) {
//        NotificationChannel channel = new NotificationChannel("AlarmChannel", "Alarm", NotificationManager.IMPORTANCE_HIGH);
//        channel.setDescription("Meeting Reminder");
//        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//
//        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//    }



