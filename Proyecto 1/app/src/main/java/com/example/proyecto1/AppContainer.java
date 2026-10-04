package com.example.proyecto1;

import android.content.Context;

import com.example.proyecto1.data.local.ImageCompressor;
import com.example.proyecto1.data.remote.FirebaseAuthSource;
import com.example.proyecto1.data.remote.FirebaseMessagingSource;
import com.example.proyecto1.data.remote.FirestoreChatSource;
import com.example.proyecto1.data.remote.FirestoreUserSource;
import com.example.proyecto1.data.remote.StorageSource;
import com.example.proyecto1.data.repository.AuthRepositoryImpl;
import com.example.proyecto1.data.repository.ChatRepositoryImpl;
import com.example.proyecto1.data.repository.NotificationRepositoryImpl;
import com.example.proyecto1.data.repository.ProfileRepositoryImpl;
import com.example.proyecto1.data.repository.UserRepositoryImpl;
import com.example.proyecto1.domain.repository.AuthRepository;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.domain.repository.NotificationRepository;
import com.example.proyecto1.domain.repository.ProfileRepository;
import com.example.proyecto1.domain.repository.UserRepository;

// Crea los repositorios UNA sola vez y los comparte con todos los ViewModels.
public class AppContainer {

    private static AppContainer instance;

    public final AuthRepository authRepository;
    public final UserRepository userRepository;
    public final ChatRepository chatRepository;
    public final ProfileRepository profileRepository;
    public final NotificationRepository notificationRepository;

    private AppContainer(Context context) {
        StorageSource storageSource = new StorageSource();
        ImageCompressor imageCompressor = new ImageCompressor(context);

        authRepository = new AuthRepositoryImpl(new FirebaseAuthSource());
        userRepository = new UserRepositoryImpl(new FirestoreUserSource());
        chatRepository = new ChatRepositoryImpl(new FirestoreChatSource(), storageSource, imageCompressor);
        profileRepository = new ProfileRepositoryImpl(imageCompressor, storageSource);
        notificationRepository = new NotificationRepositoryImpl(new FirebaseMessagingSource());
    }

    // Se llama una vez, al arrancar la app (en KairoApp)
    public static void init(Context context) {
        if (instance == null) {
            instance = new AppContainer(context.getApplicationContext());
        }
    }

    public static AppContainer get() {
        return instance;
    }
}