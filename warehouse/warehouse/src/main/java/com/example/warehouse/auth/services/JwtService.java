package com.example.warehouse.auth.services;


import com.example.warehouse.domain.documents.UserDocument;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String generateToken(UserDocument user) {

        return buildToken(user, jwtExpiration);

    }

    public String generateRefreshToken(UserDocument user) {

        return buildToken(user, refreshExpiration);

    }

    private String buildToken(UserDocument user, long expiration) {
        return Jwts.builder()
                .id(user.getId())
                .claims(Map.of("username", user.getUsername()))
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();

    }

    public String extractUsername(String token) {
        Claims jwtToken = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getSubject();
    }

    public Date extractExpiration(String token) {
        Claims jwtToken = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getExpiration();
    }

    public boolean isTokenValid(String token, UserDocument user) {

        String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);

    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSignKey() {
        byte[] keyBites = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBites);
    }

}
