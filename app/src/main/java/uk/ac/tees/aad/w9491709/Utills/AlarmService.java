package uk.ac.tees.aad.w9491709.Utills;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;



import java.util.Calendar;

public class AlarmService extends Service {

    private String TAG = "AlarmService";
    SQLiteHelpers helpers;

    @Override
    public void onCreate() {
        sendBroadcast();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        //return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        sendBroadcast();
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        sendBroadcast();
        super.onTaskRemoved(rootIntent);
    }

    private void sendBroadcast() {

        helpers=new SQLiteHelpers(this);

        String selectQuery = "SELECT  * FROM Alarm";

        SQLiteDatabase db = helpers.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int mYearawake = cursor.getInt(0);
                int mMonthawake = cursor.getInt(1);
                int mDayawake = cursor.getInt(2);
                int mHoursawake = cursor.getInt(3);
                int mMinutesawake = cursor.getInt(4);

                if (mYearawake != -1 && mMonthawake != -1 && mDayawake != -1 && mHoursawake != -1 && mMinutesawake != -1) {

                    Calendar cal3 = Calendar.getInstance();
                    cal3.setTimeInMillis(System.currentTimeMillis());
                    cal3.clear();
                    cal3.set(mYearawake, mMonthawake - 1, mDayawake, mHoursawake, mMinutesawake);

                    // cal3.set(2021,0,22,19,9);

                    if (cal3.before(Calendar.getInstance())) {
                        cal3.add(Calendar.DATE, 1);
                    }

                    AlarmManager alarmMgr3 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent3 = new Intent(this, AlertBoradCastReciever.class);
                    intent3.putExtra("requestCode","Hey!,Its time Awake");
                    PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, 1, intent3, 0);
                    // cal.add(Calendar.SECOND, 5);
                    alarmMgr3.set(AlarmManager.RTC_WAKEUP, cal3.getTimeInMillis(), pendingIntent3);
                }

            } while (cursor.moveToNext());
        }








    }
}
