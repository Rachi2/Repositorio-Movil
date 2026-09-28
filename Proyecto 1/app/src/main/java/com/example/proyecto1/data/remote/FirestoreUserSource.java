package com.example.proyecto1.data.remote;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.proyecto1.data.model.UserDto;
import com.example.proyecto1.domain.repository.ChatRepository;

import java.util.List;

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

    public void getUsers(ChatRepository.RepositoryCallback<List<UserDto>> callback){
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserDto> userList = queryDocumentSnapshots.toObjects(UserDto.class);
                    callback.onSuccess(userList);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getUserById(String id, ChatRepository.RepositoryCallback<UserDto> callback){
        db.collection("users")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot ->{
                    UserDto userDto = documentSnapshot.toObject(UserDto.class);
                    callback.onSuccess(userDto);
                })
                .addOnFailureListener(callback::onError);
    }
}