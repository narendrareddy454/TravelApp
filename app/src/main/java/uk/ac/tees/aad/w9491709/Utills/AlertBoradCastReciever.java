package uk.ac.tees.aad.w9491709.Utills;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import uk.ac.tees.aad.w9491709.Activities.AlarmStopActivity;

public class AlertBoradCastReciever extends BroadcastReceiver {

    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    public static Ringtone r;

    @Override
    public void onReceive(Context context, Intent intent) {

        String requestCode = intent.getStringExtra("requestCode");
        Toast.makeText(context, "" + requestCode, Toast.LENGTH_SHORT).show();
        NotificationHelper notificationHelperAwake = new NotificationHelper(context, requestCode);
        NotificationCompat.Builder nb = notificationHelperAwake.getChannelNotification();
        notificationHelperAwake.getManager().notify(1, nb.build());
        Intent intent1 = new Intent(context, AlarmStopActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(context, notification);
//        r.play();

        r = RingtoneManager.getRingtone(context, notification);
        //playing sound alarm
        r.play();
    }
}
