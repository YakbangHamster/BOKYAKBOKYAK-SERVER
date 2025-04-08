package com.yakbang.server.security;

import com.yakbang.server.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenProvider {
    private final byte[] secretKey;

    private final long ACCESS_TOKEN_EXPIRY = 1000L * 60 * 30; // 30분
    private final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 7; // 7일

    public TokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = Base64.getDecoder().decode(secretKey);
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getIdentity())
                .claim("userId", user.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getIdentity())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public LocalDateTime getRefreshTokenExpiryTime() {
        return LocalDateTime.now().plus(Duration.ofMillis(REFRESH_TOKEN_EXPIRY));
    }

    // access token 받아오기
    public String resolveAccessToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    // refresh token 받아오기
    public String resolveRefreshToken(HttpServletRequest request) {
        String bearer = request.getHeader("Refresh");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    // 토큰 유효성 검증
    public boolean isValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // token에서 identity 값 가져오기
    public String getIdentityFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    // token에서 userId 값 가져오기
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }
}
