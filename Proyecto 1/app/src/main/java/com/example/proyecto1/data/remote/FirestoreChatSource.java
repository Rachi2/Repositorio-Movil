package com.example.proyecto1.data.remote;

import com.example.proyecto1.data.model.MessageDto;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FirestoreChatSource {

    private final FirebaseFirestore db;

    public FirestoreChatSource() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void sendMessage(String conversationId, MessageDto messageDto, ChatRepository.RepositoryCallback<Void> callback) {
        db.collection("chats")
                .document(conversationId)
                .collection("messages")
                .document(messageDto.getId())
                .set(messageDto)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public ChatRepository.ListenerRegistration listenToMessages(String conversationId, ChatRepository.RepositoryCallback<List<MessageDto>> callback) {
        ListenerRegistration registration = db.collection("chats")
                .document(conversationId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    if (value != null) {
                        List<MessageDto> dtos = new ArrayList<>();
                        for (var doc : value.getDocuments()) {
                            MessageDto dto = doc.toObject(MessageDto.class);
                            if (dto != null) {
                                dtos.add(dto);
                            }
                        }
                        callback.onSuccess(dtos);
                    }
                });

        return registration::remove;
    }
}