package com.example.proyecto1.presentation.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto1.R;
import com.example.proyecto1.databinding.ActivityChatBinding;
import com.example.proyecto1.presentation.viewmodel.ChatViewModel;

public class ChatActivity extends AppCompatActivity {

    // Claves para recibir los datos del otro usuario desde UsersActivity
    public static final String EXTRA_USER_ID = "OTHER_USER_ID";
    public static final String EXTRA_USER_NAME = "OTHER_USER_NAME";

    private ActivityChatBinding binding;
    private ChatViewModel viewModel;
    private MessageAdapter adapter;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImage = registerForActivityResult(
            new ActivityResultContracts.PickMultipleVisualMedia(), uri ->{
                if(uri != null){
                    //to do
                    Toast.makeText(this, "Imagen elegida", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Deja espacio para las barras del sistema y el teclado
        ViewCompat.setOnApplyWindowInsetsListener(binding.chatRoot, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Lee los datos pasados desde el Intent
        String otherUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        String otherUserName = getIntent().getStringExtra(EXTRA_USER_NAME);

        if (otherUserId == null) {
            Toast.makeText(this, R.string.chat_open_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.tvUserName.setText(otherUserName);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.initConversation(otherUserId);

        // El adapter necesita el id del usuario actual para saber qué mensajes van a la derecha
        adapter = new MessageAdapter(viewModel.getCurrentUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(adapter);

        // Suscribirse a LiveData
        viewModel.getMessages().observe(this, messages -> {
            adapter.setMessages(messages);
            if (!messages.isEmpty()) {
                binding.rvMessages.smoothScrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        // SendMessageUseCase ya evita enviar mensajes vacíos
        binding.btnSend.setOnClickListener(v -> {
            viewModel.sendMessage(binding.etMessage.getText().toString());
            binding.etMessage.setText("");
        });

        binding.btnAttach.setOnClickListener(v -> pickImage.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));
    }
}