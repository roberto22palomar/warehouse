package com.example.warehouse.auth.services;


import com.example.warehouse.auth.models.requests.LoginRequest;
import com.example.warehouse.auth.models.requests.RegisterRequest;
import com.example.warehouse.auth.models.responses.TokenResponse;
import com.example.warehouse.auth.repository.TokenRepository;
import com.example.warehouse.domain.documents.UserDocument;
import com.example.warehouse.domain.repositories.UserRepository;
import com.example.warehouse.utils.exceptions.UserAlreadyExists;
import com.example.warehouse.utils.exceptions.UserCredentialsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            log.info("Register - Username already in use: {}", request.getUsername());
            throw new UserAlreadyExists("Username already in use.");
        } else {
            UserDocument userToPersist = UserDocument.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .housesId(new HashSet<>())
                    .build();

            var userPersisted = userRepository.save(userToPersist);
            var jwtToken = jwtService.generateToken(userPersisted);
            var refreshToken = jwtService.generateRefreshToken(userPersisted);
            saveUserToken(userPersisted, jwtToken);

            log.info("User: {} created", userPersisted.getUsername());

            return new TokenResponse(jwtToken, refreshToken);
        }
    }

    public TokenResponse login(LoginRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            var user = userRepository.findByUsername(request.getUsername());
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return new TokenResponse(jwtToken, refreshToken);
        } else {

            throw new UserCredentialsException("User credentials exception.");

        }


    }

    public void revokeAllUserTokens(UserDocument user) {
        List<TokenDocument> validUserTokens = tokenRepository.findAllValidTokensByUsername(user.getUsername());
        if (!validUserTokens.isEmpty()) {
            for (TokenDocument token : validUserTokens) {
                token.setExpired(true);
                token.setRevoked(true);
            }
            log.info("Old tokens revoked for user: {}", user.getUsername());
            tokenRepository.saveAll(validUserTokens);
        }
    }

    @Override
    public TokenResponse refresh(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Bearer token");
        }

        String refreshToken = authHeader.substring(7);
        String userName = jwtService.extractUsername(refreshToken);

        if (userName == null) {
            throw new IllegalArgumentException("Invalid Bearer token");
        }

        UserDocument user = userRepository.findByUsername(userName);

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid Refresh token");
        }

        String accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);

    }


    private void saveUserToken(UserDocument user, String jwtToken) {
        var token = TokenDocument.builder()
                .user(user.getUsername())
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
        log.info("New token saved for user: {}", user.getUsername());


    }
}
