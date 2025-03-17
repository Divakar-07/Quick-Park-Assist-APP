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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.RegisterDto;
import com.qpa.entity.AuthUser;
import com.qpa.entity.UserInfo;
import com.qpa.entity.Vehicle;
import com.qpa.exception.InvalidEntityException;
import com.qpa.service.AuthService;
import com.qpa.service.CloudinaryService;
import com.qpa.service.EmailService;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;






@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private AuthService authService;
    @Autowired private VehicleService vehicleService;
    @Autowired private EmailService emailService;
    @Autowired private CloudinaryService cloudinaryService;

    

    @GetMapping("/")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request){
        Long userId = authService.getUserId(request);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping("/image/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            UserInfo user = userService.getUserById(authService.getUserId(request));

            if (!"".equals(user.getImageUrl())){
                if(!deleteImage(user.getImageUrl(), request)){
                    return ResponseEntity.badRequest().body(Map.of("error", "error while deleting image"));
                }
            }
            user.setImageUrl(imageUrl);
            userService.updateUser(user);

            return ResponseEntity.ok("image uploaded successfully");
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/image/delete")
    public boolean deleteImage(String imageUrl, HttpServletRequest request) {
        try {
            cloudinaryService.deleteImage(imageUrl);
            UserInfo user = userService.getUserById(authService.getUserId(request));
            user.setImageUrl("");
            userService.updateUser(user);
            System.out.println("Image deleted successfully");
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterDto request, HttpServletResponse response) {
        try {

            if (userService.existsByEmail(request.getEmail())) {
                return "error";
            }

            UserInfo user = new UserInfo();
            System.out.println(request.getUserType());
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setUserType(request.getUserType());
            user = userService.addUser(user);

            AuthUser authUser = new AuthUser();
            authUser.setPassword(request.getPassword());
            authUser.setUsername(request.getUsername());
            authUser.setUser(user);
            
            if (!authService.addAuth(authUser, response)){
                return "error";
            }
            System.out.println("the error is coming from the email service");
            emailService.sendRegistrationEmail(request.getEmail(), request.getUsername());

            return "redirect:/dashboard";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "login";
        }
    }
    
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long id, @RequestBody UserInfo user) {
        try {
            
            if (id == null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "please provide userId"));
            }
    
            user.setUserId(id);
            userService.updateUser(user);
            return ResponseEntity.ok("user updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error unable to update user"));
        }
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
