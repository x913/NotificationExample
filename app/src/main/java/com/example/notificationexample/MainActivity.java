package com.example.notificationexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;

    public static final String ACTION_UPDATE_NOTIFICATION = "com.notificationexample.ACTION_UPDATE_NOTIFICATION";
    public static final String ACTION_DELETE_NOTIFICATION = "com.notificationexample.ACTION_DELETE_NOTIFICATION";
    public static final String ACTION_LEARNMORE_NOTIFICATION = "com.notificationexample.ACTION_LEARNMORE_NOTIFICATION";

    private NotificationManager mNotificationManager;
    private NotificationReceiver mReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        registerReceiver(mReceiver, new IntentFilter(ACTION_LEARNMORE_NOTIFICATION));
        registerReceiver(mReceiver, new IntentFilter(ACTION_DELETE_NOTIFICATION));

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotification();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelNotification();
            }
        });

        findViewById(R.id.notify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder builder = getNotificationBuilder();
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage).setBigContentTitle("Notification Updated!"));
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private NotificationCompat.Builder getNotificationBuilder() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_android);

        return notifyBuilder;
    }

    public void createNotificationChannel() {
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create notification channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);;
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifications from Mascot");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private PendingIntent buildIntentForBroadcast(String action, int notificationId, int flag) {
        Intent intent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, flag);
        return pendingIntent;
    }

    public void sendNotification() {
        NotificationCompat.Builder notificationBuilder = getNotificationBuilder();

        PendingIntent updatePendingIntent = buildIntentForBroadcast(ACTION_UPDATE_NOTIFICATION, NOTIFICATION_ID, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent learnMorePendingIntent = buildIntentForBroadcast(ACTION_LEARNMORE_NOTIFICATION, NOTIFICATION_ID, PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        notificationBuilder.addAction(R.drawable.ic_update, "Learn more", learnMorePendingIntent);

        notificationBuilder.setDeleteIntent( buildIntentForBroadcast(ACTION_DELETE_NOTIFICATION, NOTIFICATION_ID, PendingIntent.FLAG_ONE_SHOT) );

        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        Log.d("AAA", "sendNotification");
    }


    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            //Log.d("AAA", "NotificationReceiver");

            String action = intent.getAction();
            switch (action) {
                case MainActivity.ACTION_LEARNMORE_NOTIFICATION:
                    Log.d("AAA", "Learn more");
                    cancelNotification();
                    break;
                case MainActivity.ACTION_UPDATE_NOTIFICATION:
                    Log.d("AAA", "Update");
                    updateNotification();
                    break;
                case MainActivity.ACTION_DELETE_NOTIFICATION:
                    Log.d("AAA", "DELETE");
                    break;
            }


        }
    }

}