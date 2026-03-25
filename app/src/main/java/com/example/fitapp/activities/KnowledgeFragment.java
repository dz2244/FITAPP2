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
 * Fragment that provides users with educational content and articles.
 * Helps users gain knowledge about fitness, nutrition, and wellness.
 */
public class KnowledgeFragment extends Fragment {

    /**
     * Default constructor for KnowledgeFragment.
     */
    public KnowledgeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge, container, false);
    }
}
