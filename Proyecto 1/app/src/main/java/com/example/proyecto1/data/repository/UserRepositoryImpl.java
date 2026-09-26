package com.example.proyecto1.data.repository;

import com.example.proyecto1.data.model.UserDto;
import com.example.proyecto1.data.remote.FirestoreUserSource;
import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.UserRepository;

import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private final FirestoreUserSource firestoreUserSource;

    public UserRepositoryImpl(FirestoreUserSource firestoreUserSource) {
        this.firestoreUserSource = firestoreUserSource;
    }

    @Override
    public void saveUser(User user, ChatRepository.RepositoryCallback<Void> callback) {
        UserDto dto = UserDto.fromDomain(user);
        firestoreUserSource.saveUser(dto, callback);
    }

    @Override
    public void getUsers(ChatRepository.RepositoryCallback<List<User>> callback) {
        // Se implementara completamente en la Fase 2
    }

    @Override
    public void getUserById(String userId, ChatRepository.RepositoryCallback<User> callback) {
        // Se implementara completamente en la Fase 2
    }

    @Override
    public void saveFcmToken(String userId, String token, ChatRepository.RepositoryCallback<Void> callback) {
        // Se implementara en la Fase 4 (Notificaciones FCM)
    }
}