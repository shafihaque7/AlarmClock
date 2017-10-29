package com.example.rafisantu.alarmclock;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;

import static com.example.rafisantu.alarmclock.MainActivity.locationListener;

/**
 * Created by rafisantu on 9/3/2017.
 */

public class RingtonePlayingService extends Service {

    MediaPlayer media_song;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i("LocalService", "Received start id " + startId + ": "+ intent);

        //fetch the extra string values
        String state = intent.getExtras().getString("extra");

        Log.e("Ringtone extra is", state);


        // This converts the extra strings from the intent to start IDs, values 0 or 1
        assert state !=null; // to make sure its not null
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                Log.e("Start ID is ", state);
                break;
            default:
                startId = 0;
                break;
        }

        //if there is no music playing and the user pressed "alarm on"
        // music should start plating
        if (!this.isRunning && startId==1){
            // create an instance of the media player
            Log.e("there is no music ","and you want start");
            media_song = MediaPlayer.create(this, R.raw.dove);
            // start the ringtone
            media_song.start();
            MainActivity.locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

            this.isRunning = true;
            //this.startId =0;// might change this

            //put the notification here
            //notification
            //set up the notification manager

            NotificationManager notify_Manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            // set up an intent that goes to the main activity
            Intent intent_main_activity = new Intent(this.getApplicationContext(),MainActivity.class);
            // set up a pending intent
            PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this,0,intent_main_activity,0);

            // make the notification parameters.
            Notification notification_popup = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.uses)
                    .setContentTitle("An alarm is going off!")
                    .setContentText("Click me!")
                    .setContentIntent(pending_intent_main_activity)
                    .setAutoCancel(true)
                    .build();
            notify_Manager.notify(0, notification_popup);

        }
        //if there is music playing, and the user pressed "alarm off"
        // music should stop playing
        else if(this.isRunning && startId==0){
            Log.e("there is no music ","and you want end");

            // stop the ringtone
            media_song.stop();
            media_song.reset();

            this.isRunning=false;
            //this.startId = 0;
        }

        //These are if the start pressing random buttons
        // just to bug proof the app
        // if there is no music playing and the user pressed "alarm off"
        // do nothing
        else if(!this.isRunning && startId==0){
            Log.e("there is music ","and you want end");
            this.isRunning =false;
            //this.startId = 0;

        }
        //if there is music playing and the user pressed "alarm on"
        // do nothing
        else if(this.isRunning && startId== 1){
            Log.e("there is no music ","and you want end");
            this.isRunning = true;
            //this.startId =1;
        }


        // cant think of anything else
        else{
            Log.e("else ","somehow you reached this");

        }

        return START_NOT_STICKY; // if its on do turn it on again
    }

    @Override
    public void onDestroy(){
        // Tell the user we stopped

        Log.e("on Destroy called", "ta da");
        super.onDestroy();
        this.isRunning = false;
    }
}
