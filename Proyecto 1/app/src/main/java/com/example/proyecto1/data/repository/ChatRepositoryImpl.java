package com.example.proyecto1.data.repository;

import android.net.Uri;

import com.example.proyecto1.data.model.MessageDto;
import com.example.proyecto1.data.remote.FirestoreChatSource;
import com.example.proyecto1.data.remote.StorageSource;
import com.example.proyecto1.domain.model.Message;
import com.example.proyecto1.domain.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatRepositoryImpl implements ChatRepository {

    private final FirestoreChatSource chatSource;
    private final StorageSource storage;

    public ChatRepositoryImpl(FirestoreChatSource chatSource, StorageSource storage) {
        this.chatSource = chatSource;
        this.storage = storage;
    }

    @Override
    public void sendMessage(String conversationId, Message message, RepositoryCallback<Void> callback) {
        MessageDto dto = MessageDto.fromDomain(message);
        chatSource.sendMessage(conversationId, dto, callback);
    }

    @Override
    public void uploadImage(String conversationId, String imageUri, RepositoryCallback<String> callback) {
        Uri uri = Uri.parse(imageUri);
        storage.uploadImage(conversationId, uri, callback);
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