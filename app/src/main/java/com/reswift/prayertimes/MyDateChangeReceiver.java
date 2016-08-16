package com.reswift.prayertimes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Irnanto on 15-Aug-16.
 */
public class MyDateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        // compare current time and decide if it's 12 AM
        Log.d("MyDateChangeReceiver", "Time changed");

    }
}