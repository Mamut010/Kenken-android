package au.edu.jcu.cp5307.proj2.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.lang.Math;

public final class Converters {
    private Converters() {}

    private static final char FIRST_UPPER_CHAR = 'A';

    public static int upperCharToOrdinal(char upperChar) {
        return upperChar - FIRST_UPPER_CHAR + 1;
    }

    public static char ordinalToUpperChar(int ordinal) {
        return Character.toString((char) (ordinal - 1 + FIRST_UPPER_CHAR)).charAt(0);
    }

    public static float dpToPxF(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(dpToPxF(context, dp));
    }
}
