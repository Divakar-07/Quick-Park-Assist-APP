package com.qpa.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.qpa.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserUiController {

    private final AuthService authService;

    public UserUiController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/user/profile")
    public String getUserDashboard(HttpServletRequest request) {
        try {
            if (authService.isAuthenticated(request)){
                return "dashboard/profile";
            }
            return "redirect:/login";
        } catch (Exception e) {
            return "error";
        }
    }
}
