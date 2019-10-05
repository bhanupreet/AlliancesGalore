package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.Activities.MapActivity;
import com.alliancesgalore.alliancesgalore.Activities.ReportingToActivity;
import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.alliancesgalore.alliancesgalore.Utils.RecyclerItemClickListener;
import com.firebase.ui.auth.data.model.User;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import mva2.adapter.ListSection;
import mva2.adapter.MultiViewAdapter;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class LocationListFragment extends Fragment {
    ActionMode mActionMode;
    Menu context_menu;
    private RecyclerView mRecycler;
    private UserProfileAdapter adapter;
    private List<UserProfile> allsubordinatesList, subordinatesList, multiselect_list;
    private String mail;
    private Boolean isMultiselect = false;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locationlist, container, false);
        if (myProfile != null && TextUtils.isEmpty(myProfile.getReportingTo())) {
            sendToReport();
        }
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
        if (myProfile != null && TextUtils.isEmpty(myProfile.getReportingTo())) {
            sendToReport();
        }
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            allsubordinatesList.clear();
            subordinatesList.clear();
            if (dataSnapshot.exists()) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                for (DataSnapshot next : snapshotIterator) {
                    allsubordinatesList.add(next.getValue(UserProfile.class));
                }

                if (mail.equals("superadmin@gmail.com")) {
                    subordinatesList.clear();
                    subordinatesList.addAll(allsubordinatesList);
                    Collections.sort(subordinatesList, (t1, t2) -> t1.getLevel() - t2.getLevel());
                } else if (myProfile != null) {
                    fetch(mail);
                    for (UserProfile profile1 : subordinatesList)
                        fetch(profile1.getEmail());
                    if (subordinatesList.isEmpty())
                        subordinatesList.add(myProfile);
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

    private void itemClick() {

        adapter.setClickListener(view -> {
            int pos = mRecycler.indexOfChild(view);

            Intent mapIntent = new Intent(getActivity(), MapActivity.class);
            UserProfile selected = subordinatesList.get(pos);
            adapter.swap(pos, 0);
            mapIntent.putExtra("object", selected);
            mapIntent.putParcelableArrayListExtra("objectlist", (ArrayList<? extends Parcelable>) subordinatesList);
            startActivity(mapIntent);
        });

    }

    private void multiSelect(int pos) {
        fab.show();
        UserProfile selected = subordinatesList.get(pos);
        selected.setSelected(!selected.getSelected());
    }


    private void fetch(String email) {
        for (UserProfile profile : allsubordinatesList)
            if (!TextUtils.isEmpty(profile.getReportingTo()) && profile.getReportingTo().equals(email) && !subordinatesList.contains(profile))
                subordinatesList.add(profile);

    }

    private void sendToReport() {
        Intent startIntent = new Intent(getActivity(), ReportingToActivity.class);
        startActivity(startIntent);
        Objects.requireNonNull(getActivity()).finish();
    }
}