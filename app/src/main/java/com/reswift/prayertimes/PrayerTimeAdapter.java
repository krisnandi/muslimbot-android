package com.reswift.prayertimes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Irnanto on 20-Sep-16.
 */
public class PrayerTimeAdapter extends RecyclerView.Adapter<PrayerTimeAdapter.PrayerTimeCardViewHolder> {

    private List<PrayerTimeCard> contactList;

    public PrayerTimeAdapter(List<PrayerTimeCard> contactList) {
        this.contactList = contactList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(PrayerTimeCardViewHolder prayerTimeCardViewHolder, int i) {
        PrayerTimeCard ci = contactList.get(i);
        prayerTimeCardViewHolder.vName.setText(ci.solat);
        prayerTimeCardViewHolder.vSurname.setText(ci.time);
    }

    @Override
    public PrayerTimeCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardtime, viewGroup, false);

        return new PrayerTimeCardViewHolder(itemView);
    }

    public static class PrayerTimeCardViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vSurname;
        protected TextView vEmail;
        protected TextView vTitle;

        public PrayerTimeCardViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txtName);
            vSurname = (TextView)  v.findViewById(R.id.txtSurname);
            vEmail = (TextView)  v.findViewById(R.id.txtEmail);
            vTitle = (TextView) v.findViewById(R.id.title);
        }
    }


}