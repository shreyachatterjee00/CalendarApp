package com.example.appredo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.os.PowerManager;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.EventLog;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    public static final String[] EVENT_ARRAY = new String[] {
            CalendarContract.Events._ID,                              //0
            CalendarContract.Events.TITLE,                            //1
            CalendarContract.Events.DTSTART,                          //2
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 0;

    //variables that the user has picked
    int nagTime;
    int snoozeTime;
    int firstAlarm;

    //variables from the edit text inputs
    EditText alarmInput;
    EditText snoozeInput;
    EditText nagInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmInput = (EditText)findViewById(R.id.FirstAlarmInput);
        snoozeInput = (EditText)findViewById(R.id.SnoozeTimeInput);
        nagInput = (EditText)findViewById(R.id.NagTimeInput);
    }


    /* when user clicks submit button */
    public void onSubmit (View view) {

        AlertDialog.Builder errorAlert = new AlertDialog.Builder(this);
        errorAlert.setTitle("ERROR");
        errorAlert.setMessage("Please enter a valid number for each input box, and then press submit again.");


        String alarmInputString = alarmInput.getText().toString();
        try {
            firstAlarm = Integer.parseInt(alarmInputString);
        } catch (NumberFormatException exception) {
            errorAlert.show();
            firstAlarm = -1;
        }

        String snoozeInputString = snoozeInput.getText().toString();
        try {
            snoozeTime = Integer.parseInt(snoozeInputString);
        } catch (NumberFormatException exception) {
            errorAlert.show();
            snoozeTime = -1;
        }

        String nagTimeString = nagInput.getText().toString();
        try {
            nagTime = Integer.parseInt(nagTimeString);
        } catch (NumberFormatException exception) {
            errorAlert.show();
            nagTime = -1;
        }

        //set user preferences to values
        //getCalendars
        //getCalendar();
        if (firstAlarm > 0 && snoozeTime > 0 && nagTime > 0) {
            setNotif("event", "8:30 PM");
        }

    }

    /*gets Calendars on phone, only needs to be done once */
    public void getCalendar () {

        //grant permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);

        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {


            // Run query
            Cursor cur = null;
            ContentResolver cr = getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;

            // Submit the query and get a Cursor object back.
            cur = cr.query(uri, EVENT_PROJECTION, null, null, null);


            // Use the cursor to step through the returned records
            while (cur.moveToNext()) {
                long calID = 0;
                String displayName = null;
                String accountName = null;
                String ownerName = null;

                // Get the field values
                calID = cur.getLong(PROJECTION_ID_INDEX);
                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

                //ask user which calendars should be included: right now i'm just going to get events from all calendars
                //get events from those calendars

                if (displayName.equals("shreyachatterjee@gmail.com")) {
                    getEvents(calID, cr);
                }

            }
        }
    }

    /*gets user events from calendar */
    public void getEvents(Long id, ContentResolver contentResolver) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            Uri uri = CalendarContract.Events.CONTENT_URI;

            Cursor cur = null;
            cur = contentResolver.query(uri, EVENT_ARRAY, "calendar_id=" + id, null, null);

            while (cur.moveToNext()) {
                final String EventId = cur.getString(0);
                final String title = cur.getString(1);
                final Date startTime = new Date(cur.getLong(2));


                Long current = new Date().getTime();

                if (startTime.getTime() >= current) {
                    System.out.println("EventId: " + EventId + "\n");
                    System.out.println("Title" + title + "\n");
                    System.out.println("Date" + startTime);


                    //convert date to just hours and minutes, and then toString
                    DateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
                    //sdf.setTimeZone(TimeZone.getTimeZone("PDT"));
                    String formattedDate = sdf.format(startTime);


                    //convert time to correct time alarm should be set
                    Long alarmTime = startTime.getTime() - (firstAlarm * 60 * 1000);

                    //set notif for event
                    //setNotif(alarmTime, title, formattedDate);
                }

            }

        }
    }

    /*gets events ever 30 mins, if events have changed, change the alarm, if not, keep the alarm*/
    public void updateEvents () {

        //TO DO: get events every half an hour
        //check if title or start time has changed
        //if yes, find alarm in the set and delete and change it
        //if not, do nothing




    }


    /* set an intent/pending intent/alarm manager to open the alarm activity screen */
    /*
    public void setNotif(Long time, String calEventParam, String eventTimeParam) {
        String calEvent = calEventParam;
        String eventTime = eventTimeParam;

        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtra("EventName", calEvent);
        intent.putExtra("EventTime", eventTime);
        intent.putExtra("SnoozeTime", snoozeTime);
        intent.putExtra("NagTime", nagTime);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

    }*/

    /* TEST FUNCTION, SETS AN INTENT FOR FIVE SECONDS FROM NOW (missing a Long parameter, because it's not needed for this func)*/
    public void setNotif(String calEventParam, String eventTimeParam) {

        System.out.println("First Alarm: " + firstAlarm + "Snooze:" + snoozeTime + "Nag: " + nagTime);

        String calEvent = calEventParam;
        String eventTime = eventTimeParam;

        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtra("EventName", calEvent);
        intent.putExtra("EventTime", eventTime);
        intent.putExtra("SnoozeTime", snoozeTime);
        intent.putExtra("NagTime", nagTime);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, new Date().getTime() + 10000, pendingIntent);

    }

}


