package com.qpa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class HomeController {

    private final UserService userService;
    private final VehicleService vehicleService;

    public HomeController(UserService userService, VehicleService vehicleService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/")
    public String homePage(HttpServletRequest request, Model model) {
        try {
            if (!userService.isAuthenticated(request)) {
                return "redirect:/loginPage";
            }
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("vehicles", vehicleService.getAllVehicles());
            return "index";
        } catch (Exception e) {
            return "redirect:/loginPage";
            // TODO: handle exception
        }
    }

    @GetMapping("/contact")
    public String contactPage(HttpServletRequest request) {
        if (!userService.isAuthenticated(request)) {
            return "redirect:/users/login";
        }
        return "contact";
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAuth(HttpServletRequest request) {
        return ResponseEntity.ok(userService.isAuthenticated(request));
    }

    @GetMapping("/loginPage")
    public String loginPage(HttpServletRequest request) {
        try {
            if (userService.isAuthenticated(request)){
                return "index";
            }
            return "Login";
        } catch (Exception e) {
            return "Login";
            // TODO: handle exception
        }
    }
    
    @GetMapping("/registerPage")
    public String registerPage(HttpServletRequest request) {
        if (userService.isAuthenticated(request)){
            return "index";
        }
        return "Register";
    }

    

}
