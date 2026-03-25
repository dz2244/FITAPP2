package com.example.fitapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitapp.R;

/**
 * Fragment responsible for tracking the user's daily calorie intake.
 * Users can log meals and view their progress towards their daily calorie target.
 */
public class CalorieTrackingFragment extends Fragment {

    /**
     * Default constructor for CalorieTrackingFragment.
     */
    public CalorieTrackingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calorie_tracking, container, false);
    }
}
