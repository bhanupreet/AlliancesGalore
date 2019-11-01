package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Adapters.EventAdapter;
import com.alliancesgalore.alliancesgalore.Models.Event;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.YearMonth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class RemindersFragment extends Fragment {
    private EventAdapter adapter;
    private static final int TOTAL_ITEM_EACH_LOAD = 10;
    private int currentPage = 0;
    private RecyclerView mRecycler;
    private List<Event> mList = new ArrayList<>();
    private Context mCtx;
    private List<String> myEvents = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private Query query;
    private int pos = 0;
    private Calendar currentTime = Calendar.getInstance();
    private Set<LocalDate> selectedDates = new LinkedHashSet<>();
    private Calendar calendar = Calendar.getInstance();
    private int mYear = calendar.get(Calendar.YEAR);
    private int mMonth = calendar.get(Calendar.MONTH);
    private int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    private long today;
    private static int firstVisibleInListview;
    private boolean scrollup = true;
    private CheckedTextView mMonthSwitch;
    private CalendarView calendarView;
    private TextView mTextview;
    private LocalDate today1 = LocalDate.now();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidThreeTen.init(getContext());
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        FindIds(view);
        mCtx = getContext();
//        setAdapter();


        DayBinder<DayViewContainer> binder = new DayBinder<DayViewContainer>() {
            @NotNull
            @Override
            public DayViewContainer create(@NotNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NotNull DayViewContainer dayViewContainer, @NotNull CalendarDay calendarDay) {
                dayViewContainer.textView.setText(Integer.toString(calendarDay.getDate().getDayOfMonth()));
            }
        };

        calendarView.setDayBinder(binder);

        DateFormat simple = new SimpleDateFormat("MMM");


        // figure out current month
        YearMonth currentmonth = YearMonth.from(Month.NOVEMBER);
        //
        YearMonth startmonth = YearMonth.from(Month.JANUARY);
        YearMonth endMonth = YearMonth.from(Month.DECEMBER);


        calendarView.setup(startmonth, endMonth, DayOfWeek.MONDAY);
        calendarView.smoothScrollToMonth(currentmonth);


//        calendarView.setMonthHeaderBinder();


        String myemail = Functions.encodeUserEmail(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        mList.clear();

        query = FirebaseDatabase.getInstance().getReference().child("MyEvents").child(myemail);
//        query.addListenerForSingleValueEvent(q1ValueEventListener);


        return view;
    }


    private void setAdapter() {
        adapter = new EventAdapter(getContext());
        mRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(adapter);
//        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator_Event(getContext());
//        mRecycler.addItemDecoration(dividerItemDecoration);
        firstVisibleInListview = layoutManager.findFirstVisibleItemPosition();
        adapter.addItemClickListener(position -> {
            DateFormat simple = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
            Functions.toast(simple.format(mList.get(position).getDateTime()), mCtx);
        });
        mRecycler.setVisibility(View.GONE);
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentFirstVisible = layoutManager.findFirstVisibleItemPosition();


                if (currentFirstVisible > firstVisibleInListview && !scrollup && dy > 0) {
                    Log.i("RecyclerView scrolled: ", "scroll up!");
                    int bottom = layoutManager.findLastVisibleItemPosition();
                    long datetime = mList.get(bottom).getDateTime();

                    Query query = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("CalendarEvents")
                            .orderByChild("dateTime")
                            .startAt(datetime)
                            .limitToFirst(2);

                    query.addListenerForSingleValueEvent(loadData);

                } else {
                    Log.i("RecyclerView scrolled: ", "scroll down!");
                    int top = layoutManager.findFirstVisibleItemPosition();
                    long datetime = mList.get(top).getDateTime();

                    Query query = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("CalendarEvents")
                            .orderByChild("dateTime")
                            .endAt(datetime)
                            .limitToLast(2);

                    query.addListenerForSingleValueEvent(loadData);
                }
                firstVisibleInListview = currentFirstVisible;
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
//                    load bottom data
                    scrollup = false;
                }


                if (!recyclerView.canScrollVertically(-1)) {
                    //load top data

                    scrollup = true;
                }


            }
        });


    }

    private void FindIds(View view) {
        mRecycler = view.findViewById(R.id.event_recycler);
        calendarView = view.findViewById(R.id.calendarview);
        mTextview = view.findViewById(R.id.calendarDayText);

    }


    private ValueEventListener q1ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    myEvents.add(snapshot.getKey());

                }

                currentTime.set(mYear, mMonth, mDay, 0, 0, 0);
                today = currentTime.getTimeInMillis();

                Query q2 = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("CalendarEvents")
                        .orderByChild("dateTime")
                        .startAt(today)
//                        .limitToFirst(1);
                        ;
                q2.addListenerForSingleValueEvent(q2ValueEventListener);


            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener q2ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event myEvent = snapshot.getValue(Event.class);

                    String key = snapshot.getKey();

                    if (myEvents.contains(key) && !mList.contains(myEvent)) {
                        mList.add(myEvent);
                        myEvents.remove(key);
                        adapter.notifyItemInserted(mList.size() - 1);
                    }
                }
            }
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

    private ValueEventListener loadData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    Event event = snapshot.getValue(Event.class);
                    if (myEvents.contains(key) && !mList.contains(event)) {
                        mList.add(event);
                        myEvents.remove(key);
                    }
                    if (myEvents.isEmpty()) {
                        Functions.toast("no more data", mCtx);
                    }
                }
                Collections.sort(mList, (m1, m2) -> Long.compare(m1.getDateTime(), m2.getDateTime()));
                adapter.notifyDataSetChanged();
            } else {
                Functions.toast("no more data", mCtx);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public final class DayViewContainer extends ViewContainer {
        @NotNull
        public TextView textView;
        CalendarDay day;

        public DayViewContainer(@NotNull View view) {
            super(view);
            this.textView = view.findViewById(R.id.calendarDayText);
            textView.setOnClickListener(view1 -> {
                if (day.getOwner() == DayOwner.THIS_MONTH) {
                    if (selectedDates.contains(day.getDate())) {
                        selectedDates.remove(day.getDate());
                    } else {
                        selectedDates.add(day.getDate());
                    }
                }
            });
        }

    }
}

//location timning during office hours


