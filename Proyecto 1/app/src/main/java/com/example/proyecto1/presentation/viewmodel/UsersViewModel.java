package com.example.proyecto1.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto1.data.remote.FirebaseAuthSource;
import com.example.proyecto1.data.remote.FirebaseMessagingSource;
import com.example.proyecto1.data.remote.FirestoreUserSource;
import com.example.proyecto1.data.repository.AuthRepositoryImpl;
import com.example.proyecto1.data.repository.NotificationRepositoryImpl;
import com.example.proyecto1.data.repository.UserRepositoryImpl;
import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.usecase.FilterUsersUseCase;
import com.example.proyecto1.domain.usecase.GetUsersUseCase;
import com.example.proyecto1.domain.usecase.LogoutUseCase;
import com.example.proyecto1.domain.usecase.RegisterDeviceUseCase;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {
    private final GetUsersUseCase getUsersUseCase;
    private final LogoutUseCase logoutUseCase;
    private final FilterUsersUseCase filterUsersUseCase;
    private final RegisterDeviceUseCase registerDeviceUseCase;
    private final MutableLiveData<List<User>> users = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>();
    private List<User> allUsers = new ArrayList<>();
    private String currentQuery = "";

    public UsersViewModel() {
        FirebaseAuthSource firebaseSource = new FirebaseAuthSource();
        FirestoreUserSource firestoreUserSource = new FirestoreUserSource();

        UserRepositoryImpl userRepository = new UserRepositoryImpl(firestoreUserSource);
        AuthRepositoryImpl authRepository = new AuthRepositoryImpl(firebaseSource);
        NotificationRepositoryImpl notificationRepository = new NotificationRepositoryImpl(new FirebaseMessagingSource());

        this.getUsersUseCase = new GetUsersUseCase(userRepository, authRepository);
        this.logoutUseCase = new LogoutUseCase(authRepository);
        this.filterUsersUseCase = new FilterUsersUseCase();
        this.registerDeviceUseCase = new RegisterDeviceUseCase(authRepository, notificationRepository);

        registerDeviceUseCase.execute(new ChatRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Registro pedido correctamente
            }

            @Override
            public void onError(Exception e) {
                // Si falla, la app sigue funcionando; solo no llegarán notificaciones
            }
        });
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getLoggedOut() {
        return loggedOut;
    }

    public void loadUsers() {
        loading.setValue(true);
        getUsersUseCase.execute(new ChatRepository.RepositoryCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                loading.setValue(false);
                allUsers = result;
                applyFilter();
            }

            @Override
            public void onError(Exception e) {
                loading.setValue(false);
                if (e != null) {
                    error.setValue(e.getMessage());
                }
            }
        });
    }

    public void logout() {
        logoutUseCase.execute();
        loggedOut.setValue(true);
    }

    public boolean isSearching() {
        return currentQuery != null && !currentQuery.trim().isEmpty();
    }

    public void search(String query) {
        this.currentQuery = query;
        applyFilter();
    }

    private void applyFilter() {
        users.setValue(filterUsersUseCase.execute(allUsers, currentQuery));
    }
}
