package com.dsabelli.efflo.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dsabelli.efflo.R;

public class SoundtrackArtFragment extends Fragment {
    String soundTrack;
    ImageView soundtrackImageView;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization code here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_soundtrack_art, container, false);
        initializeSoundtrackArt();
        return view;
    }

    private void initializeSoundtrackArt() {
        try {
            // Retrieve arguments passed to the fragment
            Bundle args = getArguments();
            if (args != null) {
                soundTrack = args.getString("soundTrack");

                // Initialize the ImageView for displaying the soundtrack art
                soundtrackImageView = view.findViewById(R.id.soundtrack_art);

                // Set the image resource based on the soundtrack selected
                switch (soundTrack) {
                    case "sound_0":
                        soundtrackImageView.setImageResource(R.drawable.birds);
                        break;
                    case "sound_1":
                        soundtrackImageView.setImageResource(R.drawable.celestial);
                        break;
                    case "sound_2":
                        soundtrackImageView.setImageResource(R.drawable.peace);
                        break;
                    case "sound_3":
                        soundtrackImageView.setImageResource(R.drawable.water);
                        break;
                    case "sound_4":
                        soundtrackImageView.setImageResource(R.drawable.silence);
                        break;
                }
            }


        } catch (Exception e) {
            Log.e("SoundtrackrFrag", "Error in initSoundArt", e);
            // Optionally, show an error message to the user
            Toast.makeText(getActivity(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
