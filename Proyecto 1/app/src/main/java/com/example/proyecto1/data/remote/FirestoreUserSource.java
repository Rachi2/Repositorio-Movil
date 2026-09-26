package com.example.proyecto1.data.remote;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.proyecto1.data.model.UserDto;
import com.example.proyecto1.domain.repository.ChatRepository;

public class FirestoreUserSource {

    private final FirebaseFirestore db;

    public FirestoreUserSource() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void saveUser(UserDto userDto, ChatRepository.RepositoryCallback<Void> callback) {
        db.collection("users")
                .document(userDto.getId())
                .set(userDto)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }
}