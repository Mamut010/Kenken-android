package au.edu.jcu.cp5307.proj2.utils.helpers;

import java.util.function.Consumer;

public final class ObjectHelpers {
    private ObjectHelpers() {}

    public static <T> void consumeIfExists(T obj, Consumer<T> consumer) {
        if (obj != null) {
            consumer.accept(obj);
        }
    }
}
