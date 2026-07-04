package com.boxing.api.controller.dto;

import com.boxing.api.model.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponseDTO {

    private UUID id;
    private String name;
    private String email;
    private Role role;
    private String pictureUrl;
    private LocalDateTime createdAt;

    public UserResponseDTO() {}

    public UserResponseDTO(UUID id, String name, String email, Role role, String pictureUrl, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.pictureUrl = pictureUrl;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
