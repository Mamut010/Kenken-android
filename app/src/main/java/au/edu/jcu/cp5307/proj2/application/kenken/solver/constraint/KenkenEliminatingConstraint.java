package au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint;

import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.SolvingContext;

public interface KenkenEliminatingConstraint {
    boolean test(SolvingContext context, Square square, int eliminatedValue);
}
