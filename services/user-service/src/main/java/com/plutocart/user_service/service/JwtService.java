package com.plutocart.user_service.service;

import com.plutocart.user_service.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(UUID userId, String email) {
        var now = Instant.now();
        var expirationTime = Date.from(now.plusMillis(jwtConfig.getAccessTokenExpiration()));

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(expirationTime)
                .id(UUID.randomUUID().toString())

                .claim("id", userId.toString())
                .claim("email", email)
                .claim("type", "access")
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public String generateRefreshToken(UUID userId, String email) {
        var now = Instant.now();
        var expirationTime = Date.from(now.plusMillis(jwtConfig.getRefreshTokenExpiration()));

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(expirationTime)
                .id(UUID.randomUUID().toString())

                .claim("id", userId.toString())
                .claim("email", email)
                .claim("type", "refresh")
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try{
            var claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public String getEmailFromToken(String token) {
        var claims = validateToken(token);
        return claims.getSubject();
    }

    public UUID getUserIdFromToken(String token) {
        var claims = validateToken(token);
        return UUID.fromString(claims.get("id", String.class));
    }


}
