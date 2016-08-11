package com.krisnandi.prayertimes;

import android.util.Log;

/**
 * Created by Irnanto on 11-Aug-16.
 */
public class userData {
    private static userData ourInstance = new userData();
    public static userData getInstance() {
        return ourInstance;
    }

    private userData() {

    }

    public void getData(){
        Log.d("testing", "wew");
    }


}
