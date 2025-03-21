package com.qpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.RegisterDto;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.AuthUser;
import com.qpa.entity.UserInfo;
import com.qpa.entity.UserType;
import com.qpa.exception.UnauthorizedAccessException;
import com.qpa.service.AuthService;
import com.qpa.service.CloudinaryService;
import com.qpa.service.EmailService;
import com.qpa.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Retrieves the user profile based on the authenticated user.
     */
    @GetMapping("/")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserProfile(HttpServletRequest request) {
        try {
            Long userId = authService.getUserId(request);
            return ResponseEntity.ok(
                    new ResponseDTO<>("User profile fetched successfully", 0, true, userService.getUserById(userId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("Unauthorized", HttpStatus.UNAUTHORIZED.value(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
        }
    }

    /**
     * Uploads an avatar image for the authenticated user.
     */
    @PostMapping("/upload-avatar")
    public ResponseEntity<ResponseDTO<Void>> uploadImage(@RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            UserInfo user = userService.getUserById(authService.getUserId(request));

            // Delete the existing image if any
            if (user.getImageUrl() != null) {
                ResponseEntity<ResponseDTO<Void>> deleteResponse = deleteImage(user.getImageUrl(), request);
                if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.badRequest()
                            .body(new ResponseDTO<>("Error while deleting image",
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
                }
            }

            user.setImageUrl(imageUrl);
            userService.updateUser(user);

            return ResponseEntity.ok(new ResponseDTO<>("Image uploaded successfully", HttpStatus.OK.value(), true));
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>("Error while uploading image", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false));
        }
    }

    /**
     * Deletes the user's profile image.
     */
    @PostMapping("/image/delete")
    public ResponseEntity<ResponseDTO<Void>> deleteImage(String imageUrl, HttpServletRequest request) {
        try {
            cloudinaryService.deleteImage(imageUrl);
            UserInfo user = userService.getUserById(authService.getUserId(request));
            user.setImageUrl("");
            userService.updateUser(user);
            return ResponseEntity.ok(new ResponseDTO<>("Image successfully deleted", HttpStatus.OK.value(), true));
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>("Error while deleting image", HttpStatus.BAD_REQUEST.value(), false));
        }
    }

    /**
     * Registers a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Void>> register(@RequestBody RegisterDto request, HttpServletResponse response,
            HttpServletRequest request1) {
        try {
            // Check if the user is already logged in
            if (authService.isAuthenticated(request1)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("User is already logged in", HttpStatus.CONFLICT.value(), false));
            }

            // Check if email is already registered
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("Email already exists", HttpStatus.CONFLICT.value(), false));
            }

            // Prevent registration as ADMIN
            if (request.getUserType() == UserType.ADMIN) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("ADMIN Role is forbidden", HttpStatus.CONFLICT.value(), false));
            }

            // Check if username is taken
            if (authService.getAuthByUsername(request.getUsername()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("Username is already taken", HttpStatus.CONFLICT.value(), false));
            }

            // Create new user
            UserInfo user = new UserInfo();
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setUserType(request.getUserType());
            user = userService.addUser(user);

            // Create authentication details
            AuthUser authUser = new AuthUser();
            authUser.setPassword(request.getPassword());
            authUser.setUsername(request.getUsername());
            authUser.setUser(user);

            // Add authentication
            if (!authService.addAuth(authUser, response)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO<>("Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                false));
            }

            // Send registration email
            emailService.sendRegistrationEmail(request.getEmail(), request.getUsername());

            return ResponseEntity.ok(new ResponseDTO<>("User registered successfully", HttpStatus.OK.value(), true));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
        }
    }

    /**
     * Updates user details.
     */
    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<Void>> updateUserDetails(@RequestBody UserInfo user, HttpServletRequest request) {
        try {
            if (!authService.isAuthenticated(request)) {
                throw new UnauthorizedAccessException("UNAUTHORIZED REQUEST");
            }

            userService.updateUser(user);
            return ResponseEntity.ok(new ResponseDTO<>("Details updated successfully", HttpStatus.OK.value(), true));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("Duplicate entry for email", HttpStatus.CONFLICT.value(), false));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>("Database error", HttpStatus.CONFLICT.value(), false));
        }
    }

    /**
     * Retrieves a user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserById(@PathVariable Long id) {
        UserInfo user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(new ResponseDTO<>("User fetched successfully", HttpStatus.OK.value(), true, user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves a user by their vehicle ID.
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserByVehicle(@PathVariable Long vehicleId) {
        try {
            return ResponseEntity.ok(new ResponseDTO<>("User fetched successfully", HttpStatus.OK.value(), true,
                    userService.viewUserByVehicleId(vehicleId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ResponseDTO<>("User not found", HttpStatus.NOT_FOUND.value(), false));
        }
    }

    /**
     * Retrieves a user by their booking ID.
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserByBookingId(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(new ResponseDTO<>("User fetched successfully", HttpStatus.OK.value(), true,
                    userService.findByBookingId(bookingId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ResponseDTO<>("User not found", HttpStatus.NOT_FOUND.value(), false));
        }
    }
}
