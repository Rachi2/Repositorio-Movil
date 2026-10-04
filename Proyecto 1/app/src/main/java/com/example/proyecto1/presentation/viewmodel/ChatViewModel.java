package com.example.proyecto1.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto1.AppContainer;
import com.example.proyecto1.domain.model.Message;
import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.usecase.GetCurrentUserUseCase;
import com.example.proyecto1.domain.usecase.GetMessagesUseCase;
import com.example.proyecto1.domain.usecase.GetUserByIdUseCase;
import com.example.proyecto1.domain.usecase.SendImageUseCase;
import com.example.proyecto1.domain.usecase.SendMessageUseCase;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final SendMessageUseCase sendMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final SendImageUseCase sendImageUseCase;

    private final MutableLiveData<List<Message>> _messages = new MutableLiveData<>();
    private final MutableLiveData<Boolean> uploading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private ChatRepository.ListenerRegistration listenerRegistration;
    private String conversationId;
    // Datos del usuario conectado (los obtiene el ViewModel, no la Activity)
    private String currentUserId;
    private String currentUserName;


    public ChatViewModel() {
        AppContainer container = AppContainer.get();

        this.sendMessageUseCase = new SendMessageUseCase(container.chatRepository);
        this.getMessagesUseCase = new GetMessagesUseCase(container.chatRepository);
        this.getCurrentUserUseCase = new GetCurrentUserUseCase(container.authRepository);
        this.getUserByIdUseCase = new GetUserByIdUseCase(container.userRepository);
        this.sendImageUseCase = new SendImageUseCase(container.chatRepository);
    }

    public LiveData<List<Message>> getMessages() {
        return _messages;
    }

    public LiveData<Boolean> getUploading() {
        return uploading;
    }

    public LiveData<String> getError() {
        return _error;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void initConversation(String otherUserId) {
        // Al girar el teléfono la Activity se recrea, pero el ViewModel no:
        // si ya estamos escuchando, no se crea otro listener (evita mensajes duplicados)
        if (conversationId != null) return;

        User currentUser = getCurrentUserUseCase.execute();
        if (currentUser == null) {
            _error.setValue("No hay sesión iniciada");
            return;
        }
        currentUserId = currentUser.getId();
        // Nombre temporal hasta que llegue el nombre real desde Firestore
        currentUserName = currentUser.getEmail();

        // Orden alfabetico para que la conversacion sea la misma para ambos usuarios
        if (currentUserId.compareTo(otherUserId) < 0) {
            this.conversationId = currentUserId + "_" + otherUserId;
        } else {
            this.conversationId = otherUserId + "_" + currentUserId;
        }

        listenMessages();
        loadCurrentUserName();
    }

    // Busca el nombre real del usuario conectado en la colección "users"
    private void loadCurrentUserName() {
        getUserByIdUseCase.execute(currentUserId, new ChatRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null && user.getName() != null) {
                    currentUserName = user.getName();
                }
            }

            @Override
            public void onError(Exception e) {
                // Si falla, se sigue usando el correo como nombre
            }
        });
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

    public void sendMessage(String text) {
        if (conversationId == null) return;
        sendMessageUseCase.execute(conversationId, currentUserId, currentUserName, text, new ChatRepository.RepositoryCallback<Void>() {
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

    public void sendImage(String imageUri) {
        if (conversationId == null) {
            return;
        }
        uploading.setValue(true);
        sendImageUseCase.execute(conversationId, currentUserId, currentUserName, imageUri, new ChatRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                uploading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                uploading.setValue(false);
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