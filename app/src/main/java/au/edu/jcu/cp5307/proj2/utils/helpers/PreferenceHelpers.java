package au.edu.jcu.cp5307.proj2.utils.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class PreferenceHelpers {
    private PreferenceHelpers() {}

    public static boolean getSharedPreference(Context context, int resId, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(context.getString(resId), defaultValue);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
