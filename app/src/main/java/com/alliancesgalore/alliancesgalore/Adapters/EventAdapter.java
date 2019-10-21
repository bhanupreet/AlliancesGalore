package com.alliancesgalore.alliancesgalore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Models.Event;
import com.alliancesgalore.alliancesgalore.R;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private Context mCtx;
    private List<Event> mEventList = new ArrayList<>();
    private ItemClickListener mItemClickListener;
    private ItemLongClickListner mItemLongClickListener;

    public EventAdapter(Context mCtx, List<Event> mEventList) {
        this.mCtx = mCtx;
        this.mEventList = mEventList;
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

        final Event event = mEventList.get(position);
        holder.mEvent_title.setText(event.getTitle());
        holder.mEvent_date.setText(Long.toString(event.getStartTime()));

        //IT WORKS DON'T TOUCH IT
        //CONVERT TIME TO DATE FOR BETTER FUNCTIONALITY

        if (position > 0) {
            if (mEventList.get(position - 1).getStartTime() == mEventList.get(position).getStartTime()) {
                holder.mEventDatelayout.setVisibility(View.GONE);
            }
        }

        //END

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
}
