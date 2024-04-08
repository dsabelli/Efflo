package com.dsabelli.efflo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dsabelli.efflo.settings.LegalSettingsActivity;
import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;

public class DisclaimerActivity extends AppCompatActivity {
    private final String AGREEMENT = "By using this app, you agree to the terms and conditions.";
    TextView agreementTextView;
    Handler handler = new Handler(Looper.getMainLooper());
    SharedPrefsSettings prefsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);
        prefsSettings=SharedPrefsSettings.getInstance(this);

        handleDisclaimer();
        handleTimedFinish();
    }

    private void handleDisclaimer() {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Start the new activity
                Intent intent = new Intent(DisclaimerActivity.this, LegalSettingsActivity.class);
                startActivity(intent);
                finish();
            }
        };
        agreementTextView = findViewById(R.id.agreement_link_textview);
        SpannableString spannableString = new SpannableString(AGREEMENT);
        spannableString.setSpan(clickableSpan, 36, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        agreementTextView.setText(spannableString);
        agreementTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void handleTimedFinish() {
        prefsSettings.setBoolean(prefsSettings.DISCLAIMER_KEY, true);

        // Post a Runnable to the Handler with a delay of 1 second (1000 milliseconds)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Finish the activity
                finish();
            }
        }, 3000); // Delay of 1 second
    }
}