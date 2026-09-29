package com.example.proyecto1.data.repository;

import com.example.proyecto1.data.model.UserDto;
import com.example.proyecto1.data.remote.FirestoreUserSource;
import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.UserRepository;

import java.util.ArrayList;
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
        firestoreUserSource.getUsers(new ChatRepository.RepositoryCallback<List<UserDto>>() {
            @Override
            public void onSuccess(List<UserDto> result) {
                List<User> users = new ArrayList<>();
                for (UserDto dto : result) {
                    users.add(dto.toDomain());
                }
                callback.onSuccess(users);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getUserById(String userId, ChatRepository.RepositoryCallback<User> callback) {
        firestoreUserSource.getUserById(userId, new ChatRepository.RepositoryCallback<UserDto>() {

            @Override
            public void onSuccess(UserDto result) {
                if (result == null) {
                    callback.onSuccess(null);
                } else {
                    callback.onSuccess(result.toDomain());
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void saveFcmToken(String userId, String token, ChatRepository.RepositoryCallback<Void> callback) {
        // Se implementara en la Fase 4 (Notificaciones FCM)
    }

    @Override
    public void updatePhotoUrl(String userId, String photoUrl, ChatRepository.RepositoryCallback<Void> callback) {
        firestoreUserSource.updatePhotoUrl(userId, photoUrl, callback);
    }
}