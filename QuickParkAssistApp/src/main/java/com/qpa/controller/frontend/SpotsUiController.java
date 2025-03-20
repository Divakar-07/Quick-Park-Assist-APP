package com.qpa.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.qpa.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SpotsUiController {

    private final AuthService authService;

    public SpotsUiController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/spots/all")
    public String allSpotsPage(HttpServletRequest request) {
        try {
            if (!authService.isAuthenticated(request)){
                return "redirect:/login";
            }
            return "spots/ALL_spots";
        } catch (Exception e) {
            return "error";
        }
    }

}
