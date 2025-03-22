package com.capstone.dfms.components.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${app.auth.tokenSecret}")
    private String tokenSecret;

    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(tokenSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi giải mã token: " + e.getMessage());
            return null;
        }
    }
}
