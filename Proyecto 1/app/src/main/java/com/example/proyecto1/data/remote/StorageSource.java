package com.example.proyecto1.data.remote;

import android.net.Uri;

import com.example.proyecto1.domain.repository.ChatRepository;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageSource {
    private final FirebaseStorage storage;

    public StorageSource() {
        this.storage = FirebaseStorage.getInstance();
    }

    public void uploadImage(String conversationId, Uri imageUri, ChatRepository.RepositoryCallback<String> callback) {

        // Ruta para guardar la foto, actualmente esta con el id de la conversacion y el tiempo
        StorageReference ref = storage.getReference()
                .child("chat_images/" + conversationId + "/" +
                        System.currentTimeMillis() + ".jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl()
                            .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }
}
