package au.edu.jcu.cp5307.proj2.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import au.edu.jcu.cp5307.proj2.utils.AsyncTaskExecutorService;
import au.edu.jcu.cp5307.proj2.utils.Pair;
import au.edu.jcu.cp5307.proj2.utils.Point;
import au.edu.jcu.cp5307.proj2.viewmodels.GameViewModel;

public class HintTaskFragment extends Fragment {
    /**
     * Callback interface through which the fragment will report the
     * task's results back to the context.
     */
    public interface HintTaskCallbacks {
        void onPreHintTaskExecute();
        void onHintTaskCancelled(Pair<Point, Integer> hint);
        void onPostHintTaskExecute(Pair<Point, Integer> hint);
    }

    private HintTaskCallbacks callbacks;
    private HintAsyncTask task;
    private final GameViewModel gameViewModel;

    public HintTaskFragment(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }

    /**
     * Hold a reference to the parent context so we can report the
     * task's results. The Android framework will pass us a reference
     * to the newly created context after each configuration change.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HintTaskCallbacks) {
            callbacks = (HintTaskCallbacks) context;
        }
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        task = new HintAsyncTask();
        task.execute(gameViewModel);
    }

    /**
     * Set the callback to null so we don't accidentally leak the context instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    public void cancelTask() {
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * The task that performs some hint calculation in the background and
     * proxies results back to the context.
     * Note that we need to check if the callbacks are null in each
     * method in case they are invoked after the onDestroy() method have been called.
     */
    private class HintAsyncTask
            extends AsyncTaskExecutorService<GameViewModel, Void, Pair<Point, Integer>> {

        @Override
        protected Pair<Point, Integer> doInBackground(GameViewModel gameViewModel) {
            return gameViewModel.nextHint();
        }

        @Override
        protected void onPreExecute() {
            if (callbacks != null) {
                callbacks.onPreHintTaskExecute();
            }
        }

        @Override
        protected void onCancelled(Pair<Point, Integer> hint) {
            if (callbacks != null) {
                callbacks.onHintTaskCancelled(hint);
            }
        }

        @Override
        protected void onPostExecute(Pair<Point, Integer> hint) {
            if (callbacks != null) {
                callbacks.onPostHintTaskExecute(hint);
            }
        }
    }
}
