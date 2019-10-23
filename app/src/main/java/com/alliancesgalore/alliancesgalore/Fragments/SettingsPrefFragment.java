package com.alliancesgalore.alliancesgalore.Fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.alliancesgalore.alliancesgalore.R;


public class SettingsPrefFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
