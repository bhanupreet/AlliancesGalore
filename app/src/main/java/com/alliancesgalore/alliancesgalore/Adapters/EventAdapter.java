package com.alliancesgalore.alliancesgalore.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
    private int lastPosition = 0;

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

    @SuppressLint({"Assert", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        @SuppressLint("SimpleDateFormat") DateFormat simple = new SimpleDateFormat("dd MMM");
        @SuppressLint("SimpleDateFormat") DateFormat dayformat = new SimpleDateFormat("EEE");
        @SuppressLint("SimpleDateFormat") DateFormat time = new SimpleDateFormat("hh:mm:ss a");
        final CustomEvent customEvent = mCustomEventList.get(position);
        holder.mEvent_title.setText(customEvent.getTitle());
        holder.mEvent_date.setText(simple.format(customEvent.getDateTime()));
        holder.mEvent_day.setText(dayformat.format(customEvent.getDateTime()));
        holder.mEventDescription.setText(customEvent.getDescription());
        if (holder.mEventColor == null) {
            assert false;
            holder.mEventColor.setBackgroundColor(Color.GREEN);
        }
        else
            holder.mEventColor.setBackgroundColor(customEvent.getColor());

        if (customEvent.isAllDay())
            holder.mAllDay.setText("All day");
        else
            holder.mAllDay.setText(time.format(customEvent.getDateTime()));

        //IT WORKS DON'T TOUCH IT

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
        setAnimation(holder.layout, position);
        lastPosition = 0;
    }

    @Override
    public int getItemCount() {
        return mCustomEventList.size();

    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mCtx, R.anim.push_left_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
