package com.reswift.prayertimes;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import butterknife.BindView;

public class PrayerTimesActivity extends AppCompatActivity {

    //@BindView(R.id.cardList) RecyclerView recList;

    RecyclerView recList;
    LinearLayoutManager llm;
    PrayerTimeAdapter prayerTimeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_times);

        prayerTimeAdapter = new PrayerTimeAdapter(userData.getInstance(this).prayerTimeCards);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        recList.setLayoutManager(llm);

        recList.setAdapter(prayerTimeAdapter);

    }


}
