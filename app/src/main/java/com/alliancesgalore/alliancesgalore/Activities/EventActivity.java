package com.alliancesgalore.alliancesgalore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.alliancesgalore.alliancesgalore.Fragments.EventFragment;
import com.alliancesgalore.alliancesgalore.Models.CustomEvent;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;

public class EventActivity extends AppCompatActivity {
    private List<UserProfile> mList = new ArrayList<>();
    public static List<UserProfile> mSelectedList = new ArrayList<>();
    private static CustomEvent event;

    public static CustomEvent getEvent() {
        return event;
    }

    public static void setEvent(CustomEvent event) {
        EventActivity.event = event;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mList.clear();
        mSelectedList.clear();
        event = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getIntentVariables();
        setToolbar();
        setFragment();

    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        EventFragment fragment = new EventFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("objectlist", (ArrayList<? extends Parcelable>) mList);
        fragment.setArguments(bundle);
        fm.beginTransaction()
                .add(R.id.event_container, fragment)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commit();
    }

    private void getIntentVariables() {
        event = getIntent().getParcelableExtra("object");
        mList = getIntent().getParcelableArrayListExtra("objectlist");
    }

    private void setToolbar() {
        Toolbar mToolbar = findViewById(R.id.mEvent_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(event.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (myProfile != null) {
            if (!TextUtils.isEmpty(myProfile.getEmail()) && myProfile.getEmail().equals(event.getCreatedBy())) {
                getMenuInflater().inflate(R.menu.event_options, menu);
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_edit:
                editEvent();
                return true;
            case R.id.event_delete:
              deleteEventDialog();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteEventDialog() {
        AlertDialog.Builder builder = new AlertDialog
                .Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete the selected event?")
                .setPositiveButton("Yes", (dialogInterface, i) -> deleteEvent())
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void editEvent() {
        Functions.toast(getEvent().getUid(), EventActivity.this);
        Intent editintent = new Intent(EventActivity.this, AddEventActivity.class);
        editintent.putExtra("isedit", "true");
        editintent.putParcelableArrayListExtra("objectlist", (ArrayList<? extends Parcelable>) mSelectedList);
        editintent.putExtra("object", event);
        startActivity(editintent);
    }

    private void deleteEvent() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("CalendarEvents")
                .child(event.getUid())
                .removeValue();

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("EventParticipants")
                .child(event.getUid())
                .removeValue();

        for (UserProfile profile : mSelectedList) {
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("MyEvents")
                    .child(Functions.encodeUserEmail(profile.getEmail()))
                    .child(event.getUid())
                    .removeValue();
        }

        Intent intent = new Intent(EventActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        Functions.toast("Event deleted", EventActivity.this);

    }


}
