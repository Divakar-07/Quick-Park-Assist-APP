package com.qpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qpa.dto.LoginDTO;
import com.qpa.dto.RegisterDTO;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.AuthUser;
import com.qpa.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Controller for handling authentication-related operations such as
 * login, registration, and logout.
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authUiService;

    /**
     * Handles the request for the login page.
     * If the user is already authenticated, redirects to the dashboard.
     * Otherwise, loads the login page with empty DTOs for login and registration.
     */
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
        if (authUiService.isAuthenticated(request)) {
            return "redirect:/dashboard"; // Redirects authenticated users
        }
        model.addAttribute("registerUserDto", new RegisterDTO()); // Empty registration DTO
        model.addAttribute("loginDto", new LoginDTO()); // Empty login DTO
        return "login"; // Returns login page view
    }

    /**
     * Handles user registration form submission.
     * Validates the password match and user type selection.
     * Registers the user and logs them in if successful.
     */
    @PostMapping("/register/submit")
    public String registerUser(
            @Valid @ModelAttribute("registerUserDto") RegisterDTO registerDTO,
            RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {

        // Check if password and confirm password match
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Passwords don't match.");
            return "redirect:/auth/login";
        }

        // Validate that user type is selected
        if (registerDTO.getUserType() == null || registerDTO.getUserType().toString().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a user type");
            return "redirect:/auth/login";
        }

        // Call the registration service
        ResponseDTO<Void> backendResponse = authUiService.registerUser(registerDTO, request);
        if (!backendResponse.isSuccess()) {
            redirectAttributes.addFlashAttribute("error", backendResponse.getMessage());
            return "redirect:/auth/login"; // Redirect back if registration fails
        }

        redirectAttributes.addFlashAttribute("success", backendResponse.getMessage());
        System.out.println("inside the register done ");
        // Auto-login after successful registration
        authUiService.login(new AuthUser(registerDTO.getEmail(), registerDTO.getPassword()), response, request);
        return "redirect:/dashboard"; // Redirect to dashboard after registration
    }

    /**
     * Handles user login form submission.
     * If successful, redirects to the dashboard; otherwise, returns an error
     * message.
     */
    @PostMapping("/login/submit")
    public String loginUser(@ModelAttribute LoginDTO loginDTO, RedirectAttributes redirectAttributes,
            HttpServletResponse response, HttpServletRequest request) {
        AuthUser authUser = new AuthUser();
        authUser.setPassword(loginDTO.getPassword());
        authUser.setEmail(loginDTO.getEmail());

        // Attempt login
        ResponseDTO<Void> responseData = authUiService.login(authUser, response, request);

        if (!responseData.isSuccess()) {
            redirectAttributes.addFlashAttribute("error", responseData.getMessage());
            return "redirect:/auth/login"; // Redirect back if login fails
        }

        redirectAttributes.addFlashAttribute("success", responseData.getMessage());
        return "redirect:/dashboard"; // Redirect to dashboard if login is successful
    }

    /**
     * Handles user logout request.
     * Clears authentication session and redirects to the login page.
     */
    @PostMapping("/logout")
    public String logoutUser(HttpServletResponse response, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        ResponseDTO<Void> backendResponse = authUiService.logout(request, response); // Clears session and cookies
        redirectAttributes.addFlashAttribute("success", backendResponse.getMessage());
        return "redirect:/auth/login"; // Redirects to login page after logout
    }

}
