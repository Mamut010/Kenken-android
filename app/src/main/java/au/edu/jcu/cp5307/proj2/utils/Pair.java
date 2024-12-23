package au.edu.jcu.cp5307.proj2.utils;

import java.io.Serializable;

public record Pair<T1, T2> (T1 first, T2 second) implements Serializable {
}
