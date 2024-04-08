package com.dsabelli.efflo;

import static com.dsabelli.efflo.MainActivity.MEDIA_CHANNEL_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;

public class BaseActivity extends AppCompatActivity {
SharedPrefsSettings prefsSettings;
    NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsSettings = SharedPrefsSettings.getInstance(this);
        handleNotificationChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Turn off DND when app is destroyed
        handleDND();
    }
    // Reset DND setting, to be used in onStop
    private void handleDND() {
        prefsSettings.setBoolean(prefsSettings.DND_KEY, false);
        if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted()) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }
    }
    // Create a notification channel for media playback controls
    private void handleNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Media Player";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(MEDIA_CHANNEL_ID, channelName, importance);
            channel.setDescription("Media playback controls");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}