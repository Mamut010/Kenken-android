package au.edu.jcu.cp5307.proj2.utils.helpers;

import androidx.lifecycle.MutableLiveData;

import java.util.function.Predicate;

public final class LiveDataHelpers {
    private LiveDataHelpers() {}

    public static <U, T extends MutableLiveData<U>> void notifyObserversFromMainOnFulfilledAction
            (T mutableLiveData, Predicate<U> action) {
        U currentData = mutableLiveData.getValue();
        if (action.test(currentData)) {
            mutableLiveData.setValue(currentData);
        }
    }
}