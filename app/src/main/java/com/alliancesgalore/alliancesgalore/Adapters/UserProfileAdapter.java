package com.alliancesgalore.alliancesgalore.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mva2.adapter.ItemBinder;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileViewHolder> {

    private Context mCtx;
    private List<UserProfile> mUsersList;
    private View.OnClickListener mClickListener;
    private View.OnLongClickListener mLongClickListener;

    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }

    public void setLongClickListener(View.OnLongClickListener callback) {
        mLongClickListener = callback;
    }

    public UserProfileAdapter(Context mCtx, List<UserProfile> mUsersList) {
        this.mCtx = mCtx;
        this.mUsersList = mUsersList;
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.users_single_layout, parent, false);
        UserProfileViewHolder holder = new UserProfileViewHolder(view);
        holder.itemView.setOnClickListener(holderClickLisener);
        holder.itemView.setOnLongClickListener(holderLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        final UserProfile userProfile = mUsersList.get(position);
        holder.mDesignation.setText(userProfile.getRole());
        holder.mDisplayName.setText(userProfile.getDisplay_name());
        Glide.with(mCtx).load(userProfile.getImage()).placeholder(R.drawable.defaultprofile).into(holder.mProfileImage);
        holder.itemView.setBackgroundColor(userProfile.getSelected() ? Color.CYAN : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    private View.OnClickListener holderClickLisener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mClickListener.onClick(view);
        }
    };
    private View.OnLongClickListener holderLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mLongClickListener.onLongClick(view);
            return true;
        }
    };

    public void swap(int a, int b) {
        UserProfile aProfile = mUsersList.get(a), bProfile = mUsersList.get(b);
        mUsersList.set(a, bProfile);
        mUsersList.set(b, aProfile);
        Collections.sort(mUsersList.subList(1, mUsersList.size()), (t1, t2) -> t1.getDisplay_name().compareTo(t2.getDisplay_name()));
        Collections.sort(mUsersList.subList(1,mUsersList.size()),(t1,t2)-> t1.getLevel()-t2.getLevel());

    }
}