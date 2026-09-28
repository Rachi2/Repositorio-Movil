package com.example.proyecto1.presentation.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto1.databinding.ActivityImageViewerBinding;

public class ImageViewerActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URL = "IMAGE_URL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityImageViewerBinding binding = ActivityImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        if (imageUrl == null) {
            finish();
            return;
        }

        Glide.with(this)
                .load(imageUrl)
                .into(binding.photoView);

        binding.btnClose.setOnClickListener(v -> finish());
    }
}
