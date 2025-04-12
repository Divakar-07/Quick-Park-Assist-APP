package com.qpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qpa.dto.ResponseDTO;
import com.qpa.entity.Vehicle;
import com.qpa.entity.VehicleType;
import com.qpa.service.AuthService;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller for handling vehicle-related operations such as adding, editing,
 * and saving vehicles.
 */
@Controller
@RequestMapping("/vehicle")
public class VehicleController {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final AuthService authService;

    public VehicleController(AuthService authService, VehicleService vehicleService, UserService userService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    /**
     * Displays the add vehicle page.
     * Redirects to login if the user is not authenticated.
     */
    @GetMapping("/addVehicle")
    public String addVehicle(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated(request)) {
            redirectAttributes.addFlashAttribute("error", "Login to continue");
            return "redirect:/auth/login";
        }

        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("user", userService.getUserDetails(request).getData());
        model.addAttribute("vehicleTypes", VehicleType.values());

        return "dashboard/addVehicle";
    }

    /**
     * Displays the edit vehicle page.
     * Redirects to login if the user is not authenticated.
     */
    @GetMapping("/editVehicle/{id}")
    public String editVehicle(@PathVariable Long id, Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated(request)) {
            redirectAttributes.addFlashAttribute("error", "Login to continue");
            return "redirect:/auth/login";
        }

        Vehicle vehicle = vehicleService.getVehicleById(id, request).getData();
        if (vehicle == null) {
            redirectAttributes.addFlashAttribute("error", "Vehicle not found");
            return "redirect:/dashboard";
        }

        model.addAttribute("vehicle", vehicle);
        model.addAttribute("user", userService.getUserDetails(request).getData());
        model.addAttribute("vehicleTypes", VehicleType.values());

        return "dashboard/addVehicle";
    }

    /**
     * Saves or updates vehicle information.
     * Redirects to login if the user is not authenticated.
     */
    @PostMapping("/save")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (!authService.isAuthenticated(request)) {
            redirectAttributes.addFlashAttribute("error", "Login to continue");
            return "redirect:/auth/login";
        }

        vehicle.setUserObj(userService.getUserDetails(request).getData());
        ResponseDTO<Void> backendResponse = vehicleService.addVehicle(vehicle, request);
        if (backendResponse.isSuccess()) {
            redirectAttributes.addFlashAttribute("success", backendResponse.getMessage());
        } else {
            redirectAttributes.addFlashAttribute("error", backendResponse.getMessage());
        }

        return "redirect:/dashboard";
    }

    @GetMapping("deleteVehicle/{id}")
    public String deleteVehicle(@PathVariable Long id, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        ResponseDTO<Void> backendResponse = vehicleService.deleteVehicle(id, request);
        System.out.println(backendResponse.getMessage());
        if (!backendResponse.isSuccess()) {
            redirectAttributes.addFlashAttribute("error", backendResponse.getMessage());
        } else {
            redirectAttributes.addFlashAttribute("success", backendResponse.getMessage());
        }
        return "redirect:/dashboard";
    }

}
