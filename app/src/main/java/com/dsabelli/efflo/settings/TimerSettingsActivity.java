package com.dsabelli.efflo.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.dsabelli.efflo.R;
import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;

public class TimerSettingsActivity extends AppCompatActivity {
    SharedPrefsSettings prefsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_timer);

        // Initialize SharedPrefsSettings instance for saving settings
        prefsSettings = SharedPrefsSettings.getInstance(this);

        // Initialize RadioGroups for timer time and soundtrack
        RadioGroup radioGroupTimerTime = findViewById(R.id.radioGroup_timer_time);
        RadioGroup radioGroupTimerSoundtrack = findViewById(R.id.radioGroup_timer_soundtrack);

        // Load options from resources
        String[] minuteOptions = getResources().getStringArray(R.array.time);
        String[] soundtrackOptions = getResources().getStringArray(R.array.soundtracks);

        // Populate RadioGroups with options and set checked state based on saved preferences
        populateRadioGroup(radioGroupTimerTime, minuteOptions, prefsSettings.TIMER_TIME_KEY);
        populateRadioGroup(radioGroupTimerSoundtrack, soundtrackOptions, prefsSettings.TIMER_SOUNDTRACK_KEY);

        // Handle back press to finish the activity
        handleBack();
    }

    // Method to populate a RadioGroup with options and set checked state based on saved preferences
    private void populateRadioGroup(RadioGroup radioGroup, String[] options, String prefKey) {
        for (String option : options) {
            RadioButton radioButton = createRadioButton(option);
            if (prefsSettings.getString(prefKey, "").equals(option)) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }

        // Set onCheckedChangeListener to save the selected option
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            prefsSettings.setString(prefKey, checkedRadioButton.getText().toString());
        });
    }

    // Method to create a RadioButton with common settings
    private RadioButton createRadioButton(String option) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setPadding(8, 0, 0, 0);
        radioButton.setTextSize(18);
        radioButton.setText(option);
        radioButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        CompoundButtonCompat.setButtonTintList(radioButton, ContextCompat.getColorStateList(this, R.color.white));
        radioButton.setId(View.generateViewId()); // Generate a unique ID for the RadioButton
        return radioButton;
    }

    // Method to handle back press
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
