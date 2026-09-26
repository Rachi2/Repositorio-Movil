package com.example.proyecto1.data.model;

import com.example.proyecto1.domain.model.User;

public class UserDto {
    private String id;
    private String name;
    private String email;

    public UserDto() {
    }

    public UserDto(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Convertir DTO a modelo de Dominio
    public User toDomain() {
        return new User(id, name, email);
    }

    // Crear DTO a partir del modelo de Dominio
    public static UserDto fromDomain(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}