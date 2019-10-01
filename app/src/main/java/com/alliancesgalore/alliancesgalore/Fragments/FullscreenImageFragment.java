package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.transition.TransitionInflater;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.Global;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;


public class FullscreenImageFragment extends Fragment {
    private PhotoView mFullscreenImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fullscreenimage, container, false);
        FindIds(view);
        LoadImage();
        return view;
    }

    private void FindIds(View view) {
        mFullscreenImage = view.findViewById(R.id.fullscreenimage);
    }

    private void LoadImage() {
        if (Global.myProfile.getImage() != null) {
            Picasso.get()
                    .load(Global.myProfile.getImage())
                    .placeholder(R.drawable.defaultprofile)
                    .into(mFullscreenImage);
        }
    }
}
