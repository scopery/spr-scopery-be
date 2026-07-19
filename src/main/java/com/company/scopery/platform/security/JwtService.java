package com.company.scopery.platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_PRINCIPAL_TYPE = "principalType";
    public static final String PRINCIPAL_INTERNAL = "INTERNAL";
    public static final String PRINCIPAL_PORTAL = "PORTAL";

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.expirationMs = properties.getExpirationMs();
    }

    public String generateToken(UUID userId, String username) {
        return generateToken(userId, username, PRINCIPAL_INTERNAL);
    }

    public String generatePortalToken(UUID portalAccountId, String email) {
        return generateToken(portalAccountId, email, PRINCIPAL_PORTAL);
    }

    public String generateToken(UUID subjectId, String username, String principalType) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subjectId.toString())
                .claims(Map.of(CLAIM_USERNAME, username, CLAIM_PRINCIPAL_TYPE, principalType))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseToken(token).getSubject());
    }

    public String extractUsername(String token) {
        return parseToken(token).get(CLAIM_USERNAME, String.class);
    }

    public String extractPrincipalType(String token) {
        String type = parseToken(token).get(CLAIM_PRINCIPAL_TYPE, String.class);
        return type == null ? PRINCIPAL_INTERNAL : type;
    }

    public boolean isPortalToken(String token) {
        return PRINCIPAL_PORTAL.equals(extractPrincipalType(token));
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
