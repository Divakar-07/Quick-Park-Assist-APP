package com.qpa.controller.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.dto.ResponseDTO;
import com.qpa.entity.AuthUser;
import com.qpa.exception.InvalidCredentialsException;
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
    public ResponseEntity<String> logout(HttpServletResponse response) {
        
        return ResponseEntity.ok(authService.logoutUser(response));
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO> addAuth(@ModelAttribute AuthUser authUser, HttpServletRequest request, HttpServletResponse response) {
        if (authService.isAuthenticated(request)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseDTO("Already logged in", HttpStatus.FORBIDDEN, false));
        }
        authService.addAuth(authUser, response);
        
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
        .header("Location", "/dashboard")
        .build();
    }
    

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody AuthUser request, HttpServletResponse response) {
        try {
            ResponseDTO responseDTO = authService.loginUser(request, response);
            if (!responseDTO.isSuccess()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
            }
    
            return ResponseEntity.ok(responseDTO);
    
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO("Invalid username or password", HttpStatus.UNAUTHORIZED, false));
        } catch (Exception e) {
            System.out.println("An error occured inside the login controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("Error: Login failed", HttpStatus.INTERNAL_SERVER_ERROR, false));
        }
    }
    

    @GetMapping("/check")
    public ResponseEntity<ResponseDTO> checkAuth(HttpServletRequest request) {
        boolean isAuthenticated = authService.isAuthenticated(request);
        if (isAuthenticated){
            return ResponseEntity.ok(new ResponseDTO("user is authenticated", HttpStatus.UNAUTHORIZED, true));
        }
        return ResponseEntity.ok(new ResponseDTO("user is unAuthorized", HttpStatus.OK, false));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @PostMapping("/login/admin")
    public ResponseEntity<ResponseDTO> loginAdmin(@RequestBody AuthUser request, HttpServletResponse response) {
        try {
            
                    ResponseDTO responseDTO = authService.loginAdmin(request, response);
            
                    if(responseDTO.isSuccess()){
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(responseDTO);
                    }
                    return ResponseEntity.ok(responseDTO);
            
                    
        } catch (Exception e) {
            System.out.println("Error occured in loginAdmin: " + e.getMessage());
            return ResponseEntity.internalServerError().body(new ResponseDTO("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, false));
        }
    
    }
    
}
