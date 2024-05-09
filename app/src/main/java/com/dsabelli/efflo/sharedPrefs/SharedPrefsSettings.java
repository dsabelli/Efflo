package com.dsabelli.efflo.sharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsSettings {
    private static final String PREFS_NAME = "EffloSettings";
    public final String LAST_RESET_KEY = "last_reset";
    public final String DISCLAIMER_KEY = "disclaimer";
    public final String TIMER_HEADING = "Timer";
    public final String VIBRATION_KEY = "vibration";
    public final String DND_KEY = "dnd";
    public final String WEEK_TALLY_KEY = "week_tally";
    public final String BREATH_KEY = "breath_preset";
    public final String BREATH_TIME_KEY = "breath_time_preset";
    public final String BREATH_SOUNDTRACK_KEY = "breath_soundtrack_preset";
    public final String TIMER_TIME_KEY = "timer_time_preset";
    public final String TIMER_SOUNDTRACK_KEY = "timer_soundtrack_preset";
    public final String GOAL_KEY = "goal_preset";
    public final String TOTAL_MINUTES_KEY = "total_minutes";
    public final String TOTAL_SESSIONS_KEY = "total_completed";
    public final String CURRENT_STREAK_KEY = "current_streak";
    public final String LONGEST_STREAK_KEY = "longest_streak";
    public final String LAST_LOGIN_KEY = "last_login";
    public final String MEDITATED_TODAY_KEY = "meditated_today";
    private final SharedPreferences sharedPreferences;

    private SharedPrefsSettings(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPrefsSettings instance;

    public static synchronized SharedPrefsSettings getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsSettings(context);
        }
        return instance;
    }

    // Method to save a string value
    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Method to retrieve a string value
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Method to save a boolean value
    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // Method to retrieve a boolean value
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Method to save an integer value
    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // Method to retrieve an integer value
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }
    // Method to increment an integer value

    public void incrementInt(String key){
        int i=getInt(key,0);
        setInt(key,i+1);
    }
    // Method to save a long value
    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    // Method to retrieve a long value
    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void addLong(String key, long value) {
        setLong(key,getLong(key,0)+value);
    }
    // Method to clear all saved preferences
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

