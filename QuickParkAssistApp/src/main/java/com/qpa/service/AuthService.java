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
import com.qpa.exception.InvalidCredentialsException;
import com.qpa.repository.AuthRepository;
import com.qpa.security.JwtUtil;

import jakarta.servlet.http.Cookie;
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
     * Fetches an AuthUser by username.
     * 
     * @param username the username of the user.
     * @return the AuthUser if found, otherwise null.
     */
    public AuthUser getAuthByUsername(String username){
        try {
            return authRepository.findFreshByUsername(username).get();
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
    public boolean addAuth(AuthUser authUser, HttpServletResponse response){
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
            if (token == null) return false;
            String username = jwtUtil.extractUsername(token);
            return jwtUtil.validateToken(token, username);
        } catch (Exception e) {
            System.out.println("An error occurred inside the isAuthenticated method: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logs in a user and sets a JWT token in a cookie.
     * 
     * @param request the AuthUser login request containing username and password.
     * @param response the HttpServletResponse to store the JWT token.
     * @return ResponseDTO with login success status.
     */
    public ResponseDTO<Void> loginUser(AuthUser request, HttpServletResponse response) {
        System.out.println("Inside the login user");

        Optional<AuthUser> optionalAuthUser = authRepository.findFreshByUsername(request.getUsername());
        if (optionalAuthUser.isEmpty()) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        AuthUser authUser = optionalAuthUser.get();
        if (!passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(authUser.getUsername(), authUser.getUser().getUserId());

        // Create and set the cookie properly
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
            .httpOnly(false)  // Prevent JS access
            .secure(false)   // Set true if using HTTPS
            .path("/")       // Cookie available on all paths
            .sameSite("None") 
            .secure(true)
            .build();

        response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString()); // Add cookie to response

        return new ResponseDTO<>("Login Successful", HttpStatus.OK.value(), true);
    }

    /**
     * Logs out the user by clearing authentication cookies.
     * 
     * @param response the HttpServletResponse to clear the cookies.
     * @return true if logout was successful, false otherwise.
     */
    public boolean logoutUser(HttpServletResponse response){
        return jwtUtil.clearCookies(response);
    }

    /**
     * Extracts the user ID from the JWT token present in the request.
     * 
     * @param request the HttpServletRequest containing authentication information.
     * @return the user ID extracted from the JWT token.
     */
    public Long getUserId(HttpServletRequest request){
        return jwtUtil.extractUserId(jwtUtil.extractTokenFromCookie(request));
    }

    /**
     * Retrieves the authenticated user from the request.
     * 
     * @param request the HttpServletRequest containing authentication information.
     * @return the AuthUser entity if found, otherwise null.
     */
    public AuthUser getAuth(HttpServletRequest request){
        Optional<AuthUser> authUser = authRepository.findByUser_UserId(getUserId(request));

        if (authUser == null){
            return null;
        }
        return authUser.get();
    }
    
    /**
     * Deletes an authenticated user by user ID and logs them out.
     * 
     * @param userId the ID of the user to be deleted.
     * @param response the HttpServletResponse to clear cookies.
     */
    public void deleteAuth(Long userId, HttpServletResponse response){
        try {
            System.out.println("Inside the deleteAuth");
            Optional<AuthUser> authUser = authRepository.findByUser_UserId(userId);
            logoutUser(response);
            authRepository.delete(authUser.get());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Checks if the authenticated user is an admin.
     * 
     * @param request the HttpServletRequest containing authentication information.
     * @return true if the user is an admin, false otherwise.
     */
    public boolean checkAdmin(HttpServletRequest request) {
        try {
            // Extract token from cookies
            String token = jwtUtil.extractTokenFromCookie(request);
            if (token == null) return false;
    
            // Extract username from token
            String username = jwtUtil.extractUsername(token);
    
            // Validate token
            if (!jwtUtil.validateToken(token, username)) return false;
    
            // Fetch user from database
            Optional<AuthUser> optionalAuthUser = authRepository.findFreshByUsername(username);
            if (optionalAuthUser.isEmpty()) return false;
    
            // Check if the user has an admin role
            AuthUser authUser = optionalAuthUser.get();
            return authUser.getUser().getUserType().toString().equalsIgnoreCase("ADMIN");
        } catch (Exception e) {
            System.out.println("Error in checkAdmin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logs in an admin user and sets a JWT token in a cookie.
     * 
     * @param request the AuthUser login request containing username and password.
     * @param response the HttpServletResponse to store the JWT token.
     * @return ResponseDTO with admin login success status.
     */
    public ResponseDTO<Void> loginAdmin(AuthUser request, HttpServletResponse response) {
        Optional<AuthUser> optionalAuthUser = authRepository.findFreshByUsername(request.getUsername());
        if (optionalAuthUser.isEmpty()) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        AuthUser authUser = optionalAuthUser.get();
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    
        // Check if the user has an admin role
        if (!authUser.getUser().getUserType().toString().equalsIgnoreCase("ADMIN")) {
            throw new InvalidCredentialsException("User is not an admin");
        }
    
        // Generate JWT token
        String token = jwtUtil.generateToken(authUser.getUsername(), authUser.getUser().getUserId());
    
        // Create and set the cookie properly
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(86400);
        jwtCookie.setSecure(false);
        jwtCookie.setAttribute("SameSite", "None");
    
        response.addCookie(jwtCookie);
        response.addHeader("Set-Cookie", jwtCookie.toString());
    
        return new ResponseDTO<>("Admin Login Successful", HttpStatus.OK.value(), true);
    }
}
