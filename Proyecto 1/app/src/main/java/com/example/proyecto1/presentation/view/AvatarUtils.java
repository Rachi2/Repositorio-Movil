package com.example.proyecto1.presentation.view;

public class AvatarUtils {
    public static String getInitial(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        return name.trim().substring(0, 1).toUpperCase();
    }
}
