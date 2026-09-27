package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.Message;
import com.example.proyecto1.domain.repository.ChatRepository;

import java.util.List;

public class GetMessagesUseCase {

    private final ChatRepository chatRepository;

    public GetMessagesUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ChatRepository.ListenerRegistration execute(String conversationId, ChatRepository.RepositoryCallback<List<Message>> callback) {
        return chatRepository.listenToMessages(conversationId, callback);
    }
}