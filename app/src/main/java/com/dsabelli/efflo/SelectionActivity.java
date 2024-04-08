package com.dsabelli.efflo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;

public class SelectionActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;
    // UI elements
    private Spinner breathMethodSpinner, soundtrackSpinner, timeSpinner;
    private String selectedTime, selectedSoundtrack, selectedBreathMethod;
    private String heading;
    private Button startBtn;
    private ImageView selectionArt;
    private TextView breathTextView, soundtrackTextView,timerTextView;
    private View divider;
    private SharedPrefsSettings prefsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        prefsSettings = SharedPrefsSettings.getInstance(this);
        heading = getIntent().getStringExtra("heading");
        initializeUI();
        handleBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // The second activity was finished normally, not by pressing the back button
                finish(); // Finish the first activity
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeUI() {
        initializeSpinners();
        breathTextView = findViewById(R.id.breath_textview);
        soundtrackTextView = findViewById(R.id.soundtrack_textview);
        timerTextView = findViewById(R.id.timer_textview);
        divider = findViewById(R.id.divider_breath);

        // Hide breath textview and divider if heading is for a timer
        if (heading.equals(prefsSettings.TIMER_HEADING)) {
            breathTextView.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        try {
            // set the art for the activity depending on the heading from Main Activity
            selectionArt = findViewById(R.id.selection_art);
            if (heading.equals(prefsSettings.TIMER_HEADING)) {
                selectionArt.setImageResource(R.drawable.timer_selection);
            } else {
                selectionArt.setImageResource(R.drawable.breath_selection);
            }

            Intent intent = new Intent(this, MediaPlayerActivity.class);
            // Send relevant data to MediaPlayer Activity
            startBtn = findViewById(R.id.start_media_player_btn);
            startBtn.setOnClickListener(v -> {
                intent.putExtra("heading", heading);
                intent.putExtra("time", selectedTime);
                intent.putExtra("soundTrack", selectedSoundtrack);
                intent.putExtra("breathMethod", selectedBreathMethod);
                startActivityForResult(intent, REQUEST_CODE);

            });
        } catch (Exception e) {
            Log.e("SelectionActivity", "Error in initUI", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeSpinners() {
        try {
            // Initialize spinner UI elements
            breathMethodSpinner = findViewById(R.id.breathe_method_spinner);
            soundtrackSpinner = findViewById(R.id.soundtrack_spinner);
            timeSpinner = findViewById(R.id.time_spinner);
            // Create ArrayAdapters for each spinner, using resources for the data
            ArrayAdapter<CharSequence> breathMethodAdapter = ArrayAdapter.createFromResource(this, R.array.breath_methods, R.layout.spinner_item);
            ArrayAdapter<CharSequence> soundtrackAdapter = ArrayAdapter.createFromResource(this, R.array.soundtracks, R.layout.spinner_item);
            ArrayAdapter<CharSequence> timerAdapter = ArrayAdapter.createFromResource(this, R.array.time, R.layout.spinner_item);
            // Set the dropdown view resource for each adapter to ensure the spinner displays correctly
            breathMethodAdapter.setDropDownViewResource(R.layout.dropdown_item);
            soundtrackAdapter.setDropDownViewResource(R.layout.dropdown_item);
            timerAdapter.setDropDownViewResource(R.layout.dropdown_item);
            // Determine the default positions for the spinners based on saved preferences
            int breathDefaultPos = breathMethodAdapter.getPosition(prefsSettings.getString(prefsSettings.BREATH_KEY, ""));
            // initialize for if else below
            int soundtrackDefaultPos = -1;
            int timerDefaultPos = -1;
            // Adjust default positions based on the activity's heading (Timer or Breath)
            if (heading.equals(prefsSettings.TIMER_HEADING)) {
                soundtrackDefaultPos = soundtrackAdapter.getPosition(prefsSettings.getString(prefsSettings.TIMER_SOUNDTRACK_KEY, ""));
                timerDefaultPos = timerAdapter.getPosition(prefsSettings.getString(prefsSettings.TIMER_TIME_KEY, ""));
            } else {
                soundtrackDefaultPos = soundtrackAdapter.getPosition(prefsSettings.getString(prefsSettings.BREATH_SOUNDTRACK_KEY, ""));
                timerDefaultPos = timerAdapter.getPosition(prefsSettings.getString(prefsSettings.BREATH_TIME_KEY, ""));
            }
            // Set the adapters for each spinner
            breathMethodSpinner.setAdapter(breathMethodAdapter);
            soundtrackSpinner.setAdapter(soundtrackAdapter);
            timeSpinner.setAdapter(timerAdapter);
            // Set the default selections for each spinner if a default position was found
            if (breathDefaultPos != -1) {
                breathMethodSpinner.setSelection(breathDefaultPos);
            }
            if (soundtrackDefaultPos != -1) {
                soundtrackSpinner.setSelection(soundtrackDefaultPos);
            }
            if (timerDefaultPos != -1) {
                timeSpinner.setSelection(timerDefaultPos);
            }

            // Spinner selection listeners
            breathMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedBreathMethod = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });
            // Use the position to set the selected soundtrack
            soundtrackSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedSoundtrack = "sound_" + (position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });

            timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedTime = parent.getItemAtPosition(position).toString().split(" ")[0];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });
            // Hide breath method spinner if heading is for a timer
            if (heading.equals(prefsSettings.TIMER_HEADING)) {
                breathMethodSpinner.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("SelectionActivity", "Error in initSpinners", e);
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