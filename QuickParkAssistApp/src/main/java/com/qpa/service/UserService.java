package com.qpa.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.RegisterDTO;
import com.qpa.dto.SpotResponseDTO;
import com.qpa.entity.AuthUser;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;
import com.qpa.entity.UserType;
import com.qpa.entity.Vehicle;
import com.qpa.exception.InvalidEntityException;
import com.qpa.exception.UnauthorizedAccessException;
import com.qpa.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final SpotBookingService spotBookingService;
    private final UserRepository userRepository;
    private final VehicleService vehicleService;
    private final AuthService authService;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final SpotService spotService;

    public UserService(UserRepository userRepository, VehicleService vehicleService,
            AuthService authService, CloudinaryService cloudinaryService,
            EmailService emailService, SpotBookingService spotBookingService, SpotService spotService) {
        this.userRepository = userRepository;
        this.vehicleService = vehicleService;
        this.authService = authService;
        this.cloudinaryService = cloudinaryService;
        this.emailService = emailService;
        this.spotBookingService = spotBookingService;
        this.spotService = spotService;
    }

    public UserInfo getCurrentUserProfile(HttpServletRequest request) throws InvalidEntityException {
        Long userId = authService.getUserId(request);
        return getUserById(userId);
    }

    public void uploadUserAvatar(MultipartFile file, HttpServletRequest request)
            throws IOException, InvalidEntityException {
        String imageUrl = cloudinaryService.uploadImage(file);
        UserInfo user = getUserById(authService.getUserId(request));

        // Delete existing image if any
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            cloudinaryService.deleteImage(user.getImageUrl());
        }

        user.setImageUrl(imageUrl);
        updateUser(user);
    }

    public void deleteUserAvatar(String imageUrl, HttpServletRequest request)
            throws IOException, InvalidEntityException {
        cloudinaryService.deleteImage(imageUrl);
        UserInfo user = getUserById(authService.getUserId(request));
        user.setImageUrl("");
        updateUser(user);
    }

    public void registerUser(RegisterDTO request, HttpServletResponse response, HttpServletRequest httpRequest)
            throws Exception {
        // Check if the user is already logged in
        if (authService.isAuthenticated(httpRequest)) {
            throw new IllegalStateException("User is already logged in");
        }
        // Check if email is already registered
        if (existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("username already exists");
        }

        // Create new user
        UserInfo user = new UserInfo();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setUserType(request.getUserType());
        user.setUsername(request.getUsername());
        user = addUser(user);

        // Create authentication details
        AuthUser authUser = new AuthUser();
        authUser.setPassword(request.getPassword());
        authUser.setEmail(request.getEmail());
        authUser.setUser(user);

        // Add authentication
        if (!authService.addAuth(authUser, response)) {
            throw new RuntimeException("Authentication failed");
        }

        // Send registration email
        emailService.sendSimpleMail(request.getEmail(), request.getFullName(),
                "Registration Successful as " + request.getUserType());
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void deactivateInactiveUsers() {
        LocalDate cutoffDate = LocalDate.now().minusYears(1); // 1 year before today
        List<UserInfo> inactiveUsers = userRepository.findInactiveUsers(cutoffDate);

        for (UserInfo user : inactiveUsers) {
            String subject = "Account Deactivation Due to Inactivity";
            String body = "Dear " + user.getFullName() + ",\n\n" +
                    "We noticed that your account has been inactive for over a year. " +
                    "As a result, your account has been deactivated. If you wish to reactivate your account, " +
                    "please contact our support team.\n\n" +
                    "Thank you,\nQuickParkAssist Team";
            emailService.sendSimpleMail(user.getEmail(), subject, body);
            user.setStatus(UserInfo.Status.INACTIVE);
        }

        userRepository.saveAll(inactiveUsers);
    }

    public void updateUserDetails(UserInfo user, HttpServletRequest request) throws InvalidEntityException {
        if (!authService.isAuthenticated(request)) {
            throw new UnauthorizedAccessException("Unauthorized request");
        }

        try {
            updateUser(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new InvalidEntityException("Duplicate entry for username");
            }
            throw e;
        }
    }

    public UserInfo addUser(UserInfo user) {
        return userRepository.save(user);
    }

    public UserInfo getUserById(Long id) throws InvalidEntityException {
        return userRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("User not found"));
    }

    public List<UserInfo> getAllUsers(HttpServletRequest request) {
        if (authService.getUserType(request) != UserType.ADMIN) {
            throw new UnauthorizedAccessException(HttpStatus.UNAUTHORIZED.toString());
        }
        return userRepository.findAll();
    }

    public UserInfo updateUser(UserInfo user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id, HttpServletResponse response) throws InvalidEntityException {
        UserInfo user = getUserById(id);
        try {
            authService.deleteAuth(id, response);
            userRepository.delete(user);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityException("Cannot delete user. Related entities exist.");
        }
    }

    public UserInfo viewUserByVehicleId(Long vehicleId) throws InvalidEntityException {
        return vehicleService.getVehicleById(vehicleId).getUserObj();
    }

    public UserInfo findByBookingId(Long bookingId) throws InvalidEntityException {
        Vehicle vehicle = vehicleService.findByBookingId(bookingId);
        return viewUserByVehicleId(vehicle.getVehicleId());
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public UserType checkUserType(Long userId) throws InvalidEntityException {
        UserInfo user = getUserById(userId);
        return user.getUserType();
    }

    public List<SpotBookingInfo> getUsersbookings(HttpServletRequest request) throws InvalidEntityException {
        UserInfo user = getCurrentUserProfile(request);
        List<Vehicle> vehicles = vehicleService.findByUserId(user.getUserId());
        List<SpotBookingInfo> bookings = vehicles.stream().flatMap((vehicle) -> {
            try {
                return spotBookingService.getBookingsByVehicleId(vehicle.getVehicleId()).stream();
            } catch (InvalidEntityException e) {
                return Stream.empty();
            }
        }).collect(Collectors.toList());
        return bookings;
    }

    public List<UserInfo> getAllCurrentParkedUser(HttpServletRequest request) {
        List<SpotResponseDTO> ownerSpots = spotService.getSpotByOwner(authService.getUserId(request));

        return ownerSpots.stream()
                .flatMap(spot -> {
                    try {
                        return spotBookingService.getBookingsBySlotId(spot.getSpotId()).stream();
                    } catch (InvalidEntityException e) {
                        return Stream.empty(); // Return an empty stream if an exception occurs
                    }
                })
                .filter(booking -> {
                    LocalDate today = LocalDate.now();
                    LocalTime now = LocalTime.now();

                    boolean isSameDay = !booking.getStartDate().isAfter(today)
                            && !booking.getEndDate().isBefore(today);
                    boolean isWithinTime = (booking.getStartDate().isBefore(today)
                            || (booking.getStartDate().isEqual(today) && booking.getStartTime().isBefore(now))) &&
                            (booking.getEndDate().isAfter(today)
                                    || (booking.getEndDate().isEqual(today) && booking.getEndTime().isAfter(now)));
                    return isSameDay && isWithinTime;
                })
                .map(booking -> booking.getVehicle().getUserObj())
                .collect(Collectors.toList());

    }

    // 02-04
    public List<UserInfo> getAllAdmins() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getUserType() == UserType.ADMIN)
                .collect(Collectors.toList());
    }
}