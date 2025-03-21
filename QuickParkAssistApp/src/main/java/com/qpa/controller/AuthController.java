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

    // Constructor to inject AuthService dependency
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Logs out the user by clearing authentication-related data.
     * @param response The HTTP response where logout status may be set.
     * @return ResponseEntity with a success or failure message.
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logout(HttpServletResponse response) {
        if (authService.logoutUser(response)) {
            return ResponseEntity.ok(new ResponseDTO<>("Logout successful", HttpStatus.OK.value(), true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("Logout unsuccessful", HttpStatus.UNAUTHORIZED.value(), false));
        }
    }

    /**
     * Logs in the user by validating credentials.
     * @param request Contains login details (username/password).
     * @param response The HTTP response where authentication details may be set.
     * @return ResponseEntity with success or failure response.
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<Void>> login(@RequestBody AuthUser request, HttpServletResponse response) {
        ResponseDTO<Void> responseDTO = authService.loginUser(request, response);
        if (!responseDTO.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
        }
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Checks if the user is authenticated.
     * @param request The HTTP request containing authentication details.
     * @return ResponseEntity with success if the user is authenticated.
     * @throws UnauthorizedAccessException if the user is not authenticated.
     */
    @GetMapping("/check")
    public ResponseEntity<ResponseDTO<Void>> checkAuth(HttpServletRequest request) {
        boolean isAuthenticated = authService.isAuthenticated(request);
        if (!isAuthenticated) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }
        return ResponseEntity.ok(new ResponseDTO<>("User is authorized", HttpStatus.OK.value(), true));
    }

    /**
     * Checks if the current user has admin privileges.
     * @param request The HTTP request containing authentication details.
     * @return ResponseEntity with success if the user is an admin.
     * @throws UnauthorizedAccessException if the user is not an admin.
     */
    @GetMapping("/check/admin")
    public ResponseEntity<ResponseDTO<Void>> checkAdmin(HttpServletRequest request) {
        if (!authService.checkAdmin(request)) {
            throw new UnauthorizedAccessException("Admin is not authenticated");
        }
        return ResponseEntity.ok(new ResponseDTO<>("Admin is authorized", HttpStatus.OK.value(), true));
    }

    /**
     * Logs in an admin user by validating credentials.
     * @param request Contains login details (username/password).
     * @param response The HTTP response where authentication details may be set.
     * @return ResponseEntity with success or failure response.
     */
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
