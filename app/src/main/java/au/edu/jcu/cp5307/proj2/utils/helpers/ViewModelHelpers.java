package au.edu.jcu.cp5307.proj2.utils.helpers;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import java.util.Objects;

import au.edu.jcu.cp5307.proj2.KenkenApplication;
import au.edu.jcu.cp5307.proj2.di.ApplicationContainer;

public final class ViewModelHelpers {
    private ViewModelHelpers() {}

    public static ApplicationContainer getApplicationContainer(CreationExtras creationExtras) {
        KenkenApplication app = (KenkenApplication)
                creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY);
        return Objects.requireNonNull(app).getContainer();
    }
}
