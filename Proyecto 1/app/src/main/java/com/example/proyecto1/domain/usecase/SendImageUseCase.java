package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.Message;
import com.example.proyecto1.domain.repository.ChatRepository;

public class SendImageUseCase {
    private final ChatRepository chatRepository;

    public SendImageUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void execute(String conversationId, String senderId, String senderName, String imageUri, ChatRepository.RepositoryCallback<Void> callback) {
        if (imageUri == null) {
            callback.onError(new IllegalArgumentException("Imagen inválida"));
            return;
        }

        chatRepository.uploadImage(conversationId, imageUri, new ChatRepository.RepositoryCallback<String>() {
            @Override
            public void onSuccess(String result) {
                long time = System.currentTimeMillis();
                String messageId = String.valueOf(time);
                Message message = new Message(
                        messageId,
                        senderId,
                        senderName,
                        null,
                        result,
                        time
                );

                chatRepository.sendMessage(conversationId, message, callback);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
