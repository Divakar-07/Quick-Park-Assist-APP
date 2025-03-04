package com.qpa.controller;


import com.qpa.entity.Vehicle;
import com.qpa.exception.InvalidEntityException;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {
    @Autowired private VehicleService vehicleService;
    @Autowired private UserService userService;

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("users", userService.getAllUsers());
        return "ADD_vehicle";
    }

    @PostMapping("/save")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle, RedirectAttributes ra) {
        vehicleService.addVehicle(vehicle);
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
	/*
	addVehicle
	viewVehicleById
	viewVehicleByType
		
	 */
}
