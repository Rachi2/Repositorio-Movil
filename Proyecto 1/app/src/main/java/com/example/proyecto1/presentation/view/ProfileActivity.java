package com.example.proyecto1.presentation.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.proyecto1.R;
import com.example.proyecto1.databinding.ActivityProfileBinding;
import com.example.proyecto1.presentation.viewmodel.ProfileViewModel;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImage = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    openCropper(uri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> cropPhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String croppedUri = result.getData().getStringExtra(CropPhotoActivity.EXTRA_CROPPED_URI);
                    if (croppedUri != null) {
                        viewModel.updatePhoto(croppedUri);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding.btnChangePhoto.setOnClickListener(v -> pickImage.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));

        viewModel.getUser().observe(this, user -> {
            if (user == null) return;
            binding.tvName.setText(user.getName());
            binding.tvEmail.setText(user.getEmail());
            binding.tvInitial.setText(AvatarUtils.getInitial(user.getName()));

            if (user.getPhotoUrl() != null) {
                binding.ivProfilePhoto.setVisibility(View.VISIBLE);
                Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(binding.ivProfilePhoto);
            } else {
                binding.ivProfilePhoto.setVisibility(View.GONE);
            }
        });

        viewModel.getUploading().observe(this, isUploading -> {
            binding.progressUpload.setVisibility(isUploading ? View.VISIBLE : View.GONE);
            binding.btnChangePhoto.setEnabled(!isUploading);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getPhotoUpdated().observe(this, updated -> {
            if (updated) {
                Toast.makeText(this, R.string.photo_updated, Toast.LENGTH_SHORT).show();
                viewModel.resetPhotoUpdatedMessage();
            }
        });

        viewModel.loadProfile();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void openCropper(Uri uri) {
        Intent intent = new Intent(ProfileActivity.this, CropPhotoActivity.class);
        intent.putExtra(CropPhotoActivity.EXTRA_IMAGE_URI, uri.toString());
        cropPhoto.launch(intent);
    }
}
