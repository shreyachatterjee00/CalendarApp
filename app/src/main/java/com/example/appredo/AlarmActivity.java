package com.example.appredo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;

//TO DO: ADD AN ICON TO THE DESIGN
//TO DO: RUN ON AN ACTUAL DEVICE TO FIGURE OUT WHY THE DESIGN AND WAKE LOCK ARE NOT WORKING PROPERLY
/*
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AlarmActivity extends AppCompatActivity {

   int snoozeTime;
   int nagTime;

   //flags for snooze and dismiss functions
    boolean snoozeFlag, dismissFlag = false;

    //sound effect
    MediaPlayer sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*turn screen on, keep screen on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm);


        TextView titleText = (TextView) findViewById(R.id.eventTitle);
        TextView timeText = (TextView) findViewById(R.id.eventTime);

        Intent intent = getIntent();
        final String event = intent.getStringExtra("EventName");
        final String time = intent.getStringExtra("EventTime");
        snoozeTime = intent.getIntExtra("SnoozeTime", -1);
        nagTime = intent.getIntExtra("NagTime", -1);


        //set variables, eg event title and event time
        titleText.setText(event);
        timeText.setText(time);

        //cool cinematic sound

        /*Music from https://filmmusic.io
        "The Descent" by Kevin MacLeod (https://incompetech.com)
        License: CC BY (http://creativecommons.org/licenses/by/4.0/)*/

        sound = MediaPlayer.create(this, R.raw.the_descent_by_kevin_macleod);
        sound.start();


        //set listeners to buttons
        final Button snooze = (Button) findViewById(R.id.snooze);
        Button dismiss = (Button) findViewById(R.id.dismiss);


        //TO DO: CHANGE TO ACTUAL SNOOZE CAPABILITIES
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeFlag = true;
                finish();
                sound.stop();

                Intent intent = new Intent(AlarmActivity.this, AlarmActivity.class);
                intent.putExtra("EventName", event);
                intent.putExtra("EventTime", time);
                intent.putExtra("SnoozeTime", snoozeTime);
                intent.putExtra("NagTime", nagTime);

                PendingIntent pendingIntent = PendingIntent.getActivity(AlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, new Date().getTime() + snoozeTime * 60 * 1000, pendingIntent);

            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissFlag = true;
                finish();
                sound.stop();

            }
        });

        nagAsync();

    }

    public void nagAsync () {

        if (VERSION.SDK_INT > 24) {

            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();
                    long elapsedTime = 0;

                    while (elapsedTime < (nagTime * 60 * 1000)) {                  //change to 7000, for testing purposes
                        elapsedTime = (new Date().getTime() - start);
                    }
                }
            }).thenRun(new Runnable() {
                @Override
                public void run() {
                    if (!snoozeFlag && !dismissFlag) {
                        //play sound again
                        sound.stop();
                        System.out.println("got here");

                        MediaPlayer soundNag = MediaPlayer.create(AlarmActivity.this, R.raw.the_descent_by_kevin_macleod);
                        soundNag.start();
                    }
                }
            });
        }
    }

}

