package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;

public class GetCurrentUserCase {
    private final AuthRepository authRepository;

    public GetCurrentUserCase(AuthRepository auth){
        this.authRepository = auth;
    }

    public User execute(){
        return authRepository.getCurrentUserUseCase();
    }
}
