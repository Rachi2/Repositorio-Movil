package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.UserRepository;

public class GetUserByIdUseCase {
    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String userId, ChatRepository.RepositoryCallback<User> callback) {
        userRepository.getUserById(userId, callback);
    }
}
