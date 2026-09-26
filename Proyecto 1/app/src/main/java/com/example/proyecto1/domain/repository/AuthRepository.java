package com.example.proyecto1.domain.repository;

import com.example.proyecto1.domain.model.User;

public interface AuthRepository {
    void register(String email, String password, ChatRepository.RepositoryCallback<User> callback);

    void login(String email, String password, ChatRepository.RepositoryCallback<User> callback);

    void logout();

    User getCurrentUserUseCase();
}
