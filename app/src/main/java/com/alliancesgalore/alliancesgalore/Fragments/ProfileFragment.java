package com.alliancesgalore.alliancesgalore.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.FragFunctions;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.Global;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private ImageView mChangeNamebtn;
    private ImageButton mChangePhotobtn;
    private TextView mEmail, mDeesignation, mDisplayName;
    private CircleImageView mProfileImage;
    private StorageReference mImageStorage;
    private ProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        Activity activity = (Activity) getContext();
        ((AppCompatActivity) activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1565C0")));

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

    private void LoadImage() {
        if (Global.myProfile.getImage() != null) {
            Picasso.get()
                    .load(Global.myProfile.getImage())
                    .placeholder(R.drawable.defaultprofile)
                    .into(mProfileImage);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getFragmentManager().getBackStackEntryCount() != 0)
                    getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setDetails() {
        LoadImage();
        mDisplayName.setText(Global.myProfile.getDisplay_name());
        mEmail.setText(Global.myProfile.getEmail());
        mDeesignation.setText(Global.myProfile.getRole());
    }

    private void FindIds(View view) {
        mProfileImage = view.findViewById(R.id.profile_displayImage);
        mDisplayName = view.findViewById(R.id.profile_displayName);
        mDeesignation = view.findViewById(R.id.profile_display_Designation);
        mChangeNamebtn = view.findViewById(R.id.profile_editnamebtn);
        mEmail = view.findViewById(R.id.profile_display_Email);
        mChangePhotobtn = view.findViewById(R.id.profile_changePhoto);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mProgress = view.findViewById(R.id.profile_progress);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void editNameBtn() {
        mChangeNamebtn.setOnClickListener(editnameOnClick);

    }

    private View.OnClickListener editnameOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ChangeNameFragment changeNameFragment = new ChangeNameFragment();
            getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(mDisplayName, ViewCompat.getTransitionName(mDisplayName))
                    .addToBackStack("profile")
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.settings_container, changeNameFragment)
                    .commit();
        }
    };

    private void changePhotoClick() {
        mChangePhotobtn.setOnClickListener(changePhotoOnClick);
    }

    private View.OnClickListener changePhotoOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mProgress.setVisibility(View.VISIBLE);
            CropImage.activity()
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(), ProfileFragment.this);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(getContext(), "uri got successfully", Toast.LENGTH_SHORT).show();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                UploadTask uploadTask = uploadToDatabase(resultUri);
                uploadTask.addOnCompleteListener(uploadOnComplete);
            } else
                Toast.makeText(getContext(), "Error uploading File", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), "Error uploading File", Toast.LENGTH_SHORT).show();
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
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] thumb_byte = baos.toByteArray();

        StorageReference filepath = mImageStorage.child("Profile_Images").child(FirebaseAuth.getInstance().getUid() + ".jpg");
        UploadTask uploadTask = filepath.putBytes(thumb_byte);
        return uploadTask;
    }

    OnCompleteListener uploadOnComplete = new OnCompleteListener<UploadTask.TaskSnapshot>() {

        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "URL got successfully", Toast.LENGTH_SHORT).show();

                mImageStorage.child("Profile_Images").child(FirebaseAuth.getInstance().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(downloadURLsuccess);
            } else
                Functions.toast(task);
        }
    };

    private OnSuccessListener downloadURLsuccess = new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            Toast.makeText(getContext(), "URL got successfully", Toast.LENGTH_SHORT).show();

            String downloadurl = uri.toString();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
            HashMap<String, Object> result = new HashMap<>();
            result.put("image", downloadurl);
            myRef.updateChildren(result).addOnCompleteListener(updateDatabaseOnComplete);
        }
    };
    private OnCompleteListener updateDatabaseOnComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "image uploaded successfully", Toast.LENGTH_SHORT).show();
                LoadImage();
            } else
                Functions.toast(task);
        }
    };


    private void mProfileImageClick() {
        mProfileImage.setOnClickListener(mProfileImageOnClick);
    }

    private View.OnClickListener mProfileImageOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FullscreenImageFragment fullscreenImageFragment = new FullscreenImageFragment();
            getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(mProfileImage, ViewCompat.getTransitionName(mProfileImage))
                    .addToBackStack("fullscreenimage")
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.settings_container, fullscreenImageFragment)
                    .commit();
        }
    };
}
