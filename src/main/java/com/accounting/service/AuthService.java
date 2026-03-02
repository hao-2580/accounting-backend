package com.accounting.service;

import com.accounting.dto.LoginRequest;
import com.accounting.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
