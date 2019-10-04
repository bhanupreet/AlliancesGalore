package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.Activities.MapActivity;
import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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
    private String mail;
    private Boolean isMultiselect = false;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locationlist, container, false);
        mRecycler = view.findViewById(R.id.locationlist_recycler);
        fab = view.findViewById(R.id.fab_locationlist);
        fab.hide();
        allsubordinatesList = new ArrayList<>();
        subordinatesList = new ArrayList<>();
        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("display_name");
        query.keepSynced(true);
        query.addValueEventListener(valueEventListener);

        adapter = new UserProfileAdapter(getContext(), subordinatesList);
        mRecycler.setAdapter(adapter);
        setAdapter(mRecycler);
        adapter.notifyDataSetChanged();
        itemClick();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            allsubordinatesList.clear();
            subordinatesList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    allsubordinatesList.add(snapshot.getValue(UserProfile.class));
            }
            fetch(mail);
            for (UserProfile profile1 : subordinatesList)
                fetch(profile1.getEmail());

            if (subordinatesList.isEmpty())
                subordinatesList.add(myProfile);

            adapter.notifyDataSetChanged();
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

    private void itemClick() {

        adapter.setClickListener(view -> {
            int pos = mRecycler.indexOfChild(view);
            if (isMultiselect) {
                //if multiple selection is enabled then select item on single click else perform normal click on item.
                multiSelect(pos);
            } else {
                Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                UserProfile selected = subordinatesList.get(pos);
                mapIntent.putExtra("object", selected);
                startActivity(mapIntent);
            }
        });

        adapter.setLongClickListener(view -> {
            int pos = mRecycler.indexOfChild(view);
            UserProfile selected = subordinatesList.get(pos);
            Toast.makeText(getContext(), selected.getDisplay_name(), Toast.LENGTH_SHORT).show();

            return false;
        });
    }

    private void multiSelect(int pos) {
        fab.show();
        UserProfile profile = subordinatesList.get(pos);
        if (profile.getSelected()) {
            profile.setSelected(false);
        } else
            profile.setSelected(true);
    }


    private void fetch(String email) {
        for (UserProfile profile : allsubordinatesList)
            if (profile.getReportingTo().equals(email) && !subordinatesList.contains(profile))
                subordinatesList.add(profile);
    }
}