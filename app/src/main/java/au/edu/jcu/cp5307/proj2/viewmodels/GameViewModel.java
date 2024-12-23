package au.edu.jcu.cp5307.proj2.viewmodels;

import static androidx.lifecycle.SavedStateHandleSupport.createSavedStateHandle;

import static au.edu.jcu.cp5307.proj2.utils.contracts.GameScoreCalculator.StatisticalResult;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import au.edu.jcu.cp5307.proj2.constants.Constants;
import au.edu.jcu.cp5307.proj2.dal.contracts.KenkenRepository;
import au.edu.jcu.cp5307.proj2.di.ApplicationContainer;
import au.edu.jcu.cp5307.proj2.models.Kenken;
import au.edu.jcu.cp5307.proj2.application.kenken.answer.KenkenAnswer;
import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Cage;
import au.edu.jcu.cp5307.proj2.application.kenken.core.component.Square;
import au.edu.jcu.cp5307.proj2.utils.contracts.GameScoreCalculator;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.KenkenSolver;
import au.edu.jcu.cp5307.proj2.utils.helpers.CollectionHelpers;
import au.edu.jcu.cp5307.proj2.utils.helpers.KenkenHelpers;
import au.edu.jcu.cp5307.proj2.utils.helpers.LiveDataHelpers;
import au.edu.jcu.cp5307.proj2.utils.Pair;
import au.edu.jcu.cp5307.proj2.utils.Point;
import au.edu.jcu.cp5307.proj2.utils.helpers.ViewModelHelpers;

public class GameViewModel extends ViewModel {
    public record NeighborInfo(
            boolean isLeftSameCage,
            boolean isTopSameCage,
            boolean isRightSameCage,
            boolean isBottomSameCage) {
    }

    public static final ViewModelInitializer<GameViewModel> initializer = new ViewModelInitializer<>(
            GameViewModel.class,
            creationExtras -> {
                ApplicationContainer container = ViewModelHelpers.getApplicationContainer(creationExtras);
                return new GameViewModel(
                        container.getKenkenRepository(),
                        container.getKenkenSolver(),
                        container.getGameScoreCalculator(),
                        createSavedStateHandle(creationExtras)
                );
            }
    );

    private static final String ID_KEY = "ID";
    private static final String IS_PROGRESSING_KEY = "IS_PROGRESSING";

    private final KenkenRepository kenkenRepository;
    private final KenkenSolver kenkenSolver;
    private final GameScoreCalculator scoreCalculator;
    private final SavedStateHandle savedStateHandle;
    private LiveData<KenkenGame> kenkenLiveData;
    private MediatorLiveData<KenkenAnswer> answerLiveData;
    private LiveData<Map<Point, Integer>> pointValueMapLiveData;
    private LiveData<Boolean> isGameCompletedLiveData;
    private MutableLiveData<Integer> mistakeCountLiveData;
    private MutableLiveData<Integer> hintUsedCountLiveData;

    private SoftReference<KenkenAnswer> kenkenAnswerCache;

    public GameViewModel(KenkenRepository kenkenRepository,
                         KenkenSolver kenkenSolver,
                         GameScoreCalculator scoreCalculator,
                         SavedStateHandle savedStateHandle) {
        this.kenkenRepository = kenkenRepository;
        this.kenkenSolver = kenkenSolver;
        this.scoreCalculator = scoreCalculator;
        this.savedStateHandle = savedStateHandle;
        initLiveData();
    }

    public LiveData<KenkenGame> getKenken() {
        return kenkenLiveData;
    }

    public LiveData<Map<Point, Integer>> getPointValueMap() {
        return pointValueMapLiveData;
    }

    public LiveData<Integer> getMistakeCount() {
        return mistakeCountLiveData;
    }

    public LiveData<Integer> getHintUsedCount() {
        return hintUsedCountLiveData;
    }

    public LiveData<Boolean> getIsGameCompleted() {
        return isGameCompletedLiveData;
    }

    public List<Integer> getIdsSortedByGameSize() {
        return kenkenRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparingInt(kenkenModel -> kenkenModel.game().getSize()))
                .map(Kenken::id)
                .collect(Collectors.toList());
    }

    public KenkenGame getKenkenById(int id) {
        Kenken kenkenModel = kenkenRepository.findOneById(id);
        return kenkenModel != null ? kenkenModel.game() : null;
    }

    public void fetchGame(int id) {
        savedStateHandle.set(ID_KEY, id);
    }

    public Integer getCurrentGameId() {
        return savedStateHandle.get(ID_KEY);
    }

    public Integer getPointValue(Point point) {
        Map<Point, Integer> pointValueMap = pointValueMapLiveData.getValue();
        return pointValueMap != null ? pointValueMap.get(point) : null;
    }

    public void updatePointValue(Point point, int value) {
        if (!answerLiveData.isInitialized()) {
            return;
        }

        LiveDataHelpers.notifyObserversFromMainOnFulfilledAction(answerLiveData, answer -> {
            Square square = KenkenHelpers.squareOfPoint(point);
            int previousValue = answer.getValue(square);
            if (previousValue == value) {
                return false;
            }

            answer.setValue(square, value);

            if (areOtherPointsAllNulls(answer, point) && previousValue == Constants.Type.INVALID_INT) {
                setIsProgressing(true);
            }

            boolean isCorrectedMove = Boolean.TRUE.equals(
                    checkAnswer().getOrDefault(KenkenHelpers.squareOfPoint(point), true)
            );
            if (!isCorrectedMove) {
                incrementMistakeCount();
            }

            return true;
        });
    }

    public void clearPointValue(Point point) {
        if (!answerLiveData.isInitialized()) {
            return;
        }

        LiveDataHelpers.notifyObserversFromMainOnFulfilledAction(answerLiveData, answer -> {
            Square square = KenkenHelpers.squareOfPoint(point);
            int previousValue = answer.getValue(square);
            if (previousValue == Constants.Type.INVALID_INT) {
                return false;
            }

            answer.removeValue(square);

            if (areOtherPointsAllNulls(answer, point)) {
                setIsProgressing(false);
            }
            return true;
        });
    }

    public void resetMistakeCount() {
        mistakeCountLiveData.setValue(0);
    }

    public Pair<Point, Integer> nextHint() {
        KenkenGame kenken = kenkenLiveData.getValue();
        int hintUsedCount = Objects.requireNonNull(hintUsedCountLiveData.getValue());
        if (kenken == null
                || hintUsedCount >= getMaxHintCount()
                || !Boolean.FALSE.equals(isGameCompletedLiveData.getValue())) {
            return null;
        }

        KenkenAnswer answer;
        synchronized (this) {
            try {
                answer = getKenkenAnswer();
            }
            catch (StackOverflowError e) {
                Log.e(
                        GameViewModel.class.getName(),
                        "Unable to calculate a hint due to insufficient resource"
                );
                answer = null;
            }

            if (kenkenAnswerCache.get() != answer) {
                kenkenAnswerCache = new SoftReference<>(answer);
            }
        }

        if (answer == null || answer.isEmpty()) {
            return null;
        }

        return calculateNextHint(answer);
    }

    public void incrementHintUsedCount() {
        int hintUsedCount = Objects.requireNonNull(hintUsedCountLiveData.getValue());
        if (hintUsedCount < getMaxHintCount()) {
            hintUsedCountLiveData.setValue(hintUsedCount + 1);
        }
    }

    public void resetHintUsedCount() {
        hintUsedCountLiveData.setValue(0);
    }

    public int getMaxHintCount() {
        return 3;
    }

    public boolean isGameStarted() {
        return kenkenLiveData.isInitialized();
    }

    public boolean isGameProgressing() {
        return Boolean.TRUE.equals(savedStateHandle.get(IS_PROGRESSING_KEY));
    }

    public Map<Point, Boolean> checkPointAnswer() {
        return checkAnswer()
                .entrySet()
                .stream()
                .collect(
                        HashMap::new,
                        (map, e) -> map.put(KenkenHelpers.pointOfSquare(e.getKey()), e.getValue()),
                        HashMap::putAll
                );
    }

    @SuppressWarnings("ConstantConditions")
    public Map<Point, String> getFirstPointWithSuperscripts() {
        KenkenGame kenken = kenkenLiveData.getValue();
        if (kenken == null) {
            return Collections.emptyMap();
        }
        return kenken
                .getCages()
                .stream()
                .map(cage -> {
                    Square firstSquare = cage.getFirstSquare();
                    return firstSquare != null
                            ? new Pair<>(KenkenHelpers.pointOfSquare(firstSquare), cage)
                            : null;
                })
                .filter(Objects::nonNull)
                .collect(
                        HashMap::new,
                        (map, pair) -> map.put(pair.first(), pair.second().getNotation()),
                        HashMap::putAll
                );
    }

    public NeighborInfo getNeighborInfo(Point point) {
        KenkenGame kenken = kenkenLiveData.getValue();
        if (kenken == null) {
            return null;
        }
        Square square = KenkenHelpers.squareOfPoint(point);
        Cage cage = kenken.getSquareCageMap().get(square);
        if (cage == null) {
            return null;
        }

        Set<Square> squaresInCage = cage.getSquares();
        Point leftPoint = new Point(point.row(), point.column() - 1);
        Point topPoint = new Point(point.row() - 1, point.column());
        Point rightPoint = new Point(point.row(), point.column() + 1);
        Point bottomPoint = new Point(point.row() + 1, point.column());
        Square leftSquare = KenkenHelpers.squareOfPoint(leftPoint);
        Square topSquare = KenkenHelpers.squareOfPoint(topPoint);
        Square rightSquare = KenkenHelpers.squareOfPoint(rightPoint);
        Square bottomSquare = KenkenHelpers.squareOfPoint(bottomPoint);
        return new NeighborInfo(
                squaresInCage.contains(leftSquare),
                squaresInCage.contains(topSquare),
                squaresInCage.contains(rightSquare),
                squaresInCage.contains(bottomSquare)
        );
    }

    public String getSuperscriptInCageOfPoint(Point point) {
        KenkenGame kenken = kenkenLiveData.getValue();
        if (kenken == null) {
            return null;
        }
        Cage cage = kenken.getSquareCageMap().get(KenkenHelpers.squareOfPoint(point));
        return cage != null ? cage.getNotation() : null;
    }

    public int calculateScore(long timeInMillis) {
        KenkenGame kenken = kenkenLiveData.getValue();
        if (kenken == null) {
            return 0;
        }

        int mistake = Objects.requireNonNull(mistakeCountLiveData.getValue());
        int hint = Objects.requireNonNull(hintUsedCountLiveData.getValue());

        StatisticalResult statistics = new StatisticalResult(timeInMillis, mistake, hint);
        return scoreCalculator.calculateScore(kenken, statistics);
    }

    private void initLiveData() {
        this.kenkenLiveData = Transformations.map(
                Transformations.distinctUntilChanged(
                        Transformations.map(
                                Transformations.distinctUntilChanged(
                                        savedStateHandle.<Integer>getLiveData(ID_KEY)
                                ),
                                id -> {
                                    setIsProgressing(false);
                                    kenkenAnswerCache = new SoftReference<>(null);
                                    return kenkenRepository.findOneById(id);
                                }
                        )
                ),
                Kenken::game
        );
        this.answerLiveData = new MediatorLiveData<>();
        answerLiveData.addSource(
                kenkenLiveData,
                kenken -> answerLiveData.setValue(KenkenAnswer.emptyAnswer())
        );
        this.pointValueMapLiveData = Transformations.map(
                answerLiveData,
                answer -> answerToPointValueMap(Objects.requireNonNull(kenkenLiveData.getValue()), answer)
        );
        this.isGameCompletedLiveData = Transformations.distinctUntilChanged(
                Transformations.map(
                        answerLiveData,
                        answer -> {
                            KenkenGame kenken = kenkenLiveData.getValue();
                            return kenken != null && kenken.isSolution(answer);
                        }
                )
        );
        this.mistakeCountLiveData = new MutableLiveData<>(0);
        this.hintUsedCountLiveData = new MutableLiveData<>(0);
    }

    private KenkenAnswer getKenkenAnswer() {
        final KenkenAnswer cache = kenkenAnswerCache.get();
        if (cache != null) {
            KenkenAnswer answer = Objects.requireNonNull(answerLiveData.getValue());
            boolean sameAsCache = answer
                    .getSquares()
                    .stream()
                    .filter(sqr -> answer.getValue(sqr) != Constants.Type.INVALID_INT)
                    .allMatch(sqr -> answer.getValue(sqr) == cache.getValue(sqr));
            if (sameAsCache) {
                return cache;
            }
        }

        KenkenAnswer answer = cache;
        KenkenAnswer currentProgressAnswer = kenkenSolver.solve(
                kenkenLiveData.getValue(),
                getCurrentProgress()
        );
        if (currentProgressAnswer != null && !currentProgressAnswer.isEmpty()) {
            answer = currentProgressAnswer;
        }
        else if (cache == null) {
            answer = kenkenSolver.solve(kenkenLiveData.getValue());
        }
        return answer;
    }

    private Map<Square, Set<Integer>> getCurrentProgress() {
        KenkenAnswer answer = Objects.requireNonNull(answerLiveData.getValue());
        return answer
                .getSquares()
                .stream()
                .filter(sqr -> answer.getValue(sqr) != Constants.Type.INVALID_INT)
                .collect(
                        HashMap::new,
                        (map, sqr) -> map.put(sqr, Collections.singleton(answer.getValue(sqr))),
                        HashMap::putAll
                );
    }

    private Pair<Point, Integer> calculateNextHint(KenkenAnswer answer) {
        Pair<List<Point>, List<Point>> IncorrectlyFilledAndUnfilledPointsPair =
                getIncorrectlyFilledAndUnfilledPoints(answer);
        List<Point> incorrectlyFilledPoints = IncorrectlyFilledAndUnfilledPointsPair.first();
        List<Point> unfilledPoints = IncorrectlyFilledAndUnfilledPointsPair.second();

        List<Point> hintCandidates = incorrectlyFilledPoints.isEmpty()
                ? unfilledPoints
                : incorrectlyFilledPoints;
        Point hinted = CollectionHelpers.getRandomItem(hintCandidates);
        return new Pair<>(hinted, answer.getValue(KenkenHelpers.squareOfPoint(hinted)));
    }

    private Pair<List<Point>, List<Point>> getIncorrectlyFilledAndUnfilledPoints(KenkenAnswer answer) {
        Map<Point, Integer> pointValueMap = Objects.requireNonNull(pointValueMapLiveData.getValue());
        List<Point> incorrectlyFilledPoints = new ArrayList<>();
        List<Point> unfilledPoints = new ArrayList<>();
        pointValueMap
                .entrySet()
                .forEach(e -> {
                    Point point = e.getKey();
                    Integer value = e.getValue();
                    if (value == null) {
                        unfilledPoints.add(point);
                    }
                    else if (value != answer.getValue(KenkenHelpers.squareOfPoint(point))) {
                        incorrectlyFilledPoints.add(point);
                    }
                });
        return new Pair<>(incorrectlyFilledPoints, unfilledPoints);
    }

    private Map<Square, Boolean> checkAnswer() {
        KenkenGame kenken = kenkenLiveData.getValue();
        KenkenAnswer answer = answerLiveData.getValue();
        if (kenken == null || answer == null) {
            return Collections.emptyMap();
        }

        return kenken.check(answer);
    }

    private void setIsProgressing(boolean progressing){
        savedStateHandle.set(IS_PROGRESSING_KEY, progressing);
    }

    private void incrementMistakeCount() {
        int currentMistakeCount = Objects.requireNonNull(mistakeCountLiveData.getValue());
        mistakeCountLiveData.setValue(currentMistakeCount + 1);
    }

    private static boolean areOtherPointsAllNulls(KenkenAnswer answer, Point point) {
        return answer
                .getSquares()
                .stream()
                .filter(sqr -> !sqr.equals(KenkenHelpers.squareOfPoint(point)))
                .allMatch(sqr -> answer.getValue(sqr) == Constants.Type.INVALID_INT);
    }

    private static Map<Point, Integer> answerToPointValueMap(KenkenGame kenken, KenkenAnswer answer) {
        return kenken
                .getSquares()
                .stream()
                .collect(
                        HashMap::new,
                        (map, sqr) -> {
                            Point point = KenkenHelpers.pointOfSquare(sqr);
                            int value = answer.getValue(sqr);
                            map.put(point, value != Constants.Type.INVALID_INT ? value : null);
                        },
                        HashMap::putAll
                );
    }
}