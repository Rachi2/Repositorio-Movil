package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.NotificationRepository;
import com.example.proyecto1.domain.repository.UserRepository;

public class RegisterDeviceUseCase {
    private final AuthRepository authRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public RegisterDeviceUseCase(AuthRepository authRepository, NotificationRepository notificationRepository, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void execute(ChatRepository.RepositoryCallback<Void> callback) {
        User currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("No hay sesión iniciada"));
            return;
        }

        notificationRepository.registerDevice(new ChatRepository.RepositoryCallback<String>() {
            @Override
            public void onSuccess(String result) {
                userRepository.updateFcmToken(currentUser.getId(), result, callback);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}