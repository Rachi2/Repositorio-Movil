package com.example.proyecto1.data.remote;

import com.example.proyecto1.domain.repository.ChatRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthSource {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void register(String email, String password, ChatRepository.RepositoryCallback<FirebaseUser> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(callback::onError);

    }

    public void login(String email, String password, ChatRepository.RepositoryCallback<FirebaseUser> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(callback::onError);
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}
