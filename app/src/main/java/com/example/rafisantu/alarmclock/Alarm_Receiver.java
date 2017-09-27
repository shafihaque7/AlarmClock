package com.example.rafisantu.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by rafisantu on 9/3/2017.
 */

public class Alarm_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("We are in the receiver","Yay!");

        //fetch extra strings from the intent
        String get_your_string = intent.getExtras().getString("extra"); // extra is the key, the next string "alarm on" is the value

        Log.e("What is the key? ", get_your_string);


        // create an intent to the ringtone service
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        //pass the extra string from main activity to the Ringtone playing service
        service_intent.putExtra("extra",get_your_string);

        //start the ringtone service
        context.startService(service_intent);
    }
}
