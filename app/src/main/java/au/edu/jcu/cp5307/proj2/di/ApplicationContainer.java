package au.edu.jcu.cp5307.proj2.di;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.Collection;

import au.edu.jcu.cp5307.proj2.dal.datasource.KenkenInMemoryDataSource;
import au.edu.jcu.cp5307.proj2.dal.datasource.UserSharePreferencesDataSource;
import au.edu.jcu.cp5307.proj2.dal.contracts.KenkenRepository;
import au.edu.jcu.cp5307.proj2.dal.repositories.KenkenInMemoryRepository;
import au.edu.jcu.cp5307.proj2.dal.repositories.UserSharedPreferencesRepository;
import au.edu.jcu.cp5307.proj2.dal.contracts.UserRepository;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.ConstraintBasedKenkenSolver;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.KenkenSolver;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.ArcConsistencyConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.CageConsistencyConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.DualConsistencyConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.constraint.KenkenEliminatingConstraint;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.initializer.CageConsistencyValuesInitializer;
import au.edu.jcu.cp5307.proj2.application.kenken.solver.initializer.KenkenValuesInitializer;
import au.edu.jcu.cp5307.proj2.utils.DefaultGameScoreCalculator;
import au.edu.jcu.cp5307.proj2.utils.contracts.GameScoreCalculator;

public class ApplicationContainer {
    private final KenkenRepository kenkenRepository;
    private final UserRepository userRepository;

    private final KenkenSolver kenkenSolver;
    private final GameScoreCalculator gameScoreCalculator;

    public ApplicationContainer(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        UserSharePreferencesDataSource userDataSource = new UserSharePreferencesDataSource(sharedPreferences);
        userRepository = new UserSharedPreferencesRepository(userDataSource);

        KenkenInMemoryDataSource kenkenDataSource = new KenkenInMemoryDataSource();
        kenkenRepository = new KenkenInMemoryRepository(kenkenDataSource);

        KenkenValuesInitializer valuesInitializer = new CageConsistencyValuesInitializer();
        Collection<KenkenEliminatingConstraint> constraints = Arrays.asList(
                new ArcConsistencyConstraint(),
                new DualConsistencyConstraint(),
                new CageConsistencyConstraint()
        );
        kenkenSolver = new ConstraintBasedKenkenSolver(valuesInitializer, constraints);

        gameScoreCalculator = new DefaultGameScoreCalculator();
    }

    public KenkenRepository getKenkenRepository() {
        return kenkenRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public KenkenSolver getKenkenSolver() {
        return kenkenSolver;
    }

    public GameScoreCalculator getGameScoreCalculator() {
        return gameScoreCalculator;
    }
}