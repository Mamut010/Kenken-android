package au.edu.jcu.cp5307.proj2.utils;

import java.util.Collection;

public final class Validators {
    private Validators() {}
    
    public static void ensureNonNegativeInteger(int integer, String msg) throws IllegalArgumentException {
        if (integer < 0) {
            throw new IllegalArgumentException(msg);
        }
    }
    
    public static void ensureNonNegativeInteger(int integer) throws IllegalArgumentException {
        ensureNonNegativeInteger(integer, "integer must not be negative");
    }

    public static void ensureContainsAtLeast(Collection<?> collection, int count, String msg)
            throws IllegalArgumentException {
        if (collection == null || collection.size() < count) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void ensureContainsAtLeast(Collection<?> collection, int count)
            throws IllegalArgumentException {
        String msg = "collection must contain at least " + count + " element";
        ensureContainsAtLeast(collection, 1, msg);
    }

    public static void ensureContainsAtLeastOne(Collection<?> collection) throws IllegalArgumentException {
        ensureContainsAtLeast(collection, 1);
    }
}
