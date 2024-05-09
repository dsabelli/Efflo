package com.dsabelli.efflo;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.splashscreen.SplashScreen;

import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {
    public static final String MEDIA_CHANNEL_ID = "media_player_channel";
    private final int DAY_MILLIS = 86400000;
    private final int DELAY = 500;
    private boolean keep = true;
    SplashScreen splashScreen;
    long currentDate;
    SharedPrefsSettings prefsSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefsSettings = SharedPrefsSettings.getInstance(this);
        initializeSplashScreen();

        handleWeekReset();
        handleStreakReset();
        initializeGreeting();
        initializeButtons();
        handleDisclaimerAndVib();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update streak on app resume
        handleStreakReset();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    // Show disclaimer on first opening only, set vibrator to true as default
    private void handleDisclaimerAndVib() {
        if (!prefsSettings.getBoolean(prefsSettings.DISCLAIMER_KEY, false)) {
            prefsSettings.setBoolean(prefsSettings.VIBRATION_KEY, true);
            Intent intent = new Intent(this, DisclaimerActivity.class);
            startActivity(intent);
        }
    }

    // Set up the splash screen with a delay
    private void initializeSplashScreen() {
        splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> keep);
        Handler handler = new Handler();
        handler.postDelayed(() -> keep = false, DELAY);
    }


    // Manage streak reset logic based on login dates
    private void handleStreakReset() {
        // Retrieve last login time, current streak, and longest streak from preferences
        long lastLogin = prefsSettings.getLong(prefsSettings.LAST_LOGIN_KEY, 0);
        int currentStreak = prefsSettings.getInt(prefsSettings.CURRENT_STREAK_KEY, 0);
        int longestStreak = prefsSettings.getInt(prefsSettings.LONGEST_STREAK_KEY, 0);
        // Get the current date in milliseconds
        currentDate = System.currentTimeMillis();
        // If the current streak is longer than the longest streak, update the longest streak
        if (currentStreak > longestStreak) {
            prefsSettings.setInt(prefsSettings.LONGEST_STREAK_KEY, currentStreak);
        }
        // If the time since the last login is more than two days, reset the current streak
        if (currentDate - lastLogin > DAY_MILLIS * 2) {
            prefsSettings.setInt(prefsSettings.CURRENT_STREAK_KEY, 0);
        }
        // Update last login to today
        prefsSettings.setLong(prefsSettings.LAST_LOGIN_KEY, currentDate);
    }




    private void handleWeekReset() {
        // Get last login date
        long lastLoginDate = prefsSettings.getLong(prefsSettings.LAST_LOGIN_KEY,0);

        // Calculate the difference between the current date and the last launch date
        long diffInMillies = Math.abs(new Date().getTime() - lastLoginDate);
        long daysSinceLastLogin = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            // Check if today is Sunday
            if (daysSinceLastLogin>=7) {
                // Reset the week tally to 0
                prefsSettings.setInt(prefsSettings.WEEK_TALLY_KEY, 0);
                // Loop through each day of the week and reset its status to false
                for (int i = 1; i < 8; i++) {
                    prefsSettings.setBoolean("day" + i, false);
                }
            }
    }


    // Set a greeting based on the current time of day
    private void initializeGreeting() {
        TextView greeting = findViewById(R.id.greeting_textview);
        int timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (timeOfDay < 12) {
            greeting.setText("Good Morning");
        } else if (timeOfDay < 18) {
            greeting.setText("Good Afternoon");
        } else {
            greeting.setText("Good Evening");
        }
    }

    private void initializeButtons() {
        // Declare button references
        ImageView breatheActivityBtn = findViewById(R.id.breathe_activity_btn);
        ImageView timerActivityBtn = findViewById(R.id.timer_activity_btn);
        ImageView statsActivityBtn = findViewById(R.id.stats_activity_btn);
        ImageView settingsActivityBtn = findViewById(R.id.settings_activity_btn);

        // Set click listeners for each button with Intent to corresponding activities
        breatheActivityBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
            intent.putExtra("heading", "Breathe");
            startActivity(intent);
        });

        timerActivityBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
            intent.putExtra("heading", "Timer");
            startActivity(intent);
        });

        statsActivityBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
        });

        settingsActivityBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });


    }

}