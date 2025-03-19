package com.qpa.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.RegisterDto;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.AuthUser;
import com.qpa.entity.UserInfo;
import com.qpa.entity.UserType;
import com.qpa.entity.Vehicle;
import com.qpa.exception.InvalidEntityException;
import com.qpa.service.AuthService;
import com.qpa.service.CloudinaryService;
import com.qpa.service.EmailService;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;






@Controller
@RequestMapping("api/users")
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
    public ResponseEntity<ResponseDTO> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            UserInfo user = userService.getUserById(authService.getUserId(request));

            if (!(user.getImageUrl() == null)){
                
                ResponseEntity<ResponseDTO> deleteResponse = deleteImage(user.getImageUrl(), request);
                if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.badRequest().body(new ResponseDTO("error while deleting image", HttpStatus.INTERNAL_SERVER_ERROR, false));
                }
            }
            System.out.println("imageUrl: " + imageUrl);
            user.setImageUrl(imageUrl);
            userService.updateUser(user);

            return ResponseEntity.ok(new ResponseDTO("image uploaded successfully", HttpStatus.OK, true));
        } catch (java.io.IOException e) {
            System.out.println("An Error occured in the uploadImage method: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseDTO("error while deleting image ", HttpStatus.INTERNAL_SERVER_ERROR, false));
        }
    }

    @PostMapping("/image/delete")
    public ResponseEntity<ResponseDTO> deleteImage(String imageUrl, HttpServletRequest request) {
        try {
            cloudinaryService.deleteImage(imageUrl);
            UserInfo user = userService.getUserById(authService.getUserId(request));
            user.setImageUrl("");
            userService.updateUser(user);
            return ResponseEntity.ok(new ResponseDTO("image successfully deleted", HttpStatus.OK, true));
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO("image successfully deleted", HttpStatus.BAD_REQUEST, true));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody RegisterDto request, HttpServletResponse response) {
        try {
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO("Email already exists", HttpStatus.CONFLICT, false));
            }

            if (request.getUserType() == UserType.ADMIN){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO("ADMIN Role is forbidden", HttpStatus.CONFLICT, false));
            }

            UserInfo user = new UserInfo();
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setUserType(request.getUserType());
            user = userService.addUser(user);

            AuthUser authUser = new AuthUser();
            authUser.setPassword(request.getPassword());
            authUser.setUsername(request.getUsername());
            authUser.setUser(user);

            if (!authService.addAuth(authUser, response)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO("Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR, false));
            }

            try {
                emailService.sendRegistrationEmail(request.getEmail(), request.getUsername());
            } catch (Exception emailException) {
                System.err.println("Email sending failed: " + emailException.getMessage());
            }

            return ResponseEntity.ok(new ResponseDTO("User registered successfully", HttpStatus.OK, true));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR, false));
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ResponseDTO> updateUserDetails(@PathVariable Long id, @RequestBody UserInfo user) {
        try {
            
            if (id == null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseDTO("userId must be provided", HttpStatus.BAD_REQUEST, false));
            }
    
            user.setUserId(id);
            userService.updateUser(user);
            return ResponseEntity.ok(new ResponseDTO("details updated successfully", HttpStatus.OK, true));
        } catch (Exception e) {
            System.out.println("error in UserController while updateUserDetails: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, false));
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


    @DeleteMapping("delete/userId/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(Long id, HttpServletRequest request, HttpServletResponse response) {
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
