package com.dsabelli.efflo.settings;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.dsabelli.efflo.R;

public class LegalSettingsActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_legal);
        initializeWebView();
        handleBack();
    }

    private void initializeWebView() {
        webView = findViewById(R.id.webview);
        webView.loadUrl("https://dansabelli.com/efflo-terms/");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:")) {
                    // Create an Intent to direct to ContactActivity
                    Intent intent = new Intent(LegalSettingsActivity.this, ContactActivity.class);
                    view.getContext().startActivity(intent);
                    finish();
                    return true; // Return true to indicate that the URL loading is handled
                }
                return false; // Return false to let the WebView handle the URL
            }
        });
    }

    public void handleBack() {
        // Override back button behavior
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Finish the activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}