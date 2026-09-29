package com.example.proyecto1.domain.repository;

public interface NotificationRepository {
    void registerDevice(ChatRepository.RepositoryCallback<Void> callback);
}
