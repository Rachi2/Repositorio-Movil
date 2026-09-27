package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.Message;
import com.example.proyecto1.domain.repository.ChatRepository;

public class SendMessageUseCase {

    private final ChatRepository chatRepository;

    public SendMessageUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void execute(String conversationId, String senderId, String senderName, String text, ChatRepository.RepositoryCallback<Void> callback) {
        if (text == null || text.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("El mensaje no puede estar vacío"));
            return;
        }

        String messageId = String.valueOf(System.currentTimeMillis());
        Message message = new Message(
                messageId,
                senderId,
                senderName,
                text.trim(),
                null,
                System.currentTimeMillis()
        );

        chatRepository.sendMessage(conversationId, message, callback);
    }
}