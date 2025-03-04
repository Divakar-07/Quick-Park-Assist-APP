package com.qpa.controller;

import com.qpa.service.UserService;
import com.qpa.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;
    private final VehicleService vehicleService;
// Constructor injection without @Autowired
    public HomeController(UserService userService, VehicleService vehicleService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        return "index";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

}