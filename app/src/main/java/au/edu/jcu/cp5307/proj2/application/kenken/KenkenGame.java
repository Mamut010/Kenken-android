package au.edu.jcu.cp5307.proj2.application.kenken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import au.edu.jcu.cp5307.proj2.application.kenken.answer.Answers;
import au.edu.jcu.cp5307.proj2.application.kenken.answer.KenkenAnswer;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Cage;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Unit;
import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import au.edu.jcu.cp5307.proj2.utils.helpers.MathHelpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KenkenGame {
    public record AssociatedUnits(Unit rowUnit, Unit columnUnit) {
    }
    
    private int size = 0;
    private final SortedSet<Integer> possibleValues = new TreeSet<>();
    private final Set<Square> squares = new HashSet<>();
    private final Set<Cage> cages = new HashSet<>();
    private final Set<Unit> units = new HashSet<>();
    private final Map<Square, Cage> cageFor = new HashMap<>();
    private final Map<Square, AssociatedUnits> unitsFor = new HashMap<>();
    private final Map<Square, Set<Square>> peersFor = new HashMap<>();
    
    public KenkenGame(Collection<Cage> cages) {
        initSquaresCagesAndSize(cages);
        validateFields();
        initPossibleValues();
        initUnitsAndPeers();
    }
    
    public static KenkenGame parseFromDescriptions(String[] cageDescriptions, String cagePartDelimiterRegex) {
        List<Cage> cages = Arrays
                .stream(cageDescriptions)
                .map(String::trim)
                .filter(description -> !description.isBlank())
                .map(description -> Cage.parseCage(description, cagePartDelimiterRegex))
                .collect(Collectors.toList());
        return new KenkenGame(cages);
    }
    
    public static KenkenGame parseFromDescriptions(String[] cageDescriptions) {
        return KenkenGame.parseFromDescriptions(cageDescriptions, "[\\s]+");
    }
    
    public static KenkenGame parseFromDescriptions(String cageDescriptions) {
        return KenkenGame.parseFromDescriptions(cageDescriptions.split(";"));
    }
    
    public int getSize() {
        return size;
    }
    
    public Set<Integer> getPossibleValues() {
        return CollectionHelpers.copy(possibleValues);
    }

    public int[] getValueRange() {
        return possibleValues.stream().mapToInt(v -> v).toArray();
    }
    
    public Set<Square> getSquares() {
        return squares;
    }
    
    public Set<Cage> getCages() {
        return cages;
    }
    
    public Map<Square, Cage> getSquareCageMap() {
        return cageFor;
    }
    
    public Map<Square, AssociatedUnits> getSquareUnitsMap() {
        return unitsFor;
    }
    
    public Map<Square, Set<Square>> getSquarePeersMap() {
        return peersFor;
    }

    public Map<Square, Boolean> check(KenkenAnswer answer) {
        Map<Square, Boolean> result = new HashMap<>();
        
        for (Square square : answer.getSquares()) {
            if (result.getOrDefault(square, null) != null) {
                continue;
            }
            else if (!possibleValues.contains(answer.getValue(square))) {
                result.put(square, null);
                continue;
            }

            Cage cage = cageFor.get(square);
            AssociatedUnits associatedUnits = unitsFor.get(square);
            if (associatedUnits == null || cage == null) {
                result.put(square, null);
                continue;
            }
            Unit rowUnit = associatedUnits.rowUnit();
            Unit columnUnit = associatedUnits.columnUnit();
            
            boolean passed = true;
            if (areUnitValuesDuplicated(rowUnit, answer)) {
                rowUnit.getSquares().forEach(sqr -> result.put(sqr, false));
                passed = false;
            }
            if (areUnitValuesDuplicated(columnUnit, answer)) {
                columnUnit.getSquares().forEach(sqr -> result.put(sqr, false));
                passed = false;
            }
            if (isCageNotSolvedWhenExact(cage, answer)) {
                cage.getSquares().forEach(sqr -> result.put(sqr, false));
                passed = false;
            }
            
            if (passed) {
                result.put(square, true);
            }
        }
        
        return result;
    }
    
    public boolean isSolution(KenkenAnswer answer) {
        return areAllUnitsValuesDistinct(answer) && areAllCagesSolved(answer);
    }

    @NonNull
    @Override
    public String toString() {
        List<String> descriptions = cages
                .stream()
                .sorted(Comparator.comparing(Cage::getFirstSquare))
                .map(cage -> {
                    List<String> squareStrings = cage
                            .getSquares()
                            .stream()
                            .map(Square::toString)
                            .collect(Collectors.toList());
                    String squaresString = String.join(" ", squareStrings);
                    return String.format(Locale.getDefault(), "%d %s %s",
                            cage.getTarget(), cage.getOperator().notation(), squaresString);
                })
                .collect(Collectors.toList());
        return String.join(";", descriptions);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        else if (!(obj instanceof KenkenGame)) {
            return false;
        }

        KenkenGame other = (KenkenGame) obj;
        return cages.equals(other.cages);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.cages);
        return hash;
    }

    private void initSquaresCagesAndSize(Collection<Cage> cages) {
        cages.forEach(cage -> {
            cage.getSquares().forEach(sqr -> {
                if (!squares.add(sqr)) {
                    throw new IllegalArgumentException(cage.toString());
                }
                cageFor.put(sqr, cage);
                size = MathHelpers.max(size, sqr.row(), sqr.column());
            });
            this.cages.add(cage);
        });
    }

    private void initPossibleValues() {
        IntStream.range(1, size + 1).forEach(possibleValues::add);
    }
    
    private void initUnitsAndPeers() {
        squares.forEach(square -> {
            Unit rowUnit = new Unit();
            Unit columnUnit = new Unit();
            Set<Square> peers = new TreeSet<>();
            
            squares
                    .stream()
                    .filter(sqr -> sqr.row() == square.row() || sqr.column() == square.column())
                    .forEach(sqr -> {
                        if (sqr != square) {
                            peers.add(sqr);
                        }
                        if (sqr.row() == square.row()) {
                            rowUnit.add(sqr);
                        }
                        if (sqr.column() == square.column()) {
                            columnUnit.add(sqr);
                        }
                    });

            unitsFor.put(square, new AssociatedUnits(rowUnit, columnUnit));
            peersFor.put(square, peers);
            units.add(rowUnit);
            units.add(columnUnit);
        });
    }
    
    private void validateFields() {
        List<String> missingSquareLocations = new ArrayList<>();
        for (int row = 1; row <= size; row++) {
            for (int column = 1; column <= size; column++) {
                if (!squares.contains(Square.of(row, column))) {
                    String missingLocation = String.format(Locale.getDefault(), "Row: %d, Column: %d", row, column);
                    missingSquareLocations.add(missingLocation);
                }
            }
        }
        
        if (!missingSquareLocations.isEmpty()) {
            String msg = "Given cage descriptions don't cover all squares. Missing at:\n%s";
            String formattedMsg = String.format(msg, String.join("\n", missingSquareLocations));
            throw new IllegalArgumentException(formattedMsg);
        }
    }
    
    private boolean areUnitValuesDuplicated(Unit unit, KenkenAnswer answer) {
        List<Integer> valuesInUnit = filterValuesFromAnswer(answer, unit::contains);
        return CollectionHelpers.containsDuplicates(valuesInUnit);
    }

    private boolean isCageNotSolvedWhenExact(Cage cage, KenkenAnswer answer) {
        List<Integer> values = filterValuesFromAnswer(answer, cage::contains);
        return values.size() == cage.size() && !cage.isSolution(Answers.newCageAnswer(values));
    }

    private List<Integer> filterValuesFromAnswer(KenkenAnswer answer, Predicate<Square> predicate) {
        return answer
                .getSquares()
                .stream()
                .filter(predicate)
                .map(answer::getValue)
                .filter(possibleValues::contains)
                .collect(Collectors.toList());
    }

    private boolean areAllUnitsValuesDistinct(KenkenAnswer answer) {
        return units.stream().noneMatch(unit -> areUnitValuesDuplicated(unit, answer));
    }

    private  boolean areAllCagesSolved(KenkenAnswer answer) {
        return cages.stream().allMatch(cage -> {
            List<Integer> values = filterValuesFromAnswer(answer, cage::contains);
            return cage.isSolution(Answers.newCageAnswer(values));
        });
    }
}
