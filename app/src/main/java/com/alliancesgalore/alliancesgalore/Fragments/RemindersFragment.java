package com.alliancesgalore.alliancesgalore.Fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import com.alliancesgalore.alliancesgalore.Activities.EventActivity;
import com.alliancesgalore.alliancesgalore.Adapters.EventAdapter;
import com.alliancesgalore.alliancesgalore.Adapters.SnappingLinearLayoutManager;
import com.alliancesgalore.alliancesgalore.Models.CustomEvent;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Services.AlarmReceiver;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
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

import static com.alliancesgalore.alliancesgalore.Activities.MainActivity.getmList;


public class RemindersFragment extends Fragment implements CompactCalendarView.CompactCalendarViewListener {
    private EventAdapter adapter;
    private RecyclerView mRecycler;
    private List<CustomEvent> mList = new ArrayList<>(), temp = new ArrayList<>(), mRepeatList = new ArrayList<>();
    private Context mCtx;
    private List<String> myEvents = new ArrayList<>();
    private SnappingLinearLayoutManager layoutManager;
    private Query query;
    private Calendar currentTime = Calendar.getInstance();
    private CompactCalendarView exFiveCalendar;
    private CheckedTextView mMonthSwitch;
    private List<Event> mEventsList = new ArrayList<>();
    private TextView mNoEvents;
    private TextView mYear;
    private ShimmerRecyclerView mShimmer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        FindIds(view);
        mCtx = getContext();
        setAdapter();
        setMonthSwitch();
        setQuery();
        setCalendar();
        setmRecycler();


        AlarmManager alarmMgr = (AlarmManager) mCtx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mCtx, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mCtx, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 25);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        return view;
    }

    private void setmRecycler() {
        mRecycler.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (i != -1) {
                    Date date = new Date(mList.get(i).getDateTime());
                    exFiveCalendar.setCurrentDate(date);
                }
            }
        });
    }

    private void setCalendar() {
        exFiveCalendar.setFirstDayOfWeek(Calendar.MONDAY);
//        exFiveCalendar.setCurrentDate(System.currentTimeMillis());
        exFiveCalendar.setListener(this);
    }

    private void setQuery() {
        String myemail = Functions.encodeUserEmail(
                Objects.requireNonNull(
                        Objects.requireNonNull(
                                FirebaseAuth.getInstance().getCurrentUser()).getEmail()));
        mList.clear();

        query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("MyEvents")
                .child(myemail);

        query.addListenerForSingleValueEvent(q1ValueEventListener);
    }

    private void setMonthSwitch() {
        mMonthSwitch.setChecked(false);
        @SuppressLint("SimpleDateFormat") DateFormat monthFormat = new SimpleDateFormat("MMMM");
        @SuppressLint("SimpleDateFormat") DateFormat yearFormat = new SimpleDateFormat("yyyy");
        mYear.setText(yearFormat.format(System.currentTimeMillis()));
        mYear.bringToFront();
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
    }


    private void setAdapter() {
        adapter = new EventAdapter(getContext(), mList);
        mRecycler.setHasFixedSize(true);
        layoutManager = new SnappingLinearLayoutManager(mCtx, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(adapter);
        adapter.addItemClickListener(position -> {
            Intent EventIntent = new Intent(getContext(), EventActivity.class);
            EventIntent.putExtra("object", mList.get(position));
            Functions.toast(String.valueOf(getmList().size()), mCtx);
            EventIntent.putParcelableArrayListExtra("objectlist", (ArrayList<? extends Parcelable>) getmList());
            startActivity(EventIntent);
        });
    }

    private void FindIds(View view) {
        mRecycler = view.findViewById(R.id.reminder_recycler);
        mShimmer = view.findViewById(R.id.reminder_recycler_shimmer);
        exFiveCalendar = view.findViewById(R.id.compactcalendar_view);
        mMonthSwitch = view.findViewById(R.id.reminders_monthview_switch);
        mNoEvents = view.findViewById(R.id.noevents);
        mNoEvents.setVisibility(View.GONE);
        mYear = view.findViewById(R.id.reminders_year);
        mYear.setOnClickListener(view12 -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(exFiveCalendar.getFirstDayOfCurrentMonth().getTime());
            DatePickerDialog datePickerDialog = new DatePickerDialog(mCtx, (view1, year, monthOfYear, dayOfMonth) -> {
                DateFormat yearformat = new SimpleDateFormat("yyyy");
                DateFormat monthformat = new SimpleDateFormat("MMMM");

                Calendar selecteddate = Calendar.getInstance();
                selecteddate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                exFiveCalendar.setCurrentDate(selecteddate.getTime());
                mMonthSwitch.setText(monthformat.format(selecteddate.getTimeInMillis()));
                mYear.setText(yearformat.format(selecteddate.getTimeInMillis()));
                Calendar mStartOfMonth = setStartOfMonth(selecteddate);
                Calendar mEndOfMonth = setEndOfMonth(selecteddate);
                loadData(mStartOfMonth, mEndOfMonth);

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private ValueEventListener q1ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                myEvents.clear();
//                Functions.toast("datasnapshot exists", mCtx);
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    myEvents.add(snapshot.getKey());

                currentTime.setTimeInMillis(System.currentTimeMillis());
                mList.clear();
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

    private Calendar setEndOfMonth(Calendar currentTime) {
        Calendar mEndOfMonth = Calendar.getInstance();
        mEndOfMonth.set(currentTime.get(Calendar.YEAR)
                , currentTime.get(Calendar.MONTH),
                currentTime.getActualMaximum(Calendar.DAY_OF_MONTH),
                currentTime.getActualMaximum(Calendar.HOUR_OF_DAY),
                currentTime.getActualMaximum(Calendar.MINUTE),
                currentTime.getActualMaximum(Calendar.SECOND));
        return mEndOfMonth;
    }

    private Calendar setStartOfMonth(Calendar currentTime) {
        Calendar mStartOfMonth = Calendar.getInstance();
        mStartOfMonth.set(currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH),
                currentTime.getActualMinimum(Calendar.DAY_OF_MONTH),
                currentTime.getActualMinimum(Calendar.HOUR_OF_DAY),
                currentTime.getActualMinimum(Calendar.MINUTE),
                currentTime.getActualMinimum(Calendar.SECOND));
        return mStartOfMonth;
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
                Calendar previous = Calendar.getInstance();
                previous.setTimeInMillis(nextday.getTimeInMillis());
                nextday.add(Calendar.MONTH, i);
                if (previous.get(Calendar.DAY_OF_MONTH) == previous.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    nextday.set(Calendar.DAY_OF_MONTH, nextday.getActualMaximum(Calendar.DAY_OF_MONTH));
                }

            } else
                nextday.add(Calendar.DATE, i);

            event1.setDateTime(nextday.getTimeInMillis());
            if (!temp.contains(event1) && !mList.contains(event1))
                temp.add(event1);

            Event event2 = new Event(event1.getColor(), event1.getDateTime(), event1.getTitle());
            if (!mEventsList.contains(event2))
                mEventsList.add(event2);

        }
    }

    @Override
    public void onDayClick(Date dateClicked) {
        DateFormat simple = new SimpleDateFormat("dd MMM YYYY");
        for (CustomEvent event : mList) {
            if (simple.format(event.getDateTime()).equals(simple.format(dateClicked))) {
                int pos = mList.indexOf(event);
                if (pos != -1) {
                    mRecycler.smoothScrollToPosition(pos);
                    Functions.toast(String.valueOf(getmList().size()), mCtx);
                }
                return;
            }
        }
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        if (!mList.isEmpty())
            mRecycler.scrollToPosition(0);
        Calendar mStartOfMonth = Calendar.getInstance();
        mStartOfMonth.setTimeInMillis(firstDayOfNewMonth.getTime());
        Calendar mEndOfMonth = setEndOfMonth(mStartOfMonth);
        DateFormat simple = new SimpleDateFormat("MMMM");
        DateFormat year = new SimpleDateFormat("yyyy");
        mYear.setText(year.format(firstDayOfNewMonth));
        mMonthSwitch.setText(simple.format(firstDayOfNewMonth));
        exFiveCalendar.removeAllEvents();
        mList.clear();
        temp.clear();
        mRecycler.setVisibility(View.GONE);
        mShimmer.showShimmerAdapter();
        int top = layoutManager.findFirstCompletelyVisibleItemPosition();
        Calendar calendar = Calendar.getInstance();
//        if (!mList.isEmpty()) {
//            calendar.setTimeInMillis(mList.get(top).getDateTime());
//            exFiveCalendar.setCurrentDate(calendar.getTime());
//        }
        mRepeatList.clear();
        loadData(mStartOfMonth, mEndOfMonth);
    }

    //WARNING
    //DO NOT TOUCH THIS FUNCTION, TOUCHING IT BREAKS THE ADAPTER

    private void loadData(Calendar mStartOfMonth, Calendar mEndOfMonth) {
        mRecycler.setVisibility(View.GONE);
        mShimmer.showShimmerAdapter();
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

                //getting repeating events list
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomEvent myCustomEvent = snapshot.getValue(CustomEvent.class);
                        String key = snapshot.getKey();
                        if (myEvents.contains(key) && !mRepeatList.contains(myCustomEvent)) {
                            assert myCustomEvent != null;
                            myCustomEvent.setUid(key);
                            mRepeatList.add(myCustomEvent);
                        }
                    }
                    exFiveCalendar.removeAllEvents();
                    mList.clear();
                    mEventsList.clear();
                    temp.clear();

                    adapter.notifyDataSetChanged();
                    //add repeating events to mlist
                    for (CustomEvent customEvent : mRepeatList) {
                        if (!mList.contains(customEvent)) {
                            mList.add(customEvent);

                        }
                    }

                    //set repeating events according to given option
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

                    //storing repeated events in a temp list and clearing the main list
                    //traversing the temp list to get events during given month
                    mList.clear();
                    adapter.notifyDataSetChanged();
                    for (CustomEvent event : temp) {
                        if (!mList.contains(event)
                                && event.getDateTime() >= mStartOfMonth.getTimeInMillis()
                                && event.getDateTime() <= mEndOfMonth.getTimeInMillis()) {
                            mList.add(event);

                        }
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
                            //finally adding non repeating events
                            if (dataSnapshot.exists()) {

                                mRecycler.setVisibility(View.VISIBLE);
                                mShimmer.hideShimmerAdapter();
                                mNoEvents.setVisibility(View.GONE);

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    CustomEvent myCustomEvent = snapshot.getValue(CustomEvent.class);
                                    String key = snapshot.getKey();
                                    if (myEvents.contains(key) && !mList.contains(myCustomEvent)) {
                                        assert myCustomEvent != null;
                                        myCustomEvent.setUid(key);
                                        mList.add(myCustomEvent);
//                                    adapter.notifyItemInserted(mList.size()-1);
                                    }
                                }
//                            adapter.notifyDataSetChanged();
                            }

                            //sorting the list and putting dots on to the calendar
                            exFiveCalendar.removeAllEvents();
                            addDots();
                            exFiveCalendar.addEvents(mEventsList);
                            adapter.notifyDataSetChanged();
                            if (mList.isEmpty()) {
                                mRecycler.setVisibility(View.GONE);
                                mShimmer.hideShimmerAdapter();
                                mNoEvents.setVisibility(View.VISIBLE);
                            } else {
                                mRecycler.setVisibility(View.VISIBLE);
                                mShimmer.hideShimmerAdapter();
                                mNoEvents.setVisibility(View.GONE);
                            }
                            Calendar calendar = Calendar.getInstance();
                            DateFormat date = new SimpleDateFormat("dd MMM yyyy");
                            int pos = 0;
                            for (CustomEvent event : mList) {
                                if (calendar.getTimeInMillis() >= event.getDateTime()) {
                                    pos = mList.indexOf(event);
                                }
                            }
                            if (pos != -1) {
                                mRecycler.smoothScrollToPosition(pos);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    mShimmer.hideShimmerAdapter();
                    mRecycler.setVisibility(View.GONE);
                    mNoEvents.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addDots() {
        mEventsList.clear();
        Collections.sort(mList, (m1, m2) -> Long.compare(m1.getDateTime(), m2.getDateTime()));
        for (CustomEvent myCustomEvent : mList) {
            Event event = new Event(myCustomEvent.getColor(), myCustomEvent.getDateTime(), myCustomEvent.getTitle());
            if (!mEventsList.contains(event))
                mEventsList.add(event);
        }
    }
}