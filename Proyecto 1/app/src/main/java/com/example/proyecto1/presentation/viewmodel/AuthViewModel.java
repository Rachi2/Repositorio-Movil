package com.example.proyecto1.presentation.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto1.data.remote.FirebaseAuthSource;
import com.example.proyecto1.data.remote.FirestoreUserSource;
import com.example.proyecto1.data.repository.AuthRepositoryImpl;
import com.example.proyecto1.data.repository.UserRepositoryImpl;
import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.usecase.GetCurrentUserUseCase;
import com.example.proyecto1.domain.usecase.LoginUseCase;
import com.example.proyecto1.domain.usecase.RegisterUseCase;
import com.example.proyecto1.domain.usecase.SaveUseCase;

public class AuthViewModel extends ViewModel {
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AuthViewModel() {
        FirebaseAuthSource firebaseSource = new FirebaseAuthSource();
        FirestoreUserSource firestoreUserSource = new FirestoreUserSource();

        UserRepositoryImpl userRepository = new UserRepositoryImpl(firestoreUserSource);
        AuthRepositoryImpl authRepository = new AuthRepositoryImpl(firebaseSource);

        SaveUseCase saveUseCase = new SaveUseCase(userRepository);

        this.loginUseCase = new LoginUseCase(authRepository);
        this.getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
        this.registerUseCase = new RegisterUseCase(authRepository, saveUseCase);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<String> getError() {
        return error;
    }

    private String validateCredentials(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.isEmpty()) {
            return "Completa todos los campos";
        }

        String cleanEmail = email.trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return "El correo no es válido";
        }

        if (password.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }

        return null;
    }

    private ChatRepository.RepositoryCallback<User> createAuthCallback() {
        return new ChatRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User u) {
                loading.setValue(false);
                user.setValue(u);
            }

            @Override
            public void onError(Exception e) {
                loading.setValue(false);
                if (e != null) {
                    error.setValue(e.getMessage());
                }
            }
        };
    }

    public void login(String email, String password) {
        String validation = validateCredentials(email, password);
        if (validation != null) {
            error.setValue(validation);
            return;
        }

        loading.setValue(true);
        String cleanEmail = email.trim();
        loginUseCase.execute(cleanEmail, password, createAuthCallback());
    }


    public void register(String name, String email, String password, String confirmPassword) {
        if (name == null || name.trim().isEmpty()) {
            error.setValue("Completa todos los campos");
            return;
        }

        String validation = validateCredentials(email, password);
        if (validation != null) {
            error.setValue(validation);
            return;
        }

        if (password != null && !password.equals(confirmPassword)) {
            error.setValue("Las contraseñas no coinciden");
            return;
        }

        loading.setValue(true);
        String cleanEmail = email.trim();
        registerUseCase.execute(name, cleanEmail, password, createAuthCallback());
    }

    public void checkSession() {
        User currentUser = getCurrentUserUseCase.execute();

        if (currentUser != null) user.setValue(currentUser);
    }
}
