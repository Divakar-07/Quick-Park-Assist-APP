package com.qpa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.dto.LoginRequest;
import com.qpa.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/logout")
public ResponseEntity<String> logout(HttpServletResponse response) {
    
    return ResponseEntity.ok(userService.logoutUser(response));
}

     @PostMapping("/login") 
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            userService.loginUser(request, response);
            return ResponseEntity.ok("Login successful! JWT is set in the cookie.");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

     @GetMapping("/check")
    public ResponseEntity<Boolean> checkAuth(HttpServletRequest request) {
        boolean isAuthenticated = userService.isAuthenticated(request);
        return ResponseEntity.ok(isAuthenticated);
    }
}
