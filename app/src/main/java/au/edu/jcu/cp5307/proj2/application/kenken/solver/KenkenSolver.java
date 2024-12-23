package au.edu.jcu.cp5307.proj2.application.kenken.solver;

import java.util.Map;
import java.util.Set;

import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.application.kenken.answer.KenkenAnswer;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;

public interface KenkenSolver {
    KenkenAnswer solve(KenkenGame kenken, Map<Square, Set<Integer>> given);
    default KenkenAnswer solve(KenkenGame kenken) {
        return solve(kenken, null);
    }
}
