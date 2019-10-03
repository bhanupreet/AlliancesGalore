package com.alliancesgalore.alliancesgalore.Adapters;

import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.R;

import de.hdodenhof.circleimageview.CircleImageView;

class UserProfileViewHolder extends RecyclerView.ViewHolder {
    TextView mDesignation,mDisplayName;
    CircleImageView mProfileImage;

    public UserProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        mDisplayName = itemView.findViewById(R.id.users_single_displayname);
        mDesignation = itemView.findViewById(R.id.users_Single_designation);
        mProfileImage = itemView.findViewById(R.id.users_single_profile_image);
    }
}
