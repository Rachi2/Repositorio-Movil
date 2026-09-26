package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.repository.AuthRepository;

public class LogoutUseCase {
    private final AuthRepository authRepository;

    public LogoutUseCase(AuthRepository authRepository){
        this.authRepository = authRepository;
    }

    public void execute(){
        authRepository.logout();
    }
}
