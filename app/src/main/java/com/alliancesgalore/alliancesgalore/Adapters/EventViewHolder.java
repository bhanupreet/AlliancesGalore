package com.alliancesgalore.alliancesgalore.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.R;

public class EventViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout mEventDatelayout;
    TextView mEvent_date;
    TextView mEvent_day, mEvent_title, mEventDescription, mAllDay;
    View top;

    EventViewHolder(@NonNull View itemView) {
        super(itemView);

        mEventDatelayout = itemView.findViewById(R.id.event_date_layout);
        mEvent_date = itemView.findViewById(R.id.event_date);
        mEvent_day = itemView.findViewById(R.id.event_day);
        mEvent_title = itemView.findViewById(R.id.event_title);
        mEventDescription = itemView.findViewById(R.id.event_description);
        mAllDay = itemView.findViewById(R.id.event_allday);
        top = itemView.findViewById(R.id.event_topview);
    }
}
