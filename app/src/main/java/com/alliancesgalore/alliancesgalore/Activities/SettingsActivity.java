package com.alliancesgalore.alliancesgalore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.alliancesgalore.alliancesgalore.Fragments.ProfileFragment;
import com.alliancesgalore.alliancesgalore.Fragments.SettingsFragment;
import com.alliancesgalore.alliancesgalore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private String mProfileImageString,mDesignationString,mDisplayNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setToolbar();
        setFragment();
    }

    private void setToolbar() {
        mToolBar = findViewById(R.id.settings_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFragment() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.settings_container, new SettingsFragment());
        ft.commit();

//
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }




}
