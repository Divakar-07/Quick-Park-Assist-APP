package com.qpa.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.RegisterDTO;
import com.qpa.dto.ResponseDTO;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;
import com.qpa.exception.InvalidEntityException;
import com.qpa.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserProfile(HttpServletRequest request) {
        try {
            UserInfo userProfile = userService.getCurrentUserProfile(request);
            return ResponseEntity.ok(
                    new ResponseDTO<>("User profile fetched successfully", 0, true, userProfile));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), false));
        }
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<ResponseDTO<Void>> uploadImage(@RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            userService.uploadUserAvatar(file, request);
            return ResponseEntity.ok(new ResponseDTO<>("Image uploaded successfully", HttpStatus.OK.value(), true));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.CONFLICT.value(), false, null));
        }
    }

    @PostMapping("/image/delete")
    public ResponseEntity<ResponseDTO<Void>> deleteImage(String imageUrl, HttpServletRequest request) {
        try {
            userService.deleteUserAvatar(imageUrl, request);
            return ResponseEntity.ok(new ResponseDTO<>("Image successfully deleted", HttpStatus.OK.value(), true));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.CONFLICT.value(), false, null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Void>> register(@RequestBody RegisterDTO request,
            HttpServletResponse response, HttpServletRequest httpRequest) {
        try {
            userService.registerUser(request, response, httpRequest);
            return ResponseEntity.ok(new ResponseDTO<>("User registered successfully", HttpStatus.OK.value(), true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<Void>> updateUserDetails(@RequestBody UserInfo user, HttpServletRequest request) {
        try {
            userService.updateUserDetails(user, request);
            return ResponseEntity.ok(new ResponseDTO<>("Details updated successfully", HttpStatus.OK.value(), true));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.CONFLICT.value(), false, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserById(@PathVariable Long id) {
        try {
            UserInfo user = userService.getUserById(id);
            return ResponseEntity.ok(new ResponseDTO<>("User fetched successfully", HttpStatus.OK.value(), true, user));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.NOT_FOUND.value(), false, null));
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserByVehicle(@PathVariable Long vehicleId) {
        try {
            return ResponseEntity.ok(new ResponseDTO<>("User fetched successfully", HttpStatus.OK.value(), true,
                    userService.viewUserByVehicleId(vehicleId)));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>("User not found", HttpStatus.NOT_FOUND.value(), false));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserByBookingId(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(new ResponseDTO<>("User fetched successfully", HttpStatus.OK.value(), true,
                    userService.findByBookingId(bookingId)));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>("User not found", HttpStatus.NOT_FOUND.value(), false));
        }
    }

    @GetMapping("/booking-history")
    public ResponseEntity<ResponseDTO<List<SpotBookingInfo>>> getUserBookingHistory(HttpServletRequest request) {

        try {
            return ResponseEntity.ok(new ResponseDTO<>("bookings fetched successfully", 200, true,
                    userService.getUsersbookings(request)));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.NOT_FOUND.value(), false));
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<ResponseDTO<List<UserInfo>>> getAllUsers(HttpServletRequest request) {
        return ResponseEntity
                .ok(new ResponseDTO<>("users fetched successfully", 200, true, userService.getAllUsers(request)));
    }

    @GetMapping("/spots/current-active")
    public ResponseEntity<ResponseDTO<List<UserInfo>>> getAllCurrentActiveUsers(HttpServletRequest request) {
        return ResponseEntity.ok(new ResponseDTO<>("users fetched successfully", 200, true,
                userService.getAllCurrentParkedUser(request)));
    }

    // API to fetch all ADMIN users
    @GetMapping("/admin/viewAll")
    public ResponseEntity<List<UserInfo>> getAllAdmins() {
        return ResponseEntity.ok(userService.getAllAdmins());
    }
}