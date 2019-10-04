package com.alliancesgalore.alliancesgalore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.Fragments.LocationFragment;
import com.alliancesgalore.alliancesgalore.Fragments.SettingsFragment;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;

public class MapActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private UserProfile selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setFragment();
        setmToolbar();
    }

    private void setFragment() {
        LocationFragment locationFragment = new LocationFragment();
        Bundle bundle = new Bundle();
        selected = (UserProfile) getIntent().getSerializableExtra("object");
        bundle.putSerializable("object", selected);
        locationFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.map_container, locationFragment);
        ft.commit();
    }

    private void setmToolbar() {
        mToolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
