package com.refugeeintegration.backend.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String name; // Optional: only used if creating new user
    private String password;
}
