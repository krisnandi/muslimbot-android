package com.reswift.prayertimes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.reswift.model.PrayerTimesDay;
import com.reswift.model.Timings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Irnanto on 11-Aug-16.
 */
public class userData {
    private static userData ourInstance;// = new userData();

    String TAG = "testing";

    public static String ID_fajr = "id-fajr";
    public static String ID_sunrise = "id-sunrise";
    public static String ID_dhuhr = "id-dhuhr";
    public static String ID_asr = "id-asr";
    public static String ID_maghrib = "id-maghrib";
    public static String ID_isha = "id-isha";

    public DelegateBehaviour delegateBehaviour;

    public double latitude;
    public double longitude;

    public String locationName;
    public String todayDate;
    public String tomorrowDate;

    private final Context mContext;

    public PrayerTimesDay prayerTimesDay;

    public List<PrayerTimeCard> prayerTimeCards;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public static userData getInstance() {
        return ourInstance;
    }

    public static userData getInstance(Context context) {
        if(ourInstance == null)
            ourInstance = new userData(context);
        return ourInstance;
    }

    private userData(Context context) {
        this.mContext = context;
        sharedPref = mContext.getSharedPreferences("alldata", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }



    public void updateData() {
        //new DownloadFilesTask().execute();

        String BASE_URL = "http://api.aladhan.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PrayerTimesDayService service = retrofit.create(PrayerTimesDayService.class);

        Call<PrayerTimesDay> call = service.getContent(getTodayTimeStamp(), String.valueOf(latitude), String.valueOf(longitude), getCurrentTimeZone(), "5");
        Log.d(TAG, "updateData: " + getTodayTimeStamp() +" -" + String.valueOf(latitude)  +" -" + String.valueOf(longitude) +" -" + getCurrentTimeZone());

        locationName = getLocationName(latitude, longitude);
        todayDate = getTodayDate();
        tomorrowDate = getTomorrowDate();

        call.enqueue(new Callback<PrayerTimesDay>() {
            @Override
            public void onResponse(Call<PrayerTimesDay> call, Response<PrayerTimesDay> response) {
                try {
//                    Log.d(TAG, "onResponse: " + response.raw().toString());

                    prayerTimesDay = response.body();

                    Timings timing = prayerTimesDay.getData().getTimings();
                    prayerTimeCards = new ArrayList<>();
                    prayerTimeCards.add((new PrayerTimeCard(mContext.getString(R.string.id_fajr), timing.getFajr(), sharedPref.getBoolean(mContext.getString(R.string.id_fajr), true))));
                    prayerTimeCards.add((new PrayerTimeCard(mContext.getString(R.string.id_sunrise), timing.getSunrise(), sharedPref.getBoolean(mContext.getString(R.string.id_sunrise), true))));
                    prayerTimeCards.add((new PrayerTimeCard(mContext.getString(R.string.id_dhuhr), timing.getDhuhr(), sharedPref.getBoolean(mContext.getString(R.string.id_dhuhr), true))));
                    prayerTimeCards.add((new PrayerTimeCard(mContext.getString(R.string.id_asr), timing.getAsr(), sharedPref.getBoolean(mContext.getString(R.string.id_asr), true))));
                    prayerTimeCards.add((new PrayerTimeCard(mContext.getString(R.string.id_maghrib), timing.getMaghrib(), sharedPref.getBoolean(mContext.getString(R.string.id_maghrib), true))));
                    prayerTimeCards.add((new PrayerTimeCard(mContext.getString(R.string.id_ishaa), timing.getIsha(), sharedPref.getBoolean(mContext.getString(R.string.id_ishaa), true))));

                    String currentTime = todayDate + " " + currentTime();
                    Calendar currentCalendar = StringToCalendar(currentTime);

                    for(int i=0; i<prayerTimeCards.size(); i++){
                        int pPrevious = i;
                        String previous = combinePrayerDateTime(todayDate, prayerTimeCards.get(pPrevious).time);
                        Calendar previousCalendar = StringToCalendar(previous);

                        int pNext = i+1;
                        String next = "";
                        if(pNext >= prayerTimeCards.size()) {
                            pNext = 0;
                            next = combinePrayerDateTime(tomorrowDate, prayerTimeCards.get(pNext).time);
                        }
                        else
                            next = combinePrayerDateTime(todayDate, prayerTimeCards.get(pNext).time);

                        Calendar nextCalendar = StringToCalendar(next);

                        boolean isBefore = previousCalendar.before(currentCalendar);
                        boolean isAfter = nextCalendar.after(currentCalendar);

                        if(isBefore && isAfter){
                            prayerTimeCards.get(pPrevious).setStatus(PrayerTimeCard.Status.NOW);
                            prayerTimeCards.get(pNext).setStatus(PrayerTimeCard.Status.NEXT);

                            if(prayerTimeCards.get(pPrevious).solat == mContext.getString(R.string.id_sunrise)){
                                prayerTimeCards.get(pPrevious).setStatus(PrayerTimeCard.Status.PAST);
                            }

                            prayerTimeCards.get(pNext).addInfo(timeBetween(currentCalendar.getTime(), nextCalendar.getTime()));
                        }
                    }



                } catch (Exception e) {
                    Log.d(TAG, "There is an error");
                    e.printStackTrace();
                }
                finally {
                    SchedulingALLNotification();
                    delegateBehaviour.onDelegateVoid();
                }
            }

            @Override
            public void onFailure(Call<PrayerTimesDay> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
            }


        });

    }

    public void setBehaviour(DelegateBehaviour behaviour){
        delegateBehaviour = behaviour;
    }

    private String getTodayDate(){
        try {
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
            Date today = new Date();

            String currentTimeStamp = dateFormat.format(today); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private String getTomorrowDate(){
        try {
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");

            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DATE, 1);
            Date tomorrow = calendar.getTime();

            String currentTimeStamp = dateFormat.format(tomorrow); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private String getTodayTimeStamp(){
        try {

            Date today = new Date();
            Long tslong = today.getTime()/1000;

            String todayTimeStamp = tslong.toString();

            return todayTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private String getTomorrowTimeStamp(){
        try {

            Date today = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DATE, 1);
            Date tomorrow = calendar.getTime();

            Long tslong = tomorrow.getTime()/1000;

            String tomorrowTimestamp = tslong.toString();

            return tomorrowTimestamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    private String getLocationName(double latitude, double longitude)
    {
        String location = "";
        try {
            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                location = addresses.get(0).getLocality();
            }
        } catch (IOException e) {}
        catch (NullPointerException e) {}

        return location;
    }

    public String currentTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
       return String.valueOf(sdf.format(cal.getTime()));
    }

    private String getCurrentLocalTIme(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    private String getCurrentTimeZone(){
        return TimeZone.getDefault().getID();
    }

    public Calendar StringToCalendar(String str_date)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        try {
            calendar.setTime(sdf.parse(str_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }

    public String timeBetween(Date date1, Date date2){
        long diff = date2.getTime() - date1.getTime();

        String str_time = " <font color='red'> in ";

        int diffMinutes = (int) TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
        diffMinutes++;

        int hour = diffMinutes / 60;
        int minute = diffMinutes % 60;


        if(hour > 0) {
            str_time = str_time + String.valueOf(hour);
            if(hour > 1)
                str_time = str_time + " Hours ";
            else
                str_time = str_time + " Hour ";
        }

        if(minute > 0) {
            str_time = str_time + String.valueOf(minute);
            if(minute > 1)
                str_time = str_time + " Minutes";
            else
                str_time = str_time + " minute";
        }

        str_time = str_time + "</font>";

        return str_time;
    }

    //this function need some optimazion
    public void  SchedulingALLNotification(){
        CancelAllNotification();


        Activity tempActivity = (Activity) mContext;
        SharedPreferences sharedPref = tempActivity.getSharedPreferences("alldata", Context.MODE_PRIVATE);;

        Boolean fajrNotif = sharedPref.getBoolean(mContext.getString(R.string.id_fajr), true);
        Boolean sunriseNotif = sharedPref.getBoolean(mContext.getString(R.string.id_sunrise), true);
        Boolean dhuhrNotif = sharedPref.getBoolean(mContext.getString(R.string.id_dhuhr), true);
        Boolean asrNotif = sharedPref.getBoolean(mContext.getString(R.string.id_asr), true);
        Boolean maghribNotif = sharedPref.getBoolean(mContext.getString(R.string.id_maghrib), true);
        Boolean ishaNotif = sharedPref.getBoolean(mContext.getString(R.string.id_ishaa), true);

        String currentTime = todayDate + " " + currentTime();
        Calendar currentCalendar = StringToCalendar(currentTime);

        Timings timing = prayerTimesDay.getData().getTimings();

        if(fajrNotif) {
            String fajrTime = combinePrayerDateTime(todayDate, timing.getFajr());
            Calendar fajrCalendar = StringToCalendar(fajrTime);
            if (currentCalendar.after(fajrCalendar)) {
                fajrTime = combinePrayerDateTime(tomorrowDate, timing.getFajr());
                fajrCalendar = StringToCalendar(fajrTime);
            }
            String fajrTitle = "Fajr Prayer";
            String fajrInfo = "It's time for Fajr prayer";
            addNotification(fajrCalendar, ID_fajr, fajrTitle, fajrInfo, fajrInfo);
        }

        if(sunriseNotif) {
            String sunriseTime = combinePrayerDateTime(todayDate, timing.getSunrise());
            Calendar sunriseCalendar = StringToCalendar(sunriseTime);
            if (currentCalendar.after(sunriseCalendar)) {
                sunriseTime = combinePrayerDateTime(tomorrowDate, timing.getSunrise());
                sunriseCalendar = StringToCalendar(sunriseTime);
            }
            String sunriseTitle = "Sunrise";
            String sunriseInfo = "It's past Sunrise time";
            addNotification(sunriseCalendar, ID_sunrise, sunriseTitle, sunriseInfo, sunriseInfo);
        }

        if(dhuhrNotif) {
            String dhuhrTime = combinePrayerDateTime(todayDate, timing.getDhuhr());
            Calendar dhuhrCalendar = StringToCalendar(dhuhrTime);
            if (currentCalendar.after(dhuhrCalendar)) {
                dhuhrTime = combinePrayerDateTime(tomorrowDate, timing.getDhuhr());
                dhuhrCalendar = StringToCalendar(dhuhrTime);
            }
            String dhuhrTitle = "Dhuhr Prayer";
            String dhuhrInfo = "It's time for Dhuhr prayer";
            addNotification(dhuhrCalendar, ID_dhuhr, dhuhrTitle, dhuhrInfo, dhuhrInfo);
        }

        if(asrNotif) {
            String asrTime = combinePrayerDateTime(todayDate, timing.getAsr());
            Calendar asrCalendar = StringToCalendar(asrTime);
            if (currentCalendar.after(asrCalendar)) {
                asrTime = combinePrayerDateTime(tomorrowDate, timing.getAsr());
                asrCalendar = StringToCalendar(asrTime);
            }
            String asrTitle = "Asr Prayer";
            String asrInfo = "It's time for Asr prayer";
            addNotification(asrCalendar, ID_asr, asrTitle, asrInfo, asrInfo);
        }

        if(maghribNotif) {
            String maghribTime = combinePrayerDateTime(todayDate, timing.getMaghrib());
            Calendar maghribCalendar = StringToCalendar(maghribTime);
            if (currentCalendar.after(maghribCalendar)) {
                maghribTime = combinePrayerDateTime(tomorrowDate, timing.getMaghrib());
                maghribCalendar = StringToCalendar(maghribTime);
            }
            String maghribTitle = "Maghrib Prayer";
            String maghribInfo = "It's time for Maghrib prayer";
            addNotification(maghribCalendar, ID_maghrib, maghribTitle, maghribInfo, maghribInfo);
        }

        if(ishaNotif) {
            String ishaaTime = combinePrayerDateTime(todayDate, timing.getIsha());
            Calendar ishaaCalendar = StringToCalendar(ishaaTime);
            if (currentCalendar.after(ishaaCalendar)) {
                ishaaTime = combinePrayerDateTime(tomorrowDate, timing.getIsha());
                ishaaCalendar = StringToCalendar(ishaaTime);
            }
            String ishaTitle = "Ishaa Prayer";
            String ishaInfo = "It's time for Ishaa prayer";
            addNotification(ishaaCalendar, ID_isha, ishaTitle, ishaInfo, ishaInfo);
        }

    }

    private String combinePrayerDateTime(String str_date, String str_time)
    {
        return str_date + " " + str_time + ":00";
    }

    public  void SetNotification(PrayerTimeCard card){
        //editor.putBoolean(mContext.getString(R.string.id_fajr), checked);
        editor.putBoolean(card.id, !card.notif);
        editor.commit();
        SchedulingALLNotification();
    }

    private void CancelAllNotification() {
        CancelNotification(ID_fajr);
        CancelNotification(ID_sunrise);
        CancelNotification(ID_dhuhr);
        CancelNotification(ID_asr);
        CancelNotification(ID_maghrib);
        CancelNotification(ID_isha);
    }

    private void CancelNotification(String Id){
        Intent notificationIntent = new Intent(mContext, AlarmReceiver.class);
        notificationIntent.setData(Uri.parse("timer:" + Id));
        PendingIntent broadcast = PendingIntent.getBroadcast(mContext, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        // Cancel alarms
        try {
            alarmManager.cancel(broadcast);
        } catch (Exception e) {
            Log.e("Somethin wrong!!", "AlarmManager update was not canceled. " + e.toString());
        }
    }

    public void addNotification (Calendar calendar, String Id, String Title, String Info, String Tick){

        Intent notificationIntent = new Intent(mContext, AlarmReceiver.class);
        notificationIntent.setData(Uri.parse("timer:" + Id));
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, Id);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_TITLE, Title);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_INFO, Info);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_TICK, Tick);

        PendingIntent broadcast = PendingIntent.getBroadcast(mContext, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




}
