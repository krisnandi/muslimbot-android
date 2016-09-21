package com.reswift.prayertimes;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements LocationListener, DelegateBehaviour {

    private final MyTimeChangeReceiver myTimeChangeReceiver = new MyTimeChangeReceiver();

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String TAG = "testing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences("alldata", Context.MODE_PRIVATE); //getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        for (PrayerTimeCard card: userData.getInstance(this).prayerTimeCards) {
            Log.d(TAG, "onCreate: " + card.solat + " - " + card.time);
        }

        updateUserData();
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
        //else if (id == R.id.action_settings) {}

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

        Toast.makeText(this, "Refresh Prayer Times", Toast.LENGTH_LONG).show();

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


        ArrayList<Object> items = new ArrayList<>();
        items.add(new PrayerTimeLocation(userData.getInstance(this).locationName));
        items.add(new PrayerTimeDate(userData.getInstance(this).todayDate));

        for (PrayerTimeCard card:userData.getInstance(this).prayerTimeCards) {
            items.add(card);
        }

        PrayerTimeAdapter prayerTimeAdapter = new PrayerTimeAdapter(items);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.prayerCardList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(prayerTimeAdapter);

        Toast.makeText(this, "Prayer Times Updated", Toast.LENGTH_LONG).show();

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


}
