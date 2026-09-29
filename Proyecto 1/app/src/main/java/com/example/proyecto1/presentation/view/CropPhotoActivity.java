package com.example.proyecto1.presentation.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImageView;
import com.example.proyecto1.R;
import com.example.proyecto1.databinding.ActivityCropPhotoBinding;

public class CropPhotoActivity extends AppCompatActivity {
    public final static String EXTRA_IMAGE_URI = "IMAGE_URI";
    public final static String EXTRA_CROPPED_URI = "CROPPED_URI";
    private ActivityCropPhotoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String imageUri = getIntent().getStringExtra(EXTRA_IMAGE_URI);
        if (imageUri == null) {
            finish();
            return;
        }
        binding.cropImageView.setCropShape(CropImageView.CropShape.OVAL);
        binding.cropImageView.setFixedAspectRatio(true);
        binding.cropImageView.setAspectRatio(1, 1);

        binding.cropImageView.setImageUriAsync(Uri.parse(imageUri));

        binding.cropImageView.setOnCropImageCompleteListener((view, result) -> {
            if (result.isSuccessful() && result.getUriContent() != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CROPPED_URI, result.getUriContent().toString());
                setResult(RESULT_OK, intent);
            } else {
                Toast.makeText(this, R.string.crop_error, Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        binding.btnConfirm.setOnClickListener(v -> {
            binding.btnConfirm.setEnabled(false);
            binding.cropImageView.croppedImageAsync(
                    Bitmap.CompressFormat.JPEG, 90, 1024, 1024,
                    CropImageView.RequestSizeOptions.RESIZE_INSIDE, null);
        });

        binding.btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
