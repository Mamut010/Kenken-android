package au.edu.jcu.cp5307.proj2.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import au.edu.jcu.cp5307.proj2.R;
import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.ui.fragments.GameGridFragment;
import au.edu.jcu.cp5307.proj2.ui.fragments.HintTaskFragment;
import au.edu.jcu.cp5307.proj2.ui.fragments.StopwatchFragment;
import au.edu.jcu.cp5307.proj2.ui.fragments.ValueSelectionDialogFragment;
import au.edu.jcu.cp5307.proj2.constants.Constants;
import au.edu.jcu.cp5307.proj2.utils.helpers.KenkenHelpers;
import au.edu.jcu.cp5307.proj2.utils.Pair;
import au.edu.jcu.cp5307.proj2.utils.Point;
import au.edu.jcu.cp5307.proj2.utils.helpers.PreferenceHelpers;
import au.edu.jcu.cp5307.proj2.utils.helpers.UiHelpers;
import au.edu.jcu.cp5307.proj2.viewmodels.GameViewModel;
import au.edu.jcu.cp5307.proj2.viewmodels.UserViewModel;

public class GameActivity extends AppCompatActivity
        implements HintTaskFragment.HintTaskCallbacks, GameGridFragment.GameLoadListener {
    private static final String HINT_TASK_FRAGMENT_TAG = "HINT_TASK_FRAGMENT";
    private static final String STRINGIFIED_KENKEN_KEY = "STRINGIFIED_KENKEN";
    private static final String GAME_COMPLETED_DIALOG_SHOWN_KEY = "GAME_COMPLETED_DIALOG_SHOWN";
    private static final String HINT_TASK_RUNNING_KEY = "HINT_TASK_RUNNING";
    private static final String SAVED_HINT_KEY = "SAVED_HINT";

    private Button backButton;
    private Button switchButton;
    private TextView recordTextView;
    private TextView mistakeTextView;
    private Button hintButton;
    private GameViewModel gameViewModel;
    private UserViewModel userViewModel;
    private StopwatchFragment stopwatchFragment;
    private GameGridFragment gameGridFragment;
    private HintTaskFragment hintTaskFragment;
    private ContentLoadingProgressBar progressBar;
    private String lastSavedStringifiedKenken;
    private boolean isGameCompletedDialogShown;
    private boolean isHintTaskRunning;
    private Pair<Point, Integer> savedHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initFields();
        if (savedInstanceState != null) {
            restoreFromSavedState(savedInstanceState);
        }
        setupObserversAndListeners();
        runGame();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        KenkenGame kenken = gameViewModel.getKenken().getValue();
        if (kenken != null) {
            outState.putString(STRINGIFIED_KENKEN_KEY, kenken.toString());
        }
        outState.putBoolean(GAME_COMPLETED_DIALOG_SHOWN_KEY, isGameCompletedDialogShown);
        outState.putBoolean(HINT_TASK_RUNNING_KEY, isHintTaskRunning);
        if (savedHint != null) {
            outState.putSerializable(SAVED_HINT_KEY, savedHint);
        }
    }

    private void initFields() {
        initStateControlFields();
        initViewModels();
        initDirectViews();
        initFragments();
    }

    private void initStateControlFields() {
        lastSavedStringifiedKenken = Constants.Type.EMPTY_STRING;
        isGameCompletedDialogShown = false;
        isHintTaskRunning = false;
        savedHint = null;
    }

    private void initViewModels() {
        gameViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(GameViewModel.initializer)
        )
                .get(GameViewModel.class);
        userViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(UserViewModel.initializer)
        )
                .get(UserViewModel.class);
    }

    private void initDirectViews() {
        progressBar = findViewById(R.id.game_activity_hint_progress_bar);
        backButton = findViewById(R.id.game_back_button);
        switchButton = findViewById(R.id.game_switch_button);
        recordTextView = findViewById(R.id.game_record_textview);
        mistakeTextView = findViewById(R.id.game_mistake_textview);
        hintButton = findViewById(R.id.game_hint_button);
    }

    private void initFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        stopwatchFragment = (StopwatchFragment) fragmentManager
                .findFragmentById(R.id.game_stopwatch_fragment);

        gameGridFragment = (GameGridFragment) fragmentManager
                .findFragmentById(R.id.game_activity_game_grid_fragment);
        Objects.requireNonNull(gameGridFragment).setViewModel(gameViewModel);

        hintTaskFragment = (HintTaskFragment) fragmentManager
                .findFragmentByTag(HINT_TASK_FRAGMENT_TAG);
    }

    @SuppressWarnings("unchecked")
    private void restoreFromSavedState(Bundle savedInstanceState) {
        lastSavedStringifiedKenken = savedInstanceState.getString(STRINGIFIED_KENKEN_KEY);
        isGameCompletedDialogShown = savedInstanceState.getBoolean(GAME_COMPLETED_DIALOG_SHOWN_KEY);
        isHintTaskRunning = savedInstanceState.getBoolean(HINT_TASK_RUNNING_KEY);
        savedHint = (Pair<Point, Integer>)
                UiHelpers.getSerializable(savedInstanceState, SAVED_HINT_KEY, Pair.class);
    }

    private void setupObserversAndListeners() {
        setupGameViewModelObservers();
        setupUserViewModelObservers();
        setBackButtonListener();
        setSwitchButtonListener();
        setHintButtonListener();
    }

    private void setupGameViewModelObservers() {
        gameViewModel.getIsGameCompleted().observe(this, this::handleIsGameCompleted);
        gameViewModel.getMistakeCount().observe(this, mistakeCount ->
                mistakeTextView.setText(getString(R.string.mistake_format, mistakeCount))
        );
        gameViewModel.getHintUsedCount().observe(this, hintUsedCount -> {
                int maxHintCount = gameViewModel.getMaxHintCount();
                int remainingHintCount = maxHintCount - hintUsedCount;
                hintButton.setText(getString(R.string.hint_format, remainingHintCount, maxHintCount));
                boolean shouldEnableHintButton = remainingHintCount > 0
                        && !Boolean.TRUE.equals(gameViewModel.getIsGameCompleted().getValue());
                setEnabledHintButton(shouldEnableHintButton);
        });
    }

    private void setupUserViewModelObservers() {
        userViewModel.getRecord().observe(this, record ->
            recordTextView.setText(getString(R.string.record_format, record))
        );
    }

    private void setBackButtonListener() {
        backButton.setOnClickListener(v -> {
            int msgId, positiveButtonTextId;
            if (isRunningGameNotCompleted()) {
                msgId = R.string.back_to_main_dialog_warning_msg;
                positiveButtonTextId = R.string.proceed_anyway;
            }
            else {
                msgId = R.string.back_to_main_dialog_msg;
                positiveButtonTextId = android.R.string.yes;
            }
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder
                    .setTitle(getString(R.string.back_to_main_dialog_title))
                    .setMessage(getString(msgId))
                    .setPositiveButton(positiveButtonTextId, (dialogInterface, which) -> {
                        Intent backToMainIntent = new Intent(this, MainActivity.class);
                        startActivity(backToMainIntent);
                    })
                    .setNeutralButton(android.R.string.cancel, null);
            dialogBuilder.create().show();
        });
    }

    private void setSwitchButtonListener() {
        switchButton.setOnClickListener(v -> showGameSelectionDialog(true));
    }

    private void setHintButtonListener() {
        hintButton.setOnClickListener(v -> {
            hintTaskFragment = new HintTaskFragment(gameViewModel);
            attachHintTaskFragment();
        });
    }

    public void showHintDialog(Pair<Point, Integer> hint) {
        savedHint = hint;
        String title = getString(R.string.hint_dialog_title);
        CharSequence msg;
        if (hint == null) {
            setEnabledHintButton(false);
            msg = getString(R.string.hint_dialog_no_hint_msg);
        }
        else {
            Point point = hint.first();
            int row = point.row();
            int column = point.column();
            String superscript = gameViewModel.getSuperscriptInCageOfPoint(point);
            Integer previousValue = gameViewModel.getPointValue(point);
            Integer hintedValue = hint.second();
            String html;
            if (previousValue == null) {
                 html = getString(
                        R.string.hint_dialog_msg_new_number_html,
                        row,
                        column,
                        superscript,
                        hintedValue
                );
            }
            else {
                html = getString(
                        R.string.hint_dialog_msg_fix_number_html,
                        row,
                        column,
                        superscript,
                        previousValue,
                        hintedValue
                );
            }
            msg = UiHelpers.fromHtml(html);
        }
        UiHelpers.createInfoDialog(
                this,
                title,
                msg,
                (dialog, which) -> savedHint = null
        )
                .show();
    }

    private void runGame() {
        if (gameViewModel.isGameStarted()) {
            if (isHintTaskRunning) {
                progressBar.show();
            }
            if (savedHint != null) {
                showHintDialog(savedHint);
            }
            return;
        }

        boolean shouldShowRule = PreferenceHelpers
                .getSharedPreference(this, R.string.preference_show_rule, true);
        if (shouldShowRule) {
            UiHelpers.createInfoDialog(
                    this,
                    getString(R.string.game_rule_dialog_title),
                    getString(R.string.game_rule),
                    (dialog, which) -> showGameSelectionDialog(false)
            )
                    .show();
        }
        else {
            showGameSelectionDialog(false);
        }
    }

    private void showGameSelectionDialog(boolean cancelable) {
        List<Integer> selection = gameViewModel
                .getIdsSortedByGameSize()
                .stream()
                .filter(id -> !Objects.equals(id, gameViewModel.getCurrentGameId()))
                .collect(Collectors.toList());
        ValueSelectionDialogFragment<Integer> dialog = ValueSelectionDialogFragment.newInstance(selection);
        dialog.setCancelable(cancelable);
        dialog.setTitle(getString(R.string.select_game_dialog_title));
        dialog.setValueStringifier((value, index) -> {
            KenkenGame kenken = gameViewModel.getKenkenById(value);
            int size = Objects.requireNonNull(kenken).getSize();
            return getString(R.string.game_option_summary,
                    value, size, KenkenHelpers.assertDifficulty(size).getLabel());
        });
        dialog.setDialogListener(new ValueSelectionDialogFragment.ValueSelectionDialogListener<>() {
            @Override
            public void onValueClick(DialogFragment dialog, int position, Integer selectedValue) {
                if (!isRunningGameNotCompleted()) {
                    gameViewModel.fetchGame(selectedValue);
                    return;
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
                dialogBuilder
                        .setTitle(getString(R.string.switch_game_warning_dialog_title))
                        .setMessage(getString(R.string.switch_game_warning_dialog_msg))
                        .setPositiveButton(
                                R.string.proceed_anyway,
                                (dialogInterface, which) -> gameViewModel.fetchGame(selectedValue)
                        )
                        .setNeutralButton(android.R.string.cancel, null);
                dialogBuilder.create().show();
            }

            @Override
            public void onClearButtonClick(DialogFragment dialog) {
                // DO NOTHING
            }

            @Override
            public void onCancelButtonClick(DialogFragment dialog) {
                // DO NOTHING
            }
        });

        dialog.show(getSupportFragmentManager(), ValueSelectionDialogFragment.class.getName());
    }

    private void handleIsGameCompleted(boolean isGameCompleted) {
        if (!isGameCompleted) {
            stopwatchFragment.startStopwatch();
            return;
        }

        gameGridFragment.setGridInteraction(false, Constants.Ui.ALPHA_CLEAR);
        stopwatchFragment.pauseStopwatch();
        setEnabledHintButton(false);
        if (isGameCompletedDialogShown) {
            return;
        }

        long timeInMillis = stopwatchFragment.getTimeInMillis();
        int score = gameViewModel.calculateScore(timeInMillis);
        showGameCompletedDialog(stopwatchFragment.getTimeString(), score);
        userViewModel.postScore(score);
    }

    private void showGameCompletedDialog(String timeString, int score) {
        KenkenGame kenken = Objects.requireNonNull(gameViewModel.getKenken().getValue());
        int size = kenken.getSize();
        String html = getString(
                R.string.puzzle_solved_msg_html,
                size,
                KenkenHelpers.assertDifficulty(size).getLabel(),
                timeString,
                gameViewModel.getMistakeCount().getValue(),
                gameViewModel.getHintUsedCount().getValue(),
                score
        );
        UiHelpers.createInfoDialog(
                this,
                getString(R.string.puzzle_solved_dialog_title),
                UiHelpers.fromHtml(html),
                (dialog, which) -> isGameCompletedDialogShown = true
        ).show();
    }

    private void setEnabledHintButton(boolean enabled) {
        if (enabled) {
            hintButton.setClickable(true);
            hintButton.setAlpha(Constants.Ui.ALPHA_CLEAR);
        }
        else {
            hintButton.setClickable(false);
            hintButton.setAlpha(Constants.Ui.ALPHA_DIM);
        }
    }

    private void attachHintTaskFragment() {
        if (hintTaskFragment == null) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(hintTaskFragment, HINT_TASK_FRAGMENT_TAG)
                .commit();
    }

    private void detachHintTaskFragment() {
        if (hintTaskFragment == null) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .remove(hintTaskFragment)
                .commit();
    }

    private void beforeHintTask() {
        stopwatchFragment.pauseStopwatch();
        isHintTaskRunning = true;
        progressBar.show();
        gameGridFragment.setGridInteraction(false);
        setEnabledHintButton(false);
    }

    private void afterHintTask() {
        isHintTaskRunning = false;
        progressBar.hide();
        gameGridFragment.setGridInteraction(true);
        detachHintTaskFragment();
        stopwatchFragment.startStopwatch();
    }

    @Override
    public void onPreHintTaskExecute() {
        beforeHintTask();
    }

    @Override
    public void onHintTaskCancelled(Pair<Point, Integer> hint) {
        afterHintTask();
    }

    @Override
    public void onPostHintTaskExecute(Pair<Point, Integer> hint) {
        afterHintTask();
        gameViewModel.incrementHintUsedCount();
        showHintDialog(hint);
    }

    @Override
    public void onPreGameLoad() {
        progressBar.show();
        stopwatchFragment.pauseStopwatch();
    }

    @Override
    public void onPostGameLoad(KenkenGame kenken) {
        if (!isHintTaskRunning) {
            progressBar.hide();
        }
        if (kenken.toString().equals(lastSavedStringifiedKenken)) {
            onOldKenkenLoaded();
        }
        else {
            onNewKenkenLoaded();
        }
    }

    private void onOldKenkenLoaded() {
        stopwatchFragment.startStopwatch();
    }

    private void onNewKenkenLoaded() {
        if (hintTaskFragment != null && isHintTaskRunning) {
            hintTaskFragment.cancelTask();
            progressBar.hide();
        }
        gameGridFragment.setGridInteraction(true);
        stopwatchFragment.resetStopwatch();
        stopwatchFragment.startStopwatch();
        gameViewModel.resetMistakeCount();
        gameViewModel.resetHintUsedCount();
        lastSavedStringifiedKenken = Constants.Type.EMPTY_STRING;
        isGameCompletedDialogShown = false;
    }

    private boolean isRunningGameNotCompleted() {
        boolean progressing = gameViewModel.isGameProgressing();
        boolean completed = Boolean.TRUE.equals(gameViewModel.getIsGameCompleted().getValue());
        return progressing && !completed;
    }
}