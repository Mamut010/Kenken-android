package au.edu.jcu.cp5307.proj2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Objects;

import au.edu.jcu.cp5307.proj2.dal.contracts.UserRepository;
import au.edu.jcu.cp5307.proj2.di.ApplicationContainer;
import au.edu.jcu.cp5307.proj2.models.User;
import au.edu.jcu.cp5307.proj2.utils.helpers.ViewModelHelpers;

public class UserViewModel extends ViewModel {
    public static final ViewModelInitializer<UserViewModel> initializer = new ViewModelInitializer<>(
            UserViewModel.class,
            creationExtras -> {
                ApplicationContainer container = ViewModelHelpers.getApplicationContainer(creationExtras);
                return new UserViewModel(container.getUserRepository());
            }
    );

    private final UserRepository userRepository;
    private final MutableLiveData<Integer> recordLiveData;

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        recordLiveData = new MutableLiveData<>(userRepository.findCurrentUser().getRecord());
    }

    public LiveData<Integer> getRecord() {
        return recordLiveData;
    }

    public void postScore(int score) {
        int currentRecord = Objects.requireNonNull(recordLiveData.getValue());
        if (score <= currentRecord) {
            return;
        }
        User currentUser = userRepository.findCurrentUser();
        currentUser.setRecord(score);
        userRepository.saveCurrentUser(currentUser, user -> recordLiveData.setValue(user.getRecord()));
    }
}
