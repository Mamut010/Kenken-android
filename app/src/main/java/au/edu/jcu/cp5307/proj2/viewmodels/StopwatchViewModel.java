package au.edu.jcu.cp5307.proj2.viewmodels;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import au.edu.jcu.cp5307.proj2.constants.Constants;
import au.edu.jcu.cp5307.proj2.application.Stopwatch;

public class StopwatchViewModel extends ViewModel {
    private static final int SPEED_MS = Constants.Conversion.MS_PER_SEC / 10;

    private final Stopwatch stopwatch;
    private final MutableLiveData<String> timeStringLiveData;
    private final Handler handler;
    private Runnable tickRunnable;

    public StopwatchViewModel() {
        stopwatch = new Stopwatch();
        timeStringLiveData = new MutableLiveData<>(stopwatch.getTimeString());
        handler = new Handler(Looper.getMainLooper());
    }

    public LiveData<String> getStopwatchTimeString() {
        return timeStringLiveData;
    }

    public void startStopwatch() {
        if (isStopwatchRunning()) {
            return;
        }

        tickRunnable = () -> {
            String newTimeString = stopwatch.tick(SPEED_MS);
            timeStringLiveData.setValue(newTimeString);
            handler.postDelayed(tickRunnable, SPEED_MS);
        };

        handler.post(tickRunnable);
    }

    public void pauseStopwatch() {
        if (!isStopwatchRunning()) {
            return;
        }

        handler.removeCallbacks(tickRunnable);
        tickRunnable = null;
    }

    public void resetStopwatch() {
        String newTimeString = stopwatch.reset();
        timeStringLiveData.setValue(newTimeString);
    }

    public long getTimeInMillis() {
        return stopwatch.getTimeInMillis();
    }

    private boolean isStopwatchRunning() {
        return tickRunnable != null;
    }
}