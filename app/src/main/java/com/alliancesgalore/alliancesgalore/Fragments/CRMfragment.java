package com.alliancesgalore.alliancesgalore.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.AESUtils;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kotlin.Function;

import static com.alliancesgalore.alliancesgalore.Utils.Global.myProfile;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CRMfragment extends Fragment {

    private UserProfile myProfile;
    public static int count = 0;
    Bundle savedInstanceStateout = null;
    private WebView crmweb;
    private ProgressBar progressBar;
    private String email, decrypted;
    private String url = "http://we-dpms.com/AGCRM/";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: {
                    webViewGoBack();
                }
                break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crm, container, false);
        FindIds(view);
        websettings(crmweb);
        webclicklistener(crmweb);
        getemailpass();
        login();
        SavedStateCheck(savedInstanceState);
        return view;
    }

    private void FindIds(View view) {
        crmweb = view.findViewById(R.id.crm_web);
        progressBar = view.findViewById(R.id.crm_prog);
    }

    private void websettings(WebView crmweb) {
        crmweb.getSettings().setDomStorageEnabled(true);
        crmweb.getSettings().setJavaScriptEnabled(true);
        crmweb.getSettings().supportMultipleWindows();
        crmweb.getSettings().setSupportZoom(true);
        crmweb.clearHistory();
        crmweb.clearFormData();
        crmweb.clearCache(true);
        android.webkit.CookieManager.getInstance().removeAllCookie();
        crmweb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void webclicklistener(final WebView crmweb) {
        crmweb.setOnKeyListener(crmKeyListener);
    }

    private void getemailpass() {
        String uid = FirebaseAuth.getInstance().getUid();
        try {
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addValueEventListener(valueEventListener);
        } catch (Exception e) {

        }
    }


    private void login() {

        crmweb.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(crmweb, url);
                getemailpass();
                if (myProfile != null && count<2) {
                    email = myProfile.getEmail();
                    decrypted = Functions.decrypt(myProfile.getPassword());
                    crmweb.loadUrl("javascript:(function(){document.getElementsByName('email')[0].value='"
                            + email
                            + "';document.getElementsByName('password')[0].value='"
                            + decrypted
                            + "';document.getElementsByTagName('form')[0].submit();})()");
                    count++;
                }
            }
        });
    }

    private void webViewGoBack() {
        crmweb.goBack();
    }

    private void SavedStateCheck(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            crmweb.loadUrl(url);
        } else
            crmweb.restoreState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        crmweb.saveState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        count = 0;
        login();
        crmweb.restoreState(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        count = 0;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count = 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        count = 0;
        login();
    }

    @Override
    public void onResume() {
        super.onResume();
        count = 0;
        login();
        if (savedInstanceStateout != null) {
            crmweb.restoreState(savedInstanceStateout);

        }

    }

    private View.OnKeyListener crmKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (KeyCheck(keyCode, event)) {
                handler.sendEmptyMessage(1);
                return true;
            }
            return false;
        }
    };

    private Boolean KeyCheck(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == MotionEvent.ACTION_UP
                && crmweb.canGoBack())
            return true;
        else
            return false;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        crmweb.restoreState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        count = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        count = 0;
    }

    public ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                myProfile = dataSnapshot.getValue(UserProfile.class);
                Toast.makeText(getContext(), "details added", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), myProfile.getEmail(), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "details could not be fetched.", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
