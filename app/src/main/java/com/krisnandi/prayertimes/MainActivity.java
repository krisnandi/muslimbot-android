package com.krisnandi.prayertimes;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LocationListener, DelegateBehaviour {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        updateUserData();
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
            Log.d("testing", "new refresh");
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
        TextView textLocation =  (TextView) findViewById(R.id.textLocation);
        textLocation.setText(userData.getInstance(this).locationName);

        TextView textCalender =  (TextView) findViewById(R.id.textCalender);
        textCalender.setText(userData.getInstance(this).currentDate);

        TextView fajr_time =  (TextView) findViewById(R.id.fajr_time);
        fajr_time.setText(userData.getInstance(this).time_fajr);

        TextView dhuhr_time =  (TextView) findViewById(R.id.dhuhr_time);
        dhuhr_time.setText(userData.getInstance(this).time_dhuhr);

        TextView asr_time =  (TextView) findViewById(R.id.asr_time);
        asr_time.setText(userData.getInstance(this).time_asr);

        TextView maghrib_time =  (TextView) findViewById(R.id.maghrib_time);
        maghrib_time.setText(userData.getInstance(this).time_maghrib);

        TextView isha_time =  (TextView) findViewById(R.id.isha_time);
        isha_time.setText(userData.getInstance(this).time_ishaa);

    }


}
