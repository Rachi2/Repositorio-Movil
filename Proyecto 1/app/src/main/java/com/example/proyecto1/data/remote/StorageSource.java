package com.example.proyecto1.data.remote;

import com.example.proyecto1.domain.repository.ChatRepository;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

public class StorageSource {
    private final FirebaseStorage storage;

    public StorageSource() {
        this.storage = FirebaseStorage.getInstance();
    }

    public void uploadImage(String conversationId, byte[] imageData, ChatRepository.RepositoryCallback<String> callback) {

        // Ruta para guardar la foto, actualmente esta con el id de la conversacion y el tiempo
        StorageReference ref = storage.getReference()
                .child("chat_images/" + conversationId + "/" +
                        System.currentTimeMillis() + ".jpg");

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        ref.putBytes(imageData, metadata)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl()
                            .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }
}
