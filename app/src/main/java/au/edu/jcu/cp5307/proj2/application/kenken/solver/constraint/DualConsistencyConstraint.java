package au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint;

import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Unit;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.SolvingContext;

import java.util.List;
import java.util.stream.Collectors;

public class DualConsistencyConstraint implements KenkenEliminatingConstraint {
    @Override
    public boolean test(SolvingContext context, Square square, int eliminatedValue) {
        return placeForValue(context, context.getRowUnit(square), eliminatedValue) 
            && placeForValue(context, context.getColumnUnit(square), eliminatedValue);
    }
    
    // If a unit u is reduced to only one place for a value v, then put it there.
    private boolean placeForValue(SolvingContext context, Unit unit, int eliminatedValue) {
        List<Square> places = unit
                .getSquares()
                .stream()
                .filter(sqr -> context.getValues(sqr).contains(eliminatedValue))
                .collect(Collectors.toList());
        if (places.isEmpty()) {
            return false;
        }
        else if (places.size() > 1) {
            return true;
        }
        
        Square singlePlace = places.get(0);
        SolvingContext filledContext = context.fill(singlePlace, eliminatedValue);
        return !filledContext.isEmpty();
    }
}
