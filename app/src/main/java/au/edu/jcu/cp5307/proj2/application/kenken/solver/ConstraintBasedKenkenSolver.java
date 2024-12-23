package au.edu.jcu.cp5307.proj2.application.kenken.solver;

import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.application.kenken.answer.KenkenAnswer;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.KenkenEliminatingConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.initializer.KenkenValuesInitializer;
import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class ConstraintBasedKenkenSolver implements KenkenSolver, SolvingContextProcessor {
    private final KenkenValuesInitializer valuesInitializer;
    private final Collection<KenkenEliminatingConstraint> constraints;
    
    public ConstraintBasedKenkenSolver(
            KenkenValuesInitializer valuesInitializer,
            Collection<KenkenEliminatingConstraint> constraints) {
        this.valuesInitializer = valuesInitializer;
        this.constraints = constraints;
    }
    
    @Override
    public KenkenAnswer solve(KenkenGame kenken, Map<Square, Set<Integer>> given) {
        if (kenken == null) {
            return null;
        }

        Map<Square, Set<Integer>> initialValues = valuesInitializer.initialValues(kenken, given);
        SolvingContext initialContext = new SolvingContext(kenken, this, initialValues);
        SolvingContext solvedContext = solveImpl(initialContext);
        if (solvedContext.isEmpty()) {
            return KenkenAnswer.emptyAnswer();
        }
        
        return solvedContext
            .stream()
            .collect(
                    KenkenAnswer::new,
                    (answer, pair) -> {
                        Square square = pair.square();
                        int value = CollectionHelpers.singleOrThrow(pair.values());
                        answer.setValue(square, value);
                    },
                    KenkenAnswer::update
            );
    }
    
    private SolvingContext solveImpl(SolvingContext context) {
        if (context.isEmpty() || allSquareValuesSingle(context)) {
            return context;
        }
        
        Square unfilledSquare = getUnfilledSquareWithLowestPossibleValues(context);
        Set<Integer> possibleValues = context.getValues(unfilledSquare);
        for (int value : possibleValues) {
            SolvingContext filledContext = fill(context.copy(), unfilledSquare, value);
            SolvingContext solvedContext = solveImpl(filledContext);
            if (!solvedContext.isEmpty()) {
                return solvedContext;
            }
        }
        return SolvingContext.emptyContext();
    }
    
    // Eliminate all the other values (except the filled value) from values for square and propagate.
    @Override
    public SolvingContext fill(SolvingContext context, Square square, int filledValue) {
        boolean filled = CollectionHelpers
                .filter(context.getValues(square), v -> v != filledValue)
                .stream()
                .allMatch(v -> eliminate(context, square, v));
        return filled ? context : SolvingContext.emptyContext();
    }
    
    // Eliminate v from the values for square; return true if all constraints are satisfied.
    @Override
    public boolean eliminate(SolvingContext context, Square square, int eliminatedValue) {
        Set<Integer> values = context.getValues(square);
        if (!values.contains(eliminatedValue)) {
            return true; // Already eliminated
        }
        
        values.remove(eliminatedValue);
        return !values.isEmpty() && areAllConstraintsSatisfied(context, square, eliminatedValue);
    }

    private boolean areAllConstraintsSatisfied(SolvingContext context, Square square, int eliminatedValue) {
        return constraints
                .stream()
                .allMatch(constraint -> constraint.test(context, square, eliminatedValue));
    }
    
    private static boolean allSquareValuesSingle(SolvingContext context) {
        return context
                .getSquares()
                .stream()
                .map(context::getValues)
                .allMatch(CollectionHelpers::hasExactlyOneItem);
    }
    
    private static Square getUnfilledSquareWithLowestPossibleValues(SolvingContext context) {
        Set<Square> squares = context.getSquares();
        return context
                .stream()
                .filter(pair -> squares.contains(pair.square()) && pair.values().size() > 1)
                .sorted(Comparator.comparingInt(pair -> pair.values().size()))
                .map(SolvingContext.SquareValuesPair::square)
                .findFirst()
                .orElse(null);
    }
}
