package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Adapters.EventAdapter;
import com.alliancesgalore.alliancesgalore.Adapters.SnappingLinearLayoutManager;
import com.alliancesgalore.alliancesgalore.Models.CustomEvent;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class RemindersFragment extends Fragment {
    private EventAdapter adapter;
    private RecyclerView mRecycler;
    private List<CustomEvent> mList = new ArrayList<>();
    private Context mCtx;
    private List<String> myEvents = new ArrayList<>();
    private SnappingLinearLayoutManager layoutManager;
    private Query query;
    private Calendar currentTime = Calendar.getInstance();
    private CompactCalendarView exFiveCalendar;
    private int firstVisibleInListview;
    private CheckedTextView mMonthSwitch;
    private List<Event> mEventsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        FindIds(view);
        mCtx = getContext();
        setAdapter();


        mMonthSwitch.setChecked(false);
        DateFormat monthFormat = new SimpleDateFormat("MMMM");
        mMonthSwitch.setText(monthFormat.format(System.currentTimeMillis()));
        mMonthSwitch.setOnClickListener(v -> {


            if (!exFiveCalendar.isAnimating()) {
                mMonthSwitch.setChecked(!mMonthSwitch.isChecked());
                if (mMonthSwitch.isChecked()) {
                    mMonthSwitch.setCheckMarkDrawable(R.drawable.ic_arrow_drop_down);
                    exFiveCalendar.hideCalendarWithAnimation();
                    exFiveCalendar.hideCalendar();
                } else {
                    mMonthSwitch.setCheckMarkDrawable(R.drawable.ic_arrow_drop_up);
                    exFiveCalendar.showCalendarWithAnimation();
                    exFiveCalendar.setVisibility(View.VISIBLE);
                }
            } else {
                Functions.toast("Please wait for animation to complete", mCtx);
            }
        });


        DateFormat simple = new SimpleDateFormat("MMM");


//        calendarView.setMonthHeaderBinder();


        String myemail = Functions.encodeUserEmail(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        mList.clear();

        query = FirebaseDatabase.getInstance().getReference().child("MyEvents").child(myemail);
        query.addListenerForSingleValueEvent(q1ValueEventListener);
        exFiveCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        exFiveCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                DateFormat simple = new SimpleDateFormat("dd MMM YYYY");
                for (CustomEvent event : mList) {
                    if (simple.format(event.getDateTime()).equals(simple.format(dateClicked))) {
                        mRecycler.smoothScrollToPosition(mList.indexOf(event));
                        return;
                    }
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Calendar mStartOfMonth = Calendar.getInstance();
                mStartOfMonth.setTimeInMillis(firstDayOfNewMonth.getTime());
                Calendar mEndOfMonth = setEndOfMonth(mStartOfMonth);
                DateFormat simple = new SimpleDateFormat("MMMM");
                mMonthSwitch.setText(simple.format(firstDayOfNewMonth));
                loadData(mStartOfMonth, mEndOfMonth);
            }
        });

        return view;
    }


    private void setAdapter() {
        adapter = new EventAdapter(getContext(), mList);
        mRecycler.setHasFixedSize(true);
        layoutManager = new SnappingLinearLayoutManager(mCtx, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(adapter);
//        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator_Event(getContext());
//        mRecycler.addItemDecoration(dividerItemDecoration);
        firstVisibleInListview = layoutManager.findFirstVisibleItemPosition();
        adapter.addItemClickListener(position -> {
            DateFormat simple = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
            Functions.toast(simple.format(mList.get(position).getDateTime()), mCtx);
        });
//        mRecycler.setVisibility(View.GONE);


    }

    private void FindIds(View view) {
        mRecycler = view.findViewById(R.id.reminder_recycler);
        exFiveCalendar = view.findViewById(R.id.compactcalendar_view);
        mMonthSwitch = view.findViewById(R.id.reminders_monthview_switch);
    }


    private ValueEventListener q1ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    myEvents.add(snapshot.getKey());
                }
                currentTime.setTimeInMillis(System.currentTimeMillis());

                Calendar mStartOfMonth = setStartOfMonth(currentTime);
                Calendar mEndOfMonth = setEndOfMonth(currentTime);

                loadData(mStartOfMonth, mEndOfMonth);


            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private Calendar setEndOfMonth(Calendar selectedMonth) {
        Calendar mEndOfMonth = Calendar.getInstance();
        mEndOfMonth.set(selectedMonth.get(Calendar.YEAR)
                , selectedMonth.get(Calendar.MONTH) + 1
                , 0
                , 23
                , 59
                , 59);
        return mEndOfMonth;
    }

    private Calendar setStartOfMonth(Calendar currentTime) {
        Calendar mStartOfMonth = Calendar.getInstance();
        mStartOfMonth.set(currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH),
                1,
                0,
                0,
                0);
        return mStartOfMonth;

    }

    private void loadData(Calendar mStartOfMonth, Calendar mEndOfMonth) {

        mList.clear();
        Query q2 = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("CalendarEvents")
                .orderByChild("dateTime")
                .startAt(mStartOfMonth.getTimeInMillis())
                .endAt(mEndOfMonth.getTimeInMillis());
        q2.addValueEventListener(q2ValueEventListener);
    }

    private ValueEventListener q2ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                mList.clear();
                mEventsList.clear();
                exFiveCalendar.removeAllEvents();

                mRecycler.setVisibility(View.VISIBLE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CustomEvent myCustomEvent = snapshot.getValue(CustomEvent.class);

                    String key = snapshot.getKey();

                    if (myEvents.contains(key) && !mList.contains(myCustomEvent)) {
                        mList.add(myCustomEvent);
                        Event event = new Event(Color.GREEN, myCustomEvent.getDateTime(), myCustomEvent.getTitle());
                        if (!mEventsList.contains(event))
                            mEventsList.add(event);
//                        myEvents.remove(key);
//                        adapter.notifyItemInserted(mList.size() - 1);
                    }
                }
                exFiveCalendar.addEvents(mEventsList);
                adapter.notifyDataSetChanged();
            } else
                mRecycler.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private RecyclerView.SmoothScroller setSmoothScroller(Context mCtx) {
        return new LinearSmoothScroller(mCtx) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
    }


//        Functions.toast(setDay.getTime().toString(), Alliancesgalore.getAppContext());


}

//location timning during office hours


