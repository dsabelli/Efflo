package com.dsabelli.efflo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;

public class StatsActivity extends AppCompatActivity {
    SharedPrefsSettings prefsSettings;
    TextView curStreak, longestStreak, sessionsComplete, totalMinutes, mindfulDays;
    TextView[] daysOfTheWeek = new TextView[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        prefsSettings = SharedPrefsSettings.getInstance(this);
        displayDays();
        displayStats();
        handleBack();
    }

    public void displayStats() {
        try {
            // Find and assign UI elements to variables
            curStreak = findViewById(R.id.cur_streak_data);
            longestStreak = findViewById(R.id.longest_streak_data);
            sessionsComplete = findViewById(R.id.sessions_data);
            totalMinutes = findViewById(R.id.minutes_data);
            mindfulDays = findViewById(R.id.mindful_days_textview);

            // Retrieve data from shared preferences
            int curStreakData = prefsSettings.getInt(prefsSettings.CURRENT_STREAK_KEY, 0);
            int longestStreakData = prefsSettings.getInt(prefsSettings.LONGEST_STREAK_KEY, 0);
            int sessionsCompleteData = prefsSettings.getInt(prefsSettings.TOTAL_SESSIONS_KEY, 0);
            long totalMinutesData = prefsSettings.getLong(prefsSettings.TOTAL_MINUTES_KEY, 0) / 60000; // Convert milliseconds to minutes
            int mindfulDaysData = prefsSettings.getInt(prefsSettings.WEEK_TALLY_KEY, 0);
            int weeklyGoal = Integer.parseInt(prefsSettings.getString(prefsSettings.GOAL_KEY, "0"));
            // Set text for current streak, handling singular vs. plural
            curStreak.setText(curStreakData == 1 ? curStreakData + " day" : curStreakData + " days");
            // Set text for longest streak, handling singular vs. plural
            longestStreak.setText(longestStreakData == 1 ? longestStreakData + " day" : longestStreakData + " days");
            // Set text for total sessions
            sessionsComplete.setText(String.valueOf(sessionsCompleteData));
            // Set text for total minutes
            totalMinutes.setText(String.valueOf(totalMinutesData));

            // Display progress towards weekly goal, with emphasis on the current and goal values
            String goalProgress = "Mindful days " + mindfulDaysData + " of " + weeklyGoal;
            SpannableString spannableGoalProgress = new SpannableString(goalProgress);
            spannableGoalProgress.setSpan(new RelativeSizeSpan(2f), 13, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableGoalProgress.setSpan(new RelativeSizeSpan(2f), 18, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mindfulDays.setText(spannableGoalProgress);
        } catch (Exception e) {
            Log.e("StatsActivity", "Error in displayStats", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayDays() {
        try {
            // Array of IDs for the days of the week
            int[] dayIds = new int[]{R.id.sun, R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri, R.id.sat};
            Drawable check = ContextCompat.getDrawable(this, R.drawable.check_circle);

            // Loop through the array of IDs to find and assign each day of the week view
            for (int i = 0; i < dayIds.length; i++) {
                daysOfTheWeek[i] = findViewById(dayIds[i]);
            }

            // Loop through the daysOfTheWeek array to check preferences and update UI accordingly
            for (int i = 0; i < daysOfTheWeek.length; i++) {
                // Check if the day has been completed based on preferences
                if (prefsSettings.getBoolean("day" + (i + 1), false)) {
                    // If completed, set the image resource to indicate completion
                    daysOfTheWeek[i].setCompoundDrawablesWithIntrinsicBounds(null, check, null, null);
                }
            }
        } catch (Exception e) {
            Log.e("StatsActivity", "Error in displayDays", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleBack() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Finish the activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
