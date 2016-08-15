package com.krisnandi.prayertimes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Irnanto on 14-Aug-16.
 */
public class SplashScreen extends Activity implements DelegateBehaviour {

    private Thread timerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        updateUserData();

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



        //timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
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

    @Override
    public void onDelegateVoid() {
        Log.d("testing", "Go to Main Menu");
        timerThread.start();
    }
}