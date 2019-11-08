package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;

import java.util.ArrayList;
import java.util.List;

public class EventPeopleFragment extends Fragment {

    private RecyclerView mRecycler;
    private UserProfileAdapter adapter;
    private List<UserProfile> mList = new ArrayList<>();
    private Context mCtx;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_eventpeople, container, false);
        mCtx = getContext();
        mList = new ArrayList<>();
        mList.clear();

        Bundle bundle = getArguments();
        mList = bundle.getParcelableArrayList("objectlist");
        String title = bundle.getString("title");
        FragFunctions.setToolBarTitle(title, view);

        mRecycler = view.findViewById(R.id.eventpeople_recycler);

        adapter = new UserProfileAdapter(mCtx, mList);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(mCtx));
        mRecycler.setAdapter(adapter);

        return view;
    }
}
