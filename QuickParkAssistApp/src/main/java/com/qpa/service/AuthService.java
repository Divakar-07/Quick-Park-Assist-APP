package com.qpa.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qpa.dto.ResponseDTO;
import com.qpa.entity.AuthUser;
import com.qpa.entity.UserType;
import com.qpa.exception.InvalidCredentialsException;
import com.qpa.repository.AuthRepository;
import com.qpa.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service class responsible for authentication-related operations.
 */
@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor to initialize AuthService with required dependencies.
     */
    public AuthService(JwtUtil jwtUtil, AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
    }

    /**
     * Fetches an AuthUser by email.
     * 
     * @param email the email of the user.
     * @return the AuthUser if found, otherwise null.
     */
    public AuthUser getAuthByEmail(String email) {
        try {
            return authRepository.findFreshByEmail(email).get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Adds a new authenticated user to the system.
     * 
     * @param authUser the AuthUser entity to be added.
     * @param response the HttpServletResponse.
     * @return true if the user was successfully added, false otherwise.
     */
    public boolean addAuth(AuthUser authUser, HttpServletResponse response) {
        try {
            String password = passwordEncoder.encode(authUser.getPassword());
            authUser.setPassword(password);
            authRepository.save(authUser);
            return true;
        } catch (Exception e) {
            System.out.println("Error occurred inside addAuth: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the request is authenticated based on the JWT token.
     * 
     * @param request the HttpServletRequest containing authentication information.
     * @return true if authenticated, false otherwise.
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromCookie(request);
            if (token == null)
                return false;
            String email = jwtUtil.extractEmail(token);
            return jwtUtil.validateToken(token, email);
        } catch (Exception e) {
            System.out.println("An error occurred inside the isAuthenticated method: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logs in a user and sets a JWT token in a cookie.
     * 
     * @param request  the AuthUser login request containing email and password.
     * @param response the HttpServletResponse to store the JWT token.
     * @return ResponseDTO with login success status.
     */
    public ResponseDTO<Void> loginUser(AuthUser request, HttpServletResponse response) {
        System.out.println("Inside the login user");

        Optional<AuthUser> optionalAuthUser = authRepository.findFreshByEmail(request.getEmail());
        if (optionalAuthUser.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        AuthUser authUser = optionalAuthUser.get();
        if (!passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(authUser.getEmail(), authUser.getUser().getUserId(),
                authUser.getUser().getUserType());

        // Create and set the cookie properly
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                .httpOnly(false) // Prevent JS access
                .secure(false) // Set true if using HTTPS
                .path("/") // Cookie available on all paths
                .sameSite("None")
                .secure(true)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString()); // Add cookie to response

        return new ResponseDTO<>("Login Successful", HttpStatus.OK.value(), true);
    }

    public UserType getUserType(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        return jwtUtil.extractRole(token);
    }

    /**
     * Logs out the user by clearing authentication cookies.
     * 
     * @param response the HttpServletResponse to clear the cookies.
     * @return true if logout was successful, false otherwise.
     */
    public boolean logoutUser(HttpServletResponse response) {
        return jwtUtil.clearCookies(response);
    }

    /**
     * Extracts the user ID from the JWT token present in the request.
     * 
     * @param request the HttpServletRequest containing authentication information.
     * @return the user ID extracted from the JWT token.
     */
    public Long getUserId(HttpServletRequest request) {
        return jwtUtil.extractUserId(jwtUtil.extractTokenFromCookie(request));
    }

    /**
     * Retrieves the authenticated user from the request.
     * 
     * @param request the HttpServletRequest containing authentication information.
     * @return the AuthUser entity if found, otherwise null.
     */
    public AuthUser getAuth(HttpServletRequest request) {
        Optional<AuthUser> authUser = authRepository.findByUser_UserId(getUserId(request));

        if (authUser == null) {
            return null;
        }
        return authUser.get();
    }

    /**
     * Deletes an authenticated user by user ID and logs them out.
     * 
     * @param userId   the ID of the user to be deleted.
     * @param response the HttpServletResponse to clear cookies.
     */
    public void deleteAuth(Long userId, HttpServletResponse response) {
        try {
            System.out.println("Inside the deleteAuth");
            Optional<AuthUser> authUser = authRepository.findByUser_UserId(userId);
            logoutUser(response);
            authRepository.delete(authUser.get());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
