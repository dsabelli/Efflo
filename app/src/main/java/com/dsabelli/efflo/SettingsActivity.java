package com.dsabelli.efflo;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dsabelli.efflo.settings.BreathSettingsActivity;
import com.dsabelli.efflo.settings.ContactActivity;
import com.dsabelli.efflo.settings.LegalSettingsActivity;
import com.dsabelli.efflo.settings.TimerSettingsActivity;
import com.dsabelli.efflo.settings.WeeklyGoalSettingsActivity;
import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;

public class SettingsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 2;
    // Declare UI components
    LinearLayout breathSettingsBtn, goalSettingsBtn, timerSettingsBtn,
            vibrationBtn, dndBtn, contactSettingsBtn, legalSettingsBtn;
    Switch vibrationSwitch, dndSwitch;
    SharedPrefsSettings prefsSettings;
    NotificationManager notificationManager;
    Drawable vibrationThumbDrawable, vibrationTrackDrawable, dndThumbDrawable, dndTrackDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefsSettings = SharedPrefsSettings.getInstance(this);
        initializeButtons();
        initializeDND();
        initializeVibrate();
        handleBack();
    }

    private void initializeVibrate() {
        try {
            vibrationSwitch = findViewById(R.id.vibration_switch);
            // Get the drawables for the thumb and track of the switch
            vibrationThumbDrawable = vibrationSwitch.getThumbDrawable();
            vibrationTrackDrawable = vibrationSwitch.getTrackDrawable();
            // Set initial switch state based on saved preference
            vibrationSwitch.setChecked(prefsSettings.getBoolean(prefsSettings.VIBRATION_KEY, true));
            handleSwitchColour(vibrationSwitch.isChecked(), vibrationThumbDrawable, vibrationTrackDrawable);
            // Update preference and vibrate on switch toggle
            vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    prefsSettings.setBoolean(prefsSettings.VIBRATION_KEY, isChecked);
                    // Update the switch's color based on its new state
                    handleSwitchColour(isChecked, vibrationThumbDrawable, vibrationTrackDrawable);
                    if (isChecked) {
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(200);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error in initVibrate", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeDND() {
        try {
            dndSwitch = findViewById(R.id.do_not_disturb_switch);
            // Get the drawables for the thumb and track of the switch
            dndThumbDrawable = dndSwitch.getThumbDrawable();
            dndTrackDrawable = dndSwitch.getTrackDrawable();
            // Initialize the NotificationManager to manage notification policies
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Set the initial color of the switch based on the saved preference
            handleSwitchColour(dndSwitch.isChecked(), dndThumbDrawable, dndTrackDrawable);
            if (notificationManager != null) {
                // Retrieve the saved state of the Do Not Disturb setting
                boolean isDndEnabled = prefsSettings.getBoolean(prefsSettings.DND_KEY, false);
                // Set the switch to the saved state
                dndSwitch.setChecked(isDndEnabled);
            }
            // Set a listener to handle changes in the switch's state
            dndSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Update the switch's color based on its new state
                handleSwitchColour(isChecked, dndThumbDrawable, dndTrackDrawable);
                // Check if the switch is now checked (enabled)
                if (isChecked) {
                    // Check if the app has the necessary permission to change notification policies
                    if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted()) {
                        // Enable Do Not Disturb by setting the interruption filter to priority
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                        // Save the new state of the Do Not Disturb setting
                        prefsSettings.setBoolean(prefsSettings.DND_KEY, isChecked);
                    } else {
                        // If the app does not have the necessary permission, request it
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        // Handle how the user responds to request
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                } else {
                    // If the switch is unchecked (disabled), revert to allowing all notifications
                    if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted()) {
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                        // Save the new state of the Do Not Disturb setting
                        prefsSettings.setBoolean(prefsSettings.DND_KEY, isChecked);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error in initDND", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (notificationManager.isNotificationPolicyAccessGranted()) {
                // Permission granted, enable DND
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                prefsSettings.setBoolean(prefsSettings.DND_KEY, true);
                dndSwitch.setChecked(true);
            } else {
                // Permission not granted, handle accordingly
                Toast.makeText(this, "DND permission not granted", Toast.LENGTH_SHORT).show();
                dndSwitch.setChecked(false);
            }
        }
    }

    private void initializeButtons() {
        // Initialize button click listeners
        vibrationBtn = findViewById(R.id.vibration_settings_button);
        vibrationBtn.setClickable(true);
        vibrationBtn.setOnClickListener(v -> vibrationSwitch.setChecked(!vibrationSwitch.isChecked()));

        dndBtn = findViewById(R.id.dnd_settings_button);
        dndBtn.setClickable(true);
        dndBtn.setOnClickListener(v -> dndSwitch.setChecked(!dndSwitch.isChecked()));

        timerSettingsBtn = findViewById(R.id.timer_settings_button);
        timerSettingsBtn.setClickable(true);
        timerSettingsBtn.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, TimerSettingsActivity.class)));

        breathSettingsBtn = findViewById(R.id.breath_settings_button);
        breathSettingsBtn.setClickable(true);
        breathSettingsBtn.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, BreathSettingsActivity.class)));

        goalSettingsBtn = findViewById(R.id.goal_settings_button);
        goalSettingsBtn.setClickable(true);
        goalSettingsBtn.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, WeeklyGoalSettingsActivity.class)));

        contactSettingsBtn = findViewById(R.id.contact_settings_button);
        contactSettingsBtn.setClickable(true);
        contactSettingsBtn.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, ContactActivity.class)));

        legalSettingsBtn = findViewById(R.id.legal_settings_button);
        legalSettingsBtn.setClickable(true);
        legalSettingsBtn.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, LegalSettingsActivity.class)));
    }

    private void handleSwitchColour(boolean isChecked, Drawable thumb, Drawable track) {
        // Change switch thumb and track colors based on checked state
        if (isChecked) {
            thumb.setColorFilter(ContextCompat.getColor(SettingsActivity.this, R.color.accent), PorterDuff.Mode.SRC_IN);
            track.setColorFilter(ContextCompat.getColor(SettingsActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            thumb.setColorFilter(ContextCompat.getColor(SettingsActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
            track.setColorFilter(ContextCompat.getColor(SettingsActivity.this, R.color.black), PorterDuff.Mode.SRC_IN);
        }
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
