package com.azathoth.CatmonMalabonHealthCenter.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String secretKey;

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .issuer("Azathoth")
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] sk = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(sk);
    }
}
