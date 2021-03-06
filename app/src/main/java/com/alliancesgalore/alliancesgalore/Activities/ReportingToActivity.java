package com.alliancesgalore.alliancesgalore.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Global;
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

public class ReportingToActivity extends AppCompatActivity {
    private RecyclerView mRecycler;
    private Toolbar mToolbar;
    private List<UserProfile> mReportingToList;
    private int level;
    private UserProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_to);

        findIds();
        setToolBar();
        reportingToSwitch();
        query();
        setAdapter();
        recyclerClick();
    }

    private void setAdapter() {
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        adapter = new UserProfileAdapter(this, mReportingToList);
        mRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void query() {
        Query query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users")
                .orderByChild("level")
                .equalTo(level);

        query.keepSynced(true);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private void setToolBar() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Reporting To");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void findIds() {
        mRecycler = findViewById(R.id.ReportingTo_recycler);
        mToolbar = findViewById(R.id.reportingTo_toolbar);
        mReportingToList = new ArrayList<>();
    }

    private void reportingToSwitch() {
        switch (Global.myProfile.getRole().toLowerCase()) {
            case "team leader":
                level = 10;
                break;
            case "executive":
                level = 20;
                break;
            default:
                level = 0;
        }
    }

    private void recyclerClick() {
        adapter.addItemClickListener(pos -> {
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Users")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child("ReportingTo")
                    .setValue(mReportingToList.get(pos).getEmail());

            sendToMain();
        });
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(ReportingToActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mReportingToList.clear();
            if (dataSnapshot.exists())
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    mReportingToList.add(snapshot.getValue(UserProfile.class));
            Collections.sort(mReportingToList, (t1, t2) -> t1.getDisplay_name().toLowerCase().compareTo(t2.getDisplay_name().toLowerCase()));
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

}
