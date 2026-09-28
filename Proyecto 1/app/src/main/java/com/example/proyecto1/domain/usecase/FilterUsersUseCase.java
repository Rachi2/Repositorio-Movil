package com.example.proyecto1.domain.usecase;

import com.example.proyecto1.domain.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilterUsersUseCase {

    public List<User> execute(List<User> users, String query) {
        List<User> userList = new ArrayList<>();
        if (query == null) return users;
        String search = query.trim().toLowerCase(Locale.ROOT);

        if (search.isEmpty()) {
            return users;
        }

        for (User user : users) {
            if (matches(user.getName(), search) || matches(user.getEmail(), search)) {
                userList.add(user);
            }
        }

        return userList;
    }

    private boolean matches(String text, String search) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(search);
    }
}
