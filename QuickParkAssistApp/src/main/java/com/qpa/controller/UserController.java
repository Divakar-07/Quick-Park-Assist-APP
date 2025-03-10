package com.qpa.controller;

import java.util.HashMap;
import java.util.List;
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
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestParam;

import com.qpa.entity.Vehicle;




@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private AuthService authService;
    @Autowired private VehicleService vehicleService;

    

    @GetMapping("/")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request){
        Long userId = authService.getUserId(request);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    
    @PostMapping("/save")
    public ResponseEntity<?> register(@ModelAttribute UserInfo user, HttpServletRequest request, HttpSession session) {
    try {
        if (authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Already logged in"));
        }
        // Save the user and retrieve the saved entity with an ID
        UserInfo savedUser = userService.addUser(user);

        // Check if userId is generated
        if (savedUser.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "User ID was not generated"));
        }

        // Store user in session
        session.setAttribute("registeredUser", savedUser);

        // Redirect to "/add-auth"
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "/add-auth")
                .build();

    } catch (DataIntegrityViolationException e) { 
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "User already exists"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error", "details", e.getMessage()));
    }
}


    @PostMapping("/edit-save")
    public ResponseEntity<?> saveEdit(@ModelAttribute UserInfo user, HttpServletRequest request, HttpSession session) {
    try {
        // Save the user and retrieve the saved entity with an ID
        UserInfo savedUser = userService.addUser(user);

        // Check if userId is generated
        if (savedUser.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "User ID was not generated"));
        }


        // Redirect to "/add-auth"
        return ResponseEntity.status(HttpStatus.OK).body("edit successfull");

    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "User already exists"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error", "details", e.getMessage()));
    }
}

    @GetMapping("/user/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        if (authService.isAuthenticated(request)){
            model.addAttribute("user", userService.getUserById(id));
            return "ADD_user";
        }
        else {
            return "unauthorized request";
        }
            
    }


    @GetMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long userId = authService.getUserId(request);
            userService.deleteUser(userId, response);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header("Location", "/login")
            .build();
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

    @GetMapping("/user/vehicle/{vehicleId}")
    public ResponseEntity<?> getUserByVehicle(@PathVariable Long vehicleId) {
    try {
            return ResponseEntity.ok(userService.viewUserByVehicleId(vehicleId));
    } catch (Exception e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
    }

    @GetMapping("/user/booking/{bookingId}")
    public ResponseEntity<?> getUserByBookingId(@PathVariable Long bookingId) {
    try {
            return ResponseEntity.ok(userService.findByBookingId(bookingId));
    } catch (Exception e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
    }
    
    @GetMapping("/getUserDetails")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        try {
            if (!authService.isAuthenticated(request)){
                return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "/login")
                    .build();
            }

            Long userId = authService.getUserId(request);
            UserInfo user = userService.getUserById(userId);
            List<Vehicle> vehicles = vehicleService.findByUserId(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("vehicles", vehicles);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    

}
