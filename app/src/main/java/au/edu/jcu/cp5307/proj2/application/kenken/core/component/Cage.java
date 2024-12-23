package au.edu.jcu.cp5307.proj2.application.kenken.core.component;

import androidx.annotation.NonNull;

import au.edu.jcu.cp5307.proj2.application.kenken.core.operator.Operator;
import au.edu.jcu.cp5307.proj2.application.kenken.core.operator.OperatorFactory;
import au.edu.jcu.cp5307.proj2.application.kenken.answer.CageAnswer;
import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import au.edu.jcu.cp5307.proj2.utils.helpers.MathHelpers;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UnknownFormatConversionException;

public class Cage {
    private final BigDecimal target;
    private final Operator operator;
    private final SortedSet<Square> squares = new TreeSet<>();
    
    public Cage(int target, Operator operator) {
        this.target = BigDecimal.valueOf(target);
        this.operator = operator;
    }
    
    public static Cage parseCage(String cageStr, String delimiterRegex) {
        String[] tokens = Objects.requireNonNull(cageStr).trim().toUpperCase().split(delimiterRegex);
        if (tokens.length < 2) {
            throw new UnknownFormatConversionException(cageStr);
        }
        
        Cage cage;
        try {
            int target = Integer.parseInt(tokens[0]);
            Operator op = OperatorFactory.createOperator(tokens[1]);
            cage = new Cage(target, op);
            for (int i = 2; i < tokens.length; i++) {
                Square sqr = Square.of(tokens[i]);
                cage.add(sqr);
            }
        }
        catch (NumberFormatException | UnknownFormatConversionException e) {
            throw new UnknownFormatConversionException(cageStr);
        }
        
        return cage;
    }

    public int getTarget() {
        return target.intValueExact();
    }

    public Operator getOperator() {
        return operator;
    }

    public String getNotation() {
        return target + " " + operator.notation();
    }

    public int size() {
        return squares.size();
    }
    
    public void add(Square sqr) {
        squares.add(sqr);
    }
    
    public boolean contains(Square square) {
        return squares.contains(square);
    }

    public Square getFirstSquare() {
        return CollectionHelpers.firstOrDefault(squares);
    }
    
    public Set<Square> getSquares() {
        return CollectionHelpers.copy(squares);
    }

    public boolean isSolution(CageAnswer answer) {
        if (answer == null || squares.size() != answer.count()) {
            return false;
        }
        return answer
                .orderings(operator)
                .anyMatch(ordering -> MathHelpers.equals(operator.apply(ordering), target));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        else if (!(obj instanceof Cage)) {
            return false;
        }
        
        Cage other = (Cage) obj;
        return MathHelpers.equals(target, other.target)
                && operator.equals(other.operator)
                && squares.equals(other.squares);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.target);
        hash = 97 * hash + Objects.hashCode(this.operator);
        hash = 97 * hash + Objects.hashCode(this.squares);
        return hash;
    }

    @NonNull
    @Override
    public String toString() {
        Set<Square> sortedSquares = new TreeSet<>(squares);
        return String.format(Locale.ROOT, "cage[size=%d, op=%s, squares=%s]",
                size(),
                operator,
                sortedSquares
        );
    }
}
