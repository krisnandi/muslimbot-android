package com.reswift.prayertimes;

/**
 * Created by Irnanto on 16-Aug-16.
 */
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;


public class MyAlarmService extends Service
{
    private static BroadcastReceiver m_ScreenOffReceiver;

    @Override
    public IBinder onBind(Intent arg0)
    {
        Log.d("testing", "ACTION_SCREEN_OFF 1");
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d("testing", "ACTION_SCREEN_OFF 2");
        registerScreenOffReceiver();
    }

    @Override
    public void onDestroy()
    {
        Log.d("testing", "ACTION_SCREEN_OFF 3");
        unregisterReceiver(m_ScreenOffReceiver);
        m_ScreenOffReceiver = null;
    }

    private void registerScreenOffReceiver()
    {
        m_ScreenOffReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.d("testing", "ACTION_SCREEN_OFF 5");
                // do something, e.g. send Intent to main app
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(m_ScreenOffReceiver, filter);
    }
}
