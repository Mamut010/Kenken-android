package au.edu.jcu.cp5307.proj2.application.kenken.core.operator;

import static org.junit.Assert.*;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;

public class DivisionTest {

    @Test
    public void apply() {
        Division operator = new Division();
        List<Integer> numbers = Arrays.asList(5, 2);
        BigDecimal result = operator.apply(numbers);
        assertNotEquals(BigDecimal.valueOf(2), result);
    }
}