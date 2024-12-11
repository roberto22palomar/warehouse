package com.example.warehouse.auth.services;


import com.example.warehouse.auth.models.requests.LoginRequest;
import com.example.warehouse.auth.models.requests.RegisterRequest;
import com.example.warehouse.auth.models.responses.TokenResponse;

public interface IAuthService {

    TokenResponse register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    TokenResponse refresh(String autHeader);

}
