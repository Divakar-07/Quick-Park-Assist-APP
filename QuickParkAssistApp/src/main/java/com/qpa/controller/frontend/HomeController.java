package com.qpa.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.qpa.service.AuthService;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final AuthService authService;

    public HomeController(UserService userService, VehicleService vehicleService, AuthService authService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.authService = authService;
    }

    @GetMapping("/")
    public String homePage(HttpServletRequest request, Model model) {
        try {
            if (!authService.isAuthenticated(request)) {
                return "redirect:/login";
            }
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("vehicles", vehicleService.getAllVehicles());
            return "index";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request){
        try {
            if (!authService.isAuthenticated(request)) {
                return "redirect:/login";
            }
            return "dashboard/dashboard";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/contact")
    public String contactPage(HttpServletRequest request) {
        if (!authService.isAuthenticated(request)) {
            return "redirect:/login";
        }
        return "dashboard/contact";
    }

  


    
    

}
