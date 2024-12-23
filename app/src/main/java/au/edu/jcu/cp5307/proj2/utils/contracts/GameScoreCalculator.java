package au.edu.jcu.cp5307.proj2.utils.contracts;

import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;

public interface GameScoreCalculator {
    record StatisticalResult(long timeInMillis, int mistakeCount, int hintUsedCount) {}

    int calculateScore(KenkenGame kenken, StatisticalResult statistics);
}
