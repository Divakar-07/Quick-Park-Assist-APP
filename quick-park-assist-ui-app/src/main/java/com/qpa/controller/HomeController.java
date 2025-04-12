package com.qpa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qpa.dto.AdminSpotsStatistics;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.ContactMessage;
import com.qpa.entity.PaymentUI;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;
import com.qpa.entity.UserType;
import com.qpa.entity.Vehicle;
import com.qpa.service.AuthService;
import com.qpa.service.CustomRestTemplateService;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller to handle requests related to home, dashboard, and contact pages.
 */
@Controller
public class HomeController {

    @Autowired
    private AuthService authUiService; // Service for authentication-related operations
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private UserService userService; // Service for fetching user details

    @Autowired
    private CustomRestTemplateService restTemplateService;

    /**
     * Handles requests to the home page.
     * Redirects to the login page if the user is not authenticated.
     */
    @GetMapping("/")
    public String homePage() {
        return "index"; // Loads the home page
    }

    /**
     * Handles requests to the dashboard.
     * Redirects to the login page if the user is not authenticated.
     * Fetches user details and passes them to the view.
     */

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        if (!authUiService.isAuthenticated(request)) {
            return "redirect:/auth/login"; // Redirects unauthenticated users
        }

        // Fetch user details
        ResponseDTO<UserInfo> response = userService.getUserDetails(request);

        if (response.isSuccess()) {
            UserInfo user = response.getData(); // Retrieves user info
            model.addAttribute("user", user); // Pass full name to the view
            if (user.getUserType() == UserType.ADMIN) {
                AdminSpotsStatistics statistics = userService.getAdminSpotsStatistics(request);
                model.addAttribute("totalActiveUsers", statistics.getActiveBookings());
                model.addAttribute("totalParkingSpots", statistics.getTotalSpots());
                List<PaymentUI> payments = userService.getAllAdminPayments(request);
                double totalCollection = payments.stream()
                        .mapToDouble(payment -> payment.getTotalAmount() != null ? payment.getTotalAmount() : 0.0)
                        .sum();
                model.addAttribute("totalEarnings", totalCollection);
                // Fetch admins from backend API
                String apiUrl = "http://localhost:7212/api/users/admin/viewAll"; // Ensure correct backend port
                List<UserInfo> adminUsers = restTemplate.getForObject(apiUrl, List.class);
                model.addAttribute("admins", adminUsers); // Send to Thymeleaf
                return "admin/index";
            }
            List<Vehicle> vehicles = vehicleService.findUserVehicle(request).getData();
            model.addAttribute("vehicles", vehicles);
            return "dashboard/dashboard"; // Loads the dashboard view
        } else {
            return "error";
        }
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/contact")
    public String getContactPage(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "dashboard/contact";
    }

    @GetMapping("/admin/contact-messages")
    public String showAdminQueries(Model model) {
        String apiUrl = "http://localhost:7212/api/contact-messages"; // Backend API
        List<ContactMessage> messages = restTemplate.getForObject(apiUrl, List.class);
        model.addAttribute("messages", messages);
        return "admin/ADMINquery"; // Renders ADMINquery.html
    }

    @PostMapping("/contact")
    public String submitContact(@ModelAttribute("contactMessage") ContactMessage message, Model model) {
        try {
            restTemplate.postForEntity("http://localhost:7212/api/contact", message, Void.class);
            model.addAttribute("success", "Your message has been sent successfully.");
            model.addAttribute("contactMessage", new ContactMessage());
        } catch (RestClientException e) {
            model.addAttribute("error", "There was an error sending your message. Please try again later.");
        }
        return "dashboard/contact";
    }

    @GetMapping("/dashboard/reports")
    public String AdminDashboardUsersPage(HttpServletRequest request, Model model) {
        if (!authUiService.isAuthenticated(request)) {
            return "redirect:/auth/login";
        } else if (userService.getUserDetails(request).getData().getUserType() != UserType.ADMIN) {
            return "redirect:/dashboard";
        } else {
            List<SpotBookingInfo> bookings = userService.getBookingsForAdmin(request);
            model.addAttribute("bookings", bookings);
            return "admin/dashboardUsers";
        }
    }

    @GetMapping("/dashboard/userDetails/{bookingId}")
    public String AdminDashboardUserDetailsPage(HttpServletRequest request, Model model, @PathVariable Long bookingId) {
        if (!authUiService.isAuthenticated(request)) {
            return "redirect:/auth/login";
        } else if (userService.getUserDetails(request).getData().getUserType() != UserType.ADMIN) {
            return "redirect:/dashboard";
        } else {
            SpotBookingInfo booking = userService.getBookingByBookingId(bookingId, request);
            Vehicle vehicle = booking.getVehicle();
            UserInfo user = vehicle.getUserObj();

            PaymentUI payment = userService.getPaymentByBookingId(bookingId, request);
            model.addAttribute("booking", booking);
            model.addAttribute("user", user);
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("payment", payment);

            return "admin/userDetail";
        }
    }

    @GetMapping("/dashboard/parking-spot")
    public String getParkingSpotPage(HttpServletRequest request, Model model) {
        AdminSpotsStatistics statistics = userService.getAdminSpotsStatistics(request);
        model.addAttribute("statistics", statistics);
        return "admin/parkingSpots";
    }

    @GetMapping("/contact/delete/{id}")
    public String deleteMessage(@PathVariable Long id, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        ResponseDTO<Void> backendResponse = restTemplateService
                .delete("/contact/delete/"+id, null, request, new ParameterizedTypeReference<ResponseDTO<Void>>() {
                }).getBody();

        if (backendResponse != null) {
            if (backendResponse.isSuccess()) {
                redirectAttributes.addFlashAttribute("success", backendResponse.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", backendResponse.getMessage());
            }
        }
        return "redirect:/admin/contact-messages";
    }
}
