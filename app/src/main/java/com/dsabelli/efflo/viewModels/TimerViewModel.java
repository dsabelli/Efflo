package com.dsabelli.efflo.viewModels;

import androidx.lifecycle.ViewModel;

import com.dsabelli.efflo.helpers.BreathTimer;

public class TimerViewModel extends ViewModel {
    private BreathTimer breathTimer;

    // Method to set the BreathTimer instance. This is useful for injecting a mock or a real BreathTimer for testing purposes.
    public void setBreathTimer(BreathTimer breathTimer) {
        this.breathTimer = breathTimer;
    }
    public  BreathTimer getBreathTimer(){
        return  breathTimer;
    }

    // Method to stop the timer. It checks if the BreathTimer instance is not null before calling stopTimer() on it.
    public void stopTimer() {
        if (breathTimer != null) {
            breathTimer.stopTimer();
        }
    }

    // Method to configure the BreathTimer with a specified duration in milliseconds. It sets the number of repetitions for the timer.
    public void handleBreathTimer(int durationMillis) {
        if (breathTimer != null) {
            breathTimer.setRepetitions(durationMillis);
        }
    }

    // Method to start the timer. It checks if the BreathTimer instance is not null before calling startTimer() on it.
    public void startTimer() {
        if (breathTimer != null) {
            breathTimer.startTimer();
        }
    }

}
