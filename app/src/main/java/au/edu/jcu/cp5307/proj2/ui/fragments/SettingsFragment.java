package au.edu.jcu.cp5307.proj2.ui.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import au.edu.jcu.cp5307.proj2.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}