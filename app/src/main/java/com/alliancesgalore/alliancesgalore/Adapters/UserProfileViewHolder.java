package com.alliancesgalore.alliancesgalore.Adapters;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;

import de.hdodenhof.circleimageview.CircleImageView;
import mva2.adapter.ItemViewHolder;

public class UserProfileViewHolder extends ItemViewHolder<UserProfile> {
    TextView mDesignation, mDisplayName;
    CircleImageView mProfileImage;

    public UserProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        mDisplayName = itemView.findViewById(R.id.users_single_displayname);
        mDesignation = itemView.findViewById(R.id.users_Single_designation);
        mProfileImage = itemView.findViewById(R.id.users_single_profile_image);
    }
}
