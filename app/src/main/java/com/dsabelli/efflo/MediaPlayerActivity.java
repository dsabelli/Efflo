package com.dsabelli.efflo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dsabelli.efflo.fragments.BreathTimerFragment;
import com.dsabelli.efflo.fragments.SoundtrackArtFragment;
import com.dsabelli.efflo.helpers.DayOfWeek;
import com.dsabelli.efflo.helpers.Helper;
import com.dsabelli.efflo.services.MediaPlayerService;
import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;

public class MediaPlayerActivity extends BaseActivity {
    private static final String BREATH_TIMER_FRAGMENT_TAG = "BreathTimerFragment";

    TextView timerTimeRemaining, timerTotalTime;
    String timerMinutes, soundTrack, breathMethod;
    int timerDuration;
    ImageView playPauseImageView;
    String heading;
    boolean isPlaying = true;
    SeekBar seekBar;
    Intent mediaServiceIntent;
    SharedPrefsSettings prefsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        prefsSettings = SharedPrefsSettings.getInstance(this);

        // Initialize UI elements and retrieve intent data
        initializeUI();
        initializeFragments();
        // Start the MediaPlayerService
        initializeMediaPlayerService();
        // Register a BroadcastReceiver for media progress updates
        IntentFilter intentFilter = new IntentFilter("com.dsabelli.MEDIA_PROGRESS");
        ContextCompat.registerReceiver(this, broadcastReceiver, intentFilter, null, null, ContextCompat.RECEIVER_EXPORTED);
        handleBack();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        stopService(mediaServiceIntent);
    }

    // Get data from intent extras
    private void getIntents() {
        try {
            heading = getIntent().getStringExtra("heading");
            soundTrack = getIntent().getStringExtra("soundTrack");
            breathMethod = getIntent().getStringExtra("breathMethod");
            timerMinutes = getIntent().getStringExtra("time");
        } catch (Exception e) {
            Log.e("MediaPlayerActivity", "Error in getIntents", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Initialize UI elements with intent data
    private void initializeUI() {
        getIntents();
        timerDuration = Helper.stringMinsToMillis(timerMinutes);
        timerTimeRemaining = findViewById(R.id.timer_time_remaining_textview);
        timerTotalTime = findViewById(R.id.timer_total_time_textview);
        timerTotalTime.setText(Helper.formatTime(timerDuration));

        initializeSeekBar();
        initializeImageView();
    }

    // Initialize views and listeners for SeekBar
    private void initializeSeekBar() {
        try {
            seekBar = findViewById(R.id.seekBar);
            seekBar.setMax(timerDuration);
            seekBar.setProgress(0);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        Intent intent = new Intent("com.dsabelli.UPDATE_MEDIA_POSITION");
                        intent.putExtra("position", progress);
                        sendBroadcast(intent);
                    }
                }

                // If seeking and isPlaying, performClick to pause
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (!heading.equals("TIMER") && isPlaying) {
                        playPauseImageView.performClick();
                    }
                }

                // If seeking and !isPlaying, performClick to play
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (!heading.equals("TIMER") && !isPlaying) {
                        playPauseImageView.performClick();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("MediaPlayerActivity", "Error in initSeekbar", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Initialize views and listeners for play/pause button
    private void initializeImageView() {
        try {
            playPauseImageView = (ImageView) findViewById(R.id.imageView);
            playPauseImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isPlaying) {
                        sendPauseCommand();
                        // To pause the timer and media player
                        playPauseImageView.setImageResource(R.drawable.play);
                        isPlaying = false;
                        if (!heading.equals(prefsSettings.TIMER_HEADING)) {
                            removeBreathTimerFragment();
                        }
                    } else {
                        sendResumeCommand();
                        // To resume the timer and media player
                        playPauseImageView.setImageResource(R.drawable.pause);
                        isPlaying = true;
                        if (!heading.equals(prefsSettings.TIMER_HEADING)) {
                            addBreathTimerFragment();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("MediaPlayerActivity", "Error in initImageView", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Initialize and start the MediaPlayerService
    private void initializeMediaPlayerService() {
        try {
            mediaServiceIntent = new Intent(this, MediaPlayerService.class);
            mediaServiceIntent.setAction(MediaPlayerService.ACTION_PLAY);
            mediaServiceIntent.putExtra("soundTrack", soundTrack);
            mediaServiceIntent.putExtra("timerDuration", String.valueOf(timerDuration));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mediaServiceIntent);
            } else {
                startService(mediaServiceIntent);
            }
        } catch (Exception e) {
            Log.e("MediaPlayerActivity", "Error in initMPService", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Send a PAUSE command to the MediaPlayerService
    private void sendPauseCommand() {
        mediaServiceIntent.setAction("PAUSE");
        startService(mediaServiceIntent);
    }

    // Send a RESUME command to the MediaPlayerService
    private void sendResumeCommand() {
        mediaServiceIntent.setAction("RESUME");
        startService(mediaServiceIntent);
    }

    // Initialize the appropriate fragment based on the heading
    private void initializeFragments() {
        try {
            if (heading.equals("Breathe")) {
                BreathTimerFragment frag = new BreathTimerFragment();
                Bundle args = new Bundle();
                args.putString("durationMillis", String.valueOf(timerDuration));
                args.putString("breathMethod", breathMethod);
                frag.setArguments(args);
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.breath_timer_container, frag, BREATH_TIMER_FRAGMENT_TAG).commit();
            } else if (heading.equals("Timer")) {
                SoundtrackArtFragment frag = new SoundtrackArtFragment();
                Bundle args = new Bundle();
                args.putString("soundTrack", soundTrack);
                frag.setArguments(args);
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.soundtrack_art_container, frag, null).commit();
            }
        } catch (Exception e) {
            Log.e("MediaPlayerActivity", "Error in initFrags", e);
            // Optionally, show an error message to the user
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeBreathTimerFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(BREATH_TIMER_FRAGMENT_TAG);
        if (fragment != null) {
            transaction.remove(fragment);
        }
        transaction.commit();
    }

    private void addBreathTimerFragment() {
        BreathTimerFragment frag = new BreathTimerFragment();
        Bundle args = new Bundle();
        args.putString("durationMillis", String.valueOf(seekBar.getMax() - seekBar.getProgress()));
        args.putString("breathMethod", breathMethod);
        frag.setArguments(args);
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.breath_timer_container, frag, BREATH_TIMER_FRAGMENT_TAG).commit();
    }

    // Register BroadcastReceiver to receive media progress updates
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.dsabelli.MEDIA_PROGRESS")) {
                int progress = intent.getIntExtra("progress", 0);
                seekBar.setProgress(progress);
                timerTimeRemaining.setText(Helper.formatTime(progress));
                if (progress == -1) {
                    if (prefsSettings.getInt(prefsSettings.MEDITATED_TODAY_KEY, -1) == DayOfWeek.getCurrentDayOfWeek()) {
                        prefsSettings.incrementInt(prefsSettings.TOTAL_SESSIONS_KEY);
                        prefsSettings.addLong(prefsSettings.TOTAL_MINUTES_KEY, (long) timerDuration);
                    } else {
                        prefsSettings.setInt(prefsSettings.MEDITATED_TODAY_KEY, DayOfWeek.getCurrentDayOfWeek());
                        prefsSettings.setBoolean("day" + (DayOfWeek.getCurrentDayOfWeek()), true);
                        prefsSettings.incrementInt(prefsSettings.TOTAL_SESSIONS_KEY);
                        prefsSettings.addLong(prefsSettings.TOTAL_MINUTES_KEY, (long) timerDuration);
                        prefsSettings.incrementInt(prefsSettings.WEEK_TALLY_KEY);
                        prefsSettings.incrementInt(prefsSettings.CURRENT_STREAK_KEY);
                    }
                    MediaPlayerActivity.this.setResult(RESULT_OK);
                    finish();
                }
            }
        }
    };

    public void handleBack() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResult(RESULT_CANCELED);
                finish(); // Finish the activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

}
