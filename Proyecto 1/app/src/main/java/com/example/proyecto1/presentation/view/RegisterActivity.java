package com.example.proyecto1.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto1.databinding.ActivityRegisterBinding;
import com.example.proyecto1.presentation.viewmodel.AuthViewModel;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnRegister.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                Intent intent = new Intent(RegisterActivity.this, UsersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.etEmail.getText()).toString();
            String password = Objects.requireNonNull(binding.etPassword.getText()).toString();
            String name = Objects.requireNonNull(binding.etName.getText()).toString();
            String confirmPassword = Objects.requireNonNull(binding.etConfirmPassword.getText()).toString();

            viewModel.register(name, email, password, confirmPassword);
        });

        binding.tvGoToLogin.setOnClickListener(v -> {
            finish();
        });
    }
}
