package uk.ac.tees.aad.w9491709.Utills;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import uk.ac.tees.aad.w9491709.Activities.UsersActivity;
import uk.ac.tees.aad.w9491709.R;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String body = remoteMessage.getNotification().getBody();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHAT");
        builder.setContentTitle(title);
        builder.setContentTitle(body);
        builder.setSmallIcon(R.drawable.logo);

        Intent intent = new Intent(getApplicationContext(), UsersActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int id = (int) System.currentTimeMillis();
        manager.notify(id, builder.build());


    }
}
