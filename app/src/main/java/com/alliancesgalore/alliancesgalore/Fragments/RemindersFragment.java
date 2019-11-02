package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;


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
    private CalendarView exFiveCalendar;
    private TextView mTextview;
    private LocalDate selectedDate;
    private DateTimeFormatter monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM");
//    private LocalDate today1 = LocalDate.now();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidThreeTen.init(getContext());
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        FindIds(view);
        mCtx = getContext();
        setAdapter();
        selectedDate = LocalDate.now();

        mMonthSwitch.setChecked(false);
        DateFormat monthFormat = new SimpleDateFormat("MMMM");
        mMonthSwitch.setText(monthFormat.format(System.currentTimeMillis()));
        mMonthSwitch.setOnClickListener(v -> {
            mMonthSwitch.setChecked(!mMonthSwitch.isChecked());
            if (mMonthSwitch.isChecked()) {
                mMonthSwitch.setCheckMarkDrawable(R.drawable.ic_arrow_drop_down);
                Animation slide_up = AnimationUtils.loadAnimation(mCtx, R.anim.slide_up);
                exFiveCalendar.startAnimation(slide_up);
                exFiveCalendar.setVisibility(View.GONE);

            } else {
                mMonthSwitch.setCheckMarkDrawable(R.drawable.ic_arrow_drop_up);
                Animation slide_down = AnimationUtils.loadAnimation(mCtx, R.anim.slide_down);
                exFiveCalendar.startAnimation(slide_down);
                exFiveCalendar.setVisibility(View.VISIBLE);
            }
        });
        DayBinder<DayViewContainer> binder = new DayBinder<DayViewContainer>() {
            @NotNull
            @Override
            public DayViewContainer create(@NotNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NotNull DayViewContainer container, @NotNull CalendarDay day) {
                {
                    container.day = day;
                    TextView textView = container.textView;
                    ConstraintLayout layout = container.layout;
                    textView.setText(Integer.toString(day.getDate().getDayOfMonth()));

                    View flightTopView = container.flightTopView;
                    View flightBottomView = container.flightBottomView;

                    flightTopView.setBackground(null);
                    flightBottomView.setBackground(null);


//                    textView.setOnClickListener(v -> Functions.toast("day selected" + day, mCtx));
                    if (day.getOwner() == DayOwner.THIS_MONTH) {

                        textView.setTextColor(getResources().getColor(R.color.example_5_text_grey));
//
                        if (selectedDate == day.getDate()) {
                            layout.setBackground(getResources().getDrawable(R.drawable.example_5_selected_bg));
                            Calendar date = Calendar.getInstance();
                            date.set(selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth(), 0, 0, 0);
                            DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
                            Functions.toast(simple.format(date.getTimeInMillis()), mCtx);
                        } else {
                            textView.setTextColor(getResources().getColor(R.color.example_5_text_grey_light));
                            layout.setBackground(null);
                        }
//                        layout.setBackgroundResource(
//                                if (selectedDate == day.date) R.drawable.example_5_selected_bg else 0)
//                        List<Date> flights = new ArrayList<>()

                        List<Event> events = new ArrayList<>(mList);

                        Calendar date = Calendar.getInstance();
                        date.set(day.getDate().getYear(), day.getDate().getMonthValue() - 1, day.getDate().getDayOfMonth(), 0, 0, 0);
                        for (Event event : mList) {
                            Calendar eventDay = Calendar.getInstance();
                            eventDay.setTimeInMillis(event.getDateTime());
                            eventDay.set(eventDay.get(Calendar.YEAR)
                                    , eventDay.get(Calendar.MONTH)
                                    , eventDay.get(Calendar.DAY_OF_MONTH)
                                    , 0
                                    , 0
                                    , 0);
                            if (eventDay == date) {
                                flightTopView.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary_dark));
                            }
                        }
//                        flights flights[day.getDate()];
//                        if (flights != null) {
//                            if (flights.count() == 1) {
//                                flightBottomView.setBackgroundColor(view.context.getColorCompat(flights[0].color))
//                            } else {
//                                flightTopView.setBackgroundColor(view.context.getColorCompat(flights[0].color))
//                                flightBottomView.setBackgroundColor(view.context.getColorCompat(flights[1].color))
//                            }
//                        }
                    }

                }
            }
        };

        exFiveCalendar.setDayBinder(binder);

        DateFormat simple = new SimpleDateFormat("MMM");

        // figure out current month
        YearMonth currentMonth = YearMonth.now();
        exFiveCalendar.setup(
                currentMonth.minusMonths(10),
                currentMonth.plusMonths(10),
                DayOfWeek.MONDAY
        );
        exFiveCalendar.scrollToMonth(currentMonth);
        MonthHeaderFooterBinder<MonthViewContainer> header = new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NotNull
            @Override
            public MonthViewContainer create(@NotNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NotNull MonthViewContainer container, @NotNull CalendarMonth month) {

            }
        };

        exFiveCalendar.setMonthHeaderBinder(header);


        Function1<CalendarMonth, Unit> monthScrollListener = month -> {
            mMonthSwitch.setText(monthTitleFormatter.format(month.getYearMonth().getMonth()));
            return Unit.INSTANCE;
        };


        exFiveCalendar.setMonthScrollListener(monthScrollListener);
//        calendarView.setMonthHeaderBinder();


        String myemail = Functions.encodeUserEmail(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        mList.clear();

        query = FirebaseDatabase.getInstance().getReference().child("MyEvents").child(myemail);
        query.addListenerForSingleValueEvent(q1ValueEventListener);


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
        mRecycler = view.findViewById(R.id.reminder_recycler);
        exFiveCalendar = view.findViewById(R.id.exFiveCalendar);
        mTextview = view.findViewById(R.id.calendarDayText);
        mMonthSwitch = view.findViewById(R.id.reminders_monthview_switch);

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


    final class DayViewContainer extends ViewContainer {
        @NotNull
        public CalendarDay day = new CalendarDay(LocalDate.now(), DayOwner.THIS_MONTH);
        private final TextView textView;
        private final ConstraintLayout layout;
        private final View flightTopView;
        private final View flightBottomView;

        @NotNull
        public final CalendarDay getDay() {

            return this.day;
        }

        public final void setDay(@NotNull CalendarDay var1) {
            this.day = var1;
        }

        public final TextView getTextView() {
            return this.textView;
        }

        public final ConstraintLayout getLayout() {
            return this.layout;
        }

        public final View getFlightTopView() {
            return this.flightTopView;
        }

        public final View getFlightBottomView() {
            return this.flightBottomView;
        }

        public DayViewContainer(@NotNull View view) {
            super(view);
            this.textView = view.findViewById(R.id.exFiveDayText);
            this.layout = view.findViewById(R.id.exFiveDayLayout);
            this.flightTopView = view.findViewById(R.id.exFiveDayFlightTop);
            this.flightBottomView = view.findViewById(R.id.exFiveDayFlightBottom);
            selectedDate = day.getDate();

            view.setOnClickListener(it -> {
                if (day.getOwner() == DayOwner.THIS_MONTH
                        && !Intrinsics.areEqual(RemindersFragment.this.selectedDate, DayViewContainer.this.getDay().getDate())) {
                    LocalDate oldDate = RemindersFragment.this.selectedDate;
                    selectedDate = day.getDate();
//                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                        exFiveCalendar.notifyDateChanged();
                    exFiveCalendar.notifyDateChanged(day.getDate());
                    if (oldDate != null) {
                        exFiveCalendar.notifyDateChanged(oldDate);
                    }
                    ///mRecycler scroll to position

                }

            });
        }
    }

    private void updateAdapterForDate(LocalDate date) {
    }

    class MonthViewContainer extends ViewContainer {
        public LinearLayout legendLayout;

        public MonthViewContainer(@NotNull View view) {
            super(view);
            legendLayout = view.findViewById(R.id.legendLayout);
        }
    }
}

//location timning during office hours


