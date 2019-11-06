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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class RemindersFragment extends Fragment {
    private EventAdapter adapter;
    private RecyclerView mRecycler;
    private List<CustomEvent> mList = new ArrayList<>(), temp = new ArrayList<>(), mRepeatList = new ArrayList<>();
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
                exFiveCalendar.removeAllEvents();
                mList.clear();
                temp.clear();
                mRecycler.setVisibility(View.GONE);
                loadData(mStartOfMonth, mEndOfMonth);
//                loadRepeatingData(mStartOfMonth);


            }
        });


        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = (layoutManager.findFirstCompletelyVisibleItemPosition() + layoutManager.findFirstCompletelyVisibleItemPosition()) / 2;
                if (i != -1) {
                    Date date = new Date(mList.get(i).getDateTime());
                    exFiveCalendar.setCurrentDate(date);
                }
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

                mList.clear();
                temp.clear();
                mRepeatList.clear();
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
                , selectedMonth.get(Calendar.MONTH)
                , selectedMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
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


        Query q3 = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("CalendarEvents")
                .orderByChild("repetition")
                .startAt(1);
        q3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRepeatList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomEvent myCustomEvent = snapshot.getValue(CustomEvent.class);
                        String key = snapshot.getKey();
                        if (myEvents.contains(key) && !mRepeatList.contains(myCustomEvent))
                            mRepeatList.add(myCustomEvent);
//                        myEvents.remove(key);
//                        adapter.notifyItemInserted(mList.size() - 1);
                    }
                    Query q2 = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("CalendarEvents")
                            .orderByChild("dateTime")
                            .startAt(mStartOfMonth.getTimeInMillis())
                            .endAt(mEndOfMonth.getTimeInMillis());
                    q2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            exFiveCalendar.removeAllEvents();
                            mList.clear();
                            mEventsList.clear();
                            temp.clear();


                            for (CustomEvent customEvent : mRepeatList) {
                                if (!mList.contains(customEvent)) {
                                    mList.add(customEvent);
                                }
                            }

                            temp.clear();
                            for (CustomEvent event : mList) {
                                if (event.getRepetition() == 1 && !event.isRepitionFlag()) {
                                    setEvents(event, 1, mEndOfMonth, false);
                                } else if (event.getRepetition() == 2 && !event.isRepitionFlag()) {
                                    setEvents(event, 7, mEndOfMonth, false);
                                } else if (event.getRepetition() == 3) {
                                    setEvents(event, 1, mEndOfMonth, true);
                                }
                            }

                            mList.clear();
                            for (CustomEvent event : temp) {
                                if (!mList.contains(event)
                                        && event.getDateTime() > mStartOfMonth.getTimeInMillis()
                                        && event.getDateTime() < mEndOfMonth.getTimeInMillis()) {
                                    mList.add(event);

                                }
                            }

                            if (dataSnapshot.exists()) {
                                mRecycler.setVisibility(View.VISIBLE);
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    CustomEvent myCustomEvent = snapshot.getValue(CustomEvent.class);
                                    String key = snapshot.getKey();
                                    if (myEvents.contains(key) && !mList.contains(myCustomEvent))
                                        mList.add(myCustomEvent);
                                }
                            }
                            Collections.sort(mList, (m1, m2) -> Long.compare(m1.getDateTime(), m2.getDateTime()));
                            for (CustomEvent myCustomEvent : mList) {
                                Event event = new Event(Color.GREEN, myCustomEvent.getDateTime(), myCustomEvent.getTitle());
                                if (!mEventsList.contains(event))
                                    mEventsList.add(event);
                            }
//                Functions.toast(Integer.toString(mList.size()), mCtx);
                            exFiveCalendar.addEvents(mEventsList);
                            adapter.notifyDataSetChanged();
                            if (mList.isEmpty()) {
                                mRecycler.setVisibility(View.GONE);
                            } else
                                mRecycler.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }

    private void setEvents(CustomEvent event, int i, Calendar mEndOfMonth, boolean ismonth) {
        event.setRepitionFlag(true);
        Calendar nextday = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(event.getDateTime());
        nextday.setTimeInMillis(event.getDateTime());
        while (nextday.getTimeInMillis() <= mEndOfMonth.getTimeInMillis()) {
            CustomEvent event1 = new CustomEvent(event);
            event1.setRepitionFlag(true);
            if (ismonth) {
                nextday.add(Calendar.MONTH, i);
            } else
                nextday.add(Calendar.DATE, i);

            event1.setDateTime(nextday.getTimeInMillis());
            if (!temp.contains(event1) && !mList.contains(event1))
                temp.add(event1);

            Event event2 = new Event(Color.GREEN, event1.getDateTime(), event1.getTitle());
            if (!mEventsList.contains(event2))
                mEventsList.add(event2);

        }
    }


//        Functions.toast(setDay.getTime().toString(), Alliancesgalore.getAppContext());


}

//location timning during office hours


