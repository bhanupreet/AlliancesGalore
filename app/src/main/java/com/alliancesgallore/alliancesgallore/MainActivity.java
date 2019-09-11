package com.alliancesgallore.alliancesgallore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private WebView mainview;
    private Toolbar appbar;
    private FirebaseAuth mAuth;
    private RelativeLayout layout;
    private String url = "http://we-dpms.com/AGCRM/", email, password,decrypted;
    private ProgressBar progressBar;
    private DatabaseReference mUserRef;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = getIntent().getStringExtra("email");

        password = getIntent().getStringExtra("password");
        layout = findViewById(R.id.mainlayout);
        mToolbar = findViewById(R.id.mainappbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CRM");

        mAuth = FirebaseAuth.getInstance();


        progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);

        progressBar.setVisibility(View.VISIBLE);

        mainview = findViewById(R.id.mainweb);
        mainview.getSettings().setDomStorageEnabled(true);
        mainview.getSettings().setJavaScriptEnabled(true);
        mainview.getSettings().supportMultipleWindows();
        mainview.getSettings().setSupportZoom(true);


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


        mainview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(mainview, url);
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    mainview.loadUrl("javascript:(function(){document.getElementsByName('email')[0].value='"
                            + email
                            + "';document.getElementsByName('password')[0].value='"
                            + decrypted
                            + "';document.getElementsByTagName('form')[0].submit();})()");
                }
            }
        });

//        mainview.loadUrl("javascript:document.getElementsByName('_token').value = 'nLjTd6LXEKI8Bc5QzvRHhs4J4SuKSckNGkgFCzQS'");
//        mainview.loadUrl("javascript:document.forms['Login'].submit()");
        appbar = findViewById(R.id.mainappbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setTitle("Alliances Gallore");

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mainview.loadUrl("http://we-dpms.com/AGCRM/admin_login");
        } else {
            mainview.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainview.loadUrl("http://we-dpms.com/AGCRM");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            sendToStart();
        } else {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
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
                    mainview.loadUrl("javascript:(" +
                            "function(){" +
                            "document.getElementsByName('email')[0].value='"
                            + email
                            + "';document.getElementsByName('password')[0].value='"
                            + decrypted
                            + "';document.getElementsByTagName('form')[0].submit();})()");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {


            if (mAuth.getCurrentUser() != null) {

                mainview.loadUrl("javascript:" +
                        "document.getElementById('logout-form').submit();");



                mainview.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        progressBar.setProgress(progress);
                        if (progress == 100) {
                            progressBar.setVisibility(View.GONE);

                            mainview.clearHistory();
                            mainview.clearFormData();
                            mainview.clearCache(true);
                            android.webkit.CookieManager.getInstance().removeAllCookie();
//
                            mainview.stopLoading();

                            Intent StartIntent = new Intent(MainActivity.this, LoginActivity.class);
                            FirebaseAuth.getInstance().signOut();
                            WebStorage.getInstance().deleteAllData();
                            startActivity(StartIntent);

                        }
                        else{
                            progressBar.setVisibility(View.VISIBLE);
                        }

                    }

                });
//
                //mUserRef.child("lastseen").setValue(ServerValue.TIMESTAMP);
            }

//


        }
        return true;
    }
}
