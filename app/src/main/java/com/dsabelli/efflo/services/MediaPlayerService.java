package com.dsabelli.efflo.services;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.dsabelli.efflo.MainActivity;
import com.dsabelli.efflo.R;

public class MediaPlayerService extends Service implements MediaPlayer.OnErrorListener {

    int NOTIFICATION_ID = 1;
    public static final String ACTION_PLAY = "PLAY";
    MediaPlayer mediaPlayer;
    CountDownTimer countDownTimer;
    String soundTrack;
    String timerDuration;
    long durationMillis;
    long elapsedMillis = -1000;
    long remainingMillis;
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean isServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // Register a BroadcastReceiver to update the media position
        IntentFilter intentFilter = new IntentFilter("com.dsabelli.UPDATE_MEDIA_POSITION");
        ContextCompat.registerReceiver(this, updatePositionReceiver, intentFilter, null, null, ContextCompat.RECEIVER_EXPORTED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release resources and stop the service
        releaseTimer();
        releaseMediaPlayer();
        stopUpdatingProgress();
        unregisterReceiver(updatePositionReceiver);
    }

    private BroadcastReceiver updatePositionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.dsabelli.UPDATE_MEDIA_POSITION")) {
                // Update the media player position based on the received intent
                int position = intent.getIntExtra("position", 0);
                mediaPlayer.seekTo(position);
                elapsedMillis = position;
            }
        }
    };

    private void initializeMediaPlayer() {
        // Initialize the MediaPlayer with the selected sound track
        switch (soundTrack) {
            case "sound_0":
                mediaPlayer = MediaPlayer.create(this, R.raw.birds);
                break;
            case "sound_1":
                mediaPlayer = MediaPlayer.create(this, R.raw.celestial);
                break;
            case "sound_2":
                mediaPlayer = MediaPlayer.create(this, R.raw.peace);
                break;
            case "sound_3":
                mediaPlayer = MediaPlayer.create(this, R.raw.water);
                break;
            case "sound_4":
                mediaPlayer = MediaPlayer.create(this, R.raw.silence);
                mediaPlayer.setVolume(0.0f,0.0f);
                break;
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle the intent to start the service, gets the soundTrack name and timer duration
        // from MediaPlayerActivity
        if (intent != null && intent.getExtras() != null) {
            soundTrack = intent.getStringExtra("soundTrack");
            timerDuration = intent.getStringExtra("timerDuration");
            assert timerDuration != null;
            // Parse from intent to get duration in milliseconds
            durationMillis = Long.parseLong(timerDuration);
            // If no current MediaPlayer, initialize it and set the timer
            if (mediaPlayer == null) {
                initializeMediaPlayer();
                startTimer(durationMillis);
            }

            // Check if the service is already running
            if (!isServiceRunning) {
                // Your existing code to start the foreground service and display a notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.MEDIA_CHANNEL_ID)
                        .setSmallIcon(R.drawable.efflo)
                        .setContentTitle("")
                        .setContentText("Foreground service is running")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                Notification notification = builder.build();
                startForeground(NOTIFICATION_ID, notification);
                isServiceRunning = true; // Set the flag to true after starting the service
            }
        }
        // Handle play/pause actions
        // For resume, calculate remaining milliseconds and resume timer with updated duration
        if (intent != null) {
            String action = intent.getAction();
            if ("PAUSE".equals(action)) {
                pauseTimer();
                mediaPlayer.pause();
            } else if ("RESUME".equals(action)) {
                pauseTimer();
                remainingMillis = durationMillis - elapsedMillis;
                resumeTimer(remainingMillis);
                mediaPlayer.start();
            }
        }
        startUpdatingProgress();
        return START_STICKY;
    }

    private void startTimer(long durationMillis) {
        // Start a countdown timer for the media playback
        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            // Keep track of elapsed milliseconds. When > timer duration, finish the timer
            public void onTick(long millisUntilFinished) {
                elapsedMillis += 1000;
                if (elapsedMillis >= getDurationMillis()) {
                    countDownTimer.onFinish();
                }
            }

            public void onFinish() {
                // Stop the media playback and the service when the countdown finishes
                mediaPlayer.stop();
                countDownTimer.cancel();
                Intent intent = new Intent("com.dsabelli.MEDIA_PROGRESS");
                intent.putExtra("progress", -1);
                sendBroadcast(intent);
                stopForeground(true);
                stopSelf();
            }
        }.start();
    }

    private void startUpdatingProgress() {
        // Start updating the media playback progress
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int progress = (int) elapsedMillis;
                    Intent intent = new Intent("com.dsabelli.MEDIA_PROGRESS");
                    intent.putExtra("progress", progress);
                    sendBroadcast(intent);
                }
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(runnable);
    }

    private void stopUpdatingProgress() {
        // Stop updating the media playback progress
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    private void pauseTimer() {
        // Pause the countdown timer
        if (countDownTimer != null) {
            stopUpdatingProgress();
            countDownTimer.cancel();
        }
    }

    private void resumeTimer(long remainingMillis) {
        // Resume the countdown timer with the remaining time
        startUpdatingProgress();
        startTimer(remainingMillis);
    }

    private void releaseMediaPlayer() {
        // Release the MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void releaseTimer() {
        // Cancel the countdown timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // Log the error for debugging purposes
        Log.e(getPackageName(), String.format("MediaPlayer error: %s, extra: %s", what, extra));
        // Reset the MediaPlayer instance to recover from the error
        mp.reset();
        // Reinitialize the MediaPlayer with the selected sound track
        initializeMediaPlayer();
        // Return true to indicate that the error has been handled
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Return null because this service is not bound
    }
}
