package com.dsabelli.efflo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dsabelli.efflo.R;
import com.dsabelli.efflo.helpers.BreathTimer;
import com.dsabelli.efflo.sharedPrefs.SharedPrefsSettings;
import com.dsabelli.efflo.viewModels.TimerViewModel;

public class BreathTimerFragment extends Fragment implements BreathTimer.TimerCallback {
    private TimerViewModel viewModel;
    private TextView breathCounter, breathInstruction;
    private String soundTrack, breathMethod;
    private int durationMillis;
    private long[] intervals;
    private BreathTimer timer;
    private Vibrator vibrator;
    int vibrationMillis;
    SharedPrefsSettings prefsSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_breath_timer, container, false);
        prefsSettings = SharedPrefsSettings.getInstance(getActivity());
        // Initialize views and vibrator
        breathCounter = view.findViewById(R.id.breath_counter_textview);
        breathInstruction = view.findViewById(R.id.breath_instruction_textview);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        // Extract arguments and initialize variables (if arguments exist)
        if (getArguments() != null) {
            extractArguments(getArguments());
            initializeTimer();
        }

        return view;
    }

    //     Extracts arguments (durationMillis, breathMethod) from the fragment arguments bundle
    //     and populates relevant member variables.
    private void extractArguments(Bundle args) {
        try {
            durationMillis = Integer.parseInt(args.getString("durationMillis"));
            breathMethod = args.getString("breathMethod");
            intervals = parseBreathMethod(breathMethod);
        } catch (Exception e) {
            Log.e("BreathTimerFrag", "Error in extractArgs", e);
            // Optionally, show an error message to the user
            Toast.makeText(getActivity(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    //    Parses the breath method string (format "Inhale-Hold-Exhale") into an array of longs
    //     representing the duration (in milliseconds) of each breath stage.
    private long[] parseBreathMethod(String breathMethod) {
        String[] splitBreathMethod = breathMethod.split("–");
        long[] intervals = new long[splitBreathMethod.length];
        for (int i = 0; i < intervals.length; i++) {
            intervals[i] = Long.parseLong(splitBreathMethod[i]) * 1000;
        }
        return intervals;
    }

    //  Initializes the BreathTimer object with extracted arguments, text views,
    //  and sets it within the TimerViewModel. Starts the timer.
    private void initializeTimer() {
        try {
            String[] instructions = {"Inhale", "Hold", "Exhale"};
            TextView[] textViews = {breathCounter, breathInstruction};
            vibrationMillis = prefsSettings.getBoolean(prefsSettings.VIBRATION_KEY, false) ? 200 : 0;
            timer = new BreathTimer(intervals, instructions, textViews, durationMillis, this, vibrator, vibrationMillis);
            viewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);
            viewModel.setBreathTimer(timer);
            viewModel.startTimer();
        } catch (Exception e) {
            Log.e("BreathTimerFrag", "Error in initTimer", e);
            // Optionally, show an error message to the user
            Toast.makeText(getActivity(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (vibrator != null) {
            vibrator.cancel();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.stopTimer();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override
    public void updateTextView(TextView textView, int interval) {
        textView.setText(interval / 1000 + " seconds");
    }
}
