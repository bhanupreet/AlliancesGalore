package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.alliancesgalore.alliancesgalore.Activities.AddEventActivity.selectedlist;
import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;

public class AddPeopleFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecycler;
    private UserProfileAdapter adapter;
    private List<UserProfile> mList, mSearchList = new ArrayList<>(), mAllList = new ArrayList<>();
    private Context mCtx;
    private ShimmerRecyclerView shimmerRecycler;
    private Button mSaveBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addpeople, container, false);
        mCtx = getContext();
        mList = new ArrayList<>();
        mList.clear();

        mRecycler = view.findViewById(R.id.addpeople_recycler);
        shimmerRecycler = view.findViewById(R.id.addPeople_recyclershimmer);
        mSaveBtn = view.findViewById(R.id.addPeople_savebtn);
//        if (selectedlist.isEmpty()) {
//            mSaveBtn.setVisibility(View.INVISIBLE);
//        } else
//            mSaveBtn.setVisibility(View.VISIBLE);
        mSaveBtn.setOnClickListener(this);
//        shimmerRecycler.showShimmerAdapter();
        setAdapter();
        Query query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users")
                .orderByChild("display_name");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mList.add(snapshot.getValue(UserProfile.class));

                    }

                    Collections.sort(mList, (t1, t2) -> t1.getDisplay_name().toLowerCase().compareTo(t2.getDisplay_name().toLowerCase()));
                    Collections.sort(mList, (t2, t1) -> t1.getSelected().compareTo(t2.getSelected()));

                    for (UserProfile profile : selectedlist) {
                        int i = mList.indexOf(profile);
                        mList.get(i).setSelected(true);

                    }
                    mList.remove(myProfile);
                    adapter.notifyDataSetChanged();
                    shimmerRecycler.hideShimmerAdapter();
                    mAllList.addAll(mList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter.addItemClickListener(i -> {
            mList.get(i).setSelected(!mList.get(i).getSelected());
//            adapter.notifyItemChanged(i);
            UserProfile profile = mList.get(i);

            if (selectedlist.contains(profile))
                selectedlist.remove(profile);
            else
                selectedlist.add(profile);
            adapter.notifyItemChanged(i);
//            if (selectedlist.isEmpty())
//                mSaveBtn.setVisibility(View.GONE);
//            else
//                mSaveBtn.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private void setAdapter() {

        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mCtx);
        mRecycler.setLayoutManager(layoutManager);
        adapter = new UserProfileAdapter(mCtx, mList);
        mRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {//                selectedlist.clear();
            Functions.toast("backbutton press", getContext());
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {

        // Inflate the options menu from XML
        inflater.inflate(R.menu.search_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(final String newText) {

                if (newText.equals("")) {
                    mList.clear();
                    mList.addAll(mAllList);

                } else {

                    mSearchList.clear();
                    for (UserProfile profile : mAllList)
                        if (!TextUtils.isEmpty(profile.getDisplay_name())
                                && profile.getDisplay_name().toLowerCase().contains(newText.toLowerCase())
                                && !mSearchList.contains(profile))
                            mSearchList.add(profile);

                    mList.clear();
                    mList.addAll(mSearchList);
                }
                Collections.sort(mList, (t1, t2) -> t1.getDisplay_name().toLowerCase().compareTo(t2.getDisplay_name().toLowerCase()));
                Collections.sort(mList, (t2, t1) -> t1.getSelected().compareTo(t2.getSelected()));
                adapter.notifyDataSetChanged();
                return true;
            }

            public boolean onQueryTextSubmit(final String newText) {

//                mAllList.addAll(mList);
//                if (newText.equals("")) {
//                    mList.clear();
//                    mList.addAll(mAllList);
//
//                } else {
//                    mSearchList.clear();
//                    for (UserProfile profile : mAllList) {
//                        if (!TextUtils.isEmpty(profile.getDisplay_name()) && profile.getDisplay_name().contains(newText)) {
//                            mSearchList.add(profile);
//                        }
//                    }
//                    mList.clear();
//                    mList.addAll(mSearchList);
//                }
//                adapter.notifyDataSetChanged();
//                return true;
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    @Override
    public void onClick(View view) {
        if (view == mSaveBtn) {
            FragmentManager fm = Objects
                    .requireNonNull(getActivity())
                    .getSupportFragmentManager();
            fm.popBackStack();
        }
    }
}
