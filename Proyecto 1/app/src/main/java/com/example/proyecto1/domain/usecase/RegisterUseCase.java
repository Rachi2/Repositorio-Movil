package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;

public class RegisterUseCase {
    private final AuthRepository authRepository;
    private final SaveUseCase saveUseCase;

    public RegisterUseCase(AuthRepository auth, SaveUseCase save){
        this.authRepository = auth;
        this.saveUseCase = save;
    }

    public void execute(String name, String email, String password, ChatRepository.RepositoryCallback<User> callback){
        authRepository.register(email, password, new ChatRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                result.setName(name);
                saveUseCase.execute(result, new ChatRepository.RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
