package com.example.proyecto1.domain.repository;

public interface ProfileRepository {
    void uploadProfilePhoto(String userId, String imageUri, ChatRepository.RepositoryCallback<String> callback);
}
