package com.example.proyecto1.data.local;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.proyecto1.domain.repository.ChatRepository;

import java.io.ByteArrayOutputStream;

public class ImageCompressor {
    private final Context context;

    public ImageCompressor(Context context) {
        this.context = context.getApplicationContext();
    }

    public void compress(Uri imageUri, ChatRepository.RepositoryCallback<byte[]> callback) {
        Glide.with(context)
                .asBitmap()
                .load(imageUri)
                .fitCenter()
                .into(new CustomTarget<Bitmap>(1280, 1280) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        callback.onSuccess(outputStream.toByteArray());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // No se necesita liberar nada
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        callback.onError(new Exception("No se pudo leer la imagen"));
                    }
                });
    }
}
