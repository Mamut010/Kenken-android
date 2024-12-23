package au.edu.jcu.cp5307.proj2.application.kenken.solver;

import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Cage;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Unit;
import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class SolvingContext {
    public record SquareValuesPair (Square square, Set<Integer> values) {}
    
    private final KenkenGame kenken;
    private final SolvingContextProcessor processor;
    private final Map<Square, Set<Integer>> valuesFor;
    
    public SolvingContext(
            KenkenGame kenken,
            SolvingContextProcessor processor,
            Map<Square, Set<Integer>> initialValues) {
        this.processor = processor;
        this.kenken = kenken;
        this.valuesFor = initialValues;
    }
    
    public static SolvingContext emptyContext() {
        return new SolvingContext(null, null, null);
    }
    
    public boolean isEmpty() {
        return kenken == null || processor == null || CollectionHelpers.isNullOrEmpty(valuesFor);
    }

    public Map<Square, Set<Integer>> getSquareValuesMap() {
        return valuesFor;
    }

    public Stream<SquareValuesPair> stream() {
        return valuesFor
                .entrySet()
                .stream()
                .map(entry -> new SquareValuesPair(entry.getKey(), entry.getValue()));
    }
    
    public SolvingContext fill(Square square, int filledValue) {
        return processor.fill(this, square, filledValue);
    }
    
    public boolean eliminate(Square square, int eliminatedValue) {
        return processor.eliminate(this, square, eliminatedValue);
    }
    
    public Set<Square> getSquares() {
        return kenken.getSquares();
    }
    
    public Cage getCage(Square square) {
        return kenken.getSquareCageMap().get(square);
    }
    
    public Set<Square> getPeers(Square square) {
        return kenken.getSquarePeersMap().get(square);
    }
    
    public Unit getRowUnit(Square square) {
        KenkenGame.AssociatedUnits associatedUnits = kenken.getSquareUnitsMap().get(square);
        return associatedUnits != null ? associatedUnits.rowUnit() : null;
    }
    
    public Unit getColumnUnit(Square square) {
        KenkenGame.AssociatedUnits associatedUnits = kenken.getSquareUnitsMap().get(square);
        return associatedUnits != null ? associatedUnits.columnUnit() : null;
    }
    
    public Set<Integer> getValues(Square square) {
        return valuesFor.get(square);
    }
    
    public SolvingContext copy() {
        return new SolvingContext(kenken, processor, CollectionHelpers.copy(valuesFor, true));
    }
}
