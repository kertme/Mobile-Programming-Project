package com.example.calendarapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";
    public static final String CHANNEL_4_ID = "channel4";
    public static final String CHANNEL_5_ID = "channel5";
    public static final String CHANNEL_6_ID = "channel6";
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels(this);
    }

    private void createNotificationChannels(Context context){
        //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri alarmSound = Uri.parse("android.resource://"
                + this.getPackageName() + "/" + R.raw.gotitdone);

        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is Channel 1");
            channel1.enableVibration(true);
            //channel1.setSound(alarmSound,att);

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2_ID, "Channel 2", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("This is Channel 2");
            channel2.enableVibration(true);
            channel2.setSound(null,null);

            NotificationChannel channel3 = new NotificationChannel(CHANNEL_3_ID, "Channel 3", NotificationManager.IMPORTANCE_HIGH);
            channel3.setDescription("This is Channel 3");
            channel3.enableVibration(false);

            NotificationChannel channel4 = new NotificationChannel(CHANNEL_4_ID, "Channel 4", NotificationManager.IMPORTANCE_HIGH);
            channel4.setDescription("This is Channel 4");
            channel4.enableVibration(false);
            channel4.setSound(null,null);



            NotificationChannel channel5 = new NotificationChannel(CHANNEL_5_ID, "Channel 5", NotificationManager.IMPORTANCE_HIGH);
            channel5.setDescription("This is Channel 5");
            channel5.enableVibration(true);
            channel5.setSound(alarmSound,att);

            NotificationChannel channel6 = new NotificationChannel(CHANNEL_6_ID, "Channel 6", NotificationManager.IMPORTANCE_HIGH);
            channel6.setDescription("This is Channel 6");
            channel6.enableVibration(false);
            channel6.setSound(alarmSound,att);


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
            manager.createNotificationChannel(channel4);
            manager.createNotificationChannel(channel5);
            manager.createNotificationChannel(channel6);
        }
    }
}
