package com.alliancesgalore.alliancesgalore.Utils;

import android.app.Activity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class FragFunctions extends Fragment {

    public static void setToolBarTitle(String title, View view){
        Activity activity = (Activity)view.getContext();
        Objects.requireNonNull(((AppCompatActivity) activity).getSupportActionBar()).setTitle(title);
    }
}
