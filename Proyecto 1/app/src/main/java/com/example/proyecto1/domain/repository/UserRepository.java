package com.example.proyecto1.domain.repository;

import com.example.proyecto1.domain.model.User;
import java.util.List;

public interface UserRepository {

    // Método necesario para la Fase 1 (Guardar usuario al registrarse)
    void saveUser(User user, ChatRepository.RepositoryCallback<Void> callback);

    // Métodos que se utilizarán en la Fase 2 (Lista de usuarios)
    void getUsers(ChatRepository.RepositoryCallback<List<User>> callback);
    void getUserById(String userId, ChatRepository.RepositoryCallback<User> callback);
    void saveFcmToken(String userId, String token, ChatRepository.RepositoryCallback<Void> callback);
}