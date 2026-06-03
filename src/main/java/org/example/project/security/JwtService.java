package org.example.project.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.project.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final String SECRET =
            "mysecretkeymysecretkeymysecretkey12";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(Users users) {
        return Jwts.builder()
                .subject(users.getUsername())
                .claim("permissions", users.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                // ["ROLE_ADMIN", "PERMISSION_DELETE_USER", "PERMISSION_READ", ...]
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    // Yangi metod qo'shiladi
    public List<String> extractPermissions(String token) {
        return getClaims(token).get("permissions", List.class);
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }


    public boolean isTokenValid(String token) {
        try {
            getClaims(token); // xato bo'lmasa token yaroqli
            return true;
        } catch (Exception e) {
            return false; // token buzilgan yoki muddati o'tgan
        }
    }

    // Takrorlanmaslik uchun alohida metod
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}