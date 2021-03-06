package com.alliancesgalore.alliancesgalore.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alliancesgalore.alliancesgalore.Fragments.AddEventFragment;
import com.alliancesgalore.alliancesgalore.Models.CustomEvent;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddEventActivity extends AppCompatActivity {
    public static List<UserProfile> selectedlist = new ArrayList<>(), mOldList;
    private static long mDate, mTime;
    private static boolean mAlldaySwitch;
    private static String mDescription = "";
    private static String mLocation = "";
    private static String mTitle = "";
    public static String isEdit = "true", key;
    private static int color = Color.GREEN, mRepeat = 0, mNotify = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setmToolBar();

        selectedlist.clear();

        isEdit = getIntent().getStringExtra("isedit");
//        Functions.toast(isEdit, AddEventActivity.this);
        if (!TextUtils.isEmpty(isEdit) && isEdit.equalsIgnoreCase("true")) {
            selectedlist = getIntent().getParcelableArrayListExtra("objectlist");
            assert selectedlist != null;
            mOldList = new ArrayList<>(selectedlist);
//            mOldList = getIntent().getParcelableExtra("objectlist");
            CustomEvent event = getIntent().getParcelableExtra("object");

            assert event != null;
            mDescription = event.getDescription();
            mLocation = event.getLocation();
            mTitle = event.getTitle();
            color = event.getColor();
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(event.getDateTime());
            date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            mDate = date.getTimeInMillis();

            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(event.getDateTime());
            time.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), 0);

            mTime = time.getTimeInMillis();
            mAlldaySwitch = event.isAllDay();
            key = event.getUid();
//            setmNotify(event.getNotify());
            setmRepeat(event.getRepetition());
        }
        setFragment();
//        if (isEdit) {

//
//        }
    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.AddEvent_container, new AddEventFragment());
        ft.commit();
    }

    private void setmToolBar() {
        Toolbar mToolBar = findViewById(R.id.addEvent_toolbar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    public static List<UserProfile> getList() {
        return selectedlist;
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

    public static int getmRepeat() {
        return mRepeat;
    }

    public static void setmRepeat(int mRepeat) {
        AddEventActivity.mRepeat = mRepeat;
    }

    public static int getmNotify() {
        return mNotify;
    }

    public static void setmNotify(int mNotify) {
        AddEventActivity.mNotify = mNotify;
    }

    public static int getColor() {
        return color;
    }

    public static void setColor(int color) {
        AddEventActivity.color = color;
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
            setColor(Color.GREEN);
            mOldList.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
