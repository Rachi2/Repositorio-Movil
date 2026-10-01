package com.example.proyecto1.data.repository;

import com.example.proyecto1.data.remote.FirebaseMessagingSource;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.NotificationRepository;

public class NotificationRepositoryImpl implements NotificationRepository {
    private final FirebaseMessagingSource source;

    public NotificationRepositoryImpl(FirebaseMessagingSource source) {
        this.source = source;
    }

    @Override
    public void registerDevice(ChatRepository.RepositoryCallback<String> callback) {
        source.register(callback);
    }

    public void getDeviceId(ChatRepository.RepositoryCallback<String> callback) {
        source.getDeviceId(callback);
    }
}
