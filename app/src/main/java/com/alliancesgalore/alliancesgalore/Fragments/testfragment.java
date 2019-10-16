package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.Activities.AddEventActivity;
import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class testfragment extends Fragment implements CalendarPickerController {
    private CheckedTextView currentMonth;
    private AgendaCalendarView mAgendaCalendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, container, false);
        mAgendaCalendarView = view.findViewById(R.id.agenda_calendar_view);
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);


        List<CalendarEvent> eventList = new ArrayList<>();
        mockList(eventList);
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);

        return view;

    }

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }

    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        endTime1.add(Calendar.MONTH, 1);
        BaseCalendarEvent event1 = new BaseCalendarEvent("Thibault travels in Iceland", "A wonderful journey!", "Iceland",
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), startTime1, endTime1, true);
        eventList.add(event1);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar endTime2 = Calendar.getInstance();
        endTime2.add(Calendar.DAY_OF_YEAR, 3);
        BaseCalendarEvent event2 = new BaseCalendarEvent("Visit to Dalvík", "A beautiful small town", "Dalvík",
                ContextCompat.getColor(getContext(), R.color.colorPrimary), startTime2, endTime2, true);
        eventList.add(event2);

        // Example on how to provide your own layout

    }

    @Override
    public void onResume() {
        super.onResume();
        SetFAB();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SetFAB();
    }

    private void SetFAB() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity.getcurrenttabposition() == 2) {
            mainActivity.fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add, getContext().getTheme()));
            Functions.toast("set in OnResume", getContext());
            mainActivity.fab.setOnClickListener(view -> {
                Intent addIntent = new Intent(getContext(), AddEventActivity.class);
                startActivity(addIntent);
            });
        }
    }

}

