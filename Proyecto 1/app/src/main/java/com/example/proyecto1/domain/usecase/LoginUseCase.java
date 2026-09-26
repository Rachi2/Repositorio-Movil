package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;

public class LoginUseCase {
    private final AuthRepository authRepository;

    public LoginUseCase(AuthRepository authRepository){
        this.authRepository= authRepository;
    }

    public void execute(String email, String password, ChatRepository.RepositoryCallback<User> callback){
        authRepository.login(email, password, callback);
    }
}
