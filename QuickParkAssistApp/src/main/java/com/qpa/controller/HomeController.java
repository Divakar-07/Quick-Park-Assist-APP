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
import jakarta.servlet.http.HttpSession;




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
            return "dashboard";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/contact")
    public String contactPage(HttpServletRequest request) {
        if (!authService.isAuthenticated(request)) {
            return "redirect:/login";
        }
        return "contact";
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        try {
            if (authService.isAuthenticated(request)){
                return "redirect:/dashboard";
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
    public String addAuthPage(Model model, HttpSession session) {
        try {
            UserInfo registeredUser = (UserInfo) session.getAttribute("registeredUser");
            
            if (registeredUser == null || registeredUser.getUserId() == null) {
                System.out.println("No user found in session or userId is null");
                return "redirect:/register"; // If no user in session, redirect back
            }
    
            System.out.println("User found in session with ID: " + registeredUser.getUserId());
    
            AuthUser authUser = new AuthUser();
            authUser.setUser(registeredUser); // Set the UserInfo reference in AuthUser
    
            model.addAttribute("authUser", authUser);
            return "ADD_auth";
        } catch (Exception e) {
            return "error-page"; 
        }
    }

    @GetMapping("/spots/all")
    public String allSpotsPage(HttpServletRequest request) {
        try {
            if (authService.isAuthenticated(request)){
                return "ALL_spots";
            }
            return "redirect:/login";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/user/profile")
    public String getUserDashboard(HttpServletRequest request) {
        try {
            if (authService.isAuthenticated(request)){
                return "profile";
            }
            return "redirect:/login";
        } catch (Exception e) {
            return "error";
        }
    }
    
    

}
