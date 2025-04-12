package com.qpa.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.qpa.entity.UserType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtUtil {
    private static final String SECRET_KEY_STRING = "YourSuperSecretKeyWithAtLeast32Characters!"; // Store securely
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    public static String getSecretKeyAsString() {
        return Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded());
    }

    public String generateToken(String email, Long userId, UserType role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId) // ✅ Adding userId to the token
                .claim("role", role) // ✅ Adding userId to the token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean clearCookies(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("jwt", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Set to false if not using HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(0); // Expire the cookie immediately
            response.addCookie(cookie);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class); // ✅ Extract userId
    }

    public UserType extractRole(String token) {
        String roleStr = extractClaims(token).get("role", String.class); // ✅ Get role as String
        if (roleStr == null) {
            return null; // Role not found in token
        }

        try {
            return UserType.valueOf(roleStr); // ✅ Convert String to Enum
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role in token: " + roleStr);
            return null;
        }
    }

    public boolean validateToken(String token, String email) {
        return (email.equals(extractEmail(token)) && !isTokenExpired(token));
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractClaims(token);
        String email = claims.getSubject();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }
}
