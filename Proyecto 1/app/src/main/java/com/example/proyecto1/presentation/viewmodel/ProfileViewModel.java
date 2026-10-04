package com.example.proyecto1.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto1.AppContainer;
import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.usecase.GetCurrentUserUseCase;
import com.example.proyecto1.domain.usecase.GetUserByIdUseCase;
import com.example.proyecto1.domain.usecase.UpdateProfilePhotoUseCase;

public class ProfileViewModel extends ViewModel {
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateProfilePhotoUseCase updateProfilePhotoUseCase;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> uploading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> photoUpdated = new MutableLiveData<>();

    public ProfileViewModel() {
        AppContainer container = AppContainer.get();

        getCurrentUserUseCase = new GetCurrentUserUseCase(container.authRepository);
        getUserByIdUseCase = new GetUserByIdUseCase(container.userRepository);
        updateProfilePhotoUseCase = new UpdateProfilePhotoUseCase(
                container.authRepository,
                container.profileRepository,
                container.userRepository
        );
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Boolean> getUploading() {
        return uploading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getPhotoUpdated() {
        return photoUpdated;
    }

    public void loadProfile() {
        User currentUser = getCurrentUserUseCase.execute();
        if (currentUser == null) {
            error.setValue("No hay sesión iniciada");
            return;
        }

        getUserByIdUseCase.execute(currentUser.getId(), new ChatRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                user.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                error.setValue(e.getMessage());
            }
        });
    }

    public void updatePhoto(String imageUri) {
        uploading.setValue(true);
        photoUpdated.setValue(false);

        updateProfilePhotoUseCase.execute(imageUri, new ChatRepository.RepositoryCallback<String>() {
            @Override
            public void onSuccess(String photoUrl) {
                uploading.setValue(false);

                User currentUser = user.getValue();
                if (currentUser != null) {
                    currentUser.setPhotoUrl(photoUrl);
                    user.setValue(currentUser);
                }

                photoUpdated.setValue(true);
            }

            @Override
            public void onError(Exception e) {
                uploading.setValue(false);
                error.setValue(e.getMessage());
            }
        });
    }

    public void resetPhotoUpdatedMessage() {
        photoUpdated.setValue(false);
    }
}