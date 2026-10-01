package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.NotificationRepository;
import com.example.proyecto1.domain.repository.UserRepository;

public class LogoutUseCase {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public LogoutUseCase(AuthRepository authRepository, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public void execute(ChatRepository.RepositoryCallback<Void> callback) {
        User currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            authRepository.logout();
            callback.onSuccess(null);
        } else {
            notificationRepository.getDeviceId(new ChatRepository.RepositoryCallback<String>() {
                @Override
                public void onSuccess(String deviceId) {
                    userRepository.clearFcmTokenIfMatches(currentUser.getId(), deviceId,
                            new ChatRepository.RepositoryCallback<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    authRepository.logout();
                                    callback.onSuccess(null);
                                }

                                @Override
                                public void onError(Exception e) {
                                    // Aunque falle el borrado, el usuario igual tiene que poder salir
                                    authRepository.logout();
                                    callback.onSuccess(null);
                                }
                            });
                }

                @Override
                public void onError(Exception e) {
                    authRepository.logout();
                    callback.onSuccess(null);
                }
            });
        }
    }
}
