package com.example.proyecto1.domain.repository;

import com.example.proyecto1.domain.model.Message;

public interface ChatRepository {
    void sendMessage(String conversationId, Message message, RepositoryCallback<Void> callback);
    void sendImage(String conversationId, String imageUri, RepositoryCallback<Void> callback);

    // Interfaz auxiliar para manejar callbacks asincronos en Java
    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}