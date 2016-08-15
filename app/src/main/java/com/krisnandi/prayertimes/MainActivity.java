package com.krisnandi.prayertimes;

import android.animation.TimeAnimator;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LocationListener, DelegateBehaviour {

    private final MyTimeChangeReceiver myTimeChangeReceiver = new MyTimeChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //updateUserData();
        updateTheSchedule();
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
        GPSTracker gps = new GPSTracker(this);

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

        /*
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
        */
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

        }
    }

}
