package au.edu.jcu.cp5307.proj2.application;

import androidx.annotation.NonNull;

import java.util.Locale;

import au.edu.jcu.cp5307.proj2.constants.Constants;

public class Stopwatch {
    public static final String TIME_STRING_FORMAT = "%02d:%02d:%02d";

    private long hour;
    private long min;
    private long sec;
    private long ms;

    public Stopwatch() {
        hour = min = sec = ms = 0;
    }

    public long getTimeInMillis() {
        long totalMin = hour * Constants.Conversion.MIN_PER_HOUR + min;
        long totalSec = totalMin * Constants.Conversion.SEC_PER_MIN + sec;
        return totalSec * Constants.Conversion.MS_PER_SEC + ms;
    }

    public String getTimeString() {
        return String.format(Locale.getDefault(), TIME_STRING_FORMAT, hour, min, sec);
    }

    public String reset() {
        if (!isAtInitialState()) {
            hour = min = sec = ms = 0;
        }
        return getTimeString();
    }

    public String tick(long byMs) {
        ms += byMs;
        long bySec = ms / Constants.Conversion.MS_PER_SEC;
        ms %= Constants.Conversion.MS_PER_SEC;

        if (bySec == 0) {
            return getTimeString();
        }

        sec += bySec;
        long byMin = sec / Constants.Conversion.SEC_PER_MIN;
        sec %= Constants.Conversion.SEC_PER_MIN;
        if (byMin > 0) {
            min += byMin;
            hour += min / Constants.Conversion.MIN_PER_HOUR;
            min %= Constants.Conversion.MIN_PER_HOUR;
        }

        return getTimeString();
    }

    @NonNull
    @Override
    public String toString() {
        return getTimeString();
    }

    private boolean isAtInitialState() {
        return hour == 0 && min == 0 && sec == 0 && ms == 0;
    }
}
