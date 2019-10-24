package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alliancesgalore.alliancesgalore.Activities.AddEventActivity;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddEventFragment extends Fragment implements View.OnClickListener {
    private TextInputEditText mTitle;
    private SwitchCompat mAllDaySwitch;
    private int mrepeat = 0;
    private ConstraintLayout mAlldayLayout;
    private long mDateTime = 0;
    private TextView mDate, mTime, mDescription, mRepition, mNotify, mLocation, mAddPeople;
    private Button mSaveBtn;
    private Calendar cStartDate = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private Context mCtx;
    private Date date;
    private TimePickerDialog myTimePicker;
    private Date time;
    private Date temptime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addevent_base, container, false);
        FindIds(view);

        mCtx = getContext();
        mDate.setOnClickListener(this);
        mAlldayLayout.setOnClickListener(this);
        mAllDaySwitch.setOnClickListener(this);
        mTime.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mRepition.setOnClickListener(this);
        mAddPeople.setOnClickListener(this);
        mDescription.setOnClickListener(this);
        mNotify.setOnClickListener(this);
        mLocation.setOnClickListener(this);
        return view;
    }

    private void setTitle() {

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
        mDescription.setText(simple.format(mDateTime));
    }

    @Override
    public void onClick(View view1) {

        switch (view1.getId()) {
            case R.id.addEvent_startDate:
                setDate();
                break;

            case R.id.addEvent_startTime:
                setTime();
                break;

            case R.id.addEvent_allday_switch:
                setTimeVisibility();
                break;

            case R.id.addEvent_repeat_text:
                setRepetition();
                break;

            case R.id.addEvent_savebtn:
                saveBtnClick();
                break;

            case R.id.addEvent_AddPeople_text:
                setAddPeople();
                break;

            case R.id.addEvent_description_text:
            case R.id.addEvent_Notify_text:
            case R.id.addEvent_Location_text:
                break;

        }
    }

    private void saveBtnClick() {
        if (TextUtils.isEmpty(mTitle.getText())) {
            Functions.toast("Please add a title to event", mCtx);
        } else if (mDate.getText().equals("Date")) {
            Functions.toast("Please add Date", mCtx);
        } else if (!mAllDaySwitch.isChecked()) {
            if (mTime.getText().equals("Time"))
                Functions.toast("Please add time", mCtx);
            else
                setTitle();
        } else {
            setTitle();
        }
    }

    private void setRepetition() {
        final CharSequence[] repeat = {"Does not repeat", "Every day", "Every week", "Every month"};
        AlertDialog.Builder alert = new AlertDialog.Builder(mCtx);
        alert.setSingleChoiceItems(repeat, mrepeat, (dialog, which) -> {
            if (repeat[which] == "Every day") {
                mrepeat = 1;
            } else if (repeat[which] == "Every week") {
                mrepeat = 2;
            } else if (repeat[which] == "Every month") {
                mrepeat = 3;
            } else
                mrepeat = 0;

            mRepition.setText(repeat[which]);
            dialog.dismiss();
        });
        alert.show();
    }

    private void setTimeVisibility() {
        if (mAllDaySwitch.isChecked()) {
            mTime.setVisibility(View.GONE);
        } else {
            mTime.setVisibility(View.VISIBLE);
        }
    }

    private void setTime() {
        Calendar calender = Calendar.getInstance();
        myTimePicker = new TimePickerDialog(mCtx, (view1, hourOfDay, minute) -> {
            Calendar newTime = Calendar.getInstance();
            newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            newTime.set(Calendar.MINUTE, minute);
            time = newTime.getTime();
            temptime = time;
            DateFormat simple = new SimpleDateFormat("hh:mm a");
            mTime.setText(simple.format(time));
        }, calender.get((Calendar.HOUR_OF_DAY)), calender.get(Calendar.MINUTE), false);
        myTimePicker.show();
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mCtx, (view1, year, monthOfYear, dayOfMonth) -> {
            mDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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

        return calendarA.getTime();
    }

    private void setAddPeople() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addSharedElement(mAddPeople, ViewCompat.getTransitionName(mAddPeople))
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.AddEvent_container, new AddPeopleFragment())
                .addToBackStack("addPeople")
                .commit();
    }

    private void FindIds(View view) {
        mDate = view.findViewById(R.id.addEvent_startDate);
        mTitle = view.findViewById(R.id.addEvent_title);
        mAlldayLayout = view.findViewById(R.id.addEvent_allday_layout);
        mAllDaySwitch = view.findViewById(R.id.addEvent_allday_switch);
        mTime = view.findViewById(R.id.addEvent_startTime);
        mSaveBtn = view.findViewById(R.id.addEvent_savebtn);
        mDescription = view.findViewById(R.id.addEvent_description_text);
        mAddPeople = view.findViewById(R.id.addEvent_AddPeople_text);
        mDescription = view.findViewById(R.id.addEvent_description_text);
        mNotify = view.findViewById(R.id.addEvent_Notify_text);
        mLocation = view.findViewById(R.id.addEvent_Location_text);
        mRepition = view.findViewById(R.id.addEvent_repeat_text);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<UserProfile> selectedlist = AddEventActivity.getList();
        if (!selectedlist.isEmpty()) {
            Functions.toast(selectedlist.get(0).getDisplay_name(), mCtx);
            mAddPeople.setText(selectedlist.get(0).getDisplay_name());
        }
    }
}