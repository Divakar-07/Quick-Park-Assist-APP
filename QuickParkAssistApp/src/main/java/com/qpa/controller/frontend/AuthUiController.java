package com.qpa.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.qpa.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthUiController {

    private final AuthService authService;

    public AuthUiController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        try {
            if (authService.isAuthenticated(request)){
                return "redirect:/dashboard";
            }
            return "login/Login";
        } catch (Exception e) {
            System.out.println("An error occured while getting to the login page " + e.getMessage());
            return "error";
        }
    }
    
}
