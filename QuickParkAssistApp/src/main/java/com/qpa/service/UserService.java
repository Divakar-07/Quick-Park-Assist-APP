package com.qpa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qpa.dto.LoginRequest;
import com.qpa.entity.UserInfo;
import com.qpa.exception.InvalidEntityException;
import com.qpa.repository.UserRepository;
import com.qpa.security.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserInfo addUser(UserInfo user) { 
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

     public void loginUser(LoginRequest request, HttpServletResponse response) {
        Optional<UserInfo> user = userRepository.findByUsername(request.getUsername());
        System.out.println("Reached here inside the loginuser");
        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.get().getUsername());
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);  // Prevent JavaScript access
        jwtCookie.setSecure(true);    // Send only over HTTPS (set to false for local testing)
        jwtCookie.setPath("/");       // Available for all endpoints
        jwtCookie.setMaxAge(60 * 60 * 24); // 1 day expiration

        response.addCookie(jwtCookie);
    }
    
    public UserInfo getUserById(Long id) { return userRepository.findById(id).orElseThrow(() -> new InvalidEntityException("User not found")); }
    public List<UserInfo> getAllUsers() { return userRepository.findAll(); }
    public UserInfo updateUser(UserInfo user) { return userRepository.save(user); }

    public void deleteUser(Long id) {
        UserInfo user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("User not found with ID: " + id));
    
        try {
            userRepository.delete(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new InvalidEntityException("Cannot delete user. Related entities exist.");
        }
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        
        try{
            String token = jwtUtil.extractTokenFromCookie(request);
            if (token == null) {
                return false;
            }
            String username = jwtUtil.extractUsername(token);
            return jwtUtil.validateToken(token, username);
        }

        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String logoutUser(HttpServletResponse response){
        try {
            if (jwtUtil.clearCookies(response)){
                return "Logged out successfully!";
            }
            else{
                return "Logout unsuccessful";
            }

        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
}