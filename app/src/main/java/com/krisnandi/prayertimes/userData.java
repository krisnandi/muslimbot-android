package com.krisnandi.prayertimes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Irnanto on 11-Aug-16.
 */
public class userData {
    private static userData ourInstance;// = new userData();

    public DelegateBehaviour delegateBehaviour;

    private final Context mContext;

    public double latitude;
    public double longitude;

    public String locationName;
    public String currentDate;
    public String time_fajr;
    public String time_sunrise;
    public String time_dhuhr;
    public String time_asr;
    public String time_maghrib;
    public String time_ishaa;


    public static userData getInstance(Context context) {
        if(ourInstance == null)
            ourInstance = new userData(context);
        return ourInstance;
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

    private String getCurrentDate(){
        try {
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private String getCurrentTimeStamp(){
        try {

            Long tsLong = System.currentTimeMillis()/1000;
            String currentTimeStamp = tsLong.toString();

            return currentTimeStamp;
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

            //Set GPSTracker
            //GPSTracker gps = new GPSTracker(mContext);

            //get gps latitude and longitude
            //double latitude = gps.getLatitude();
            //double longitude = gps.getLongitude();

            //update location name
            locationName = getLocationName(latitude, longitude);

            Log.d("testing 1", String.valueOf(latitude));
            Log.d("testing 2", String.valueOf(longitude));
            Log.d("testing 3", locationName);

            //update current date
            currentDate = getCurrentDate();

            String dataURL = "http://api.aladhan.com/timings/"+ getCurrentTimeStamp()
                    +"?latitude=" + String.valueOf(latitude)
                    + "&longitude=" + String.valueOf(longitude)
                    + "&timezonestring=" + getCurrentTimeZone()
                    +"&method=5";

            Log.d("testing", dataURL);

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

                //urlConnection.setRequestProperty("latitude","51.508515");
                //urlConnection.setRequestProperty("longitude","-0.1254872");
                //urlConnection.setRequestProperty("timezonestring","Europe/London");
                //urlConnection.setRequestProperty("method","2");

                //urlConnection.setUseCaches(false);
                //urlConnection.setDoInput(true);
                //urlConnection.setDoOutput(true);
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
                Log.d("testing 2", forecastJsonStr);
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
                        Log.e("testingxxx", "Error closing stream", e);
                    }
                }
            }


            try {
                JSONObject jsonAll = new JSONObject(forecastJsonStr);

                //Log.d("testing", jsonAll.getString("code"));
                //Log.d("testing", jsonAll.getString("status"));
                //Log.d("testing", jsonAll.getString("data"));

                JSONObject jsonData = jsonAll.getJSONObject("data");
                //Log.d("testing", jsonData.getString("timings"));
                //Log.d("testing", jsonData.getString("date"));

                JSONObject jsonTimings = jsonData.getJSONObject("timings");
                time_fajr = jsonTimings.getString("Fajr");
                time_sunrise = jsonTimings.getString("Sunrise");
                time_dhuhr = jsonTimings.getString("Dhuhr");
                time_asr = jsonTimings.getString("Asr");
                time_maghrib = jsonTimings.getString("Maghrib");
                time_ishaa = jsonTimings.getString("Isha");

                Log.d("testing", time_fajr);
                Log.d("testing", time_sunrise);
                Log.d("testing", time_dhuhr);
                Log.d("testing", time_asr);
                Log.d("testing", time_maghrib);
                Log.d("testing", time_ishaa);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //delegateBehaviour.onDelegateVoid();



            //Log.d("testing", forecastJsonStr);
            //Log.d("testingzxzxzx", tes.toString());
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
            delegateBehaviour.onDelegateVoid();

        }


    }



}
