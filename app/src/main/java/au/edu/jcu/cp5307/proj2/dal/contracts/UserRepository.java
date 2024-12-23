package au.edu.jcu.cp5307.proj2.dal.contracts;

import au.edu.jcu.cp5307.proj2.models.User;

public interface UserRepository {
    interface OnUserSavedListener {
        void onUserSaved(User user);
    }

    User findCurrentUser();
    void saveCurrentUser(User user, OnUserSavedListener listener);
}
