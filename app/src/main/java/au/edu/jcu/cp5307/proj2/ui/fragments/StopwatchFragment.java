package au.edu.jcu.cp5307.proj2.ui.fragments;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.edu.jcu.cp5307.proj2.R;
import au.edu.jcu.cp5307.proj2.viewmodels.StopwatchViewModel;

public class StopwatchFragment extends Fragment {
    private TextView timeTextView;
    private StopwatchViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stopwatch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFields(view);
        setupObservers();
    }

    public String getTimeString() {
        String timeString = viewModel.getStopwatchTimeString().getValue();
        return timeString != null ? timeString : getString(R.string.stopwatch_initial_time);
    }

    public long getTimeInMillis() {
        return viewModel.getTimeInMillis();
    }

    public void startStopwatch() {
        viewModel.startStopwatch();
    }

    public void pauseStopwatch() {
        viewModel.pauseStopwatch();
    }

    public void resetStopwatch() {
        viewModel.resetStopwatch();
    }

    private void initFields(View view) {
        timeTextView = view.findViewById(R.id.stopwatch_time_textview);
    }

    private void setupObservers() {
        LiveData<String> stopwatchTime = viewModel.getStopwatchTimeString();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        stopwatchTime.observe(lifecycleOwner, timeTextView::setText);
    }
}