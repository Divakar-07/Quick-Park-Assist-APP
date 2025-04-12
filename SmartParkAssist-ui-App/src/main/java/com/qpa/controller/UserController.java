package com.qpa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qpa.dto.ResponseDTO;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;
import com.qpa.entity.Vehicle;
import com.qpa.service.AuthService;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller for handling user-related operations such as profile retrieval,
 * avatar upload, and user updates.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthService authUiService; // Handles authentication checks

    @Autowired
    private UserService userService; // Handles user-related operations

    @Autowired
    private VehicleService vehicleService; // Handles user vehicle-related operations

    /**
     * Retrieves and displays the user profile page.
     * Redirects to login if the user is not authenticated.
     */
    @GetMapping("/profile")
    public String getUserDashboard(Model model, HttpServletRequest request) {
        if (!authUiService.isAuthenticated(request)) {
            return "redirect:/auth/login"; // Redirects unauthenticated users
        }

        // Fetch user details
        UserInfo user = userService.getUserDetails(request).getData();
        if (user == null) {
            return "redirect:/error"; // Redirects to an error page if user data is null
        }

        model.addAttribute("user", user);

        // Fetch user vehicles
        List<Vehicle> vehicles = vehicleService.findUserVehicle(request).getData();
        vehicles.forEach(System.out::println); // Debugging: Print vehicles list

        model.addAttribute("vehicles", vehicles);

        return "dashboard/profile"; // Return profile page view
    }

    /**
     * Handles avatar (profile picture) upload.
     * Returns a ResponseEntity with a success or failure response.
     */
    @PostMapping("/avatar")
    public ResponseEntity<ResponseDTO<Void>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            return ResponseEntity.ok(userService.uploadImage(file, request)); // Uploads the image
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("Failed to upload image", 500, false));
        }
    }

    /**
     * Updates user information and redirects to the profile page.
     * Uses RedirectAttributes to show success or error messages.
     */
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") UserInfo user,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            ResponseDTO<Void> backendResponse = userService.updateUser(user, request);
            redirectAttributes.addFlashAttribute("success", backendResponse.getMessage()); // Success message
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user details."); // Error message
        }
        return "redirect:/dashboard"; // Redirect to profile page
    }

    @GetMapping("/booking-history")
    public String getBookingHistory(HttpServletRequest request, Model model) {
        ResponseDTO<List<SpotBookingInfo>> listData = userService.getUserBookingHistory(request);
        ResponseDTO<UserInfo> response = userService.getUserDetails(request);

        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage()); // Adds error message if fetching fails
        } else {
            UserInfo user = response.getData(); // Retrieves user info
            model.addAttribute("user", user); // Pass full name to the view
        }
        model.addAttribute("bookingList", listData.getData());
        return "dashboard/bookingHistory";
    }

    
}
