package com.example.proyecto1.data.remote;

import com.example.proyecto1.domain.repository.ChatRepository;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseMessagingSource {
    public void register(ChatRepository.RepositoryCallback<String> callback) {
        FirebaseMessaging.getInstance().register()
                .onSuccessTask(unused -> FirebaseInstallations.getInstance().getId())
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(callback::onError);
    }

    public void getDeviceId(ChatRepository.RepositoryCallback<String> callback) {
        FirebaseInstallations.getInstance().getId()
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(callback::onError);
    }
}
