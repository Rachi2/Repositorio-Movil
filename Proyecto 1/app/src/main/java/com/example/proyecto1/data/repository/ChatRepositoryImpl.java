package com.example.proyecto1.data.repository;

import com.example.proyecto1.data.model.MessageDto;
import com.example.proyecto1.data.remote.FirestoreChatSource;
import com.example.proyecto1.domain.model.Message;
import com.example.proyecto1.domain.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatRepositoryImpl implements ChatRepository {

    private final FirestoreChatSource chatSource;

    public ChatRepositoryImpl(FirestoreChatSource chatSource) {
        this.chatSource = chatSource;
    }

    @Override
    public void sendMessage(String conversationId, Message message, RepositoryCallback<Void> callback) {
        MessageDto dto = MessageDto.fromDomain(message);
        chatSource.sendMessage(conversationId, dto, callback);
    }

    @Override
    public void sendImage(String conversationId, String imageUri, RepositoryCallback<Void> callback) {
        // Se implementara en la fase 3
    }

    @Override
    public ListenerRegistration listenToMessages(String conversationId, RepositoryCallback<List<Message>> callback) {
        return chatSource.listenToMessages(conversationId, new RepositoryCallback<List<MessageDto>>() {
            @Override
            public void onSuccess(List<MessageDto> result) {
                List<Message> messages = new ArrayList<>();
                for (MessageDto dto : result) {
                    messages.add(dto.toDomain());
                }
                callback.onSuccess(messages);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}