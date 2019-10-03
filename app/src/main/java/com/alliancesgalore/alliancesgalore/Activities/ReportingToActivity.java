package com.alliancesgalore.alliancesgalore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Adapters.UserProfileAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ReportingToActivity extends AppCompatActivity {
    private RecyclerView mRecycler;
    private Toolbar mToolbar;
    private List<UserProfile> mReportingToList;
    private int level;
    private UserProfileAdapter adapter;
    private String toastLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_to);
        FindIds();
        setToolBar();
        ReportingToSwitch();
        serAdapter(mRecycler);
        query();
        adapter = new UserProfileAdapter(this, mReportingToList);
        mRecycler.setAdapter(adapter);
        RecyclerClick();
    }

    private void query() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users");
        query.keepSynced(true);
        query.addValueEventListener(valueEventListener);
    }

    private void serAdapter(RecyclerView mRecycler) {
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);


    }

    private void setToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reporting To");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    private void FindIds() {
        mRecycler = findViewById(R.id.ReportingTo_recycler);
        mToolbar = findViewById(R.id.reportingTo_toolbar);
        mReportingToList = new ArrayList<>();
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mReportingToList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile getProfile = snapshot.getValue(UserProfile.class);
//                    Toast.makeText(ReportingToActivity.this, toastLevel, Toast.LENGTH_SHORT).show();
                    if (getProfile.getLevel() == level) {
//                    if (getProfile.getLevel().equals(ReportingTo)) {
//                        Toast.makeText(ReportingToActivity.this, getProfile.getDisplay_name() + " " + getProfile.getLevel(), Toast.LENGTH_SHORT).show();
                        mReportingToList.add(getProfile);
//                    }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void ReportingToSwitch() {
//        Toast.makeText(ReportingToActivity.this, Global.myProfile.getRole(), Toast.LENGTH_SHORT).show();
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
        toastLevel = String.valueOf(level);
    }

    private void RecyclerClick() {
        adapter.setClickListener(adapterClickListener);
        adapter.setLongClickListener(adapterLongClickListener);
    }

    private View.OnClickListener adapterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = mRecycler.indexOfChild(view);
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("ReportingTo").setValue(mReportingToList.get(pos).getEmail());
            sendToMain();
        }
    };

    private void sendToMain() {
        Intent mainIntent = new Intent(ReportingToActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private View.OnLongClickListener adapterLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            int pos = mRecycler.indexOfChild(view);
            return false;
        }
    };
}
