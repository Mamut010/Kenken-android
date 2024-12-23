package au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint;

import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Cage;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.SolvingContext;
import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import au.edu.jcu.cp5307.proj2.utils.helpers.KenkenHelpers;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CageConsistencyConstraint implements KenkenEliminatingConstraint {
    // Make sure that there is some assignment that satisfies the cage for square s,
    // and eliminate the values that are impossible.
    @Override
    public boolean test(SolvingContext context, Square square, int eliminatedValue) {
        Cage cage = context.getCage(square);
        Map<Square, Set<Integer>> possible = KenkenHelpers.possibleCageValues(
                context.getSquareValuesMap(),
                cage
        );
        if (CollectionHelpers.isNullOrEmpty(possible)) {
            return false;
        }
        return cage
                .getSquares()
                .stream()
                .allMatch(sqr -> {
                    Set<Integer> possibleValues = Objects.requireNonNull(possible.get(sqr));
                    return CollectionHelpers
                            .filter(context.getValues(sqr), v -> !possibleValues.contains(v))
                            .stream()
                            .allMatch(v -> context.eliminate(sqr, v));
                });
    }
}
