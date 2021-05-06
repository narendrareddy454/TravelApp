package uk.ac.tees.aad.w9491709.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import uk.ac.tees.aad.w9491709.R;
import uk.ac.tees.aad.w9491709.Utills.AlarmService;
import uk.ac.tees.aad.w9491709.Utills.AlertBoradCastReciever;

public class AlarmStopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_stop);


        CardView cardStop = findViewById(R.id.cardStop);
        cardStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(getApplicationContext(), AlertBoradCastReciever.class);
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 1, intent1, 0);
                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);


                AlertBoradCastReciever.r.stop();
                alarmManager1.cancel(pendingIntent1);


                stopService(new Intent(getApplicationContext(), AlarmService.class));
                Intent alarmIntent = new Intent(getApplicationContext(), AlarmService.class);
                startService(alarmIntent);

                Intent intenta = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intenta);
                finish();
            }
        });
    }
}
