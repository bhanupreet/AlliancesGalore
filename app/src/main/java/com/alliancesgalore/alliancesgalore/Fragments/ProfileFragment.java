package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;


public class ProfileFragment extends Fragment {

    private ImageView mChangeNameBtn;
    private ImageButton mChangePhotoBtn;
    private TextView mEmail, mDesignation, mDisplayName;
    private CircleImageView mProfileImage;
    private StorageReference mImageStorage;
    private ProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FragFunctions.setToolBarTitle("Profile", view);
        setHasOptionsMenu(true);
        FindIds(view);
        changePhotoClick();
        setDetails();
        mProfileImageClick();
        editNameBtn();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            showProgressbar();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                UploadTask uploadTask = uploadToDatabase(resultUri);
                uploadTask.addOnCompleteListener(uploadOnComplete);
            } else
                hideProgressbar();

        } else
            Toast.makeText(getContext(), "Error uploading File", Toast.LENGTH_SHORT).show();
//        mProgress.setVisibility(View.INVISIBLE);

    }

    private void hideProgressbar() {
        mProgress.setVisibility(View.GONE);
        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void showProgressbar() {
        mProgress.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (Objects
                    .requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .getBackStackEntryCount() != 0) {

                getActivity()
                        .getSupportFragmentManager()
                        .popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void LoadImage() {
        if (myProfile.getImage() != null) {
            Picasso.get()
                    .load(myProfile.getImage())
                    .placeholder(R.drawable.defaultprofile)
                    .into(mProfileImage);
        }
    }

    private void setDetails() {
        LoadImage();
        mDisplayName.setText(myProfile.getDisplay_name());
        mEmail.setText(myProfile.getEmail());
        mDesignation.setText(myProfile.getRole());
    }

    private void FindIds(View view) {
        mProfileImage = view.findViewById(R.id.profile_displayImage);
        mDisplayName = view.findViewById(R.id.profile_displayName);
        mDesignation = view.findViewById(R.id.profile_display_Designation);
        mChangeNameBtn = view.findViewById(R.id.profile_editnamebtn);
        mEmail = view.findViewById(R.id.profile_display_Email);
        mChangePhotoBtn = view.findViewById(R.id.profile_changePhoto);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mProgress = view.findViewById(R.id.profile_progress);
    }

    private void editNameBtn() {
        mChangeNameBtn.setOnClickListener(editnameOnClick);

    }

    private void mProfileImageClick() {
        mProfileImage.setOnClickListener(mProfileImageOnClick);
    }

    private void changePhotoClick() {
        mChangePhotoBtn.setOnClickListener(changePhotoOnClick);
    }

    private UploadTask uploadToDatabase(Uri resultUri) {
        File thumb_filepath = new File(resultUri.getPath());
        Bitmap thumb_bitmap = null;
        try {
            thumb_bitmap = new Compressor(getContext())
                    .setMaxWidth(512)
                    .setMaxHeight(512)
                    .setQuality(70)
                    .compressToBitmap(thumb_filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assert thumb_bitmap != null;
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] thumb_byte = baos.toByteArray();

        StorageReference filepath = mImageStorage.child("Profile_Images").child(FirebaseAuth.getInstance().getUid() + ".jpg");
        return filepath.putBytes(thumb_byte);
    }

    private View.OnClickListener editnameOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ChangeNameFragment changeNameFragment = new ChangeNameFragment();
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(mDisplayName, Objects.requireNonNull(ViewCompat.getTransitionName(mDisplayName)))
                    .addToBackStack("profile")
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.settings_container, changeNameFragment)
                    .commit();
        }
    };

    private View.OnClickListener changePhotoOnClick = view -> {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Objects.requireNonNull(getContext()), ProfileFragment.this);
       hideProgressbar();

    };

    private OnCompleteListener<UploadTask.TaskSnapshot> uploadOnComplete = new OnCompleteListener<UploadTask.TaskSnapshot>() {

        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "URL got successfully", Toast.LENGTH_SHORT).show();

                mImageStorage.child("Profile_Images").child(FirebaseAuth.getInstance().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(downloadURLsuccess);
            } else
                Functions.toast(task);
        }
    };

    private OnSuccessListener<Uri> downloadURLsuccess = new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            Toast.makeText(getContext(), "URL got successfully", Toast.LENGTH_SHORT).show();

            String downloadurl = uri.toString();
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference myRef = database
                    .getReference()
                    .child("Users")
                    .child(Objects.requireNonNull(FirebaseAuth
                            .getInstance()
                            .getUid()));

            HashMap<String, Object> result = new HashMap<>();
            result.put("image", downloadurl);
            myProfile.setImage(downloadurl);
            myRef.updateChildren(result).addOnCompleteListener(updateDatabaseOnComplete);
        }
    };

    private OnCompleteListener updateDatabaseOnComplete = task -> {
        if (task.isSuccessful()) {
            Toast.makeText(getContext(), "image uploaded successfully", Toast.LENGTH_SHORT).show();
        } else
            Functions.toast(task);
       hideProgressbar();
    };

    private View.OnClickListener mProfileImageOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FullscreenImageFragment fullscreenImageFragment = new FullscreenImageFragment();
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(mProfileImage, Objects.requireNonNull(ViewCompat.getTransitionName(mProfileImage)))
                    .addToBackStack("fullscreenimage")
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.settings_container, fullscreenImageFragment)
                    .commit();
        }
    };

}