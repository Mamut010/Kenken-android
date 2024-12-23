package au.edu.jcu.cp5307.proj2.application.kenken.core.component;

import androidx.annotation.NonNull;

import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Unit {
    private final Set<Square> squares = new HashSet<>();

    public int size() {
        return squares.size();
    }
    
    public void add(Square square) {
        squares.add(square);
    }
    
    public boolean contains(Square square) {
        return squares.contains(square);
    }
    
    public Set<Square> getSquares() {
        return CollectionHelpers.copy(squares);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        else if (!(obj instanceof Unit)) {
            return false;
        }
        
        Unit other = (Unit) obj;
        return squares.equals(other.squares);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.squares);
        return hash;
    }

    @NonNull
    @Override
    public String toString() {
        Set<Square> sortedSquares = new TreeSet<>(squares);
        return String.format(Locale.ROOT, "unit[size=%d, squares=%s]", size(), sortedSquares);
    }
}
