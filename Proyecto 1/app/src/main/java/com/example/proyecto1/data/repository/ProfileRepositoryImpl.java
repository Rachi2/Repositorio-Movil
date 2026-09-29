package com.example.proyecto1.data.repository;

import android.net.Uri;

import com.example.proyecto1.data.local.ImageCompressor;
import com.example.proyecto1.data.remote.StorageSource;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.ProfileRepository;

public class ProfileRepositoryImpl implements ProfileRepository {
    private final ImageCompressor imageCompressor;
    private final StorageSource storageSource;

    public ProfileRepositoryImpl(ImageCompressor imageCompressor, StorageSource storageSource) {
        this.imageCompressor = imageCompressor;
        this.storageSource = storageSource;
    }

    @Override
    public void uploadProfilePhoto(String userId, String imageUri, ChatRepository.RepositoryCallback<String> callback) {
        Uri uri = Uri.parse(imageUri);
        imageCompressor.compress(uri, new ChatRepository.RepositoryCallback<byte[]>() {
            @Override
            public void onSuccess(byte[] imageData) {
                String path = "profile_images/" + userId + "/" + System.currentTimeMillis() + ".jpg";
                storageSource.uploadImage(path, imageData, callback);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
