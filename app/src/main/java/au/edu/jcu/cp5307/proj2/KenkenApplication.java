package au.edu.jcu.cp5307.proj2;

import android.app.Application;

import androidx.annotation.NonNull;

import au.edu.jcu.cp5307.proj2.di.ApplicationContainer;

public class KenkenApplication extends Application {
    private ApplicationContainer container;

    @Override
    public void onCreate() {
        super.onCreate();

        container = new ApplicationContainer(this);
    }

    @NonNull
    public ApplicationContainer getContainer() {
        return container;
    }
}