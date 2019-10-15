package com.alliancesgalore.alliancesgalore.Fragments;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Event;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.jaiselrahman.agendacalendar.view.AgendaCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class RemindersFragment extends Fragment {
    private AgendaCalendar agendaCalendar;
    private CheckedTextView currentMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reminders, container, false);
        List<Event> events = mockEvents();

        agendaCalendar = view.findViewById(R.id.calendar);

        agendaCalendar.setEvents(events);

        agendaCalendar.setOnEventClickListener(event -> Toast.makeText(getContext(), event.getTitle(), Toast.LENGTH_SHORT).show());
        currentMonth = view.findViewById(R.id.currentMonth);
        currentMonth.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        currentMonth.setOnClickListener(v -> {
            if (currentMonth.isChecked()) {
                agendaCalendar.hideCalendar();
            } else {
                agendaCalendar.showCalendar();
            }
            currentMonth.setChecked(!currentMonth.isChecked());
        });

        agendaCalendar.agendaView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int pos = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                if (pos < events.size()) {
                    Calendar cal = events.get(pos).getTime();
                    Functions.toast(events.get(pos).getTitle(), getContext());
                    String month = new SimpleDateFormat("MMM").format(cal.getTime());
                    currentMonth.setText(month);


                }
            }
        });
        agendaCalendar.setListener(new AgendaCalendar.CalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                currentMonth.setText(new SimpleDateFormat("MMM").format(dateClicked));
                agendaCalendar.scrollTo(dateClicked.getTime());
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currentMonth.setText(new SimpleDateFormat("MMM").format(firstDayOfNewMonth));
            }
        });

        agendaCalendar.setHeightAnimDuration(250);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.reminder_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.today) {
            agendaCalendar.scrollTo(System.currentTimeMillis());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Event> mockEvents() {
        List<Event> events = new ArrayList<>();
        for (int i = -30; i <= 100; i++) {
            if (i % 2 == 0) {
                events.addAll(getEvents(i, 1));
            } else if (i % 3 == 0) {
                events.addAll(getEvents(i, 2));
            } else if (i % 5 == 0) {
                events.addAll(getEvents(i, 3));
            } else if (i == 1) {
                events.addAll(getEvents(i, 4));
            }
        }
        return events;
    }

    private List<Event> getEvents(int day, int count) {
        List<Event> events = new ArrayList<>();
        Calendar cal;
        switch (count) {
            case 4:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day, "Location " + day, cal, Color.RED));
            case 3:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day, "Location " + day, cal, Color.GREEN));
            case 2:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day, "Location " + day, cal, Color.BLUE));
            case 1:
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, day);
                events.add(new Event("Event " + cal.get(Calendar.DAY_OF_MONTH),
                        "Description " + day,
                        "Location " + day,
                        cal, Color.MAGENTA));
        }
        return events;
    }
}

