package com.dsabelli.efflo.helpers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;


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
// Method to determine if two dates are in the same calendar week.
    public static boolean inSameCalendarWeek(LocalDate firstDate) {
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        WeekFields weekFields = WeekFields.of(Locale.US);
        int firstDatesCalendarWeek = firstDate.get(weekFields.weekOfWeekBasedYear());
        int secondDatesCalendarWeek = currentDate.get(weekFields.weekOfWeekBasedYear());
        int firstWeekBasedYear = firstDate.get(weekFields.weekBasedYear());
        int secondWeekBasedYear = currentDate.get(weekFields.weekBasedYear());

        return firstDatesCalendarWeek == secondDatesCalendarWeek && firstWeekBasedYear == secondWeekBasedYear;
    }
}
