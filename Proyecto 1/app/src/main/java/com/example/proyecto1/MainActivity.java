package com.example.proyecto1;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto1.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

// Pantalla temporal para comprobar que Firebase está conectado.
// Se reemplazará por LoginActivity en la Fase 1.
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FIREBASE";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);

        // Prueba 1: ¿la app encontró el google-services.json?
        String projectId = FirebaseApp.getInstance().getOptions().getProjectId();
        Log.d(TAG, "Conectado al proyecto: " + projectId);
        binding.content.tvStatus.setText("Firebase conectado ✅\nProyecto: " + projectId);

        // Prueba 2: ¿se puede escribir en Firestore?
        binding.content.btnTestFirestore.setOnClickListener(v -> testFirestore());
    }

    private void testFirestore() {
        binding.content.tvFirestore.setText("Enviando…");

        Map<String, Object> data = new HashMap<>();
        data.put("mensaje", "Hola desde Android");
        data.put("fecha", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("pruebas")
                .add(data)
                .addOnSuccessListener(doc -> {
                    Log.d(TAG, "Firestore OK, documento: " + doc.getId());
                    binding.content.tvFirestore.setText("Firestore funciona ✅\nDocumento: " + doc.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore falló", e);
                    binding.content.tvFirestore.setText("Error en Firestore ❌\n" + e.getMessage());
                });
    }
}