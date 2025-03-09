package com.qpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.qpa.entity.AuthUser;
import com.qpa.entity.UserInfo;
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
                return "redirect:/loginPage";
            }
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("vehicles", vehicleService.getAllVehicles());
            return "index";
        } catch (Exception e) {
            return "redirect:/loginPage";
        }
    }

    @GetMapping("/contact")
    public String contactPage(HttpServletRequest request) {
        if (!authService.isAuthenticated(request)) {
            return "redirect:/users/login";
        }
        return "contact";
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        try {
            if (authService.isAuthenticated(request)){
                return "index";
            }
            return "Login";
        } catch (Exception e) {
            return "Login";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(Model model, HttpServletRequest request) {
        try {
            if (authService.isAuthenticated(request)){
            return "index";
        }
        System.out.println("inside the register page controller");
        model.addAttribute("user", new UserInfo());
        return "register";
        } catch (Exception e) {
            return "Something went wrong " + e.getMessage();
        }
    }
   
    @GetMapping("/add-auth")
    public String addAuthPage(Model model, HttpServletRequest request) {
        try {
            if (authService.isAuthenticated(request)){
            return "index";
        }
        System.out.println("inside the add-auth page controller");
        model.addAttribute("authUser", new AuthUser());
        return "register";
        } catch (Exception e) {
            return "Something went wrong " + e.getMessage();
        }
    }

}
