package au.edu.jcu.cp5307.proj2.application.kenken.solver.initializer;

import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import java.util.Map;
import java.util.Set;

public interface KenkenValuesInitializer {
    Map<Square, Set<Integer>> initialValues(KenkenGame kenken, Map<Square, Set<Integer>> given);
}
