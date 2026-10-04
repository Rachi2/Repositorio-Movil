package com.example.proyecto1.presentation.notification;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.example.proyecto1.KairoApp;
import com.example.proyecto1.R;
import com.example.proyecto1.data.remote.FirestoreUserSource;
import com.example.proyecto1.domain.repository.ChatRepository;
import com.example.proyecto1.presentation.view.ChatActivity;
import com.example.proyecto1.presentation.view.UsersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "kairo_chat_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String senderId = message.getData().get("senderId");
        String senderName = message.getData().get("senderName");
        String body = message.getData().get("body");
        if (senderId == null) return;
        if (senderId.equals(ChatActivity.openChatUserId)) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            Activity activity = KairoApp.getCurrentActivity();
            if (activity != null) {
                // La app esta abierta: banner propio dentro de la app
                InAppNotifier.show(activity, senderId, senderName, body);
            } else {
                // La app esta cerrada o en segundo plano: notificacion del sistema
                showNotification(senderId, senderName, body);
            }
        });
    }

    @Override
    public void onRegistered(@NonNull String installationId) {
        super.onRegistered(installationId);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) return;

        new FirestoreUserSource().updateFcmToken(currentUserId, installationId,
                new ChatRepository.RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Identificador guardado
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("FCM", "No se pudo guardar el identificador", e);
                    }
                });
    }

    private void showNotification(String senderId, String senderName, String body) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones de Chat",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra(ChatActivity.EXTRA_USER_ID, senderId);
        chatIntent.putExtra(ChatActivity.EXTRA_USER_NAME, senderName);

        // Un código por persona: si Ana envía 3 mensajes, se actualiza la misma notificación
        int notificationId = senderId.hashCode();

        PendingIntent pendingIntent = TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, UsersActivity.class))
                .addNextIntent(chatIntent)
                .getPendingIntent(notificationId,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(this, R.color.cherry_bright))
                .setContentTitle(senderName)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        manager.notify(notificationId, builder.build());
    }
}