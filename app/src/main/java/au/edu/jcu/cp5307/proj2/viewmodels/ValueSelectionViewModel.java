package au.edu.jcu.cp5307.proj2.viewmodels;

import androidx.lifecycle.ViewModel;

import au.edu.jcu.cp5307.proj2.ui.fragments.ValueSelectionDialogFragment;

public class ValueSelectionViewModel extends ViewModel {
    private Object listener;
    private Object stringifier;

    @SuppressWarnings("unchecked")
    public <T> ValueSelectionDialogFragment.ValueSelectionDialogListener<T> getListener() {
        return (ValueSelectionDialogFragment.ValueSelectionDialogListener<T>) listener;
    }

    @SuppressWarnings("unchecked")
    public <T> ValueSelectionDialogFragment.ValueStringifier<T> getStringifier() {
        return (ValueSelectionDialogFragment.ValueStringifier<T>) stringifier;
    }

    public void setListener(ValueSelectionDialogFragment.ValueSelectionDialogListener<?> listener) {
        this.listener = listener;
    }

    public void setStringifier(ValueSelectionDialogFragment.ValueStringifier<?> stringifier) {
        this.stringifier = stringifier;
    }
}
