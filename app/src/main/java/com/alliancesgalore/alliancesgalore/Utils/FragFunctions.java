package com.alliancesgalore.alliancesgalore.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class FragFunctions extends Fragment {

    public static void setToolBarTitle(String title, View view){
        Activity activity = (Activity)view.getContext();
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
    }
    public static void LoadImage(Context mContext,String url,ImageView imageView){
        Glide.with(mContext)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }
}
