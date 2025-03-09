package  com.qpa.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qpa.entity.AuthUser;
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


    public AuthService(JwtUtil jwtUtil, AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
    }

    public boolean addAuth(AuthUser authUser, HttpServletResponse response){
        try {
            String password = passwordEncoder.encode(authUser.getPassword());
            authUser.setPassword(password);
            authUser = authRepository.save(authUser);
            loginUser(authUser, response);
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
            return false;
        }
    }


      public void loginUser(AuthUser request, HttpServletResponse response) {
        Optional<AuthUser> authUser = authRepository.findByUsername(request.getUsername());
        try {
            if (authUser.isEmpty() || !passwordEncoder.matches(request.getPassword(), authUser.get().getPassword())) {
                throw new RuntimeException("Invalid username or password");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        String token = jwtUtil.generateToken(authUser.get().getUsername(), authUser.get().getUser().getUserId());
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);  // Prevent JavaScript access
        jwtCookie.setSecure(true);    // Send only over HTTPS (set to false for local testing)
        jwtCookie.setPath("/");       // Available for all endpoints
        jwtCookie.setMaxAge(60 * 60 * 24); // 1 day expiration

        response.addCookie(jwtCookie);
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
