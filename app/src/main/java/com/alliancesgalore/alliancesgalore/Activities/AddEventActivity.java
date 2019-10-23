package com.alliancesgalore.alliancesgalore.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolBar;
    private TextInputEditText mTitle;
    private SwitchCompat mAllDaySwitch;
    private ConstraintLayout mAlldayLayout, mDateTimeLayout;
    private long mDateTime = 0;
    private TextView mStartDate, mStartTime;
    private Button materialButton;
    private Calendar cStartDate = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Context mCtx = AddEventActivity.this;
    private Date date;
    private TimePickerDialog myTimePicker;
    private Date time;
    private Date temptime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        FindIds();
        SetmToolBar();

        mStartDate.setOnClickListener(this);
        mAlldayLayout.setOnClickListener(this);
        mAllDaySwitch.setOnClickListener(this);
        mStartTime.setOnClickListener(this);

        materialButton.setOnClickListener(view -> {



            DateFormat simple = new SimpleDateFormat("dd MMM yyyy hh:mm a");

            if (!mAllDaySwitch.isChecked()) {
                time = temptime;
            } else {

                Calendar newTime = Calendar.getInstance();
                newTime.set(Calendar.HOUR_OF_DAY, 0);
                newTime.set(Calendar.MINUTE, 0);
                time = newTime.getTime();
            }


            Date datetime = combineDateTime(date, time);
            mDateTime = datetime.getTime();

            mTitle.setText(simple.format(mDateTime));

        });
    }


    private void FindIds() {
        mToolBar = findViewById(R.id.addEvent_toolbar);
        mStartDate = findViewById(R.id.addEvent_startDate);
        mTitle = findViewById(R.id.addEvent_title);
        mAlldayLayout = findViewById(R.id.addEvent_allday_layout);
        mAllDaySwitch = findViewById(R.id.addEvent_allday_switch);
        mStartTime = findViewById(R.id.addEvent_startTime);
        materialButton = findViewById(R.id.setbtn);
    }


    private void SetmToolBar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Event");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (view == mStartDate) {
            setDate(mStartDate);
        }
        if (view == mAllDaySwitch) {
            if (mAllDaySwitch.isChecked()) {
                mStartTime.setVisibility(View.GONE);
            } else
                mStartTime.setVisibility(View.VISIBLE);
        }

        if (view == mStartTime) {
            Calendar calender = Calendar.getInstance();
            myTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                    Calendar newTime = Calendar.getInstance();

                    //newTime.set(hourOfDay, minute); // remove this line

                    //Add these two line
                    newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    newTime.set(Calendar.MINUTE, minute);
//                    mStartDate.setText(hourOfDay + minute);
                    time = newTime.getTime();
                    temptime = time;

                    DateFormat simple = new SimpleDateFormat("hh:mm a");
                    mStartTime.setText(simple.format(time));

                }
            }, calender.get((Calendar.HOUR_OF_DAY)), calender.get(Calendar.MINUTE), false);
            myTimePicker.show();
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
                    date = calendar.getTime();
                    String full = new SimpleDateFormat("dd-MM-yyyy").format(date);
                    Functions.toast(full, mCtx);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private Date combineDateTime(Date date, Date time) {
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(date);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(time);

        calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
        calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
        calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
        calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));

        Date result = calendarA.getTime();
        return result;
    }

}
