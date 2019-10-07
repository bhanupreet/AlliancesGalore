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
import com.alliancesgalore.alliancesgalore.Adapters.ItemClickListener;
import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.alliancesgalore.alliancesgalore.Utils.RecyclerItemClickListener;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
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
import java.util.concurrent.CopyOnWriteArrayList;

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
    private ShimmerRecyclerView shimmerRecycler;
    private Boolean isMultiselect = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locationlist, container, false);
        if (myProfile != null && TextUtils.isEmpty(myProfile.getReportingTo())) {
            sendToReport();
        }
        shimmerRecycler = view.findViewById(R.id.locationlist_recyclershimmer);
        shimmerRecycler.showShimmerAdapter();
        mRecycler = view.findViewById(R.id.locationlist_recycler);
        allsubordinatesList = new ArrayList<>();
        multiselect_list = new ArrayList<>();
        subordinatesList = new CopyOnWriteArrayList<>();
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
                Collections.sort(subordinatesList, (t1, t2) -> t1.getDisplay_name().compareTo(t2.getDisplay_name()));
                Collections.sort(subordinatesList, (t1, t2) -> t1.getLevel() - (t2.getLevel()));
                adapter.notifyDataSetChanged();
                shimmerRecycler.hideShimmerAdapter();
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
        adapter.addItemClickListener(pos -> {
            if (isMultiselect) {
                UserProfile selectedprofile = subordinatesList.get(pos);
                selectedprofile.setSelected(!selectedprofile.getSelected());
                Functions.toast(selectedprofile.getDisplay_name() + selectedprofile.getSelected(), getContext());
                adapter.notifyDataSetChanged();
            } else {
                Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                UserProfile selected = subordinatesList.get(pos);
                adapter.swap(pos, 0);
                mapIntent.putExtra("object", selected);
                List<UserProfile> temp = new ArrayList<>();
                temp.clear();
                temp.addAll(subordinatesList);
                mapIntent.putParcelableArrayListExtra("objectlist", (ArrayList<? extends Parcelable>) temp);
                startActivity(mapIntent);
                MainActivity mainActivity = (MainActivity) getActivity();

            }
        });
//        adapter.setClickListener(view -> {
//            int pos = mRecycler.indexOfChild(view);
//            if (isMultiselect) {
//                UserProfile selectedprofile = subordinatesList.get(pos);
//                selectedprofile.setSelected(!selectedprofile.getSelected());
//                Functions.toast(selectedprofile.getDisplay_name() + selectedprofile.getSelected(), getContext());
//                adapter.notifyDataSetChanged();
//            } else {
//                Intent mapIntent = new Intent(getActivity(), MapActivity.class);
//                UserProfile selected = subordinatesList.get(pos);
//                adapter.swap(pos, 0);
//                mapIntent.putExtra("object", selected);
//                List<UserProfile> temp = new ArrayList<>();
//                temp.clear();
//                temp.addAll(subordinatesList);
//                mapIntent.putParcelableArrayListExtra("objectlist", (ArrayList<? extends Parcelable>) temp);
//                startActivity(mapIntent);
//                MainActivity mainActivity = (MainActivity) getActivity();
//
//            }
//
//        });

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

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist_add_check_black_24dp, getContext().getTheme()));
        mainActivity.fab.setOnClickListener(v -> isMultiselect = !isMultiselect);
    }
}