package com.example.proyecto1.data.remote;

import com.example.proyecto1.domain.repository.ChatRepository;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseMessagingSource {
    public void register(ChatRepository.RepositoryCallback<Void> callback) {
        FirebaseMessaging.getInstance().register()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }
}
