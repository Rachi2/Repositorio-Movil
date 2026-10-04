package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.UserRepository;

public class SaveUserUseCase {

    private final UserRepository userRepository;

    public SaveUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user, ChatRepository.RepositoryCallback<Void> callback) {
        userRepository.saveUser(user, callback);
    }
}