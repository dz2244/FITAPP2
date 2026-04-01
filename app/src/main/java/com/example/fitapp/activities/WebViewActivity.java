package com.example.fitapp.activities;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fitapp.R;

/**
 * Activity that displays a web page within the app using a {@link WebView}.
 * It receives the target URL and an optional title via Intent extras.
 */
public class WebViewActivity extends AppCompatActivity {

    /**
     * Initializes the activity, sets up the toolbar with a back button,
     * and configures the WebView to load the provided URL.
     *
     * @param savedInstanceState Bundle containing activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        if (title != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient());

        if (url != null) {
            webView.loadUrl(url);
        }
    }
}
