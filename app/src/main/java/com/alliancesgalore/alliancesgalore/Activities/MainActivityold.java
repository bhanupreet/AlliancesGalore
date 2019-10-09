package com.alliancesgalore.alliancesgalore.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alliancesgalore.alliancesgalore.R;
import com.alliancesgalore.alliancesgalore.Utils.AESUtils;
import com.alliancesgalore.alliancesgalore.Utils.NestedScrollWebView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivityold extends AppCompatActivity {

    private List<String> xyz;

    public  int count = 0;

    private WebView mainview;
    private String url = "http://we-dpms.com/AGCRM/", email, password, decrypted;
    private ProgressBar progressBar;
    private DatabaseReference mUserRef;
    private NestedScrollWebView mainwebview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //getting email password
//        email = getIntent().getStringExtra("email");
//        password = getIntent().getStringExtra("password");
//        String encrypted = password;
//        decrypted = "";
//        try {
//            decrypted = AESUtils.decrypt(encrypted);
//            Log.d("TEST", "decrypted:" + decrypted);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //


        FloatingActionButton mChat = findViewById(R.id.chatbutton);
        CoordinatorLayout layout = findViewById(R.id.mainlayout);
        Toolbar mToolbar = findViewById(R.id.mainappbar);

        mChat.setOnClickListener(view -> {

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.thoughtcrime.securesms");
            try {
                launchIntent.setComponent(new ComponentName("org.thoughtcrime.securesms", "org.thoughtcrime.securesms.ConversationListActivity"));
                startActivity(launchIntent);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Ag-Chat not available", Toast.LENGTH_SHORT).show();
            }

        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CRM");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.mainprogress);
        progressBar.setVisibility(View.VISIBLE);

        mainview = findViewById(R.id.mainweb);
        mainview.getSettings().setDomStorageEnabled(true);
        mainview.getSettings().setJavaScriptEnabled(true);
        mainview.getSettings().supportMultipleWindows();
        mainview.getSettings().setSupportZoom(true);
        mainview.clearHistory();
        mainview.clearFormData();
        mainview.clearCache(true);
        android.webkit.CookieManager.getInstance().removeAllCookie();



        mainview.setWebChromeClient(new WebChromeClient() {
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


//

        mainview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    mainview.loadUrl("javascript:(function(){document.getElementsByName('email')[0].value='"
                            + email
                            + "';document.getElementsByName('password')[0].value='"
                            + decrypted
                            + "';document.getElementsByTagName('form')[0].submit();})()");
                    count++;
                    if(count>1)
                    {
                        email = null;
                        password = null;
                    }

                }
                super.onPageFinished(mainview, url);
            }
        });


        Toolbar appbar = findViewById(R.id.mainappbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setTitle("Alliances Galore");

        if (savedInstanceState == null) {
            mainview.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {

        if (mainview.canGoBack()) {
            mainview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            sendToStart();
        } else {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    email = dataSnapshot.child("email").getValue().toString();
                    password = dataSnapshot.child("password").getValue().toString();
                    String encrypted = password;
                    decrypted = "";
                    try {
                        decrypted = AESUtils.decrypt(encrypted);
                        Log.d("TEST", "decrypted:" + decrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    WebStorage.getInstance().deleteAllData();
                    mainview.loadUrl(url);
                    if (!TextUtils.isEmpty(email)) {
                        mainview.loadUrl("javascript:(" +
                                "function(){" +
                                "document.getElementsByName('email')[0].value='"
                                + email
                                + "';document.getElementsByName('password')[0].value='"
                                + decrypted
                                + "';document.getElementsByTagName('form')[0].submit();})()");

                        mainview.getSettings().setBuiltInZoomControls(true);
                        mainview.getSettings().setDisplayZoomControls(false);
                        mainview.getSettings().supportZoom();
                        mainview.getSettings().setLoadWithOverviewMode(true);
                        mainview.getSettings().setUseWideViewPort(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivityold.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//
//        if (item.getItemId() == R.id.main_logout_btn) {
//
//
//            if (mAuth.getCurrentUser() != null) {
//
//                mainview.loadUrl("javascript:document.getElementById('logout-form').submit();");
//                Intent StartIntent = new Intent(MainActivityold.this, StartActivity.class);
//                FirebaseAuth.getInstance().signOut();
//                WebStorage.getInstance().deleteAllData();
//                startActivity(StartIntent);
//
//                mainview.setWebChromeClient(new WebChromeClient() {
//                    @Override
//                    public void onProgressChanged(WebView view, int progress) {
//                        progressBar.setProgress(progress);
//                        if (progress == 100) {
//                            progressBar.setVisibility(View.GONE);
//
//                            mainview.clearHistory();
//                            mainview.clearFormData();
//                            mainview.clearCache(true);
//                            android.webkit.CookieManager.getInstance().removeAllCookie();
//                            mainview.stopLoading();
//
//                        } else {
//                            progressBar.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                });
//            }
//        }
//        if (item.getItemId() == R.id.main_delete_account_btn) {
//
//            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityold.this);
//            alert.setTitle("Delete Account");
//            alert.setMessage("Are you sure you want to delete the Account?");
//
//            alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    progressBar.setVisibility(View.VISIBLE);
//                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    user.delete()
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Intent StartIntent = new Intent(MainActivityold.this, StartActivity.class);
//                                        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                if (task.isSuccessful()) {
//                                                    Toast.makeText(MainActivityold.this, "Account Deleted Successfully", Toast.LENGTH_LONG).show();
//                                                } else {
//                                                    Toast.makeText(MainActivityold.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//
//                                                }
//                                                progressBar.setVisibility(View.INVISIBLE);
//                                            }
//                                        });
//                                        startActivity(StartIntent);
//                                    }
//                                }
//                            });
//                }
//            });
//
//            alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.cancel();
//                }
//            });
//
//            alert.show();
//
//        }
//
//
//        if (item.getItemId() == R.id.change_password_btn) {
//            Intent changepasswordintent = new Intent(MainActivityold.this, ChangePasswordActivity.class);
//            startActivity(changepasswordintent);
//            finish();
//        }
//        //delete accountbtn
//        return true;
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        mainview.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mainview.restoreState(savedInstanceState);
    }
}
