package com.example.proyecto1.presentation.view;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto1.R;
import com.example.proyecto1.data.remote.FirestoreChatSource;
import com.example.proyecto1.data.repository.ChatRepositoryImpl;
import com.example.proyecto1.domain.usecase.GetMessagesUseCase;
import com.example.proyecto1.domain.usecase.SendMessageUseCase;
import com.example.proyecto1.presentation.viewmodel.ChatViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel viewModel;
    private MessageAdapter adapter;
    private EditText etMessage;
    private RecyclerView rvMessages;

    private String currentUserId;
    private String currentUserName = "Usuario";
    private String otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUserId = FirebaseAuth.getInstance().getUid();

        // Lee los datos pasados desde el Intent
        otherUserId = getIntent().getStringExtra("OTHER_USER_ID");
        String otherUserName = getIntent().getStringExtra("OTHER_USER_NAME");

        if (otherUserId == null) {
            // ID por defecto en caso de prueba directa
            otherUserId = "test_user_id";
            otherUserName = "Usuario Prueba";
        }

        TextView tvName = findViewById(R.id.tvUserName);
        tvName.setText(otherUserName);

        etMessage = findViewById(R.id.etMessage);
        ImageButton btnSend = findViewById(R.id.btnSend);
        rvMessages = findViewById(R.id.rvMessages);

        // Inicializar ViewModel
        FirestoreChatSource source = new FirestoreChatSource();
        ChatRepositoryImpl repository = new ChatRepositoryImpl(source);
        SendMessageUseCase sendUseCase = new SendMessageUseCase(repository);
        GetMessagesUseCase getUseCase = new GetMessagesUseCase(repository);

        viewModel = new ChatViewModel(sendUseCase, getUseCase);

        // Adapter
        adapter = new MessageAdapter(currentUserId != null ? currentUserId : "test_id");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);

        // Suscribirse a LiveData
        viewModel.getMessages().observe(this, messages -> {
            adapter.setMessages(messages);
            if (!messages.isEmpty()) {
                rvMessages.smoothScrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        viewModel.initConversation(currentUserId != null ? currentUserId : "my_id", otherUserId);

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString();
            if (!text.trim().isEmpty()) {
                viewModel.sendMessage(currentUserId != null ? currentUserId : "my_id", currentUserName, text);
                etMessage.setText("");
            }
        });
    }
}