package com.alliancesgalore.alliancesgalore.Fragments;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alliancesgalore.alliancesgalore.Activities.MainActivity;
import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.SwipeToRefresh;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CRMfragment extends Fragment {

    private UserProfile myProfile;
    public static int count = 0;
    Bundle savedInstanceStateout = null;
    private WebView crmweb;
    private SwipeToRefresh mRefresh;
    private ProgressBar progressBar;
    private String email, decrypted;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 1)
                webViewGoBack();
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
        mRefreshFunction();
        setFAB();
        SavedStateCheck(savedInstanceState);
        return view;
    }

    private void FindIds(View view) {
        crmweb = view.findViewById(R.id.crm_web);
        progressBar = view.findViewById(R.id.crm_prog);
        mRefresh = view.findViewById(R.id.crm_refresh);
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
                    mRefresh.setRefreshing(false);
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
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(valueEventListener);
        } catch (Exception e) {

        }
    }

    private void login() {
        crmweb.getSettings().setSupportZoom(true);
        crmweb.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(crmweb, url);
                if (myProfile != null && count < 2) {
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
            String url = "http://we-dpms.com/AGCRM/";
            crmweb.loadUrl(url);
        } else
            crmweb.restoreState(savedInstanceState);
    }

    private void mRefreshFunction() {
        mRefresh.setOnRefreshListener(mRefreshListener);
    }

    private Boolean KeyCheck(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == MotionEvent.ACTION_UP
                && crmweb.canGoBack();
    }

    private View.OnKeyListener crmKeyListener = (v, keyCode, event) -> {
        if (KeyCheck(keyCode, event)) {
            handler.sendEmptyMessage(1);
            return true;
        }
        return false;
    };

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                myProfile = dataSnapshot.getValue(UserProfile.class);
                login();
            } else {
            }
//                Toast.makeText(getContext(), "details could not be fetched.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            crmweb.reload();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        count = 0;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        crmweb.restoreState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        count = 0;
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onResume();
        }
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
        getemailpass();
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
        setFAB();
    }

    @Override
    public void onStart() {
        super.onStart();
        count = 0;
        getemailpass();
        login();
        setFAB();
    }

    @Override
    public void onResume() {
        super.onResume();
        setFAB();
        count = 0;
        getemailpass();
        login();
        if (savedInstanceStateout != null) {
            crmweb.restoreState(savedInstanceStateout);
        }
    }

    private void setFAB() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_white_24dp, getContext().getTheme()));
        Functions.toast("set in CRM onResume", getContext());
        mainActivity.fab.setOnClickListener(v -> {
            Intent launchIntent = mainActivity.getPackageManager().getLaunchIntentForPackage("org.thoughtcrime.securesms");
            try {
                launchIntent.setComponent(new ComponentName("org.thoughtcrime.securesms", "org.thoughtcrime.securesms.ConversationListActivity"));
                startActivity(launchIntent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Ag-Chat not available", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
