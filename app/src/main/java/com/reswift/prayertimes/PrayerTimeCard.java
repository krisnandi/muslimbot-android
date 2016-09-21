package com.reswift.prayertimes;

import android.util.Log;

import java.security.PublicKey;
import java.util.Date;

/**
 * Created by Irnanto on 20-Sep-16.
 */
public class PrayerTimeCard {

    String id;
    String solat;
    String time;
    Boolean notif;

    public enum Status{
        NONE,PAST, NOW, NEXT;
    }
    public Status status;

    public PrayerTimeCard(String solat, String time, Boolean notif)
    {
        id = solat;
        this.solat = solat;
        this.time = time;
        this.notif = notif;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public void setNotif(Boolean notif){
        this.notif = notif;
    }

    public void addInfo(String info){
        this.solat = this.solat + info;
    }
}
