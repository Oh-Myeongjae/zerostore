package com.zerobase.zerostore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 유효기간 최대 하루

    public String generateToken(String phoneNumber) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        Claims claims = Jwts.claims().setSubject(phoneNumber);

        System.out.println("SECRET_KEY = " + SECRET_KEY);

        String key = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());

        System.out.println("key = " + key);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        System.out.println("token = " + token);

        return token;
    }
}

