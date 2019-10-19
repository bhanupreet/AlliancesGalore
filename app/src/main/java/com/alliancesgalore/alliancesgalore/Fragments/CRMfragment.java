package com.alliancesgalore.alliancesgalore.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
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
import com.alliancesgalore.alliancesgalore.Models.UserProfile;
import com.alliancesgalore.alliancesgalore.Utils.ExtendedWebview;
import com.alliancesgalore.alliancesgalore.Utils.Functions;
import com.alliancesgalore.alliancesgalore.Utils.SwipeToRefresh;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class CRMfragment extends Fragment {

    private UserProfile myProfile;
    public static int count = 0;
    Bundle savedInstanceStateout = null;
    private ExtendedWebview crmweb;
    private SwipeToRefresh mRefresh;
    private ProgressBar progressBar;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
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
        crmweb.clearHistory();
        crmweb.clearFormData();
        crmweb.clearCache(true);
        android.webkit.CookieManager.getInstance().removeAllCookie();
        crmweb.getSettings().setJavaScriptEnabled(true);
        crmweb.getSettings().supportMultipleWindows();
        crmweb.getSettings().setSupportZoom(true);
        crmweb.getSettings().setPluginState(WebSettings.PluginState.ON);
        crmweb.getSettings().setAllowFileAccess(true);
        crmweb.getSettings().setDomStorageEnabled(true);
        crmweb.setInitialScale(120);
        crmweb.setScrollContainer(true);
        crmweb.bringToFront();
        crmweb.setScrollbarFadingEnabled(true);
        crmweb.setVerticalScrollBarEnabled(true);
        crmweb.setHorizontalScrollBarEnabled(true);
        crmweb.getSettings().setUseWideViewPort(true);
        crmweb.setWebChromeClient(new WebChromeClient() {

            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    } catch (IOException ex) {
                        Log.e("tag", "Image file creation failed", ex);
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
                super.onPermissionRequest(request);
            }

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

    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }

    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
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
        if (mainActivity.getcurrenttabposition() == 0) {
//            mainActivity.fab.show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }
}
