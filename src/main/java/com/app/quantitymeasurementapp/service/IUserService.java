package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.auth.AuthResponse;
import com.app.quantitymeasurementapp.auth.LoginRequest;
import com.app.quantitymeasurementapp.auth.RegisterRequest;

public interface IUserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
