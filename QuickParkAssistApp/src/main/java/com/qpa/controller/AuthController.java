package com.qpa.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.entity.AuthUser;
import com.qpa.exception.InvalidCredentialsException;
import com.qpa.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        
        return ResponseEntity.ok(authService.logoutUser(response));
    }

    @PostMapping("/save")
    public ResponseEntity<?> addAuth(@ModelAttribute AuthUser authUser, HttpServletRequest request, HttpServletResponse response) {
        if (authService.isAuthenticated(request)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Already logged in"));
        }
        System.out.println("user object: " + authUser.getUser());
        authService.addAuth(authUser, response);
        
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
        .header("Location", "/dashboard")
        .build();
    }
    

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthUser request, HttpServletResponse response) {
        String result = authService.loginUser(request, response);
        return ResponseEntity.ok(result);
    
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAuth(HttpServletRequest request) {
        boolean isAuthenticated = authService.isAuthenticated(request);
        return ResponseEntity.ok(isAuthenticated);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
