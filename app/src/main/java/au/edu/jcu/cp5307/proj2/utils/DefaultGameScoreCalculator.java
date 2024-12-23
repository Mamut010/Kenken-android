package au.edu.jcu.cp5307.proj2.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import au.edu.jcu.cp5307.proj2.utils.contracts.GameScoreCalculator;
import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.utils.helpers.KenkenHelpers;
import au.edu.jcu.cp5307.proj2.utils.helpers.MathHelpers;

public class DefaultGameScoreCalculator implements GameScoreCalculator {
    private static final BigDecimal SIZE_MULTIPLIER = BigDecimal.valueOf(1e2);
    private static final BigDecimal DIFFICULTY_MULTIPLIER = BigDecimal.valueOf(1e3);
    private static final BigDecimal TIME_MULTIPLIER = BigDecimal.valueOf(1e-2);
    private static final BigDecimal MISTAKE_MULTIPLIER = BigDecimal.valueOf(2e3);
    private static final BigDecimal HINT_MULTIPLIER = BigDecimal.valueOf(5e2);
    private static final BigDecimal SCORE_LOWER_BOUND = BigDecimal.ZERO;
    private static final BigDecimal SCORE_UPPER_BOUND = BigDecimal.valueOf(Integer.MAX_VALUE);

    @Override
    public int calculateScore(KenkenGame kenken, StatisticalResult statistics) {
        int size = kenken.getSize();

        BigDecimal baseScore = calculateBaseScore(size);
        BigDecimal penalty = calculatePenalty(size, statistics);

        BigDecimal finalScore = baseScore.subtract(penalty);
        return finalScore.max(SCORE_LOWER_BOUND).min(SCORE_UPPER_BOUND).intValue();
    }

    private BigDecimal calculateBaseScore(int size) {
        int difficultyLevel = KenkenHelpers.assertDifficulty(size).getLevel();

        BigDecimal sizeScore = MathHelpers
                .multiply(Math.pow(size, 3), SIZE_MULTIPLIER);

        BigDecimal difficultyScore = MathHelpers
                .multiply(Math.pow(difficultyLevel, 2), DIFFICULTY_MULTIPLIER);

        return sizeScore.add(difficultyScore);
    }

    private BigDecimal calculatePenalty(int size, StatisticalResult statistics) {
        BigDecimal timePenalty = MathHelpers
                .multiply(statistics.timeInMillis(), TIME_MULTIPLIER)
                .divide(BigDecimal.valueOf(size), RoundingMode.DOWN);

        BigDecimal mistakePenalty = MathHelpers
                .multiply(statistics.mistakeCount(), MISTAKE_MULTIPLIER)
                .divide(BigDecimal.valueOf(Math.sqrt(size)), RoundingMode.DOWN);

        BigDecimal hintPenalty = MathHelpers
                .multiply(statistics.hintUsedCount(), HINT_MULTIPLIER)
                .multiply(BigDecimal.valueOf(size));

        return timePenalty.add(mistakePenalty).add(hintPenalty);
    }
}
