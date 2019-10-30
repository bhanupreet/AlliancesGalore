package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


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

    private Calendar calendar = Calendar.getInstance();
    private int mYear = calendar.get(Calendar.YEAR);
    private int mMonth = calendar.get(Calendar.MONTH);
    private int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    private long today;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, container, false);
        FindIds(view);
        mCtx = getContext();
        setAdapter();
        String myemail = Functions.encodeUserEmail(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()));


        query = FirebaseDatabase.getInstance().getReference().child("MyEvents").child(myemail);
        query.addListenerForSingleValueEvent(q1ValueEventListener);


        return view;
    }


    private void setAdapter() {
        adapter = new EventAdapter(getContext(), mList);
        mRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(adapter);
//        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator_Event(getContext());
//        mRecycler.addItemDecoration(dividerItemDecoration);

        adapter.notifyDataSetChanged();
    }

    private void FindIds(View view) {
        mRecycler = view.findViewById(R.id.event_recycler);

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
                        .startAt(today);

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
            mList.clear();
            if (dataSnapshot.exists()) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event myEvent = snapshot.getValue(Event.class);

                    String key = snapshot.getKey();

                    if (myEvents.contains(key))
                        mList.add(myEvent);

                    Calendar calDate = Calendar.getInstance();
                    calDate.setTimeInMillis(myEvent.getDateTime());
                    calDate.set(
                            calDate.get(Calendar.YEAR),
                            calDate.get(Calendar.MONTH),
                            calDate.get(Calendar.DATE),
                            0,
                            0,
                            0);

                    if (today == calDate.getTimeInMillis())
                        pos = mList.indexOf(myEvent);

                }
            }

            adapter.notifyDataSetChanged();

            RecyclerView.SmoothScroller smoothScroller = setSmoothScroller(mCtx);


            smoothScroller.setTargetPosition(pos);
            layoutManager.startSmoothScroll(smoothScroller);
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
}

//location timning during office hours


