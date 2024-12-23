package au.edu.jcu.cp5307.proj2.application.kenken.answer;

import au.edu.jcu.cp5307.proj2.application.kenken.core.operator.Operator;
import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class CageAnswer {
    private final List<Integer> numbers = new ArrayList<>();
    
    public void add(int number) {
        numbers.add(number);
    }
    
    public int count() {
        return numbers.size();
    }

    public Stream<List<Integer>> orderings(Operator operator) {
        if (operator.isCommutative()) {
            return Stream.of(numbers);
        }
        else if (operator.hasCommutativeInverse()) {
            return swappedFirstOrderings();
        }
        else {
            return CollectionHelpers.permutationsStream(numbers);
        }
    }
    
    private Stream<List<Integer>> swappedFirstOrderings() {
        List<List<Integer>> orderings = new ArrayList<>();
        orderings.add(numbers);
        for (int i = 1; i < numbers.size(); i++) {
            List<Integer> ordering = CollectionHelpers.copy(numbers);
            Collections.swap(ordering, 0, i);
            orderings.add(ordering);
        }
        return orderings.stream();
    }
}
