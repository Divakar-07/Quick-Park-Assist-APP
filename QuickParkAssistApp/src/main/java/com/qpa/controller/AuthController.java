package com.qpa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.AuthUser;
import com.qpa.exception.UnauthorizedAccessException;
import com.qpa.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logout(HttpServletResponse response) {
        if (authService.logoutUser(response)) {
            return ResponseEntity.ok(new ResponseDTO<>("Logout successful", HttpStatus.OK.value(), true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("Logout unsuccessful", HttpStatus.UNAUTHORIZED.value(), false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<Void>> login(@RequestBody AuthUser request, HttpServletResponse response) {
        ResponseDTO<Void> responseDTO = authService.loginUser(request, response);
        if (!responseDTO.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
        }
        return ResponseEntity.ok(responseDTO);
    
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseDTO<Void>> checkAuth(HttpServletRequest request) {
        boolean isAuthenticated = authService.isAuthenticated(request);
        if (!isAuthenticated) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }
        return ResponseEntity.ok(new ResponseDTO<>("User is authorized", HttpStatus.OK.value(), true));
    }

    @GetMapping("/check/admin")
    public ResponseEntity<ResponseDTO<Void>> checkAdmin(HttpServletRequest request) {
        if (!authService.checkAdmin(request)) {
            throw new UnauthorizedAccessException("Admin is not authenticated");
        }
        return ResponseEntity.ok(new ResponseDTO<>("Admin is authorized", HttpStatus.OK.value(), true));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<ResponseDTO<Void>> loginAdmin(@RequestBody AuthUser request, HttpServletResponse response) {
        ResponseDTO<Void> responseDTO = authService.loginAdmin(request, response);
        if (!responseDTO.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("Invalid Credentials", HttpStatus.UNAUTHORIZED.value(), false));
        }
        return ResponseEntity.ok(responseDTO);
    }
}
