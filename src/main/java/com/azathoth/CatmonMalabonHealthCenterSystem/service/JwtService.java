package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String secretKey;

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .issuer("Azathoth")
                .claim("role", "ROLE_" + role)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getKey() {
        byte[] sk = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(sk);
    }

    public String extractEmail(String token) {
        // extract email from jwt
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .clockSkewSeconds(60) // Allow 60 seconds of clock skew
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);

        return(email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }
}
