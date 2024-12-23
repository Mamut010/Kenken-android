package au.edu.jcu.cp5307.proj2.models.kenken.core.component;

import static org.junit.Assert.*;

import org.junit.Test;

import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Cage;

public class CageTest {

    @Test
    public void testEquals_same_description_should_identical() {
        String description = "12 × e7 e8 f8";
        Cage cage1 = Cage.parseCage(description, " ");
        Cage cage2 = Cage.parseCage(description, " ");
        assertEquals(cage1, cage2);
    }
}