package com.refugeeintegration.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpResponse {
    private Long userId;
    private String name;
    private String message;

    public SignUpResponse() {

    }
}
