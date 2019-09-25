package com.alliancesgalore.alliancesgalore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.alliancesgalore.alliancesgalore.Fragments.CRMfragment;
import com.alliancesgalore.alliancesgalore.Fragments.LocationFragment;
import com.alliancesgalore.alliancesgalore.Adapters.MainActivityAdapter;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Fragments.RemindersFragment;
import com.google.android.material.tabs.TabLayout;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private MainActivityAdapter adapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        mViewPager = findViewById(R.id.main_viewpager);
        mTabLayout = findViewById(R.id.main_tablayout);
        mToolbar = findViewById(R.id.main_app_bar);

        setmToolbar(mToolbar," Alliances Galore", R.mipmap.ic_launcher);

        adapter = new MainActivityAdapter(getSupportFragmentManager());

        adapter.addFragment(new CRMfragment(),"CRM");
        adapter.addFragment(new LocationFragment(),"Location");
        adapter.addFragment(new RemindersFragment(),"Reminders");

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setmToolbar(Toolbar mToolbar, String title, int Resid){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setLogo(Resid);

    }
}
