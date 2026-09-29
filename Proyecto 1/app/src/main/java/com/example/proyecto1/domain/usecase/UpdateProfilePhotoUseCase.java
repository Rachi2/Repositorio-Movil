package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.ProfileRepository;
import com.example.proyecto1.domain.repository.UserRepository;

public class UpdateProfilePhotoUseCase {
    private final AuthRepository authRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public UpdateProfilePhotoUseCase(AuthRepository authRepository, ProfileRepository profileRepository, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public void execute(String imageUri, ChatRepository.RepositoryCallback<String> callback) {
        User currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("No hay sesión iniciada"));
            return;
        }
        if (imageUri == null) {
            callback.onError(new Exception("Imagen inválida"));
            return;
        }
        String userId = currentUser.getId();
        profileRepository.uploadProfilePhoto(userId, imageUri, new ChatRepository.RepositoryCallback<String>() {
            @Override
            public void onSuccess(String photoUrl) {
                // La foto ya se subió: ahora se guarda su enlace en el perfil
                userRepository.updatePhotoUrl(userId, photoUrl, new ChatRepository.RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onSuccess(photoUrl);
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
