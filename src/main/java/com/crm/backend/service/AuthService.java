package com.crm.backend.service;

import com.crm.backend.dto.LoginRequest;
import com.crm.backend.dto.LoginResponse;

public interface AuthService {
    LoginResponse authenticateUser(LoginRequest loginRequest);
}