package com.alliancesgalore.alliancesgalore.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
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
import java.util.List;

import static com.alliancesgalore.alliancesgalore.Activities.EventActivity.getEvent;
import static com.alliancesgalore.alliancesgalore.Activities.EventActivity.mSelectedList;
import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;

public class EventFragment extends Fragment implements View.OnClickListener {
    private TextInputEditText mTitle;
    private SwitchCompat mAllDaySwitch;
    private TextView mDate, mTime, mDescription, mRepition, mNotify, mLocation, mAddPeople;
    private View mColorView;
    private List<String> emailList = new ArrayList<>();
    private CharSequence[] repeat = {"Does not repeat", "Every day", "Every week", "Every month"};
    private CharSequence[] notify = {"5 minutes before", "10 minutes before", "15 minutes before", "30 minutes before", "1 hour before"};
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
                        if (emailList.contains(profile.getEmail()) && !selectedlist.contains(profile)) {
                            selectedlist.add(profile);
                        }
                    }
                }
                setAddPeopleview();
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
        int mrepeat = getEvent().getRepetition();
        int mnotify = getEvent().getNotify();
        mRepition.setText(repeat[mrepeat]);
        mNotify.setText(notify[mnotify]);
        setAddPeopleview();
        mColorView.setBackgroundColor(getEvent().getColor());

        mTitle.setEnabled(false);
        mAllDaySwitch.setEnabled(false);
    }

    private void setTimeVisibility() {
        if (getEvent().isAllDay()) {
            mTime.setVisibility(View.GONE);
        } else {
            mTime.setVisibility(View.VISIBLE);
        }

    }

    private void setAddPeopleview() {

        if (!selectedlist.isEmpty()) {
//            List<UserProfile> temp = new ArrayList<>(selectedlist);
//            selectedlist.clear();
//            for (UserProfile profile : temp) {
//                if (!temp.contains(profile))
//                    selectedlist.add(profile);
//            }
//            toast(String.valueOf(selectedlist.size()), mCtx);
            if (!selectedlist.contains(myProfile)) {
                selectedlist.add(myProfile);
            }
            if (selectedlist.get(0).equals(myProfile)) {
                UserProfile profile0 = selectedlist.get(selectedlist.size() - 1);
                selectedlist.set(selectedlist.size() - 1, myProfile);
                selectedlist.set(0, profile0);

            }
            String addpeopletext;

            if (selectedlist.size() == 1) {
                addpeopletext = "You";
            } else if (selectedlist.size() == 2) {
                addpeopletext = "You and " + selectedlist.get(0).getDisplay_name();
            } else if (selectedlist.size() > 2) {
                addpeopletext = "You and " + selectedlist.get(0).getDisplay_name() + " + " + (selectedlist.size() - 2) + " others";
            } else {
                addpeopletext = "else condition";
            }
            mAddPeople.setText(addpeopletext);
            for (UserProfile profile : selectedlist)
                if (!mSelectedList.contains(profile)) {
                    mSelectedList.add(profile);
                }
        }

//        toast(String.valueOf(selectedlist.size()), getContext());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addEvent_AddPeople_layout:
                EventPeopleFragment fragment = new EventPeopleFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("objectlist", (ArrayList<? extends Parcelable>) selectedlist);
                bundle.putString("title", getEvent().getTitle());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.event_container, fragment)
                        .addToBackStack("eventfragment")
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .commit();

        }
    }

    private void FindIds(View view) {

        mTitle = view.findViewById(R.id.addEvent_title);
        mAllDaySwitch = view.findViewById(R.id.addEvent_allday_switch);
        mDate = view.findViewById(R.id.addEvent_startDate);
        mTime = view.findViewById(R.id.addEvent_startTime);

        Button mSaveBtn = view.findViewById(R.id.addEvent_savebtn);
        mSaveBtn.setVisibility(View.GONE);

        mRepition = view.findViewById(R.id.addEvent_repeat_text);
        mAddPeople = view.findViewById(R.id.addEvent_AddPeople_text);
        mDescription = view.findViewById(R.id.addEvent_description_text);
        mNotify = view.findViewById(R.id.addEvent_Notify_text);
        mLocation = view.findViewById(R.id.addEvent_Location_text);
        mColorView = view.findViewById(R.id.addEvent_color_img);
        ConstraintLayout mColorLayout = view.findViewById(R.id.addEvent_color_layout);

        ConstraintLayout mAddPeopleLayout = view.findViewById(R.id.addEvent_AddPeople_layout);
        mAddPeopleLayout.setOnClickListener(this);
    }


}
