package com.example.proyecto1.data.repository;

import com.example.proyecto1.data.remote.FirebaseAuthSource;
import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuthSource firebaseSource;

    public AuthRepositoryImpl(FirebaseAuthSource source) {
        this.firebaseSource = source;
    }

    // // Convierte el FirebaseUser en el User del dominio
    private User toUser(FirebaseUser firebaseUser) {
        if (firebaseUser == null)
            return null;

        String id = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String name = null; //temporal

        return new User(id, name, email);
    }

    // translateError se encarga de las traducciones de los errores que tira el Firebase
    private Exception translateError(Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) {
            return new Exception("La contraseña es muy débil");
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return new Exception("Este correo ya está registrado");
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return new Exception("Correo o contraseña incorrectos");
        } else if (e instanceof FirebaseNetworkException) {
            return new Exception("Sin conexión a internet");
        } else {
            return new Exception("Ocurrió un error, intenta de nuevo");
        }
    }

    @Override
    public void register(String email, String password, ChatRepository.RepositoryCallback<User> callback) {
        firebaseSource.register(email, password, new ChatRepository.RepositoryCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser result) {
                User user = toUser(result);
                callback.onSuccess(user);
            }

            @Override
            public void onError(Exception e) {
                Exception exception = translateError(e);
                callback.onError(exception);
            }
        });
    }

    @Override
    public void login(String email, String password, ChatRepository.RepositoryCallback<User> callback) {
        firebaseSource.login(email, password, new ChatRepository.RepositoryCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser result) {
                User user = toUser(result);
                callback.onSuccess(user);
            }

            @Override
            public void onError(Exception e) {
                Exception exception = translateError(e);
                callback.onError(exception);
            }
        });
    }

    @Override
    public void logout() {
        firebaseSource.logout();
    }

    @Override
    public User getCurrentUserUseCase() {
        return toUser(firebaseSource.getCurrentUser());
    }
}
