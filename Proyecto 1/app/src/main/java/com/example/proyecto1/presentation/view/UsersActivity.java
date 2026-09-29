package com.example.proyecto1.presentation.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto1.R;
import com.example.proyecto1.databinding.ActivityUsersBinding;
import com.example.proyecto1.domain.model.User;
import com.example.proyecto1.presentation.viewmodel.UsersViewModel;

public class UsersActivity extends AppCompatActivity {
    private ActivityUsersBinding binding;
    private UsersViewModel viewModel;
    private UserAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        adapter = new UserAdapter(user -> openChat(user));
        binding.rvUsers.setAdapter(adapter);

        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getUsers().observe(this, users -> {
            adapter.setUsers(users);
            if (users.isEmpty()) {
                // Mensaje distinto si la lista está vacía por una búsqueda o porque no hay usuarios
                binding.tvEmpty.setText(viewModel.isSearching()
                        ? R.string.search_no_results
                        : R.string.users_empty);
                binding.tvEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getLoggedOut().observe(this, isLoggedOut -> {
            if (isLoggedOut) {
                Intent intent = new Intent(UsersActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Pedir el permiso de notificaciones en Android 13 o superior (Fase 4)
        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadUsers();
    }

    private void openChat(User user) {
        Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_USER_ID, user.getId())
                .putExtra(ChatActivity.EXTRA_USER_NAME, user.getName());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        assert searchView != null;
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.search(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            viewModel.logout();
            return true;
        } else if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}