package com.alliancesgallore.alliancesgallore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView mainview;
    private Toolbar appbar;
    private RelativeLayout layout;
    private String url = "http://we-dpms.com/AGCRM/";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String email = getIntent().getStringExtra("email");
        final String password = getIntent().getStringExtra("password");

        layout = findViewById(R.id.mainlayout);

        progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);

        progressBar.setVisibility(View.VISIBLE);

        mainview = findViewById(R.id.mainweb);
        mainview.getSettings().setDomStorageEnabled(true);
        mainview.getSettings().setJavaScriptEnabled(true);
        mainview.getSettings().setUseWideViewPort(true);
        mainview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(mainview, url);
                mainview.loadUrl("javascript:(function(){document.getElementsByName('email')[0].value='"
                        + email
                        + "';document.getElementsByName('password')[0].value='"
                        + password
                        + "';document.getElementsByTagName('form')[0].submit();})()");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
//        mainview.loadUrl("javascript:document.getElementsByName('_token').value = 'nLjTd6LXEKI8Bc5QzvRHhs4J4SuKSckNGkgFCzQS'");
//        mainview.loadUrl("javascript:document.forms['Login'].submit()");
        appbar = findViewById(R.id.mainappbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setTitle("Alliances Gallore");

        mainview.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainview.loadUrl("http://we-dpms.com/AGCRM");
    }
}
