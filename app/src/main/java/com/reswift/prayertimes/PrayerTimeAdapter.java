package com.reswift.prayertimes;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Irnanto on 20-Sep-16.
 */
public class PrayerTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private List<PrayerTimeCard> prayerTimeCards;
    private List<Object> items;

    private final int LOCATION = 0, DATE = 1, CARD = 2;

    public PrayerTimeAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof PrayerTimeLocation) {
            return LOCATION;
        } else if (items.get(position) instanceof PrayerTimeDate) {
            return DATE;
        }
        else if (items.get(position) instanceof PrayerTimeCard) {
            return CARD;
        }
        return  -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case LOCATION:
              View itemView1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlocation, parent, false);
                viewHolder = new PrayerTimeLocationViewHolder(itemView1);
                break;

            case  DATE:
                View itemView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.carddate, parent, false);
                viewHolder = new PrayerTimeDateViewHolder(itemView2);
            break;
            case CARD:
                View itemView3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardtime, parent, false);
                viewHolder = new PrayerTimeCardViewHolder(itemView3);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        switch (holder.getItemViewType()) {
            case LOCATION:
                PrayerTimeLocation location = (PrayerTimeLocation) items.get(position);
                PrayerTimeLocationViewHolder prayerTimeLocationViewHolder = (PrayerTimeLocationViewHolder) holder;
                prayerTimeLocationViewHolder.info.setText(location.location);
                break;

            case  DATE:
                PrayerTimeDate date = (PrayerTimeDate) items.get(position);
                PrayerTimeDateViewHolder prayerTimeDateViewHolder = (PrayerTimeDateViewHolder) holder;
                prayerTimeDateViewHolder.info.setText(date.date);
                break;
            case CARD:
                final PrayerTimeCard card = (PrayerTimeCard) items.get(position);
                PrayerTimeCardViewHolder prayerTimeCardViewHolder = (PrayerTimeCardViewHolder) holder;
                prayerTimeCardViewHolder.info.setText(Html.fromHtml(card.solat), TextView.BufferType.SPANNABLE);

                prayerTimeCardViewHolder.time.setText(card.time);

                prayerTimeCardViewHolder.past.setVisibility(View.GONE);
                prayerTimeCardViewHolder.now.setVisibility(View.GONE);
                prayerTimeCardViewHolder.next.setVisibility(View.GONE);

                if(card.status == PrayerTimeCard.Status.PAST) prayerTimeCardViewHolder.past.setVisibility(View.VISIBLE);
                else if(card.status == PrayerTimeCard.Status.NOW) prayerTimeCardViewHolder.now.setVisibility(View.VISIBLE);
                else if(card.status == PrayerTimeCard.Status.NEXT) prayerTimeCardViewHolder.next.setVisibility(View.VISIBLE);

                prayerTimeCardViewHolder.isCheck.setChecked(card.notif);
                prayerTimeCardViewHolder.isCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userData.getInstance().SetNotification(card);
                    }
                });

                break;
        }

    }





















    public class PrayerTimeCardViewHolder extends RecyclerView.ViewHolder {
        protected TextView next;
        protected TextView now;
        protected TextView past;
        protected TextView info;
        protected TextView time;
        protected CheckBox isCheck;

        public PrayerTimeCardViewHolder(View v) {
            super(v);
            next =  (TextView) v.findViewById(R.id.solat_next);
            now = (TextView)  v.findViewById(R.id.solat_now);
            past = (TextView)  v.findViewById(R.id.solat_psst);
            info = (TextView) v.findViewById(R.id.solat_info);
            time = (TextView) v.findViewById(R.id.solat_time);
            isCheck = (CheckBox) v.findViewById(R.id.solat_check);
        }
    }

    public class PrayerTimeDateViewHolder extends RecyclerView.ViewHolder {
        protected TextView info;

        public PrayerTimeDateViewHolder(View v) {
            super(v);
            info = (TextView) v.findViewById(R.id.textCalender);
        }
    }

    public class PrayerTimeLocationViewHolder extends RecyclerView.ViewHolder {

        protected TextView info;

        public PrayerTimeLocationViewHolder(View v) {
            super(v);
            info = (TextView) v.findViewById(R.id.textLocation);
        }
    }
}