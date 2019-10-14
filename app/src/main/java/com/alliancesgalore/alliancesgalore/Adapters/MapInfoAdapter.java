package com.alliancesgalore.alliancesgalore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alliancesgalore.alliancesgalore.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;
    private Context mCtx;
    private String imgURl;

    public MapInfoAdapter(Context mCtx, String imgURl) {
        this.mCtx = mCtx;
        this.imgURl = imgURl;
        myContentsView = LayoutInflater.from(mCtx).inflate(R.layout.custom_map_info_window, null);
    }


    @Override
    public View getInfoWindow(Marker marker) {
        TextView mTitle = myContentsView.findViewById(R.id.map_info_name);
        TextView mSnippet = myContentsView.findViewById(R.id.map_info_lastupdated);
        CircleImageView profileimage = myContentsView.findViewById(R.id.map_info_profileimage);

        mTitle.setText(marker.getTitle());
        mSnippet.setText(marker.getSnippet());
        Picasso.get().load(imgURl).placeholder(R.drawable.defaultprofile).into(profileimage);
        return myContentsView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
