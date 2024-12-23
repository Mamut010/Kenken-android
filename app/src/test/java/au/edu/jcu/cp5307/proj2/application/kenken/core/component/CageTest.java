package au.edu.jcu.cp5307.proj2.application.kenken.core.component;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;

import au.edu.jcu.cp5307.proj2.application.kenken.answer.Answers;
import au.edu.jcu.cp5307.proj2.application.kenken.answer.CageAnswer;

public class CageTest {

    @Test
    public void isSolution_wrong_answer_should_fail() {
        Cage cage = Cage.parseCage("2 / d2 e2", " ");
        CageAnswer cageAnswer = Answers.newCageAnswer(Arrays.asList(2, 5));

        assertFalse(cage.isSolution(cageAnswer));
    }
}