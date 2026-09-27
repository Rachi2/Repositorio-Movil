package com.example.proyecto1.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto1.domain.model.Message;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.usecase.GetMessagesUseCase;
import com.example.proyecto1.domain.usecase.SendMessageUseCase;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final SendMessageUseCase sendMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;

    private final MutableLiveData<List<Message>> _messages = new MutableLiveData<>();
    public LiveData<List<Message>> getMessages() { return _messages; }

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() { return _error; }

    private ChatRepository.ListenerRegistration listenerRegistration;
    private String conversationId;

    public ChatViewModel(SendMessageUseCase sendMessageUseCase, GetMessagesUseCase getMessagesUseCase) {
        this.sendMessageUseCase = sendMessageUseCase;
        this.getMessagesUseCase = getMessagesUseCase;
    }

    public void initConversation(String currentUserId, String otherUserId) {
        // Orden alfabetico para que la conversacion sea la misma para ambos usuarios
        if (currentUserId.compareTo(otherUserId) < 0) {
            this.conversationId = currentUserId + "_" + otherUserId;
        } else {
            this.conversationId = otherUserId + "_" + currentUserId;
        }

        listenMessages();
    }

    private void listenMessages() {
        if (conversationId == null) return;
        listenerRegistration = getMessagesUseCase.execute(conversationId, new ChatRepository.RepositoryCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                _messages.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue(e.getMessage());
            }
        });
    }

    public void sendMessage(String senderId, String senderName, String text) {
        if (conversationId == null) return;
        sendMessageUseCase.execute(conversationId, senderId, senderName, text, new ChatRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // success en envio del mensaje
            }

            @Override
            public void onError(Exception e) {
                _error.setValue(e.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}