package com.crm.backend.controller;

import com.crm.backend.dto.LoginRequest;
import com.crm.backend.dto.LoginResponse;
import com.crm.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // Adjust if your React port differs
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/unlock")
    public ResponseEntity<?> unlockAccount(@RequestBody LoginRequest request) {
        authService.unlockAccount(request.getUsername());
        return ResponseEntity.ok("Account unlocked");
    }

}