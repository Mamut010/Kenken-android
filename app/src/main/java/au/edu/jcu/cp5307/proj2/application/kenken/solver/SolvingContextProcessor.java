package au.edu.jcu.cp5307.proj2.application.kenken.solver;

import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;

public interface SolvingContextProcessor {
    SolvingContext fill(SolvingContext context, Square square, int filledValue);
    boolean eliminate(SolvingContext context, Square square, int eliminatedValue);
}
