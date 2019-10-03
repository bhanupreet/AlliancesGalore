package com.alliancesgalore.alliancesgalore.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;


public class LocationListFragment extends Fragment {
    private RecyclerView mRecycler;
    private UserProfileAdapter adapter;
    private List<UserProfile> allsubordinatesList, subordinatesList, subsubordinateslist, newlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_locationlist, container, false);
        mRecycler = view.findViewById(R.id.locationlist_recycler);
        allsubordinatesList = new ArrayList<>();
        allsubordinatesList = new ArrayList<>();
        subsubordinateslist = new ArrayList<>();
        subordinatesList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference().child("Users");
        query.keepSynced(true);
        query.addValueEventListener(valueEventListener);

        newlist = new ArrayList<>();
        newlist.clear();

        adapter = new UserProfileAdapter(getContext(), newlist);
        mRecycler.setAdapter(adapter);
        setAdapter(mRecycler);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onStart() {
        if (myProfile != null && myProfile.getLevel() == 30) {
            LocationFragment locationFragment = new LocationFragment();
            getFragmentManager()
                    .beginTransaction()
                    .addToBackStack("location")
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.simpleFrameLayout, locationFragment)
                    .commit();
        }
        super.onStart();
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            allsubordinatesList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile profile = snapshot.getValue(UserProfile.class);
                    allsubordinatesList.add(profile);
                    fetch();
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void setAdapter(RecyclerView mRecycler) {
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(layoutManager);
    }

    private void fetch() {

        subordinatesList.clear();
        subsubordinateslist.clear();
        newlist.clear();

        for (UserProfile profile : allsubordinatesList) {
            if (profile.getReportingTo().equals(myProfile.getEmail())) {
                subordinatesList.add(profile);
            }
        }
        for (UserProfile Profile : allsubordinatesList) {
            for (UserProfile subProfile : subordinatesList) {
                if (Profile.getReportingTo().equals(subProfile.getEmail())) {
                    subsubordinateslist.add(Profile);
                    subsubordinateslist.add(subProfile);
                }
            }
        }
        for (UserProfile element : subsubordinateslist) {
            if (!newlist.contains(element))
                newlist.add(element);
        }
        Comparator<UserProfile> compareByrole = (UserProfile o2, UserProfile o1) ->
                o1.getRole().compareTo(o1.getRole());

        Comparator<UserProfile> compareByname = (UserProfile o1, UserProfile o2) ->
                o1.getDisplay_name().compareTo(o2.getDisplay_name());

        Collections.sort(newlist, compareByrole);
        Collections.sort(newlist,compareByname);
    }
}
