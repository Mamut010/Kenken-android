package au.edu.jcu.cp5307.proj2.application.kenken.core.component;

import androidx.annotation.NonNull;

import au.edu.jcu.cp5307.proj2.utils.Converters;
import au.edu.jcu.cp5307.proj2.utils.Validators;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UnknownFormatConversionException;

public record Square(int row, int column) implements Comparable<Square> {
    private static final Map<Integer, Square> pool = new HashMap<>();

    /**
     * @throws IllegalArgumentException: if row or column is negative
     */
    public Square {
        Validators.ensureNonNegativeInteger(row);
        Validators.ensureNonNegativeInteger(column);
    }

    public static Square of(int row, int column) throws IllegalArgumentException {
        Square instance = getFromPool(row, column);
        if (instance == null) {
            instance = new Square(row, column);
            addToPool(instance);
        }

        return instance;
    }

    public static Square of(String sqrString) throws IllegalArgumentException {
        sqrString = sqrString.trim();
        if (sqrString.length() <= 1 || !Character.isUpperCase(sqrString.charAt(0))) {
            throw new UnknownFormatConversionException(sqrString);
        }

        char rowLabel = Character.toUpperCase(sqrString.charAt(0));
        int row = Converters.upperCharToOrdinal(rowLabel);
        int column = Integer.parseInt(sqrString.substring(1));
        return of(row, column);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Square)) {
            return false;
        }

        Square other = (Square) obj;
        return row == other.row && column == other.column;
    }

    @Override
    public int hashCode() {
        return getCoordinatesHashCode(row, column);
    }

    @Override
    public int compareTo(Square o) {
        int rowCompareToResult = Integer.compare(row, o.row);
        if (rowCompareToResult != 0) {
            return rowCompareToResult;
        }
        return Integer.compare(column, o.column);
    }

    @NonNull
    @Override
    public String toString() {
        if (isRowConvertibleToUpperChar()) {
            return String.format(Locale.getDefault(), "%s%d", Converters.ordinalToUpperChar(row), column);
        } else {
            return String.format(Locale.getDefault(), "sqr(%d, %d)", row, column);
        }
    }

    private boolean isRowConvertibleToUpperChar() {
        final int lowerBound = 1;
        final int upperBound = 'Z' - 'A' + 1;
        return lowerBound <= row && row <= upperBound;
    }

    private static int getCoordinatesHashCode(int row, int column) {
        int hash = 3;
        hash = 13 * hash + row;
        hash = 13 * hash + column;
        return hash;
    }

    private static Square getFromPool(int row, int column) {
        int hash = getCoordinatesHashCode(row, column);
        return pool.getOrDefault(hash, null);
    }

    private static void addToPool(Square instance) {
        int hash = instance.hashCode();
        pool.putIfAbsent(hash, instance);
    }
}
