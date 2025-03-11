package com.qpa.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qpa.entity.UserInfo;
import com.qpa.entity.Vehicle;
import com.qpa.exception.InvalidEntityException;
import com.qpa.service.AuthService;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {
    @Autowired private VehicleService vehicleService;
    @Autowired private UserService userService;
    @Autowired private AuthService authService;

    @GetMapping("/new")
    public String showAddForm(Model model, HttpServletRequest request) {
        model.addAttribute("vehicle", new Vehicle());
        Long userId = authService.getUserId(request);
        UserInfo user = userService.getUserById(userId);
        if (user == null){
            System.out.println("user not found!");
        }
        model.addAttribute("user", user);
        return "ADD_vehicle";
    }

    @PostMapping("/save")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle, RedirectAttributes ra, HttpServletRequest request) {
        vehicleService.addVehicle(vehicle, request);
        ra.addFlashAttribute("success", "Vehicle saved successfully!");
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.getVehicleById(id));
        model.addAttribute("users", userService.getAllUsers());
        return "ADD_vehicle";
    }

    @GetMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes ra) {
        vehicleService.deleteVehicle(id);
        ra.addFlashAttribute("success", "Vehicle deleted successfully!");
        return "redirect:/";
    }

    // Get Vehicle by ID 
    @GetMapping("/{id}")
    @ResponseBody
    public Vehicle viewVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    //Get Vehicles by Type 
    @GetMapping("/type/{vehicleType}")
    @ResponseBody
    public List<Vehicle> viewVehiclesByType(@PathVariable String vehicleType) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByType(vehicleType);
        if (vehicles.isEmpty()) {
            throw new InvalidEntityException("No vehicles found for type: " + vehicleType);
        }
        return vehicles;
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getVehicleByBookingId(@PathVariable Long bookingId, HttpServletRequest request) {
        
        if (!authService.isAuthenticated(request)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized request"));
        }
        
        
        return ResponseEntity.ok(vehicleService.findByBookingId( bookingId));
    }
	/*
	addVehicle
	viewVehicleById
	viewVehicleByType
		
	 */
}
