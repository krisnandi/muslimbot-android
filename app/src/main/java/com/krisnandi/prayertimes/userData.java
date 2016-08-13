package com.krisnandi.prayertimes;

import android.app.Activity;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
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
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        Log.d("testing", getCurrentDate());
        Log.d("testing", getCurrentTimeStamp());

        //Log.d("testing", TimeZone.getDefault().getDisplayName());
        //Log.d("testing", TimeZone.getDefault().getID());


        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        String localTime = date.format(currentLocalTime);

        Log.d("testing", localTime);





        //new DownloadFilesTask().execute();
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

            Log.d("testing", "0 wew do in background");

            String dataURL = "http://api.aladhan.com/timings/1398332113?latitude=51.508515&longitude=-0.1254872&timezonestring=Europe/London&method=2";
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

                Log.d("testing", jsonAll.getString("code"));
                Log.d("testing", jsonAll.getString("status"));
                Log.d("testing", jsonAll.getString("data"));

                //JSONObject jsonData = new JSONObject(jsonAll.getString("data"));
                JSONObject jsonData = jsonAll.getJSONObject("data");
                Log.d("testing", jsonData.getString("timings"));
                Log.d("testing", jsonData.getString("date"));

                JSONObject jsonTimings = jsonData.getJSONObject("timings");
                Log.d("testing", jsonTimings.getString("Fajr"));
                Log.d("testing", jsonTimings.getString("Sunrise"));
                Log.d("testing", jsonTimings.getString("Dhuhr"));
                Log.d("testing", jsonTimings.getString("Asr"));
                Log.d("testing", jsonTimings.getString("Maghrib"));
                Log.d("testing", jsonTimings.getString("Isha"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

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

            Log.d("testing", "wew on post execute");
        }
    }



}
