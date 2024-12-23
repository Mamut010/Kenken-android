package au.edu.jcu.cp5307.proj2.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import au.edu.jcu.cp5307.proj2.R;
import au.edu.jcu.cp5307.proj2.utils.Pair;
import au.edu.jcu.cp5307.proj2.utils.helpers.ObjectHelpers;
import au.edu.jcu.cp5307.proj2.utils.helpers.UiHelpers;
import au.edu.jcu.cp5307.proj2.viewmodels.ValueSelectionViewModel;

public class ValueSelectionDialogFragment<T extends Serializable> extends DialogFragment {
    public interface ValueSelectionDialogListener<T> {
        void onValueClick(DialogFragment dialog, int position, T selectedValue);
        void onClearButtonClick(DialogFragment dialog);
        void onCancelButtonClick(DialogFragment dialog);
    }

    public interface ValueStringifier<T> {
        String stringify(T value, int index);
    }

    private static final String VALUES_KEY = "VALUES";
    private static final String CLEARABLE_KEY = "CLEARABLE";
    private static final String TITLE_KEY = "TITLE";

    private ArrayList<T> values;
    private boolean clearable;
    private String title;
    private ValueSelectionDialogListener<T> listener;
    private ValueStringifier<T> stringifier;
    private ValueSelectionViewModel viewModel;

    public static <T extends Serializable> ValueSelectionDialogFragment<T>
        newInstance(Collection<T> values) {
        ValueSelectionDialogFragment<T> fragment = new ValueSelectionDialogFragment<>();
        Bundle args = new Bundle();

        ArrayList<T> arrayListValues = new ArrayList<>(values != null ? values : Collections.emptyList());
        args.putSerializable(VALUES_KEY, arrayListValues);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            initArgs(args);
        }

        viewModel = new ViewModelProvider(this).get(ValueSelectionViewModel.class);
        if (savedInstanceState != null) {
            restoreFromSavedState(savedInstanceState);
            restoreObjectsFromViewModel();
        }
        else {
            viewModel.setListener(listener);
            viewModel.setStringifier(stringifier);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(CLEARABLE_KEY, clearable);
        outState.putString(TITLE_KEY, title);
    }

    public void setClearable(boolean clearable) {
        this.clearable = clearable;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDialogListener(ValueSelectionDialogListener<T> listener) {
        this.listener = listener;
    }

    public void setValueStringifier(ValueStringifier<T> stringifier) {
        this.stringifier = stringifier;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        ValueSelectionDialogListener<T> listener = viewModel.getListener();

        builder
                .setTitle(title)
                .setItems(getStringValues(), (dialog, which) ->
                        ObjectHelpers.consumeIfExists(
                                listener,
                                obj -> obj.onValueClick(this, which, values.get(which))
                        )
                );

        if (isCancelable()) {
            builder.setNeutralButton(android.R.string.cancel, (dialog, which) ->
                    ObjectHelpers.consumeIfExists(
                            listener,
                            obj -> obj.onCancelButtonClick(this)
                    )
            );
        }
        if (clearable) {
            builder.setPositiveButton(R.string.clear, (dialog, which) ->
                    ObjectHelpers.consumeIfExists(
                            listener,
                            obj -> obj.onClearButtonClick(this)
                    )
            );
        }

        return builder.create();
    }

    @SuppressWarnings("unchecked")
    private void initArgs(Bundle args) {
        values = (ArrayList<T>) UiHelpers.getSerializable(args, VALUES_KEY, ArrayList.class);
    }

    private void restoreFromSavedState(Bundle savedInstanceState) {
        clearable = savedInstanceState.getBoolean(CLEARABLE_KEY);
        title = savedInstanceState.getString(TITLE_KEY);
    }

    private void restoreObjectsFromViewModel() {
        listener = viewModel.getListener();
        stringifier = viewModel.getStringifier();
    }

    private String[] getStringValues() {
        if (values == null) {
            return new String[0];
        }
        ValueStringifier<T> stringifier = viewModel.getStringifier();
        return IntStream
                .range(0, values.size())
                .mapToObj(i -> new Pair<>(values.get(i), i))
                .map(pair -> stringifier != null
                        ? stringifier.stringify(pair.first(), pair.second())
                        : String.valueOf(pair.first())
                )
                .toArray(String[]::new);
    }
}
