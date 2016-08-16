package com.krisnandi.prayertimes;

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
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Irnanto on 11-Aug-16.
 */
public class userData {
    private static userData ourInstance;// = new userData();

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

    public String time_fajr;
    public String time_sunrise;
    public String time_dhuhr;
    public String time_asr;
    public String time_maghrib;
    public String time_ishaa;

    private final Context mContext;
    private boolean isUpdateData;

    public static userData getInstance(Context context) {
        if(ourInstance == null)
            ourInstance = new userData(context);
        return ourInstance;
    }

    public boolean successUpdateData(){
        if(false){
            isUpdateData = true;
            return false;
        }
        else {
            return isUpdateData;
        }
    }


    private userData(Context context) {
        this.mContext = context;
    }

    public void updateData(){
        new DownloadFilesTask().execute();
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

    public boolean isDateTimeAfter(String str_date1, String str_date2){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf.parse(str_date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = sdf.parse(str_date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date1.after(date2);
    }

    public boolean isDateTimeBefore(String str_date1, String str_date2){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf.parse(str_date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = sdf.parse(str_date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date1.before(date2);
    }

    public String timeBetween(String str_date1, String str_date2){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf.parse(str_date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = sdf.parse(str_date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = date2.getTime() - date1.getTime();

        String str_time = "<font color='red'> in ";
        SimpleDateFormat tesFormat = new SimpleDateFormat("HH:mm");

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

    public void  SchedulingALLNotification(){
        CancelAllNotification();

        Activity tempActivity = (Activity) mContext;
        SharedPreferences sharedPref = tempActivity.getSharedPreferences("alldata", Context.MODE_PRIVATE);;

        Boolean fajrNotif = sharedPref.getBoolean(mContext.getString(R.string.id_fajr), false);
        Boolean sunriseNotif = sharedPref.getBoolean(mContext.getString(R.string.id_sunrise), false);
        Boolean dhuhrNotif = sharedPref.getBoolean(mContext.getString(R.string.id_dhuhr), false);
        Boolean asrNotif = sharedPref.getBoolean(mContext.getString(R.string.id_asr), false);
        Boolean maghribNotif = sharedPref.getBoolean(mContext.getString(R.string.id_maghrib), false);
        Boolean ishaNotif = sharedPref.getBoolean(mContext.getString(R.string.id_ishaa), false);

        String currentTime = todayDate + " " + currentTime();
        Calendar currentCalendar = StringToCalendar(currentTime);


        if(fajrNotif) {
            String fajrTime = combinePrayerDateTime(todayDate, time_fajr);
            Calendar fajrCalendar = StringToCalendar(fajrTime);
            if (currentCalendar.after(fajrCalendar)) {
                fajrTime = combinePrayerDateTime(tomorrowDate, time_fajr);
                fajrCalendar = StringToCalendar(fajrTime);
            }
            String fajrTitle = "Fajr Prayer";
            String fajrInfo = "It's time for Fajr prayer";
            addNotification(fajrCalendar, ID_fajr, fajrTitle, fajrInfo, fajrInfo);
        }

        if(sunriseNotif) {
            String sunriseTime = combinePrayerDateTime(todayDate, time_sunrise);
            Calendar sunriseCalendar = StringToCalendar(sunriseTime);
            if (currentCalendar.after(sunriseCalendar)) {
                sunriseTime = combinePrayerDateTime(tomorrowDate, time_sunrise);
                sunriseCalendar = StringToCalendar(sunriseTime);
            }
            String sunriseTitle = "Sunrise";
            String sunriseInfo = "It's past Sunrise time";
            addNotification(sunriseCalendar, ID_sunrise, sunriseTitle, sunriseInfo, sunriseInfo);
        }

        if(dhuhrNotif) {
            String dhuhrTime = combinePrayerDateTime(todayDate, time_dhuhr);
            Calendar dhuhrCalendar = StringToCalendar(dhuhrTime);
            if (currentCalendar.after(dhuhrCalendar)) {
                dhuhrTime = combinePrayerDateTime(tomorrowDate, time_dhuhr);
                dhuhrCalendar = StringToCalendar(dhuhrTime);
            }
            String dhuhrTitle = "Dhuhr Prayer";
            String dhuhrInfo = "It's time for Dhuhr prayer";
            addNotification(dhuhrCalendar, ID_dhuhr, dhuhrTitle, dhuhrInfo, dhuhrInfo);
        }

        if(asrNotif) {
            String asrTime = combinePrayerDateTime(todayDate, time_asr);
            Calendar asrCalendar = StringToCalendar(asrTime);
            if (currentCalendar.after(asrCalendar)) {
                asrTime = combinePrayerDateTime(tomorrowDate, time_asr);
                asrCalendar = StringToCalendar(asrTime);
            }
            String asrTitle = "Asr Prayer";
            String asrInfo = "It's time for Asr prayer";
            addNotification(asrCalendar, ID_asr, asrTitle, asrInfo, asrInfo);
        }

        if(maghribNotif) {
            String maghribTime = combinePrayerDateTime(todayDate, time_maghrib);
            Calendar maghribCalendar = StringToCalendar(maghribTime);
            if (currentCalendar.after(maghribCalendar)) {
                maghribTime = combinePrayerDateTime(tomorrowDate, time_maghrib);
                maghribCalendar = StringToCalendar(maghribTime);
            }
            String maghribTitle = "Maghrib Prayer";
            String maghribInfo = "It's time for Maghrib prayer";
            addNotification(maghribCalendar, ID_maghrib, maghribTitle, maghribInfo, maghribInfo);
        }

        if(ishaNotif) {
            String ishaaTime = combinePrayerDateTime(todayDate, time_ishaa);
            Calendar ishaaCalendar = StringToCalendar(ishaaTime);
            if (currentCalendar.after(ishaaCalendar)) {
                ishaaTime = combinePrayerDateTime(tomorrowDate, time_ishaa);
                ishaaCalendar = StringToCalendar(ishaaTime);
            }
            String ishaTitle = "Ishaa Prayer";
            String ishaInfo = "It's time for Ishaa prayer";
            addNotification(ishaaCalendar, ID_isha, ishaTitle, ishaInfo, ishaInfo);
        }

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
        //Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        //notificationIntent.addCategory("android.intent.category.DEFAULT");

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

    private String combinePrayerDateTime(String str_date, String str_time)
    {
        return str_date + " " + str_time + ":00";
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            /*
             *    do things before doInBackground() code runs
             *    such as preparing and showing a Dialog or ProgressBar
            */
            Log.d("testing", "wew preexecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            /*
             *    updating data
             *    such a Dialog or ProgressBar
            */
            Log.d("testing", "wew update");
        }

        @Override
        protected Void doInBackground(Void... params) {
            //do your work here

            //update location name
            locationName = getLocationName(latitude, longitude);

            //Log.d("testing 1", String.valueOf(latitude));
            //Log.d("testing 2", String.valueOf(longitude));
            //Log.d("testing 3", locationName);

            //update current date
            todayDate = getTodayDate();
            tomorrowDate = getTomorrowDate();

            String dataURL = "http://api.aladhan.com/timings/"+ getTodayTimeStamp()
                    +"?latitude=" + String.valueOf(latitude)
                    + "&longitude=" + String.valueOf(longitude)
                    + "&timezonestring=" + getCurrentTimeZone()
                    +"&method=5";

            //Log.d("testing", dataURL);

            //String dataURL = "http://api.aladhan.com/timings/1398332113?latitude=51.508515&longitude=-0.1254872&timezonestring=Europe/London&method=2";
            //String dataURL = "http://api.aladhan.com/timings/1398332113";

            HttpURLConnection urlConnection  = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            try {
                // Create connection
                URL url = new URL(dataURL);
                urlConnection  = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection .connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                //Log.d("testing 2", forecastJsonStr);
            }
            catch (IOException e) {
                Log.e("testing", "Error ", e);
                //Handles input and output errors
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("testing", "Error closing stream", e);
                    }
                }
            }


            try {
                JSONObject jsonAll = new JSONObject(forecastJsonStr);
                JSONObject jsonData = jsonAll.getJSONObject("data");

                JSONObject jsonTimings = jsonData.getJSONObject("timings");
                time_fajr = jsonTimings.getString("Fajr");
                time_sunrise = jsonTimings.getString("Sunrise");
                time_dhuhr = jsonTimings.getString("Dhuhr");
                time_asr = jsonTimings.getString("Asr");
                time_maghrib = jsonTimings.getString("Maghrib");
                time_ishaa = jsonTimings.getString("Isha");

                /*
                Log.d("testing", time_fajr);
                Log.d("testing", time_sunrise);
                Log.d("testing", time_dhuhr);
                Log.d("testing", time_asr);
                Log.d("testing", time_maghrib);
                Log.d("testing", time_ishaa);
                */

            } catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            /*
             *    do something with data here
             *    display it or send to mainactivity
             *    close any dialogs/ProgressBars/etc...
            */

            //Log.d("testing", "wew on post execute");

            SchedulingALLNotification();
            delegateBehaviour.onDelegateVoid();

        }


    }



}
