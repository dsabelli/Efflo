package com.dsabelli.efflo.helpers;

import android.annotation.SuppressLint;

public class Helper {

    @SuppressLint("DefaultLocale")
    public static String formatTime(int millis) {
        // Calculate minutes and seconds from milliseconds
        int minutes = millis / 1000 / 60;
        int seconds = (millis / 1000) % 60;
        // Format and return the time as a string in "MM:SS" format
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static int stringMinsToMillis(String mins) {
        // Convert minutes string to integer and then to milliseconds
        return Integer.parseInt(mins) * 60000;
    }
}
