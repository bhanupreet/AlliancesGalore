package com.alliancesgalore.alliancesgalore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Models.Event;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private Context mCtx;
    private List<Event> mEventList;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListner mItemLongClickListner;

    public EventAdapter(Context mCtx, List<Event> mEventList) {
        this.mCtx = mCtx;
        this.mEventList = mEventList;
    }

    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void addItemLongClickListener(ItemLongClickListner listener) {
        mItemLongClickListner = listener;
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
        holder.mEvent_date.setText((int) event.getStartTime());

        if (position + 1 < mEventList.size() && mEventList.get(position).getStartTime() == mEventList.get(position + 1).getStartTime()) {
            holder.mEventDatelayout.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(view -> {
            if (mItemLongClickListner != null) {
                mItemLongClickListner.onItemLongClick(position);
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
