package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.Activities.EventActivity;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.alliancesgalore.alliancesgalore.Activities.EventActivity.getEvent;
import static com.alliancesgalore.alliancesgalore.Utils.Functions.toast;
import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;

public class EventFragment extends Fragment implements View.OnClickListener {
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
    private List<String> emailList = new ArrayList<>();
    private CharSequence[] repeat = {"Does not repeat", "Every day", "Every week", "Every month"};
    private CharSequence[] notify = {"5 minutes before", "10 minutes before", "15 minutes before", "30 minutes before", "1 hour before"};
    private View mColorView;
    private List<UserProfile> mList = new ArrayList<>(), selectedlist = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_addevent_base, container, false);
        FindIds(view);

        Bundle bundle = getArguments();
        mList = bundle.getParcelableArrayList("objectlist");

        Query query = FirebaseDatabase.getInstance().getReference().child("EventParticipants").child(EventActivity.getEvent().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String email = snapshot.getKey();
                        emailList.add(Functions.decodeUserEmail(email));
                    }
                    for (UserProfile profile : mList) {
                        if (emailList.contains(profile.getEmail())) {
                            selectedlist.add(profile);
                        }
                    }

                    setAddPeopleview();
//                    Functions.toast(String.valueOf(mSelectedList.size()), EventActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setViews();
        return view;
    }

    private void setViews() {
        if (TextUtils.isEmpty(getEvent().getLocation())) {
            mLocation.setText("Location");
        } else
            mLocation.setText(getEvent().getLocation().trim());


        if (TextUtils.isEmpty(getEvent().getDescription()))
            mDescription.setText("Description");
        else
            mDescription.setText(getEvent().getDescription().trim());


        if (getEvent().getDateTime() == 0) {
            mDate.setText("Date");
        } else {
            long date = getEvent().getDateTime();
            String full = new SimpleDateFormat("dd-MM-yyyy").format(date);
            mDate.setText(full);
        }


        if (getEvent().getDateTime() == 0) {
            mTime.setText("Time");
        } else {
            long time = getEvent().getDateTime();
            DateFormat simple = new SimpleDateFormat("hh:mm a");
            mTime.setText(simple.format(time));
        }


        mTitle.setText(getEvent().getTitle());
        mAllDaySwitch.setChecked(getEvent().isAllDay());

        setTimeVisibility();
        mrepeat = getEvent().getRepetition();
        mnotify = getEvent().getNotify();
        mRepition.setText(repeat[mrepeat]);
        mNotify.setText(notify[mnotify]);
        setAddPeopleview();
        mColorLayout.setBackgroundColor(getEvent().getColor());

        mTitle.setEnabled(false);
        mAllDaySwitch.setEnabled(false);
    }

    private void setTimeVisibility() {
        if (getEvent().isAllDay()) {
            mTime.setVisibility(View.GONE);
            timeText = "time";
        } else {
            mTime.setVisibility(View.VISIBLE);
            timeText = mTime.getText().toString();
        }

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

        toast(String.valueOf(selectedlist.size()), getContext());
    }

    @Override
    public void onClick(View view) {

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
}
