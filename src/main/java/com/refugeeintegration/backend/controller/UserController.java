package com.refugeeintegration.backend.controller;

import com.refugeeintegration.backend.model.LoginRequest;
import com.refugeeintegration.backend.model.LoginResponse;
import com.refugeeintegration.backend.model.SignUpRequest;
import com.refugeeintegration.backend.model.SignUpResponse;
import com.refugeeintegration.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }
}
