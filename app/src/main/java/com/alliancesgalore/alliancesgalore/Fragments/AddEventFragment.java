package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionInflater;
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
import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.ColorPickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.getDate;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.getDescription;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.getList;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.getLocation;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.getTime;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.getmAllDaySwitch;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.getmTitle;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.isEdit;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.selectedlist;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.setmAlldaySwitch;
import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.setmTitle;
import static com.alliancesgalore.alliancesgalore.Utils.Functions.encodeUserEmail;
import static com.alliancesgalore.alliancesgalore.Utils.Functions.toast;
import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;


public class AddEventFragment extends Fragment implements View.OnClickListener {
    private TextInputEditText mTitle;
    private SwitchCompat mAllDaySwitch;
    private int mrepeat = 0, mnotify = 0;
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
    private ConstraintLayout mRepeatLayout, mAddPeopleLayout, mDescriptionLayout, mNotifyLayout, mLocationLayout, mColorLayout;
    private String timeText;
    private CharSequence[] repeat = {"Does not repeat", "Every day", "Every week", "Every month"};
    private CharSequence[] notify = {"5 minutes before", "10 minutes before", "15 minutes before", "30 minutes before", "1 hour before"};
    private View mColorView;
    private DatabaseReference calEvents;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addevent_base, container, false);

        FindIds(view);
        SetViews();

        mCtx = getContext();
        mDate.setOnClickListener(this);
        mAllDaySwitch.setOnClickListener(this);
        mTime.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mRepeatLayout.setOnClickListener(this);
        mAddPeopleLayout.setOnClickListener(this);
        mDescriptionLayout.setOnClickListener(this);
        mNotifyLayout.setOnClickListener(this);
        mLocationLayout.setOnClickListener(this);
        mColorLayout.setOnClickListener(this);

        return view;
    }

    private void SetViews() {

        if (TextUtils.isEmpty(getLocation())) {
            mLocation.setText("Location");
        } else
            mLocation.setText(getLocation().trim());


        if (TextUtils.isEmpty(getDescription()))
            mDescription.setText("Description");
        else
            mDescription.setText(getDescription().trim());


        if (getDate() == 0) {
            mDate.setText("Date");
        } else {
            long date = getDate();
            String full = new SimpleDateFormat("dd-MM-yyyy").format(date);
            mDate.setText(full);
        }


        if (getTime() == 0) {
            mTime.setText("Time");
        } else {
            long time = getTime();
            DateFormat simple = new SimpleDateFormat("hh:mm a");
            mTime.setText(simple.format(time));
        }


        mTitle.setText(getmTitle());
        mAllDaySwitch.setChecked(getmAllDaySwitch());

        setTimeVisibility();
        mRepition.setText(repeat[mrepeat]);
        mNotify.setText(notify[mnotify]);
        setAddPeopleview();
        mColorView.setBackgroundColor(AddEventActivity.getColor());
    }

    private void setAddPeopleview() {

        if (!selectedlist.isEmpty()) {
//            toast(String.valueOf(selectedlist.size()), mCtx);
            if (!selectedlist.contains(myProfile)) {
                selectedlist.add(myProfile);
            }
            if (selectedlist.get(0).equals(myProfile)) {
                UserProfile profile0 = selectedlist.get(selectedlist.size() - 1);
                selectedlist.set(selectedlist.size() - 1, myProfile);
                selectedlist.set(0, profile0);

            }

            String addpeopletext = "Add People";
            if (selectedlist.size() == 2) {
                addpeopletext = "You and " + selectedlist.get(0).getDisplay_name();
            } else if (selectedlist.size() > 2)
                addpeopletext = "You and " + selectedlist.get(0).getDisplay_name() + " + " + (selectedlist.size() - 2) + " others";
            mAddPeople.setText(addpeopletext);

        } else
            mAddPeople.setText("Add people");

//        toast(String.valueOf(selectedlist.size()), getContext());
    }

    @Override
    public void onResume() {

        super.onResume();
        SetViews();
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

            case R.id.addEvent_repeat_layout:
                setRepetition();
                break;

            case R.id.addEvent_AddPeople_layout:
                setAddPeople();
                break;

            case R.id.addEvent_Description_layout:
                setDescriptionLocation("desc", mDescriptionLayout);
                break;

            case R.id.addEvent_Notify_layout:
                setNotify();
                break;

            case R.id.addEvent_Location_layout:
                setDescriptionLocation("loc", mLocationLayout);
                break;

            case R.id.addEvent_savebtn:
                saveBtnClick();
                break;
            case R.id.addEvent_color_layout:
                colorpicker();

        }
    }

    private void colorpicker() {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(mCtx, Color.GREEN, color -> {
            // do action
            mColorView.setBackgroundColor(color);
            AddEventActivity.setColor(color);
//            toast(Integer.toString(color), mCtx);
        });
        colorPickerDialog.show();
    }


    private void setDescriptionLocation(String key, ConstraintLayout layout) {

        Bundle bundl = new Bundle();
        bundl.putString("desc_loc", key);

        AddDescriptionLocatonFragment dv = new AddDescriptionLocatonFragment();
        dv.setArguments(bundl);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addSharedElement(layout, ViewCompat.getTransitionName(layout))
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.AddEvent_container, dv);
        ft.addToBackStack("add_desc_loc");
        ft.commit();
    }

    private void FindIds(View view) {

        mTitle = view.findViewById(R.id.addEvent_title);

        mAllDaySwitch = view.findViewById(R.id.addEvent_allday_switch);
        mDate = view.findViewById(R.id.addEvent_startDate);
        mTime = view.findViewById(R.id.addEvent_startTime);

        mSaveBtn = view.findViewById(R.id.addEvent_savebtn);

        mRepition = view.findViewById(R.id.addEvent_repeat_text);
        mAddPeople = view.findViewById(R.id.addEvent_AddPeople_text);
        mDescription = view.findViewById(R.id.addEvent_description_text);
        mNotify = view.findViewById(R.id.addEvent_Notify_text);
        mLocation = view.findViewById(R.id.addEvent_Location_text);
        mColorView = view.findViewById(R.id.addEvent_color_img);
        mColorLayout = view.findViewById(R.id.addEvent_color_layout);

        mRepeatLayout = view.findViewById(R.id.addEvent_repeat_layout);
        mAddPeopleLayout = view.findViewById(R.id.addEvent_AddPeople_layout);
        mDescriptionLayout = view.findViewById(R.id.addEvent_Description_layout);
        mNotifyLayout = view.findViewById(R.id.addEvent_Notify_layout);
        mLocationLayout = view.findViewById(R.id.addEvent_Location_layout);
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
            AddEventActivity.setDate(date.getTime());
            String full = new SimpleDateFormat("dd-MM-yyyy").format(date);
//            toast(full, mCtx);
        }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void setTime() {

        Calendar calender = Calendar.getInstance();
        myTimePicker = new TimePickerDialog(mCtx, (view1, hourOfDay, minute) -> {
            Calendar newTime = Calendar.getInstance();
            newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            newTime.set(Calendar.MINUTE, minute);
            newTime.set(Calendar.SECOND, 0);
            time = newTime.getTime();
            temptime = time;
            DateFormat simple = new SimpleDateFormat("hh:mm a");
            mTime.setText(simple.format(time));
            timeText = simple.format(time);
            AddEventActivity.setTime(time.getTime());
        }, calender.get((Calendar.HOUR_OF_DAY)), calender.get(Calendar.MINUTE), false);
        myTimePicker.show();

    }

    private void setTimeVisibility() {

        if (mAllDaySwitch.isChecked()) {
            mTime.setVisibility(View.GONE);
            timeText = "time";
        } else {
            mTime.setVisibility(View.VISIBLE);
            timeText = mTime.getText().toString();
        }

        setmAlldaySwitch(mAllDaySwitch.isChecked());
    }

    private void setRepetition() {

        AlertDialog.Builder alert = new AlertDialog.Builder(mCtx);
        alert.setSingleChoiceItems(repeat, mrepeat, (dialog, which) -> {
            mrepeat = which;
            mRepition.setText(repeat[which]);
            dialog.dismiss();
        });
        alert.show();
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

    private void setNotify() {

        AlertDialog.Builder alert = new AlertDialog.Builder(mCtx);
        alert.setSingleChoiceItems(notify, mnotify, (dialog, which) -> {
            mnotify = which;
            mNotify.setText(notify[which]);
            dialog.dismiss();
        });
        alert.show();
    }

    private void saveBtnClick() {

        if (TextUtils.isEmpty(mTitle.getText())) {
            toast("Please add a title to event", mCtx);
        } else if (mDate.getText().equals("Date")) {
            toast("Please add Date", mCtx);
        } else if (mDescription.getText().equals("Description")) {
            toast("Please enter Description", mCtx);
        } else if (mLocation.getText().equals("Location")) {
            toast("Please enter Location", mCtx);
        } else if (mTime.getText().equals("Time") && !mAllDaySwitch.isChecked()) {
            toast("Please add time", mCtx);
        } else {
            save();
        }
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

    @Override
    public void onPause() {
        super.onPause();
        setmTitle(mTitle.getText().toString());
    }

    //TO - DO
    private void save() {

        if (!mAllDaySwitch.isChecked()) {
            time = temptime;
        } else {
            Calendar newTime = Calendar.getInstance();
            newTime.set(Calendar.HOUR_OF_DAY, 0);
            newTime.set(Calendar.MINUTE, 0);
            newTime.set(Calendar.SECOND, 0);
            time = newTime.getTime();
        }

        Date staticstate = new Date(AddEventActivity.getDate());
        Date statictime = new Date(AddEventActivity.getTime());
        Date datetime = combineDateTime(staticstate, statictime);
        mDateTime = datetime.getTime();

        String key;
        if (!TextUtils.isEmpty(isEdit) && isEdit.equalsIgnoreCase("true")) {
            calEvents = FirebaseDatabase.getInstance().getReference().child("CalendarEvents").child(AddEventActivity.key);
            key = AddEventActivity.key;

        } else {
            calEvents = FirebaseDatabase.getInstance().getReference().child("CalendarEvents").push();
            key = calEvents.getKey();
        }

        HashMap<String, Object> map = new HashMap<>();
//        toast(key, mCtx);
        map.put("title", mTitle.getText().toString());
        map.put("allDay", mAllDaySwitch.isChecked());
        map.put("dateTime", mDateTime);
        map.put("repetition", mrepeat);
        map.put("description", mDescription.getText().toString());
        map.put("notify", mnotify);
        map.put("location", mLocation.getText().toString());
        map.put("createdBy", myProfile.getEmail());
        map.put("color", AddEventActivity.getColor());

        calEvents.updateChildren(map).addOnSuccessListener(aVoid -> toast("Data added", mCtx));
        HashMap<String, Object> eventParticipants = new HashMap<>();

        List<UserProfile> myList = getList();
        if (!myList.contains(myProfile)) {
            myList.add(myProfile);
        }

        for (UserProfile profile : myList) {
            eventParticipants.put(encodeUserEmail(profile.getEmail()), true);
        }

        eventParticipants.put(encodeUserEmail(myProfile.getEmail()), true);

        DatabaseReference eventParticipantsref = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("EventParticipants")
                .child(key);

        String finalKey = key;
        eventParticipantsref.updateChildren(eventParticipants).addOnSuccessListener(aVoid12 -> {
            toast("part1 updated", mCtx);
            HashMap<String, Object> myEvents = new HashMap<>();
            myEvents.put(finalKey, true);
            for (UserProfile profile : selectedlist) {
                DatabaseReference myEventsref = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("MyEvents")
                        .child(encodeUserEmail(profile.getEmail()));

                myEventsref.updateChildren(myEvents).addOnSuccessListener(aVoid1 -> {
                    toast("Data updated successfully", mCtx);
                    Intent mainIntent = new Intent(getContext(), MainActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();

                });
            }
        });


    }
}