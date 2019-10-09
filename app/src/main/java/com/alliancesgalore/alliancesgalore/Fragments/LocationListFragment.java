package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;


public class LocationListFragment extends Fragment {
    private RecyclerView mRecycler;
    private UserProfileAdapter adapter;
    private List<UserProfile> allsubordinatesList;
    private List<UserProfile> subordinatesList;
    private List<UserProfile> multiselect_list;
    private String mail;
    private ShimmerRecyclerView shimmerRecycler;
    private Boolean isMultiselect = false;
    private ArrayList<UserProfile> temp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locationlist, container, false);

        ReportingToCheck();
        FindIds(view);
        query();
        settingadapter(subordinatesList);
        itemClick(subordinatesList);
        fabclick();
        return view;
    }

    private void fabclick() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.fab.setOnClickListener(v -> {
                    temp.clear();
                    temp.addAll(subordinatesList);
                    isMultiselect = !isMultiselect;
                    if (mActionmode == null) {
                        startActionMode(mainActivity);

                    } else {
                        resetActionMode();
                    }
                }
        );
    }

    private void startActionMode(MainActivity mainActivity) {
        mainActivity.mToolbar.startActionMode(actionMode);
        settingadapter(temp);
        adapter.addItemClickListener(pos -> {
            if (isMultiselect) {
                UserProfile selectedprofile = temp.get(pos);

                setSelectedTick(selectedprofile);
                setActionModeTitle();

                Functions.toast(selectedprofile.getDisplay_name() + " added", getContext());

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setSelectedTick(UserProfile selectedprofile) {
        selectedprofile.setSelected(!selectedprofile.getSelected());
        if (multiselect_list.contains(selectedprofile))
            multiselect_list.remove(selectedprofile);
        else
            multiselect_list.add(selectedprofile);
    }

    private void settingadapter(List<UserProfile> subordinatesList) {
        adapter = new UserProfileAdapter(getContext(), subordinatesList);
        mRecycler.setAdapter(adapter);
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
    }

    private void query() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("display_name");
        query.keepSynced(true);
        query.addValueEventListener(valueEventListener);
    }

    private void ReportingToCheck() {
        if (myProfile != null && TextUtils.isEmpty(myProfile.getReportingTo()))
            sendToReport();
    }

    private void FindIds(View view) {
        shimmerRecycler = view.findViewById(R.id.locationlist_recyclershimmer);
        shimmerRecycler.showShimmerAdapter();
        mRecycler = view.findViewById(R.id.locationlist_recycler);
        allsubordinatesList = new ArrayList<>();
        multiselect_list = new ArrayList<>();
        multiselect_list.clear();
        temp = new ArrayList<>();
        subordinatesList = new CopyOnWriteArrayList<>();
        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (myProfile != null && TextUtils.isEmpty(myProfile.getReportingTo()))
            sendToReport();

    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            allsubordinatesList.clear();
            subordinatesList.clear();

            if (dataSnapshot.exists()) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                for (DataSnapshot next : snapshotIterator)
                    allsubordinatesList.add(next.getValue(UserProfile.class));

                if (mail.equals("superadmin@gmail.com")) {
                    subordinatesList.clear();
                    subordinatesList.addAll(allsubordinatesList);
                    sort(subordinatesList);

                } else if (myProfile != null) {
                    fetch(mail);
                    for (UserProfile profile1 : subordinatesList)
                        fetch(profile1.getEmail());

                    if (subordinatesList.isEmpty())
                        subordinatesList.add(myProfile);
                }

                sort(subordinatesList);

                adapter.notifyDataSetChanged();
                shimmerRecycler.hideShimmerAdapter();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void sort(List<UserProfile> subordinatesList) {
        Collections.sort(subordinatesList, (t1, t2) -> t1.getDisplay_name().compareTo(t2.getDisplay_name()));
        Collections.sort(subordinatesList, (t1, t2) -> t1.getLevel() - (t2.getLevel()));
    }

    private void itemClick(List<UserProfile> subordinatesList) {
        adapter.addItemClickListener(pos -> {
            if (!isMultiselect) {
                UserProfile selected = subordinatesList.get(pos);
                adapter.swap(pos, 0);
                sendToMap(selected, subordinatesList);
            }
        });
    }

    private void sendToMap(UserProfile selected, List<UserProfile> subordinatesList) {
        Intent mapIntent = new Intent(getActivity(), MapActivity.class);
        mapIntent.putExtra("object", selected);
        List<UserProfile> temp = new ArrayList<>();
        temp.clear();
        temp.addAll(subordinatesList);
        mapIntent.putParcelableArrayListExtra("objectlist", (ArrayList<? extends Parcelable>) temp);
        startActivity(mapIntent);
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
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.selection_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mActionmode != null) {
            resetActionMode();
        }

        if (!getUserVisibleHint()) {
            return;
        }
        SetFAB();
        fabclick();

    }

    private void SetFAB() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist_add_check_black_24dp, getContext().getTheme()));
    }

    private void resetActionMode() {
        isMultiselect = false;
        mActionmode.finish();

        for (UserProfile profile : subordinatesList)
            profile.setSelected(false);

        multiselect_list.clear();
        sort(subordinatesList);
        settingadapter(subordinatesList);
        itemClick(subordinatesList);
        adapter.notifyDataSetChanged();
    }

    private ActionMode mActionmode = null;

    private ActionMode.Callback actionMode = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.selection_menu, menu);
            mActionmode = actionMode;
            setActionModeTitle();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_cancel:
                    resetActionMode();
                    break;

                case R.id.action_select:

                    if (!multiselect_list.isEmpty())
                        sendToMap(multiselect_list.get(0), multiselect_list);
                    else
                        resetActionMode();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionmode = null;
        }
    };

    private void setActionModeTitle() {
        if (multiselect_list.isEmpty())
            mActionmode.setTitle("Select");
        else
            mActionmode.setTitle("Selected: " + multiselect_list.size());
    }
}