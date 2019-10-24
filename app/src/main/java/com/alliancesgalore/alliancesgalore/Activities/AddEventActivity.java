package com.alliancesgalore.alliancesgalore.Activities;

import android.icu.lang.UScript;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alliancesgalore.alliancesgalore.Fragments.AddEventFragment;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;

import java.util.ArrayList;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {
    private static List<UserProfile> selectedlist = new ArrayList<>();

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        SetmToolBar();
        SetFragment();
    }

    private void SetFragment() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.AddEvent_container, new AddEventFragment());
        ft.commit();
    }


    private void SetmToolBar() {
        mToolBar = findViewById(R.id.addEvent_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Event");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    public static List<UserProfile> getList() {
        return selectedlist;
    }

    public static void setSelectedlist(List<UserProfile> selectedlist1) {
        selectedlist = selectedlist1;
    }
}
