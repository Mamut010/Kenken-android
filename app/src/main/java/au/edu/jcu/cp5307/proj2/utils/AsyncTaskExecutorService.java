package au.edu.jcu.cp5307.proj2.utils;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncTaskExecutorService<Params, Progress, Result> {
    public static final ExecutorService THREAD_POOL = newThreadPoolExecutor();
    public static final ExecutorService SERIAL_EXECUTOR = newSerialExecutor();

    private static ExecutorService newThreadPoolExecutor() {
        return Executors.newCachedThreadPool();
    }

    private static ExecutorService newSerialExecutor() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    private ExecutorService executor;
    private Handler handler;

    protected abstract Result doInBackground(Params params);

    // used for pushing progress report to UI
    protected final void publishProgress(@NotNull Progress values) {
        getHandler().post(() -> onProgressUpdate(values));
    }

    protected void onProgressUpdate(@NotNull Progress values) {
        // Override this method wherever you want to update a progress result
    }

    protected void onPreExecute() {
        // Override this method wherever you want to perform task before background execution get started
    }

    protected void onPostExecute(Result result) {
        // Override this method wherever you want to perform task
        // after background execution has successfully ended
    }

    protected void onCancelled(Result result) {
        // Override this method wherever you want to perform task
        // after background execution has been cancelled
        onCancelled();
    }

    protected void onCancelled() {
        // DO NOTHING
    }

    public void execute(Params params) {
        executeOnExecutor(null, params);
    }

    public void executeOnExecutor(ExecutorService suppliedExecutor, Params params) {
        executor = getExecutor(suppliedExecutor);
        getHandler().post(() -> {
            onPreExecute();
            executor.execute(() -> {
                Result result = doInBackground(params);
                Runnable postRunnable = !isCancelled()
                        ? () -> onPostExecute(result)
                        : () -> onCancelled(result);
                getHandler().post(postRunnable);
            });
        });
    }

    public void cancel() {
        if (executor == null || isCancelled()) {
            return;
        }
        executor.shutdownNow();
    }

    public boolean isCancelled() {
        return executor != null && (executor.isTerminated() || executor.isShutdown());
    }

    private ExecutorService getExecutor(ExecutorService suppliedExecutor) {
        if (suppliedExecutor != null || executor == null) {
            synchronized(AsyncTaskExecutorService.class) {
                cancel();
                executor = suppliedExecutor != null ? suppliedExecutor : newSerialExecutor();
            }
        }
        return executor;
    }

    private Handler getHandler() {
        if (handler == null) {
            synchronized(AsyncTaskExecutorService.class) {
                handler = new Handler(Looper.getMainLooper());
            }
        }
        return handler;
    }
}