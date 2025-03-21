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

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserProfile(HttpServletRequest request) {
        try {
            Long userId = authService.getUserId(request);
            return ResponseEntity.ok(
                    new ResponseDTO<>("user profile fetched successfully", 0, true, userService.getUserById(userId)));
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException inside the getUserProfile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), false));
        } catch (Exception e) {
            System.out.println("Exception inside the getUserProfile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
        }
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<ResponseDTO<Void>> uploadImage(@RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            UserInfo user = userService.getUserById(authService.getUserId(request));

            if (!(user.getImageUrl() == null)) {

                ResponseEntity<ResponseDTO<Void>> deleteResponse = deleteImage(user.getImageUrl(), request);
                if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.badRequest().body(new ResponseDTO<>("error while deleting image",
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
                }
            }
            System.out.println("imageUrl: " + imageUrl);
            user.setImageUrl(imageUrl);
            userService.updateUser(user);

            return ResponseEntity.ok(new ResponseDTO<>("image uploaded successfully", HttpStatus.OK.value(), true));
        } catch (java.io.IOException e) {
            System.out.println("An Error occured in the uploadImage method: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    new ResponseDTO<>("error while deleting image ", HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
        }
    }

    @PostMapping("/image/delete")
    public ResponseEntity<ResponseDTO<Void>> deleteImage(String imageUrl, HttpServletRequest request) {
        try {
            cloudinaryService.deleteImage(imageUrl);
            UserInfo user = userService.getUserById(authService.getUserId(request));
            user.setImageUrl("");
            userService.updateUser(user);
            return ResponseEntity.ok(new ResponseDTO<>("image successfully deleted", HttpStatus.OK.value(), true));
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>("image successfully deleted", HttpStatus.BAD_REQUEST.value(), true));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Void>> register(@RequestBody RegisterDto request, HttpServletResponse response,
            HttpServletRequest request1) {
        try {

            if (authService.isAuthenticated(request1)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("User is already logged in", HttpStatus.CONFLICT.value(), false));
            }

            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("Email already exists", HttpStatus.CONFLICT.value(), false));
            }

            if (request.getUserType() == UserType.ADMIN) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("ADMIN Role is forbidden", HttpStatus.CONFLICT.value(), false));
            }

            if (authService.getAuthByUsername(request.getUsername()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("username is already taken", HttpStatus.CONFLICT.value(), false));

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
                        .body(new ResponseDTO<>("Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                false));
            }

            try {
                emailService.sendRegistrationEmail(request.getEmail(), request.getUsername());
            } catch (Exception emailException) {
                System.err.println("Email sending failed: " + emailException.getMessage());
            }

            return ResponseEntity.ok(new ResponseDTO<>("User registered successfully", HttpStatus.OK.value(), true));

        } catch (Exception e) {
            System.out.println("Error inside the Register Controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(), false));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<Void>> updateUserDetails(@RequestBody UserInfo user, HttpServletRequest request) {
        try {
            if (!authService.isAuthenticated(request)){
                throw new UnauthorizedAccessException("UNAUTHORIZED REQUEST");
            }
            System.out.println(user.getAddress());
            System.out.println(user.getContactNumber());
            System.out.println(user.getDob());
            userService.updateUser(user);
            return ResponseEntity.ok(new ResponseDTO<>("details updated successfully", HttpStatus.OK.value(), true));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();

            if (errorMessage.contains("Duplicate entry")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO<>("Duplicate entry for email", HttpStatus.CONFLICT.value(), false));
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>("Database error: " + e.getMessage(), HttpStatus.CONFLICT.value(), false));
        }
    }

    @GetMapping("/{id}") // This maps to /users/{id}
    public ResponseEntity<ResponseDTO<UserInfo>> getUserById(@PathVariable Long id) {
        UserInfo user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity
                    .ok(new ResponseDTO<>("user fetched successfully", HttpStatus.OK.value(), false, user));
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if user not found
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserByVehicle(@PathVariable Long vehicleId) {
        try {
            return ResponseEntity.ok(new ResponseDTO<>("user fetched successfully", HttpStatus.OK.value(), true,
                    userService.viewUserByVehicleId(vehicleId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ResponseDTO<>("user not found", HttpStatus.NOT_FOUND.value(), false));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ResponseDTO<UserInfo>> getUserByBookingId(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(new ResponseDTO<>("user fetched successfully", HttpStatus.OK.value(), true,
                    userService.findByBookingId(bookingId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ResponseDTO<>("user not found", HttpStatus.NOT_FOUND.value(), false));
        }
    }

    // @DeleteMapping("delete/userId/{id}")
    // @ResponseBody
    // public ResponseEntity<?> deleteUser(Long id, HttpServletRequest request,
    // HttpServletResponse response) {
    // try {
    // Long userId = authService.getUserId(request);
    // userService.deleteUser(userId, response);
    // return ResponseEntity.status(HttpStatus.SEE_OTHER)
    // .header("Location", "/login")
    // .build();
    // } catch (DataIntegrityViolationException e) {
    // return ResponseEntity.badRequest().body("Cannot delete user as it has related
    // entities.");
    // } catch (InvalidEntityException e) {
    // return ResponseEntity.badRequest().body(e.getMessage());
    // } catch (Exception e) {
    // return ResponseEntity.internalServerError().body("An error occurred while
    // deleting the user.");
    // }
    // }

}
