package dev.nelit.api.services.impl;

import dev.nelit.api.services.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-days}")
    private Long expirationDays;

    private static final long DAY_IN_MS = 24 * 60 * 60 * 1000L;

    public String generate(Long idUser) {
        return Jwts.builder()
            .subject(idUser.toString())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationDays * DAY_IN_MS))
            .signWith(getSigningKey())
            .compact();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
