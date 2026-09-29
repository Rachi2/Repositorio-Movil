package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.UserRepository;

public class SaveFcmTokenUseCase {

    private final UserRepository userRepository;

    public SaveFcmTokenUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String userId, String token, ChatRepository.RepositoryCallback<Void> callback) {
        if (userId == null || token == null) return;
        userRepository.updateFcmToken(userId, token, callback);
    }
}