package au.edu.jcu.cp5307.proj2.application.kenken.solver.initializer;

import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.utils.helpers.KenkenHelpers;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CageConsistencyValuesInitializer implements KenkenValuesInitializer {
    @Override
    public Map<Square, Set<Integer>> initialValues(KenkenGame kenken, Map<Square, Set<Integer>> given) {
        Map<Square, Set<Integer>> supplied = given != null ? given : Collections.emptyMap();
        Map<Square, Set<Integer>> values = kenken
                .getSquares()
                .stream()
                .collect(Collectors.toMap(sqr -> sqr, sqr -> valuesForSquare(sqr, kenken, supplied)));
        kenken.getCages().forEach(cage -> values.putAll(KenkenHelpers.possibleCageValues(values, cage)));
        return values;
    }

    private Set<Integer> valuesForSquare(Square square, KenkenGame kenken, Map<Square, Set<Integer>> supplied) {
        Set<Integer> suppliedValues =
                Objects.requireNonNull(
                        supplied.getOrDefault(square, Collections.emptySet())
                )
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return !suppliedValues.isEmpty() ? suppliedValues : kenken.getPossibleValues();
    }
}
