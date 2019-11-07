package com.alliancesgalore.alliancesgalore.Activities;

import android.graphics.Color;
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
    public static List<UserProfile> selectedlist = new ArrayList<>();

    private Toolbar mToolBar;
    private static String mDescription = "";
    private static String mLocation = "";
    private static String mTitle = "";


    private static int color = Color.GREEN;
    private static long mDate, mTime;
    private static boolean mAlldaySwitch;

    public static int getColor() {
        return color;
    }

    public static void setColor(int color) {
        AddEventActivity.color = color;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        SetmToolBar();
        SetFragment();
        selectedlist.clear();
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
        getSupportActionBar().setTitle("Add CustomEvent");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        selectedlist.clear();
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

    public static String getDescription() {
        return mDescription;
    }

    public static void setDescription(String desc) {
        mDescription = desc;
    }

    public static String getLocation() {
        return mLocation;
    }

    public static void setLocation(String loc) {
        mLocation = loc;
    }

    public static void setDate(long date) {
        mDate = date;
    }

    public static long getDate() {
        return mDate;
    }

    public static void setTime(long time) {
        mTime = time;
    }

    public static long getTime() {
        return mTime;
    }

    public static void setmAlldaySwitch(boolean alldaySwitch) {
        mAlldaySwitch = alldaySwitch;
    }

    public static boolean getmAllDaySwitch() {
        return mAlldaySwitch;
    }

    public static String getmTitle() {
        return mTitle;
    }

    public static void setmTitle(String title) {
        mTitle = title;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        try {
            Runtime.getRuntime().gc();
            finish();
            setmTitle("");
            setmAlldaySwitch(false);
            setTime(0);
            setDate(0);
            setLocation("");
            setDescription("");
            selectedlist.clear();
            setColor(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
