package com.reswift.prayertimes;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Irnanto on 14-Aug-16.
 */
public class SplashScreen extends Activity implements DelegateBehaviour {

    String TAG = "Testing";

    private Thread timerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };


        updateUserData();
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


    private void updateUserData(){

        if(!userData.getInstance(this).isNetworkAvailable()){
            Log.d(TAG, "no internet");
            alertNoInternet();
            return;
        }

        GPSTracker gps = new GPSTracker(this);

        if(!gps.isGPSEnabled){

            Log.d(TAG, "no gps");
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

    @Override
    public void onDelegateVoid() {
        Log.d(TAG, "Go to Main Menu");
        timerThread.start();
    }

    private void alertNoInternet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet!");
        builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                finish();
                System.exit(0);
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
        builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                finish();
                System.exit(0);
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