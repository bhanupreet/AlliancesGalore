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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Activities.AddEventActivity;
import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.Adapters.EventAdapter;
import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.Models.Event;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class testfragment extends Fragment {
    private CheckedTextView currentMonth;
    private List<Date> dateList = new ArrayList<>();
    private EventAdapter adapter;
    private RecyclerView mRecycler;
    private List<Event> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, container, false);
        FindIds(view);


        Event item1 = new Event("My Title1", "Description", "Location", 2, 1234, 1234, true);
        Event item2 = new Event("My Title2", "Description", "Location", 2, 1234, 1234, true);
        Event item3 = new Event("My Title3", "Description", "Location", 2, 1235, 1234, true);
        mList.add(item1);
        mList.add(item2);
        mList.add(item3);

        adapter = new EventAdapter(getContext(), mList);
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    private void FindIds(View view) {
        mRecycler = view.findViewById(R.id.event_recycler);

    }
}

//location timning during office hours


