package com.reswift.prayertimes;

import com.reswift.model.PrayerTimesDay;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Irnanto on 19-Sep-16.
 */
public interface PrayerTimesDayService {
    //@GET("timings/{timestamp}?latitude={latitude}&longitude={longitude}&timezonestring={timezonestring}&method=5")
    @GET("timings/{timestamp}")
    Call<PrayerTimesDay> getContent(
            @Path("timestamp") String timestamp,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("timezonestring") String timezonestring,
            @Query("method") String method);


}
