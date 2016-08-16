package com.krisnandi.prayertimes;

import android.animation.TimeAnimator;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements LocationListener, DelegateBehaviour {

    private final MyTimeChangeReceiver myTimeChangeReceiver = new MyTimeChangeReceiver();

    SharedPreferences sharedPref;// = getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor;// = sharedPref.edit();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        CheckBox fajrcheck = (CheckBox) findViewById(R.id.fajr_check);
        fajrcheck.setChecked(sharedPref.getBoolean(getString(R.string.id_fajr), false));

        CheckBox sunrisecheck = (CheckBox) findViewById(R.id.sunrise_check);
        sunrisecheck.setChecked(sharedPref.getBoolean(getString(R.string.id_sunrise), false));

        CheckBox dhuhrcheck = (CheckBox) findViewById(R.id.dhuhr_check);
        dhuhrcheck.setChecked(sharedPref.getBoolean(getString(R.string.id_dhuhr), false));

        CheckBox asrcheck = (CheckBox) findViewById(R.id.asr_check);
        asrcheck.setChecked(sharedPref.getBoolean(getString(R.string.id_asr), false));

        CheckBox maghribcheck = (CheckBox) findViewById(R.id.maghrib_check);
        maghribcheck.setChecked(sharedPref.getBoolean(getString(R.string.id_maghrib), false));

        CheckBox ishaacheck = (CheckBox) findViewById(R.id.isha_check);
        ishaacheck.setChecked(sharedPref.getBoolean(getString(R.string.id_ishaa), false));

        //updateUserData();
        updateTheSchedule();

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("tesinfo","prayer time");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        //calendar.set(Calendar.HOUR_OF_DAY, 15);
        //calendar.set(Calendar.MINUTE, 17);
        //calendar.set(Calendar.SECOND, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);

        Intent notificationIntent1 = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent1.addCategory("android.intent.category.DEFAULT");
        notificationIntent1.putExtra("tesinfo","prayer time 1");
        PendingIntent broadcast1 = PendingIntent.getBroadcast(this, 100, notificationIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 15);
        calendar1.set(Calendar.MINUTE, 17);
        calendar1.set(Calendar.SECOND, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), broadcast1);

        Intent notificationIntent2 = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent2.addCategory("android.intent.category.DEFAULT");
        notificationIntent2.putExtra("tesinfo","prayer time 2");
        PendingIntent broadcast2 = PendingIntent.getBroadcast(this, 100, notificationIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 15);
        calendar2.set(Calendar.MINUTE, 18);
        calendar2.set(Calendar.SECOND, 0);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager2.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), broadcast2);


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



        Log.d("testing", item.toString());

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateUserData();
        }
        else if (id == R.id.rate_app) {
            launchMarket();
        }
        else if (id == R.id.action_settings) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myTimeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(myTimeChangeReceiver);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. It's a known bug and is exactly what is desired.
                //Log.w(TAG,"Tried to unregister the receiver when it's not registered");
            } else {
                // unexpected, re-throw
                throw e;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void updateUserData(){
        if(!userData.getInstance(this).isNetworkAvailable()){
            Log.d("testing", "no internet");
            alertNoInternet();
            return;
        }

        GPSTracker gps = new GPSTracker(this);

        if(!gps.isGPSEnabled){

            Log.d("testing", "no gps");
            alertNoGPS();
            return;
        }

        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        gps.stopUsingGPS();

        userData.getInstance(this).latitude = latitude;
        userData.getInstance(this).longitude = longitude;

        userData.getInstance(this).setBehaviour(this);
        userData.getInstance(this).updateData();
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDelegateVoid() {
        updateTheSchedule();
    }

    private void updateTheSchedule()
    {

        TextView textLocation =  (TextView) findViewById(R.id.textLocation);
        textLocation.setText(userData.getInstance(this).locationName);

        TextView textCalender =  (TextView) findViewById(R.id.textCalender);
        textCalender.setText(userData.getInstance(this).todayDate);

        //set current time for comparasion
        String currentTime = userData.getInstance(this).todayDate + " " + userData.getInstance(this).currentTime();

        //set fajr
        TextView fajr_time =  (TextView) findViewById(R.id.fajr_time);
        fajr_time.setText(userData.getInstance(this).time_fajr);
        String fajrTime = combinePrayerDateTime(userData.getInstance(this).todayDate, userData.getInstance(this).time_fajr);

        //set sunrise
        TextView sunrise_time =  (TextView) findViewById(R.id.sunrise_time);
        sunrise_time.setText(userData.getInstance(this).time_sunrise);
        String sunriseTime = combinePrayerDateTime(userData.getInstance(this).todayDate, userData.getInstance(this).time_sunrise);

        //set dhuhr
        TextView dhuhr_time =  (TextView) findViewById(R.id.dhuhr_time);
        dhuhr_time.setText(userData.getInstance(this).time_dhuhr);
        String dhuhrTime = combinePrayerDateTime(userData.getInstance(this).todayDate, userData.getInstance(this).time_dhuhr);

        //set asr
        TextView asr_time =  (TextView) findViewById(R.id.asr_time);
        asr_time.setText(userData.getInstance(this).time_asr);
        String asrTime = combinePrayerDateTime(userData.getInstance(this).todayDate, userData.getInstance(this).time_asr);

        //set maghrib
        TextView maghrib_time =  (TextView) findViewById(R.id.maghrib_time);
        maghrib_time.setText(userData.getInstance(this).time_maghrib);
        String maghribTime = combinePrayerDateTime(userData.getInstance(this).todayDate, userData.getInstance(this).time_maghrib);

        //set ishaa
        TextView isha_time =  (TextView) findViewById(R.id.isha_time);
        isha_time.setText(userData.getInstance(this).time_ishaa);
        String ishaaTime = combinePrayerDateTime(userData.getInstance(this).todayDate, userData.getInstance(this).time_ishaa);

        boolean isBeforeFajr = userData.getInstance(this).isDateTimeBefore(currentTime,fajrTime);
        boolean isAfterFajr = userData.getInstance(this).isDateTimeAfter(currentTime,fajrTime);
        boolean isAfterSunrise = userData.getInstance(this).isDateTimeAfter(currentTime,sunriseTime);
        boolean isAfterDhuhr = userData.getInstance(this).isDateTimeAfter(currentTime,dhuhrTime);
        boolean isAfterAsr = userData.getInstance(this).isDateTimeAfter(currentTime,asrTime);
        boolean isAfterMaghrib = userData.getInstance(this).isDateTimeAfter(currentTime,maghribTime);
        boolean isAfterishaa = userData.getInstance(this).isDateTimeAfter(currentTime,ishaaTime);

        TextView mNowFajr =  (TextView) findViewById(R.id.fajr_now);
        TextView mNowSunrise =  (TextView) findViewById(R.id.sunrise_now);
        TextView mNowDhuhr =  (TextView) findViewById(R.id.dhuhr_now);
        TextView mNowAsr =  (TextView) findViewById(R.id.asr_now);
        TextView mNowMaghrib =  (TextView) findViewById(R.id.maghrib_now);
        TextView mNowIshaa =  (TextView) findViewById(R.id.isha_now);

        mNowFajr.setVisibility(View.GONE);
        mNowSunrise.setVisibility(View.GONE);
        mNowDhuhr.setVisibility(View.GONE);
        mNowAsr.setVisibility(View.GONE);
        mNowMaghrib.setVisibility(View.GONE);
        mNowIshaa.setVisibility(View.GONE);

        TextView mNextFajr =  (TextView) findViewById(R.id.fajr_next);
        TextView mNextSunrise =  (TextView) findViewById(R.id.sunrise_next);
        TextView mNextDhuhr =  (TextView) findViewById(R.id.dhuhr_next);
        TextView mNextAsr =  (TextView) findViewById(R.id.asr_next);
        TextView mNextMaghrib =  (TextView) findViewById(R.id.maghrib_next);
        TextView mNextIshaa =  (TextView) findViewById(R.id.isha_next);


        mNextFajr.setVisibility(View.GONE);
        mNextSunrise.setVisibility(View.GONE);
        mNextDhuhr.setVisibility(View.GONE);
        mNextAsr.setVisibility(View.GONE);
        mNextMaghrib.setVisibility(View.GONE);
        mNextIshaa.setVisibility(View.GONE);


        TextView fajrInfo =  (TextView) findViewById(R.id.fajr_info);
        fajrInfo.setText(R.string.id_fajr);

        TextView sunriseInfo =  (TextView) findViewById(R.id.sunrise_info);
        sunriseInfo.setText(R.string.id_sunrise);

        TextView dhuhrInfo =  (TextView) findViewById(R.id.dhuhr_info);
        dhuhrInfo.setText(R.string.id_dhuhr);

        TextView asrInfo =  (TextView) findViewById(R.id.asr_info);
        asrInfo.setText(R.string.id_asr);

        TextView maghribInfo =  (TextView) findViewById(R.id.maghrib_info);
        maghribInfo.setText(R.string.id_maghrib);

        TextView ishaaInfo =  (TextView) findViewById(R.id.isha_info);
        ishaaInfo.setText(R.string.id_ishaa);



        if(isAfterFajr){
            if(!isAfterSunrise){
                mNowFajr.setVisibility(View.VISIBLE);
                mNextSunrise.setVisibility(View.VISIBLE);

                String text = sunriseInfo.getText() + userData.getInstance(this).timeBetween(currentTime, sunriseTime);
                sunriseInfo.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            }
        }

        if(isAfterSunrise){
            if(!isAfterDhuhr){
                mNowSunrise.setVisibility(View.VISIBLE);
                mNextDhuhr.setVisibility(View.VISIBLE);

                String text = dhuhrInfo.getText() + userData.getInstance(this).timeBetween(currentTime, dhuhrTime);
                dhuhrInfo.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            }
        }

        if(isAfterDhuhr){
            if(!isAfterAsr){
                mNowDhuhr.setVisibility(View.VISIBLE);
                mNextAsr.setVisibility(View.VISIBLE);

                String text = asrInfo.getText() + userData.getInstance(this).timeBetween(currentTime, asrTime);
                asrInfo.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            }
        }

        if(isAfterAsr){
            if(!isAfterMaghrib){
                mNowAsr.setVisibility(View.VISIBLE);
                mNextMaghrib.setVisibility(View.VISIBLE);

                String text = maghribInfo.getText() + userData.getInstance(this).timeBetween(currentTime, maghribTime);
                maghribInfo.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            }
        }

        if(isAfterMaghrib){
            if(!isAfterishaa){
                mNowMaghrib.setVisibility(View.VISIBLE);
                mNextIshaa.setVisibility(View.VISIBLE);

                String text = ishaaInfo.getText() + userData.getInstance(this).timeBetween(currentTime, ishaaTime);
                ishaaInfo.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            }
        }


        if(isAfterishaa){
            mNowIshaa.setVisibility(View.VISIBLE);
            mNextFajr.setVisibility(View.VISIBLE);

            String nextfajrTime = combinePrayerDateTime(userData.getInstance(this).tomorrowDate, userData.getInstance(this).time_fajr);

            String text = fajrInfo.getText() + userData.getInstance(this).timeBetween(currentTime, nextfajrTime);
            fajrInfo.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        }

        if(isBeforeFajr){
            mNowIshaa.setVisibility(View.VISIBLE);
            mNextFajr.setVisibility(View.VISIBLE);

            String text = fajrInfo.getText() + userData.getInstance(this).timeBetween(currentTime, fajrTime);
            fajrInfo.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        }

    }

    private String combinePrayerDateTime(String str_date, String str_time)
    {
        return str_date + " " + str_time + ":00";
    }

    private class MyTimeChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {

            // compare current time and decide if it's 12 AM
            Log.d("MyDateChangeReceiver", "Time changed");
            updateTheSchedule();
        }
    }

    private void alertNoInternet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet!");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                updateUserData();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void alertNoGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activate your GPS!");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                updateUserData();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.fajr_check:
                editor.putBoolean(getString(R.string.id_fajr), checked);
                break;
            case R.id.sunrise_check:
                editor.putBoolean(getString(R.string.id_sunrise), checked);
                break;
            case R.id.dhuhr_check:
                editor.putBoolean(getString(R.string.id_dhuhr), checked);
                break;
            case R.id.asr_check:
                editor.putBoolean(getString(R.string.id_asr), checked);
                break;
            case R.id.maghrib_check:
                editor.putBoolean(getString(R.string.id_maghrib), checked);
                break;
            case R.id.isha_check:
                editor.putBoolean(getString(R.string.id_ishaa), checked);
                break;
            // TODO: Veggie sandwich
        }

        editor.commit();
    }

}
