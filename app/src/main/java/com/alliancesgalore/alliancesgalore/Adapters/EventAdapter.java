package com.alliancesgalore.alliancesgalore.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Models.CustomEvent;
import com.alliancesgalore.alliancesgalore.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private Context mCtx;
    private List<CustomEvent> mCustomEventList;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListner mItemLongClickListener;

    public EventAdapter(Context mCtx, List<CustomEvent> mCustomEventList) {
        this.mCustomEventList = mCustomEventList;
        this.mCtx = mCtx;
    }

    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void addItemLongClickListener(ItemLongClickListner listener) {
        mItemLongClickListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.custom_event_layout, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        DateFormat simple = new SimpleDateFormat("dd MMM");
        DateFormat dayformat = new SimpleDateFormat("EEE");
        DateFormat time = new SimpleDateFormat("hh:mm:ss a");
        final CustomEvent customEvent = mCustomEventList.get(position);
        holder.mEvent_title.setText(customEvent.getTitle());
        holder.mEvent_date.setText(simple.format(customEvent.getDateTime()));
        holder.mEvent_day.setText(dayformat.format(customEvent.getDateTime()));
        holder.mEventDescription.setText(customEvent.getDescription());
        if (holder.mEventColor == null)
            holder.mEventColor.setBackgroundColor(Color.GREEN);
        else
            holder.mEventColor.setBackgroundColor(customEvent.getColor());

        if (customEvent.isAllDay())
            holder.mAllDay.setText("All day");
        else
            holder.mAllDay.setText(time.format(customEvent.getDateTime()));

        //IT WORKS DON'T TOUCH IT
        //CONVERT TIME TO DATE FOR BETTER FUNCTIONALITY

        if (position > 0) {
            String date1 = simple.format(mCustomEventList.get(position - 1).getDateTime());
            String date2 = simple.format(mCustomEventList.get(position).getDateTime());
            if (date1.equals(date2))
                holder.mEventDatelayout.setVisibility(View.INVISIBLE);
            else
                holder.mEventDatelayout.setVisibility(View.VISIBLE);
        }

        if (position == 0)
            holder.mEventDatelayout.setVisibility(View.VISIBLE);

        holder.itemView.setOnLongClickListener(view -> {
            if (mItemLongClickListener != null)
                mItemLongClickListener.onItemLongClick(position);
            return false;
        });

        holder.itemView.setOnClickListener(view1 -> {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return mCustomEventList.size();

    }
}
