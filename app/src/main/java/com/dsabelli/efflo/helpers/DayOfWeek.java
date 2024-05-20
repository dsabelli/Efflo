package com.dsabelli.efflo.helpers;

import java.util.Calendar;


public class DayOfWeek {
    private static int currentDayOfWeek;
    static Calendar calendar = Calendar.getInstance();

    // Constructor that initializes the current day of the week when an instance is created
    public DayOfWeek() {
        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    }

    // Method to get the current day of the week
    public static int getCurrentDayOfWeek() {
        updateCurrentDayOfWeek(); // Update the current day of the week before returning it
        return currentDayOfWeek;
    }

    // Method to update the current day of the week
    public static void updateCurrentDayOfWeek() {
        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    }

}
