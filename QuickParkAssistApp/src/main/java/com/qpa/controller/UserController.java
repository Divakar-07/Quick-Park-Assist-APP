package com.qpa.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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


import com.qpa.entity.UserInfo;
import com.qpa.exception.InvalidEntityException;
import com.qpa.service.AuthService;
import com.qpa.service.UserService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private AuthService authService;

    @GetMapping("/login")
     public String Login(HttpServletRequest request) {
        if (authService.isAuthenticated(request)){
            return "redirect:/";
        }
        return "Login";
    }
    

    @PostMapping("/save")
    public ResponseEntity<?> register(@ModelAttribute UserInfo user, HttpServletRequest request) {
    try {
        if (authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Already logged in"));
        }

        if (user.getDateOfRegister() == null) {
            user.setDateOfRegister(LocalDate.now());
        }
        if (user.getStatus() == null) {
            user.setStatus(UserInfo.Status.ACTIVE);
        }

        userService.addUser(user);

        return ResponseEntity.ok(Map.of("message", "User has successfully registered"));

    } catch (DataIntegrityViolationException e) { 
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "User already exists"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error", "details", e.getMessage()));
    }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        if (authService.isAuthenticated(request)){
            model.addAttribute("user", userService.getUserById(id));
            return "ADD_user";
        }
        else {
            return "unauthorized request";
        }
            
    }

    @GetMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Cannot delete user as it has related entities.");
        } catch (InvalidEntityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while deleting the user.");
        }
    }

    @GetMapping("/{id}")  // This maps to /users/{id}
    public ResponseEntity<UserInfo> getUserById(@PathVariable Long id) {
        UserInfo user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();  // Return 404 if user not found
        }
    }
}
