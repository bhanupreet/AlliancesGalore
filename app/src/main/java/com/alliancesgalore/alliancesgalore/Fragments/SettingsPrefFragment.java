package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alliancesgalore.alliancesgalore.R;


public class SettingsPrefFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
