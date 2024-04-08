package com.dsabelli.efflo.helpers;

import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.widget.TextView;

public class BreathTimer extends Thread {

    private long[] intervals; // Intervals for each breathing phase in milliseconds
    private TextView[] textViews; // TextViews to display instructions and time
    private String[] instructions; // Instructions for each breathing phase
    private int initialDuration, repetitions; // Total duration and repetitions for the timer
    private long elapsedTime; // Elapsed time for the current interval
    private Vibrator vibrator;  // Vibrator for haptic feedback
    int vibrationMillis; // Duration of vibration in milliseconds
    private int currentRepetition = 0;
    private Handler handler;// Handler for UI updates on the main thread
    private TimerCallback callback;// Callback for updating TextViews
    boolean isRunning = true;// Flag to indicate if the timer is running


    // Constructor initializes the timer with intervals, instructions, TextViews, duration, callback, vibrator, and vibration duration
    public BreathTimer(long[] intervals, String[] instructions, TextView[] textViews, int durationMillis, TimerCallback callback, Vibrator vibrator, int vibrationMillis) {
        this.intervals = intervals;
        this.instructions = instructions;
        this.textViews = textViews;
        this.callback = callback;
        this.vibrator = vibrator;
        this.vibrationMillis = vibrationMillis;
        this.initialDuration = durationMillis - 6000; //for warm-up and cool-down
        setRepetitions(durationMillis);
        this.handler = new Handler(Looper.getMainLooper());
    }

    // Sets the number of repetitions based on the total duration and intervals
    public void setRepetitions(int durationMillis) {
        repetitions = calculateRepetitions(durationMillis);
        isRunning = repetitions != 0;
    }

    // Calculates the number of repetitions based on the total duration and intervals
    private int calculateRepetitions(int durationMillis) {
        long intervalsTotal = 0;
        for (long interval : intervals) {
            intervalsTotal += interval;
        }
        return (int) (Math.floor(((double) durationMillis / intervalsTotal)));
    }

    // Main loop for the timer, handling warm-up, intervals, and cool-down
    public void run() {
        if (repetitions > 0) warmUp();
        while (isRunning && currentRepetition < repetitions) {
            for (int i = 0; isRunning && i < intervals.length; i++) {
                final int index = i;
                final long interval = intervals[i];
                final long startTime = System.currentTimeMillis();

                // Initial UI update
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        vibrator.vibrate(vibrationMillis);
                        textViews[0].setText(instructions[index]);
                        textViews[1].setText(interval / 1000 + " seconds");
                    }
                });

                // Schedule updates every second until the interval expires
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isRunning) return;
                        setElapsedTime((long) (Math.floor(System.currentTimeMillis() - startTime) / 1000) * 1000);
                        String timeToText = (interval - elapsedTime) / 1000 + " seconds";
                        if (elapsedTime < interval) {
                            // Update every second
                            textViews[0].setText(instructions[index]);
                            textViews[1].setText(timeToText);
                            handler.postDelayed(this, 1000); // Schedule the next update in 1 second
                        }
                    }
                }, 1000); // Start the first update in 1 second

                // Wait for the interval to expire before moving to the next iteration
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentRepetition++;
        }
        coolDown();
        this.stopTimer();
    }

    // Starts the timer thread
    public void startTimer() {
        this.start();
    }

    // Stops the timer, cancels vibration, and clears UI updates
    public void stopTimer() {
        isRunning = false;
        vibrator.cancel();
        handler.removeCallbacksAndMessages(null);
    }

    // Handles warm-up phase before first interval
    private void warmUp() {
        for (int i = 3; i > 0; i--) {
            final int count = i;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textViews[0].setText(String.valueOf(count));
                    textViews[1].setText("Get ready");
                }
            });
            try {
                Thread.sleep(1000); // Wait for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Handles cool-down phase after last interval
    private void coolDown() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                textViews[0].setText("");
                textViews[1].setText("Session complete...");
            }
        });
        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    // Callback interface for updating TextViews
    public interface TimerCallback {
        void updateTextView(TextView textView, int interval);
    }
}
