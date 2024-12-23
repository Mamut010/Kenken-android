package au.edu.jcu.cp5307.proj2.utils.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import au.edu.jcu.cp5307.proj2.utils.Validators;

public class CollectionHelpers {
    private CollectionHelpers() {}

    public static boolean containsDuplicates(Collection<?> collection) {
        return collection.size() != new HashSet<>(collection).size();
    }
    
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    public static boolean hasExactlyOneItem(Collection<?> collection) {
        return collection != null && collection.size() == 1;
    }
    
    public static <T> List<T> asList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }
    
    public static <T> Set<T> copy(Set<T> src) {
        return new HashSet<>(src);
    }
    
    public static <T> List<T> copy(List<T> src) {
        return new ArrayList<>(src);
    }

    public static <K, V, S extends Set<V>> Map<K, S> copy(Map<K, S> src) {
        return new HashMap<>(src);
    }

    @SuppressWarnings("unchecked")
    public static <K, V, S extends Set<V>> Map<K, S> copy(Map<K, S> src, boolean copiedSets) {
        if (!copiedSets) {
            return copy(src);
        }
        else {
            return (Map<K, S>) src
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> copy(entry.getValue())));
        }
    }
    
    public static <K, V> Map<K, V> concatMap(Map<K, V> into, Map<K, V> other) {
        into.putAll(other);
        return into;
    }
    
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }
    
    public static <T> Set<List<T>> cartesianProduct(List<List<T>> lists) {
        if (lists == null || lists.isEmpty()) {
            return Collections.emptySet();
        }

        return cartesianProductImpl(lists.size() - 1, lists);
    }
    
    public static <T> T firstOrThrow(Collection<T> collection, String msg) {
        return collection.stream().findFirst().orElseThrow(() -> new IllegalArgumentException(msg));
    }
    
    public static <T> T firstOrThrow(Collection<T> collection) {
        return firstOrThrow(collection, "Empty collection");
    }

    public static <T> T firstOrDefault(Collection<T> collection, T defaultValue) {
        return collection.stream().findFirst().orElse(defaultValue);
    }

    public static <T> T firstOrDefault(Collection<T> collection) {
        return firstOrDefault(collection, null);
    }
    
    public static <T> T singleOrThrow(Collection<T> collection, String msg) {
        if (collection.size() != 1) {
            throw new IllegalArgumentException(msg);
        }
        return firstOrThrow(collection);
    }
    
    public static <T> T singleOrThrow(Collection<T> collection) {
        return singleOrThrow(collection, "Non-single collection");
    }

    public static <T> T getRandomItem(List<T> items) {
        return items.get(ThreadLocalRandom.current().nextInt(items.size()));
    }
    
    public static <T> Stream<List<T>> permutationsStream(List<T> original) {
        return possiblePermutationsAsMaps(original)
                .stream()
                .map(Map::values)
                .map(CollectionHelpers::asList);
    }
    
    public static <T> T firstOrReduce(List<T> items, BinaryOperator<T> accumulator) {
        Validators.ensureContainsAtLeastOne(items);
        return items.stream().skip(1).reduce(items.get(0), accumulator);
    }
    
    private static <T> Set<List<T>> cartesianProductImpl(int index, List<List<T>> lists) {
        Set<List<T>> ret = new HashSet<>();
        if (index < 0) {
            ret.add(new ArrayList<>());
        } 
        else {
            for (T obj : lists.get(index)) {
                for (List<T> set : cartesianProductImpl(index - 1, lists)) {
                    set.add(obj);
                    ret.add(set);
                }
            }
        }
        return ret;
    }
    
    /**
     * @param list the input list, may contain duplicates
     * @param <E>  the type of the element of the list
     * @return the list of possible permutations
     */
    private static <E> List<Map<Integer, E>> possiblePermutationsAsMaps(List<E> list) {
        // check if the list is non-null and non-empty
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return IntStream.range(0, list.size())
                // represent each list element as a list of permutation maps
                .mapToObj(i -> IntStream.range(0, list.size())
                        // key - element position, value - element itself
                        .mapToObj(j -> Collections.singletonMap(j, list.get(j)))
                        // Stream<List<Map<Integer,E>>>
                        .collect(Collectors.toList()))
                // reduce a stream of lists to a single list
                .reduce((list1, list2) -> list1.stream()
                        .flatMap(map1 -> list2.stream()
                                // filter out those keys that are already present
                                .filter(map2 -> map2.keySet().stream()
                                        .noneMatch(map1::containsKey))
                                // concatenate entries of two maps, order matters
                                .map(map2 -> new LinkedHashMap<Integer, E>() {{
                                    putAll(map1);
                                    putAll(map2);
                                }}))
                        // list of combinations
                        .collect(Collectors.toList()))
                // otherwise an empty collection
                .orElse(Collections.emptyList());
    }
}
