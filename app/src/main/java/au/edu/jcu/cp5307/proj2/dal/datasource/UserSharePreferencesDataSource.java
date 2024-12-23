package au.edu.jcu.cp5307.proj2.dal.datasource;

import android.content.SharedPreferences;

import java.util.UUID;

import au.edu.jcu.cp5307.proj2.models.User;

public class UserSharePreferencesDataSource {
    private static final String USER_ID_PREFERENCE_KEY = "preference::user_id";
    private static final String RECORD_PREFERENCE_KEY = "preference::record";

    private final SharedPreferences sharedPreferences;

    public UserSharePreferencesDataSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public User fetchCurrentUser() {
        String userId = sharedPreferences.getString(USER_ID_PREFERENCE_KEY, generateRandomId());
        int record = sharedPreferences.getInt(RECORD_PREFERENCE_KEY, 0);
        User user = new User();
        user.setId(userId);
        user.setRecord(record);
        return user;
    }

    public void saveCurrentUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID_PREFERENCE_KEY, user.getId());
        editor.putInt(RECORD_PREFERENCE_KEY, user.getRecord());
        editor.apply();
    }

    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }
}
