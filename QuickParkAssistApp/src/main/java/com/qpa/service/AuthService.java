package  com.qpa.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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

@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;
   
    public AuthService(JwtUtil jwtUtil, AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
    }

    public AuthUser getAuthByUsername(String username){
        try {
            return authRepository.findFreshByUsername(username).get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public boolean addAuth(AuthUser authUser, HttpServletResponse response){
        try {
            String password = passwordEncoder.encode(authUser.getPassword());
            
            authUser.setPassword(password);
            authRepository.save(authUser);
            return true;
        } catch (Exception e) {
            System.out.println("error occured inside addAuth: " + e.getMessage());
            return false;
        }
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromCookie(request);
            if (token == null) return false;
            String username = jwtUtil.extractUsername(token);
            return jwtUtil.validateToken(token, username);
        } catch (Exception e) {
            System.out.println("an error occured inside the isAuthenticated method " + e.getMessage());
            return false;
        }
    }

    public ResponseDTO<Void> loginUser(AuthUser request, HttpServletResponse response) {
        System.out.println("iside the login user");

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
            .httpOnly(true)  // Prevent JS access
            .secure(false)   // Set true if using HTTPS
            .path("/")       // Cookie available on all paths
            .sameSite("None") // 🔥 Needed for cross-origin requests
            .secure(false)
            .build();

        response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString()); // Add cookie to response

                return new ResponseDTO<>("Login Successful", HttpStatus.OK.value(), true);
    }

    
    public boolean logoutUser(HttpServletResponse response){
        return jwtUtil.clearCookies(response);
    }

    public Long getUserId(HttpServletRequest request){
        return jwtUtil.extractUserId(jwtUtil.extractTokenFromCookie(request));
    }

    public AuthUser getAuth(HttpServletRequest request){
        Optional<AuthUser> authUser = authRepository.findByUser_UserId(getUserId(request));

        if (authUser == null){
            return null;
        }
        return authUser.get();
    }
    
    public void deleteAuth(Long userId, HttpServletResponse response){
        try {
            System.out.println("inside the deleteAuth");
            Optional<AuthUser> authUser = authRepository.findByUser_UserId(userId);
            logoutUser(response);
            authRepository.delete(authUser.get());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

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
    
        return new ResponseDTO<> ("Admin Login Successful", HttpStatus.OK.value(), true);
    }
    
}
