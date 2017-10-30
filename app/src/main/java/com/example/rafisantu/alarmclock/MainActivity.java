package com.example.rafisantu.alarmclock;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //Latitude and Longitude stuff
    static boolean get_initial_location = true;
    double initial_Latitude;
    double initial_Longitude;

    double Latitude;
    double Longitude;

    TextView locationText;
    static LocationListener locationListener;
    static LocationManager locationManager;

    //Stuff from the alarm

    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    TextView update_text;
    Context context;
    PendingIntent pending_intent;

    Intent my_intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        this.context = this;
        locationStuff();


        //Initialize alarm manager
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Initialize time picker
        alarm_timepicker = (TimePicker)findViewById(R.id.timePicker);

        //Initialize our text update box.
        update_text = (TextView) findViewById(R.id.update_text);



        //Create an instance of the calendar
        final Calendar calendar = Calendar.getInstance();


        // Creating the intent
        my_intent = new Intent(this.context, Alarm_Receiver.class);





        // Initiliaze start button
        Button alarm_on = (Button) findViewById(R.id.alarm_on);




        // On click listener for the start button
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Setting calendar with hour and minute
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());

                // Getting the value of hour and minute
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();

                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(minute);

                // Convert 24 hour to 12 hour time
                if (hour>12){
                    hour_string = String.valueOf(hour-12);
                }
                if(minute<10){
                    //10:7 to 10:07
                    minute_string= "0"+String.valueOf(minute);
                }
                // method that changes the update text textbox
                set_alarm_text("Alarm set to:" + hour_string + ":"+ minute_string);

                //put in extra string into my_intent
                // tells the clock you pressed the "alarm on" button

                my_intent.putExtra("extra","alarm on");



                //create a pentding intent that delays the intent
                // untl the specified calendar time
                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT); // my_intent shows the file

                // set the alarm manager
                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pending_intent);

                // create the spinner in the main UI.
                //locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

            }
        });



        Button alarm_off = (Button) findViewById(R.id.alarm_off);

        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                set_alarm_text("Alarm off!");

                //cancel the alarm
                alarm_manager.cancel(pending_intent);

                //put extra string in to my_intent
                //tells the clock you pressed the "alarm off" button
                my_intent.putExtra("extra","alarm off");

                //stop the ringtone
                sendBroadcast(my_intent);
            }



        });





    }

    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void locationStuff(){
        locationText = (TextView) findViewById(R.id.textViewLocation);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationText.append("\n "+ location.getLatitude() +" "+location.getLongitude());
                if (get_initial_location){
                    initial_Latitude=location.getLatitude();
                    initial_Longitude=location.getLongitude();
                    get_initial_location=false;
                }
                else {
                    Latitude = location.getLatitude();
                    Longitude = location.getLongitude();
                    //converter();
                    if (Longitude> initial_Longitude + 0.00003 || Longitude < initial_Longitude - 0.00003|| Latitude>initial_Latitude+ 0.00003 || Latitude<initial_Latitude -  0.00003){
                        Toast.makeText(MainActivity.this,"The alarm should be off",Toast.LENGTH_LONG).show();
                        set_alarm_text("Alarm off!");

                        //cancel the alarm
                        alarm_manager.cancel(pending_intent);

                        //put extra string in to my_intent
                        //tells the clock you pressed the "alarm off" button
                        my_intent.putExtra("extra","alarm off");

                        //stop the ringtone
                        sendBroadcast(my_intent);
                        get_initial_location = true;



                    }
                }



            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            },10);
            return;
        }else{
            //configureButton();

        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //configureButton();

                return;

        }
    }
}
