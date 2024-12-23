package au.edu.jcu.cp5307.proj2.utils.helpers;

import java.math.BigDecimal;
import java.util.stream.IntStream;

public final class MathHelpers {
    private MathHelpers() {}
    
    public static int max(int first, int... others) {
        return IntStream.concat(IntStream.of(first), IntStream.of(others)).max().orElseThrow();
    }

    public static BigDecimal multiply(double a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }

    public static boolean equals(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }
}
