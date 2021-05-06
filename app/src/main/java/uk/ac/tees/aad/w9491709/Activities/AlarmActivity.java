package uk.ac.tees.aad.w9491709.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import uk.ac.tees.aad.w9491709.R;
import uk.ac.tees.aad.w9491709.Utills.AlarmService;
import uk.ac.tees.aad.w9491709.Utills.AlertBoradCastReciever;
import uk.ac.tees.aad.w9491709.Utills.SQLiteHelpers;

public class AlarmActivity extends AppCompatActivity {


    Toolbar toolbar;
    Button btnDatePicker;
    Button btnTimePicker;
    Button btnSetAlarm;


    int mYear = -1;
    int mMonth = -1;
    int mDay = -1;
    int mHours = -1;
    int mSecond = -1;
    int mMinutes = -1;
    TextView alarmName, alarmTime;
    SQLiteHelpers helpers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Set Alarm");
        helpers=new SQLiteHelpers(this);



        alarmName = findViewById(R.id.alarmName);
        alarmTime = findViewById(R.id.alarmTime);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnTimePicker = findViewById(R.id.btnTimePicker);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);

        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });

        btnDatePicker.setOnClickListener(view -> showDateTimePicker());

        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });

        loadSharePrefernceSetTime();


    }
    public void setAlarm() {
        if (mYear == -1 || mMonth == -1 || mDay == -1 || mHours == -1 || mMinutes == -1) {
            Toast.makeText(this, "Please select Both date and time ", Toast.LENGTH_SHORT).show();
        } else {
            helpers.insertData(mYear,mMonth,mDay,mHours,mMinutes);
        }
        stopService(new Intent(this, AlarmService.class));
        Intent alarmIntent = new Intent(this, AlarmService.class);
        startService(alarmIntent);
        loadSharePrefernceSetTime();

    }

    public void showDateTimePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(AlarmActivity.this, (view, year, month, dayOfMonth) -> {
            String _year = String.valueOf(year);
            String _month = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String _date = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
            String _pickedDate = year + "-" + _month + "-" + _date;
            mMonth = month + 1;
            mYear = year;
            mDay = dayOfMonth;
            Log.e("PickedDate: ", "Date: " + _pickedDate); //2019-02-12
//            date = _pickedDate;
            btnDatePicker.setText(_pickedDate + "");

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void loadSharePrefernceSetTime() {

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
                if (mHoursawake==-1)
                {
                    alarmTime.setText("00 : 00");
                }else
                {
                    alarmTime.setText(mHoursawake+" : "+mMinutesawake);
                }

            } while (cursor.moveToNext());
        }





    }

    private void pickTime() {

        Calendar mcurrentDate = Calendar.getInstance();
        int hours = mcurrentDate.get(Calendar.HOUR);
        int minutes = mcurrentDate.get(Calendar.MINUTE);
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                btnTimePicker.setText(selectedHour + ":" + selectedMinute);
                mHours = selectedHour;
                mMinutes = selectedMinute;
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


    }
}