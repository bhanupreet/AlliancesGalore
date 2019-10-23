package com.alliancesgalore.alliancesgalore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.R;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileViewHolder> {


    private Context mCtx;
    private List<UserProfile> mUsersList;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListner mItemLongClickListner;

    public UserProfileAdapter(Context mCtx, List<UserProfile> mUsersList) {
        this.mCtx = mCtx;
        this.mUsersList = mUsersList;
    }


    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void addItemLongClickListener(ItemLongClickListner listner) {
        mItemLongClickListner = listner;
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.users_single_layout, parent, false);
        UserProfileViewHolder holder = new UserProfileViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        final UserProfile userProfile = mUsersList.get(position);
        holder.mDesignation.setText(userProfile.getRole());
        holder.mDisplayName.setText(userProfile.getDisplay_name());
        Glide.with(mCtx).load(userProfile.getImage()).placeholder(R.drawable.defaultprofile).into(holder.mProfileImage);
        holder.mTick.setVisibility(userProfile.getSelected() ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setOnLongClickListener(view -> {
            if (mItemLongClickListner != null) {
                mItemLongClickListner.onItemLongClick(position);
            }
            return false;
        });

        holder.itemView.setOnClickListener(view1 -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }

        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public void swap(int a, int b) {
        UserProfile aProfile = mUsersList.get(a), bProfile = mUsersList.get(b);
        mUsersList.set(a, bProfile);
        mUsersList.set(b, aProfile);
        Collections.sort(mUsersList.subList(1, mUsersList.size()), (t1, t2) -> t1.getDisplay_name().toLowerCase().compareTo(t2.getDisplay_name().toLowerCase()));
        Collections.sort(mUsersList.subList(1, mUsersList.size()), (t1, t2) -> t1.getLevel() - t2.getLevel());
    }
}