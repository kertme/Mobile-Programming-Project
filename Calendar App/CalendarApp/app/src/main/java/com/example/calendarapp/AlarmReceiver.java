package com.example.calendarapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;


    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedpreferences;
        sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String sound = sharedpreferences.getString("soundKey","Default");

        notificationManager = NotificationManagerCompat.from(context);
        String test = intent.getStringExtra("set");

        if (test.equals("notify")){
            Toast.makeText(context, "notification arrived", Toast.LENGTH_SHORT).show();

            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("desc");
            String notifyOption = intent.getStringExtra("option");

            Intent intentOpen = new Intent(context, MainActivity.class);
            intentOpen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pi = PendingIntent.getActivity(context,0,intentOpen,0);

            //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Uri alarmSound = Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.gotitdone);

            if (notifyOption.equals("Vibrate+Sound")){
                if (sound.equals("Default")){
                    Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_one)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                            .build();
                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                }
                else{
                    Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_5_ID)
                            .setSmallIcon(R.drawable.ic_one)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                            .setSound(alarmSound)
                            .build();
                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                }

            }
            else if (notifyOption.equals("Vibrate")){
                Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_2_ID)
                        .setSmallIcon(R.drawable.ic_one)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .build();
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            }
            else if (notifyOption.equals("Sound")){
                if(sound.equals("Default")){
                    Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_3_ID)
                            .setSmallIcon(R.drawable.ic_one)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();
                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                }
                else{
                    Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_6_ID)
                            .setSmallIcon(R.drawable.ic_one)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .setSound(alarmSound)
                            .build();
                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                }
            }
            else if (notifyOption.equals("None")){
                Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_4_ID)
                        .setSmallIcon(R.drawable.ic_one)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            }

        }
    }
}
