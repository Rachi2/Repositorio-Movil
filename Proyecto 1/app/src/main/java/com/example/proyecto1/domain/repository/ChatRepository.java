package com.example.proyecto1.domain.repository;

import com.example.proyecto1.domain.model.Message;
import java.util.List;

public interface ChatRepository {
    void sendMessage(String conversationId, Message message, RepositoryCallback<Void> callback);
    void sendImage(String conversationId, String imageUri, RepositoryCallback<Void> callback);

    // Escuchar mensajes en tiempo real
    ListenerRegistration listenToMessages(String conversationId, RepositoryCallback<List<Message>> callback);

    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    interface ListenerRegistration {
        void remove();
    }
}