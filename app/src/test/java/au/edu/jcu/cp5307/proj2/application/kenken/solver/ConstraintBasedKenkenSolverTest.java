package au.edu.jcu.cp5307.proj2.application.kenken.solver;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.application.kenken.answer.KenkenAnswer;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.ArcConsistencyConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.CageConsistencyConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.DualConsistencyConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.KenkenEliminatingConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.initializer.CageConsistencyValuesInitializer;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.initializer.KenkenValuesInitializer;

public class ConstraintBasedKenkenSolverTest {
    ConstraintBasedKenkenSolver solver;

    @Before
    public void setUp() {
        KenkenValuesInitializer valuesInitializer = new CageConsistencyValuesInitializer();
        Collection<KenkenEliminatingConstraint> constraints = List.of(
                new ArcConsistencyConstraint(),
                new DualConsistencyConstraint(),
                new CageConsistencyConstraint()
        );

        solver = new ConstraintBasedKenkenSolver(valuesInitializer, constraints);
    }

    @Test
    public void solve_should_correct() {
        KenkenGame kenken1 = KenkenGame.parseFromDescriptions(
            """
            3 + A1 A2; 3 = A3;
            3 = B1; 4 + B2 B3 C3;
            5 + C1 C2
            """
        );
        KenkenGame kenken2 = KenkenGame.parseFromDescriptions(
            """
            7 + A1 B1; 2 / C1 D1;
            1 - A2 A3; 3 - B2 B3;
            2 / A4 B4; 3 = C2;
            12 × C3 C4 D4; 2 / D2 D3
            """
        );
        KenkenGame kenken3 = KenkenGame.parseFromDescriptions(
            """
            10 + A1 B1; 7 * A2 A3; 13 + A4 A5 B4; 2 / A6  A7; 12 + A8 B7 B8; 63 * A9 B9;
            15 * B2 B3; 144 * B5 B6 C5;
            6 * C1 D1 E1; 4 - C2 C3; 8 + C4 D4; 22 + C6 D5 D6; 2 / C7 D7; 5 + C8 C9;
            25 + D2 D3 E2 E3; 1 - D8 E8; 11 + D9 E9;
            36 * E4 F4; 15 * E5 F5; 4 - E6 E7;
            2 - F1 G1; 6 - F2 F3; 2 / F6 F7; 56 * F8 F9 G8 G9;
            9 * G2 G3; 10 + G4 G5; 24 * G6 G7;
            1 - H1 I1; 1 - H2 I2; 3 / H3 I3; 3 - H4 I4; 5 - H5 I5; 35 * H6 I6; 5 - H7 H8; 9 + H9 I9;
            4 - I7 I8
            """
        );

        KenkenAnswer answer1 = solver.solve(kenken1);
        KenkenAnswer answer2 = solver.solve(kenken2);
        KenkenAnswer answer3 = solver.solve(kenken3);

        assertTrue(kenken1.isSolution(answer1));
        assertTrue(kenken2.isSolution(answer2));
        assertTrue(kenken3.isSolution(answer3));
    }
}