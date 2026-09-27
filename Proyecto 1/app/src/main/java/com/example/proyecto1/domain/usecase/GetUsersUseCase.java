package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Devuelve la lista de usuarios para mostrar en pantalla:
// sin el usuario conectado y ordenada por nombre (ignorando mayúsculas).
public class GetUsersUseCase {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    public GetUsersUseCase(UserRepository userRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
    }

    public void execute(ChatRepository.RepositoryCallback<List<User>> callback) {
        // Puede ser null si no hay sesión; en ese caso no se filtra a nadie
        User currentUser = authRepository.getCurrentUser();

        userRepository.getUsers(new ChatRepository.RepositoryCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                List<User> users = new ArrayList<>();
                for (User user : result) {
                    // Verifica que el id no sea el de la sesion actual y que no sea null
                    boolean isCurrentUser = currentUser != null && currentUser.getId().equals(user.getId());
                    // Si no es el mismo usuario se agrega a la lista de usuarios
                    if (!isCurrentUser) {
                        users.add(user);
                    }
                }

                // Ordena por nombre sin distinguir mayúsculas; los que no tengan nombre van al final
                users.sort(Comparator.comparing(User::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

                callback.onSuccess(users);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}