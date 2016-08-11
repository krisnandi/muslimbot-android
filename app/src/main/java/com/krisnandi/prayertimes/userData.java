package com.krisnandi.prayertimes;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        new DownloadFilesTask().execute();
    }


    private void HTTPGet() {
        //String dataUrl = "http://api.aladhan.com/timings/1398332113?latitude=51.508515&longitude=-0.1254872&timezonestring=1&method=2";
        String dataUrl = "http://api.aladhan.com/timings/1398332113";

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                //return null;
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
                //return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            //return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        Log.d("testing", forecastJsonStr);

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

            //String dataURL = "http://api.aladhan.com/timings/1398332113?latitude=51.508515&longitude=-0.1254872&timezonestring=Europe/London&method=2";
            String dataURL = "http://api.aladhan.com/timings/1398332113";
            HttpURLConnection urlConnection  = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            try {
                // Create connection
                URL url = new URL(dataURL);
                urlConnection  = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("latitude","51.508515");
                urlConnection.setRequestProperty("longitude","-0.1254872");
                urlConnection.setRequestProperty("timezonestring","Europe/London");
                urlConnection.setRequestProperty("method","2");
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
                Log.e("testingxxx", "Error ", e);
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

            Log.d("testing", "1 wew do in background");
            Log.d("testing", forecastJsonStr);
            Log.d("testing", "2 wew do in background");
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
