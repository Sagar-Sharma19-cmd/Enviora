package com.enviora.auth.dto;

import com.enviora.user.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private UserStatus status;
    private Instant createdAt;

    public UserResponse() {
    }

    public UserResponse(UUID id, String name, String email, UserStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
