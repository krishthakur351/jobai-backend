package com.krish.jobai.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtUtil {

    private final String SECRET_KEY =
            "mysecretkeymysecretkeymysecretkey12345";

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes()
        );
    }

    // Generate Token
    public String generateToken(String email, String role) {

        return Jwts.builder()

                .subject(email)

                .claim("role", role)

                .issuedAt(new Date())

                .expiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)
                )

                .signWith(getSigningKey())

                .compact();
    }

    // Extract Email
    public String extractEmail(String token) {

        Claims claims = Jwts.parser()

                .verifyWith(getSigningKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();

        return claims.getSubject();
    }

    // Validate Token
    public boolean validateToken(String token, String email) {

        String extractedEmail = extractEmail(token);

        return extractedEmail.equals(email);
    }
}