package au.edu.jcu.cp5307.proj2.utils.helpers;

import au.edu.jcu.cp5307.proj2.constants.Difficulty;
import au.edu.jcu.cp5307.proj2.application.kenken.answer.Answers;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Cage;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.utils.Point;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class KenkenHelpers {
    private KenkenHelpers() {}

    public static Square squareOfPoint(Point point) {
        try {
            return Square.of(point.row(), point.column());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Point pointOfSquare(Square square) {
        return new Point(square.row(), square.column());
    }

    public static Difficulty assertDifficulty(int kenkenSize) {
        if (kenkenSize < 0) {
            throw new IllegalArgumentException("Invalid kenken size: " + kenkenSize);
        }

        int[] thresholds = {2, 4, 6, 7, 8, 9};
        Difficulty[] difficulties = {
                Difficulty.Basic,
                Difficulty.Easy,
                Difficulty.Normal,
                Difficulty.Medium,
                Difficulty.Hard,
                Difficulty.VeryHard,
                Difficulty.Extreme
        };
        int i = 0;
        while (i < thresholds.length && kenkenSize > thresholds[i]) {
            i++;
        }
        return difficulties[i];
    }
    
    public static Map<Square, Set<Integer>> possibleCageValues(Map<Square, Set<Integer>> values, Cage cage) {
        Set<Square> squares = cage.getSquares();
        List<List<Integer>> cageSquaresValues = squares
                .stream()
                .filter(values::containsKey)
                .map(values::get)
                .map(collection -> collection != null
                        ? CollectionHelpers.asList(collection)
                        : Collections.<Integer>emptyList())
                .collect(Collectors.toList());
        
        return CollectionHelpers
                .cartesianProduct(cageSquaresValues)
                .stream()
                .filter(perm -> cage.isSolution(Answers.newCageAnswer(perm)))
                .reduce(
                        (Map<Square, Set<Integer>>) new HashMap<Square, Set<Integer>>(),
                        (possible, perm) -> addPossible(possible, squares, perm),
                        CollectionHelpers::concatMap
                );
    }
    
    private static Map<Square, Set<Integer>> addPossible(
            Map<Square, Set<Integer>> possible,
            Collection<Square> squares,
            List<Integer> numbers) {
        int i = 0;
        for (Square square : squares) {
            int squareNumber = numbers.get(i);
            Set<Integer> squareValues = possible.getOrDefault(square, null);
            if (squareValues != null) {
                squareValues.add(squareNumber);
            }
            else {
                squareValues = new TreeSet<>();
                squareValues.add(squareNumber);
                possible.put(square, squareValues);
            }
            i++;
        }
        return possible;
    }
}
