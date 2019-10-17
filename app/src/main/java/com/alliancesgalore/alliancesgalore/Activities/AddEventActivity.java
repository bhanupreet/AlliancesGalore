package com.alliancesgalore.alliancesgalore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alliancesgalore.alliancesgalore.Models.Event;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolBar;
    private TextInputLayout mTitle;
    private TextView mStartDate, mEndDate, mStartTime, mEndTime, getEventText;
    private Calendar cStartDate = Calendar.getInstance(), cEndDate = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Context mCtx = AddEventActivity.this;
    private Button getevent, setevent;
    private List<CalendarEvent> eventList = new ArrayList<>();
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event baseCalendarEvent = snapshot.getValue(Event.class);
                    Calendar start = Calendar.getInstance();
                    start.setTimeInMillis(baseCalendarEvent.getStartTime());
                    Calendar end = Calendar.getInstance();
                    end.setTimeInMillis(baseCalendarEvent.getEndTime());

                    BaseCalendarEvent event1 = new BaseCalendarEvent(baseCalendarEvent.getTitle(),
                            "A wonderful journey!",
                            "Iceland",
                            ContextCompat.getColor(AddEventActivity.this, R.color.colorPrimaryDark),
                            start, end, baseCalendarEvent.isAllDay());
                    eventList.add(event1);
                }
                getEventText.setText(eventList.get(0).getTitle());
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        FindIds();
        SetmToolBar();

        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        getevent.setOnClickListener(this);
        setevent.setOnClickListener(this);

    }


    private void FindIds() {
        mToolBar = findViewById(R.id.addEvent_toolbar);
        mStartDate = findViewById(R.id.addEvent_startDate);
        mTitle = findViewById(R.id.addEvent_title);
        mEndDate = findViewById(R.id.addEvent_endDate);
        getevent = findViewById(R.id.geteventbtn);
        setevent = findViewById(R.id.setEventbtn);
        getEventText = findViewById(R.id.getEventText);
    }

    private void SetmToolBar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Event");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes the event
     *
     * @param title       The title of the event.
     * @param description The description of the event.
     * @param location    The location of the event.
     * @param color       The color of the event (for display in the app).
     * @param startTime   The start time of the event.
     * @param endTime     The end time of the event.
     * @param allDay      Indicates if the event lasts the whole day.
     */
    @Override
    public void onClick(View view) {

        if (mStartDate == (view)) {
            setDate(mStartDate);
        }
        if (view == mEndDate) {
            setDate(mEndDate);
        }
        if (view == getevent) {
            FirebaseDatabase.getInstance().getReference().child("CalendarEvents").addValueEventListener(valueEventListener);
        }
        if (view == setevent) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("CalendarEvents").push();
            ref.getKey();
            HashMap<String, Object> map = new HashMap<>();
            map.put("Title", Functions.TextOf(mTitle));
            map.put("Description", "default");
            map.put("Location", "default");
            map.put("Color", 3);
            map.put("StartTime", cStartDate.getTimeInMillis());
            map.put("EndTime", cEndDate.getTimeInMillis());

            map.put("AllDay", true);

            ref.setValue(map).addOnSuccessListener(aVoid -> Log.d("data upload", "success"));
        }

    }

    private void setDate(TextView dateview) {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    dateview.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    calendar.set(year, monthOfYear, dayOfMonth);
                    Date date = calendar.getTime();
                    String full = new SimpleDateFormat("dd-MM-yyyy").format(date);
                    Functions.toast(full, mCtx);
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}
