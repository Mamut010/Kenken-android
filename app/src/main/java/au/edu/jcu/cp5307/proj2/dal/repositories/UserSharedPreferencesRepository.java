package au.edu.jcu.cp5307.proj2.dal.repositories;

import au.edu.jcu.cp5307.proj2.dal.contracts.UserRepository;
import au.edu.jcu.cp5307.proj2.dal.datasource.UserSharePreferencesDataSource;
import au.edu.jcu.cp5307.proj2.models.User;

public class UserSharedPreferencesRepository implements UserRepository {
    private final UserSharePreferencesDataSource userDataSource;

    public UserSharedPreferencesRepository(UserSharePreferencesDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @Override
    public User findCurrentUser() {
        return userDataSource.fetchCurrentUser();
    }

    @Override
    public void saveCurrentUser(User user, OnUserSavedListener listener) {
        userDataSource.saveCurrentUser(user);
        if (listener != null) {
            listener.onUserSaved(user);
        }
    }
}
