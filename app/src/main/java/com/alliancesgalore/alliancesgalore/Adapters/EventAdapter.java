package com.alliancesgalore.alliancesgalore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Models.Event;
import com.alliancesgalore.alliancesgalore.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private Context mCtx;
    private List<Event> mEventList;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListner mItemLongClickListener;

    public EventAdapter(Context mCtx) {
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
        EventViewHolder holder = new EventViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
        DateFormat dayformat = new SimpleDateFormat("EEEE");
        DateFormat time = new SimpleDateFormat("hh:mm:ss a");
        final Event event = mEventList.get(position);
        holder.mEvent_title.setText(event.getTitle());
        holder.mEvent_date.setText(simple.format(event.getDateTime()));
        holder.mEvent_day.setText(dayformat.format(event.getDateTime()));
        holder.mEventDescription.setText(event.getDescription());

        if (event.isAllDay()) {
            holder.mAllDay.setText("All day");
        } else
            holder.mAllDay.setText(time.format(event.getDateTime()));
        //IT WORKS DON'T TOUCH IT
        //CONVERT TIME TO DATE FOR BETTER FUNCTIONALITY

        if (position > 0) {

            String date1 = simple.format(mEventList.get(position - 1).getDateTime());
            String date2 = simple.format(mEventList.get(position).getDateTime());
            if (date1.equals(date2)) {
                holder.mEventDatelayout.setVisibility(View.GONE);
            } else {
                holder.mEventDatelayout.setVisibility(View.VISIBLE);
            }
        }
        //END
//
        if (position == 0) {
            holder.top.setVisibility(View.GONE);
        } else {
            holder.top.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnLongClickListener(view -> {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(position);
            }

            return false;
        });

        holder.itemView.setOnClickListener(view1 -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEventList.size();

    }

    public void addAll(List<Event> newEvents) {
        int initialSize = mEventList.size();
        for (Event event : newEvents) {
            if (!mEventList.contains(event)) {
                mEventList.add(event);
            }
        }
        notifyItemRangeInserted(initialSize, mEventList.size());
    }

    public long getLastItemDateTime() {
        return mEventList.get(mEventList.size() - 1).getDateTime();
    }

    public void setData(List<Event> mList) {
        this.mEventList.addAll(mList);
    }
}
