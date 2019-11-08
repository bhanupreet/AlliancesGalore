package com.alliancesgalore.alliancesgalore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.alliancesgalore.alliancesgalore.Fragments.EventFragment;
import com.alliancesgalore.alliancesgalore.Models.CustomEvent;
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Functions;

import java.util.ArrayList;
import java.util.List;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;

public class EventActivity extends AppCompatActivity {
    private Toolbar mToolbar;
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

        event = getIntent().getParcelableExtra("object");

        mList = getIntent().getParcelableArrayListExtra("objectlist");

        mToolbar = findViewById(R.id.mEvent_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(event.getTitle());


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
                Functions.toast(getEvent().getUid(), EventActivity.this);
                Intent editintent = new Intent(EventActivity.this, AddEventActivity.class);
                editintent.putExtra("isedit", "true");
                editintent.putParcelableArrayListExtra("objectlist", (ArrayList<? extends Parcelable>) mSelectedList);
                editintent.putExtra("object", event);
                startActivity(editintent);
                return true;
            case R.id.event_delete:
                Functions.toast("Event delete", EventActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
