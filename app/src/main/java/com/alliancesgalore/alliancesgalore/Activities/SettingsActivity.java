package com.alliancesgalore.alliancesgalore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.alliancesgalore.alliancesgalore.Fragments.SettingsFragment;
import com.alliancesgalore.alliancesgalore.Fragments.SettingsPrefFragment;
import com.alliancesgalore.alliancesgalore.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setToolbar();
        setFragment();
    }

    private void setToolbar() {
        Toolbar mToolBar = findViewById(R.id.settings_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.settings_container, new SettingsFragment());
        ft.add(R.id.settings_container_pref,new SettingsPrefFragment());
        ft.commit();
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
