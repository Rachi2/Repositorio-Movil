package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.NotificationRepository;

public class RegisterDeviceUseCase {
    private final AuthRepository authRepository;
    private final NotificationRepository notificationRepository;

    public RegisterDeviceUseCase(AuthRepository authRepository, NotificationRepository notificationRepository) {
        this.authRepository = authRepository;
        this.notificationRepository = notificationRepository;
    }

    public void execute(ChatRepository.RepositoryCallback<Void> callback) {
        User currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("No hay sesión iniciada"));
            return;
        }

        notificationRepository.registerDevice(callback);
    }
}