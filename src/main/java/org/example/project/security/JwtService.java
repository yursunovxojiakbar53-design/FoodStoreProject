package org.example.project.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.project.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret:mysecretkeymysecretkeymysecretkey12}")
    private String SECRET;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(Users users) {
        return Jwts.builder()
                .subject(users.getUsername())

                .claim("roles",
                        users.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(a -> a.startsWith("ROLE_"))
                                .toList()
                )

                .claim("permissions",
                        users.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(a -> a.startsWith("PERMISSION_"))
                                .toList()
                )

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
    public List<String> extractRoles(String token) {
        Claims claims = getClaims(token);

        Object roles = claims.get("roles");

        if (roles instanceof List<?> roleList) {
            return roleList.stream()
                    .map(String::valueOf)
                    .toList();
        }

        return List.of();
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
        return Jwts.parser()                    // ← parserBuilder() emas
                .verifyWith(key)                // ← setSigningKey() emas
                .build()
                .parseSignedClaims(token)       // ← parseClaimsJws() emas
                .getPayload();                  // ← getBody() emas
    }
}
