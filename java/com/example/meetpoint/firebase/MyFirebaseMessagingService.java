package com.example.meetpoint.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.meetpoint.R;
import com.example.meetpoint.activities.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "meetpoint_notifications";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // You can upload the token to Firestore for sending targeted notifications
        // Example: saveTokenToDatabase(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String msg = remoteMessage.getNotification().getBody();
            sendNotification(title, msg);
        }

        // If message contains data payload
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String msg = remoteMessage.getData().get("message");
            sendNotification(title, msg);
        }
    }

    private void sendNotification(String title, String message) {

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create Notification Channel for Android 8+
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "MeetPoint Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("General notifications for MeetPoint application");

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification) // <-- ADD THIS ICON
                        .setContentTitle(title != null ? title : "MeetPoint Alert")
                        .setContentText(message != null ? message : "New message received")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        }
    }
}
