package com.qpa.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qpa.dto.ResponseDTO;
import com.qpa.dto.SpotBookingDTO;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;
import com.qpa.service.UserService;
import com.qpa.service.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/ui/booking")
public class SpotBookingUIController {

    private final VehicleService vehicleService;
    private final UserService usersService;
    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:7212/api/bookSlot"; // Backend base URL

    public SpotBookingUIController(RestTemplate restTemplate, VehicleService vehicleService, UserService usersService) {
        this.restTemplate = restTemplate;
        this.vehicleService = vehicleService;
        this.usersService = usersService;
    }

    /**
     * Displays the home page for the booking module.
     */
    @GetMapping("/home")
    public String homePage() {
        return "bookings/home";
    }

    /**
     * Displays the form to add a new booking, pre-filling spotId and user vehicles.
     */
    @GetMapping("/add")
    public String showAddBookingPage(Model model, HttpServletRequest request, @RequestParam Long spotId) {
        try {
            model.addAttribute("vehicles", vehicleService.findUserVehicle(request).getData());
            model.addAttribute("spotId", spotId);
        } catch (Exception e) {
            model.addAttribute("error", "❌ Failed to fetch user vehicles: " + e.getMessage());
        }
        return "bookings/addBooking";
    }

    /**
     * Saves a new booking by calling the backend API.
     */
    @PostMapping("/save")
    public String saveBooking(
            @ModelAttribute SpotBookingDTO spotBookingDTO,
            RedirectAttributes redirectAttributes) {
        try {
            SpotBookingInfo booking = new SpotBookingInfo();
            booking.setStartDate(spotBookingDTO.getStartDate());
            booking.setEndDate(spotBookingDTO.getEndDate());
            booking.setStartTime(spotBookingDTO.getStartTime());
            booking.setEndTime(spotBookingDTO.getEndTime());

            // Call Backend API to save the booking
            restTemplate.postForEntity(
                    BASE_URL + "/add/" + spotBookingDTO.getSpotId() + "/" + spotBookingDTO.getRegistrationNumber(),
                    booking,
                    SpotBookingInfo.class);
            redirectAttributes.addFlashAttribute("message", "✅ Booking created successfully!");
            return "redirect:/ui/booking/viewAll";
        } catch (HttpClientErrorException e) {
            String errorMessage = extractCleanErrorMessage(e.getResponseBodyAsString());
            redirectAttributes.addFlashAttribute("spotBookingDTO", spotBookingDTO); // Preserve form data
            if (errorMessage.contains("Spot is not available")) {
                redirectAttributes.addFlashAttribute("error",
                        "❌ Spot with ID " + spotBookingDTO.getSpotId() + " is not available. Please select another spot.");
            } else if (errorMessage.contains("Spot is already booked")) {
                redirectAttributes.addFlashAttribute("error",
                        "❌ Spot with ID " + spotBookingDTO.getSpotId() + " is already booked for the given time. Please choose a different time slot.");
            } else if (errorMessage.contains("Start date, start time, and end time must be provided")) {
                redirectAttributes.addFlashAttribute("error",
                        "❌ Start Date, Start Time, and End Time are required fields. Please fill them correctly.");
            } else if (errorMessage.contains("Start date cannot be in the past")) {
                redirectAttributes.addFlashAttribute("error",
                        "❌ Start Date cannot be in the past. Please select a future date.");
            } else if (errorMessage.contains("Vehicle with Registration Number")) {
                redirectAttributes.addFlashAttribute("error",
                        "❌ No vehicle found with Registration Number: " + spotBookingDTO.getRegistrationNumber() + ". Please check and try again.");
            } else if (errorMessage.contains("Spot with ID")) {
                redirectAttributes.addFlashAttribute("error",
                        "❌ Spot with ID " + spotBookingDTO.getSpotId() + " does not exist. Please enter a valid Spot ID.");
            } else {
                redirectAttributes.addFlashAttribute("error", "❌ " + errorMessage);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("spotBookingDTO", spotBookingDTO); // Preserve form data
            redirectAttributes.addFlashAttribute("error", "❌ Unexpected error: " + e.getMessage());
        }
        return "redirect:/ui/booking/add?spotId=" + spotBookingDTO.getSpotId();
    }

    /**
     * Displays all bookings with filtering options.
     */
    @GetMapping("/viewAll")
    public String viewAllBookings(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) Long spotId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model,
            HttpServletRequest request) {
        List<SpotBookingInfo> bookings = new ArrayList<>();
        String errorMessage = null;

        try {
            // Fetch the logged-in user's details
            ResponseDTO<UserInfo> userResponse = usersService.getUserDetails(request);
            if (userResponse == null || !userResponse.isSuccess() || userResponse.getData() == null) {
                model.addAttribute("error", "Unable to fetch user details. Please ensure you are logged in.");
                model.addAttribute("bookings", Collections.emptyList());
                return "bookings/viewAllBooking";
            }

            String contactNumber = userResponse.getData().getContactNumber();
            // if (contactNumber == null || !isValidContactNumber(contactNumber)) {
            //     model.addAttribute("error", "Invalid contact number for the logged-in user.");
            //     model.addAttribute("bookings", Collections.emptyList());
            //     return "bookings/viewAllBooking";
            // }

            model.addAttribute("userContactNumber", contactNumber);

            // Fetch bookings based on filters
            if (bookingId != null) {
                ResponseEntity<SpotBookingInfo> response = restTemplate.getForEntity(
                        BASE_URL + "/viewBookingById/" + bookingId, SpotBookingInfo.class);
                if (response.getBody() != null) {
                    bookings = Collections.singletonList(response.getBody());
                }
            } else if (spotId != null) {
                ResponseEntity<List<SpotBookingInfo>> response = restTemplate.exchange(
                        BASE_URL + "/viewBookingBySlotId/" + spotId, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<SpotBookingInfo>>() {});
                bookings = response.getBody() != null ? response.getBody() : new ArrayList<>();
            } else if (startDate != null && endDate != null) {
                ResponseEntity<List<SpotBookingInfo>> response = restTemplate.exchange(
                        BASE_URL + "/viewBetweenDates/" + startDate + "/" + endDate, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<SpotBookingInfo>>() {});
                bookings = response.getBody() != null ? response.getBody() : new ArrayList<>();
            } else {
                // // Default: Fetch bookings for the logged-in user's contact number
                // ResponseEntity<List<SpotBookingInfo>> response = restTemplate.exchange(
                //         BASE_URL + "/viewByContactNumber/" + contactNumber, HttpMethod.GET, null,
                //         new ParameterizedTypeReference<List<SpotBookingInfo>>() {});
                // bookings = response.getBody() != null ? response.getBody() : new ArrayList<>();
                bookings =usersService.getUserBookingHistory(request).getData();
            }

            if (bookings.isEmpty()) {
                errorMessage = "No bookings found for your contact number: " + contactNumber;
            }
            model.addAttribute("bookings", bookings);
            model.addAttribute("error", errorMessage);

        } catch (HttpClientErrorException e) {
            try {
                String responseBody = e.getResponseBodyAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String backendMessage = jsonNode.has("message") ? jsonNode.get("message").asText() : "No bookings found.";

                if (bookingId != null) {
                    errorMessage = "No bookings found for Booking ID: " + bookingId;
                } else if (spotId != null) {
                    errorMessage = "No bookings found for Slot ID: " + spotId;
                } else if (startDate != null && endDate != null) {
                    errorMessage = "No bookings found between " + startDate + " and " + endDate;
                } else {
                    errorMessage = backendMessage;
                }
            } catch (Exception ex) {
                errorMessage = "No bookings found.";
            }
            model.addAttribute("error", errorMessage);
            model.addAttribute("bookings", Collections.emptyList());
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred while fetching bookings: " + e.getMessage());
            model.addAttribute("bookings", Collections.emptyList());
        }

        return "bookings/viewAllBooking";
    }

    /**
     * Fetches a booking for cancellation.
     */
    @GetMapping("/fetchBooking")
    public String fetchBooking(@RequestParam Long bookingId, Model model) {
        try {
            ResponseEntity<SpotBookingInfo> response = restTemplate.getForEntity(
                    BASE_URL + "/viewBookingById/" + bookingId,
                    SpotBookingInfo.class);
            SpotBookingInfo booking = response.getBody();

            if (booking == null) {
                model.addAttribute("error", "Booking ID " + bookingId + " does not exist.");
                return "cancelBooking";
            }

            // Case-insensitive status check
            if (booking.getStatus() != null) {
                String status = booking.getStatus().toLowerCase();
                if (status.equals("cancelled")) {
                    model.addAttribute("error", "⚠ Cannot cancel booking; it is already cancelled.");
                } else if (status.equals("completed")) {
                    model.addAttribute("error", "⚠ Cannot cancel booking; it is already completed.");
                } else {
                    model.addAttribute("booking", booking);
                }
            } else {
                model.addAttribute("error", "⚠ Booking status is not available.");
            }
        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e.getResponseBodyAsString(), bookingId);
            model.addAttribute("error", errorMessage);
        } catch (Exception e) {
            model.addAttribute("error", "Booking ID " + bookingId + " does not exist.");
        }
        return "cancelBooking";
    }

    /**
     * Displays the cancel booking page.
     */
    @GetMapping("/cancel")
    public String showCancelBookingPage() {
        return "cancelBooking";
    }

    /**
     * Cancels a booking by ID.
     */
    @PostMapping("/cancelByBookingId")
    public String cancelBookingById(@RequestParam Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            // Fetch the booking to check its status
            ResponseEntity<SpotBookingInfo> response = restTemplate.getForEntity(
                    BASE_URL + "/viewBookingById/" + bookingId,
                    SpotBookingInfo.class);
            SpotBookingInfo booking = response.getBody();

            if (booking == null) {
                redirectAttributes.addFlashAttribute("error", "Booking ID " + bookingId + " does not exist.");
                return "redirect:/ui/booking/cancel";
            }

            // Case-insensitive status check
            if (booking.getStatus() != null) {
                String status = booking.getStatus().toLowerCase();
                if (status.equals("cancelled")) {
                    redirectAttributes.addFlashAttribute("error", "⚠ Cannot cancel booking; it is already cancelled.");
                    return "redirect:/ui/booking/cancel";
                } else if (status.equals("completed")) {
                    redirectAttributes.addFlashAttribute("error", "⚠ Cannot cancel booking; it is already completed.");
                    return "redirect:/ui/booking/cancel";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "⚠ Booking status is not available.");
                return "redirect:/ui/booking/cancel";
            }

            // Proceed with cancellation
            restTemplate.delete(BASE_URL + "/cancel/" + bookingId);
            redirectAttributes.addFlashAttribute("message", "Booking ID " + bookingId + " cancelled successfully!");
        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e.getResponseBodyAsString(), bookingId);
            redirectAttributes.addFlashAttribute("error", errorMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking ID " + bookingId + " does not exist.");
        }
        return "redirect:/ui/booking/cancel";
    }

    /**
     * Fetches a booking for updating.
     */
    @GetMapping("/update")
    public String fetchBookingForUpdate(@RequestParam(required = false) Long bookingId, Model model) {
        if (bookingId == null) {
            model.addAttribute("error", "⚠ Please enter a Booking ID!");
            return "bookings/updateBooking";
        }

        try {
            ResponseEntity<SpotBookingInfo> response = restTemplate.getForEntity(
                    BASE_URL + "/viewBookingById/" + bookingId,
                    SpotBookingInfo.class);
            SpotBookingInfo booking = response.getBody();

            if (booking == null) {
                model.addAttribute("error", "⚠ No booking found with the provided ID.");
                return "bookings/updateBooking";
            }

            // Case-insensitive status check
            if (booking.getStatus() != null) {
                String status = booking.getStatus().toLowerCase();
                if (status.equals("cancelled")) {
                    model.addAttribute("error", "⚠ Cannot update booking; it is already cancelled.");
                } else if (status.equals("completed")) {
                    model.addAttribute("error", "⚠ Cannot update booking; it is already completed.");
                } else {
                    model.addAttribute("booking", booking);
                }
            } else {
                model.addAttribute("error", "⚠ Booking status is not available.");
            }
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("error", "⚠ Booking not found! Please check the ID.");
        } catch (Exception e) {
            model.addAttribute("error", "❌ Booking ID " + bookingId+" does not exist" );
        }
        return "bookings/updateBooking";
    }

    /**
     * Updates a booking's end date and time.
     */
   @PostMapping("/update")
public String updateBooking(
        @RequestParam Long bookingId,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate newEndDate,
        @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime newEndTime,
        RedirectAttributes redirectAttributes) {
    try {
        if (bookingId == null) {
            redirectAttributes.addFlashAttribute("error", "⚠ Booking ID is required!");
            return "redirect:/ui/booking/update";
        }

        // Fetch existing booking details
        ResponseEntity<SpotBookingInfo> response = restTemplate.getForEntity(
                BASE_URL + "/viewBookingById/" + bookingId,
                SpotBookingInfo.class);
        SpotBookingInfo booking = response.getBody();

        // Check for 400 error and parse the message
        if (response.getStatusCode().value() == 400 && booking == null) {
            String errorMessage = extractMessage(response.getBody() != null ? response.toString() : "");
            if (errorMessage.contains("Booking does not exist")) {
                redirectAttributes.addFlashAttribute("error", "⚠ Booking not found. Please verify the booking ID and try again.");
            } else {
                redirectAttributes.addFlashAttribute("error", "❌ " + errorMessage);
            }
            return "redirect:/ui/booking/update?bookingId=" + bookingId;
        }

        if (booking == null) {
            redirectAttributes.addFlashAttribute("error", "⚠ Booking not found!");
            return "redirect:/ui/booking/update?bookingId=" + bookingId;
        }

        // Case-insensitive status check
        if (booking.getStatus() != null) {
            String status = booking.getStatus().toLowerCase();
            if (status.equals("cancelled")) {
                redirectAttributes.addFlashAttribute("error", "⚠ Cannot update booking; it is already cancelled.");
                return "redirect:/ui/booking/update?bookingId=" + bookingId;
            } else if (status.equals("completed")) {
                redirectAttributes.addFlashAttribute("error", "⚠ Cannot update booking; it is already completed.");
                return "redirect:/ui/booking/update?bookingId=" + bookingId;
            }
        }

        LocalDate startDate = booking.getStartDate();
        LocalTime startTime = booking.getStartTime();
        if (startDate == null || startTime == null) {
            redirectAttributes.addFlashAttribute("error", "⚠ Start date or time is missing for the booking.");
            return "redirect:/ui/booking/update?bookingId=" + bookingId;
        }

        // Validate: End Date & Time must be after Start Date & Time
        if (newEndDate == null || newEndTime == null) {
            redirectAttributes.addFlashAttribute("error", "⚠ New End Date and Time are required!");
            return "redirect:/ui/booking/update?bookingId=" + bookingId;
        }

        LocalDateTime startDateTime = startDate.atTime(startTime);
        LocalDateTime endDateTime = newEndDate.atTime(newEndTime);
        if (!startDateTime.isBefore(endDateTime)) {
            redirectAttributes.addFlashAttribute("error", "⚠ End date & time must be after start date & time. " +
                    "Start: " + startDateTime + ", End: " + endDateTime);
            return "redirect:/ui/booking/update?bookingId=" + bookingId;
        }

        // Prepare update request
        booking.setEndDate(newEndDate);
        booking.setEndTime(newEndTime);

        // Check for conflicts before updating
        try {
            ResponseEntity<String> conflictCheckResponse = restTemplate.postForEntity(
                    BASE_URL + "/check-update-conflict/" + bookingId,
                    booking,
                    String.class);

            if (conflictCheckResponse.getStatusCode() == HttpStatus.OK) {
                restTemplate.put(BASE_URL + "/update/" + bookingId, booking);
                redirectAttributes.addFlashAttribute("message", "✅ Booking updated successfully!");
            } else {
                String errorMessage = extractMessage(conflictCheckResponse.getBody());
                redirectAttributes.addFlashAttribute("error", "❌ " + errorMessage);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                String errorMessage = extractMessage(e.getResponseBodyAsString());
                redirectAttributes.addFlashAttribute("error", "❌ " + errorMessage);
            } else {
                redirectAttributes.addFlashAttribute("error", "❌ Unexpected error: " + e.getResponseBodyAsString());
            }
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "❌ Failed to update booking: " + e.getMessage());
    }
    return "redirect:/ui/booking/update?bookingId=" + bookingId;


}

    /**
     * Displays the form to view cancelled bookings, automatically fetching the user's contact number.
     */
    @GetMapping("/viewCancelledBookingForm")
    public String showViewCancelledBookingForm(Model model, HttpServletRequest request) {
        // Fetch the logged-in user's details
        ResponseDTO<UserInfo> userResponse = usersService.getUserDetails(request);
        if (userResponse == null || !userResponse.isSuccess() || userResponse.getData() == null) {
            model.addAttribute("error", "Unable to fetch user details. Please ensure you are logged in.");
            return "viewCancelledBookingForm";
        }

        UserInfo user = userResponse.getData();
        String contactNumber = user.getContactNumber();
        if (contactNumber == null || !isValidContactNumber(contactNumber)) {
            model.addAttribute("error", "Invalid contact number for the logged-in user.");
            return "viewCancelledBookingForm";
        }

        // Pass the contact number to the model
        model.addAttribute("contactNumber", contactNumber);
        // Directly fetch cancelled bookings for the contact number
        return viewMyCancelledBookings(contactNumber, model);
    }

    /**
     * Displays the cancelled bookings for the given contact number.
     */
    @GetMapping("/cancelledBookings")
    public String viewMyCancelledBookings(@RequestParam(required = false) String contactNumber, Model model) {
        if (contactNumber == null || contactNumber.trim().isEmpty() || !isValidContactNumber(contactNumber)) {
            model.addAttribute("error", "Valid contact number is required to view cancelled bookings.");
            return "viewCancelledBookings";
        }

        try {
            // Call backend API to fetch cancelled bookings
            ResponseEntity<List<SpotBookingInfo>> response = restTemplate.exchange(
                    BASE_URL + "/getCancelledBookingByContactNumber/" + contactNumber,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<SpotBookingInfo>>() {});

            List<SpotBookingInfo> cancelledBookings = response.getBody();
            if (cancelledBookings == null || cancelledBookings.isEmpty()) {
                model.addAttribute("error", "No cancelled bookings found for contact number: " + contactNumber);
            }
            model.addAttribute("cancelledBookings", cancelledBookings != null ? cancelledBookings : new ArrayList<>());
            model.addAttribute("contactNumber", contactNumber);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "No cancelled bookings found");
            model.addAttribute("cancelledBookings", Collections.emptyList());
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("cancelledBookings", Collections.emptyList());
        }

        return "viewCancelledBookings";
    }

    // Helper Methods

    /**
     * Validates a contact number (10 digits, does not start with 0).
     */
    private boolean isValidContactNumber(String contactNumber) {
        return contactNumber != null && contactNumber.matches("\\d{10}") && !contactNumber.startsWith("0");
    }

    /**
     * Extracts a clean error message from the backend response.
     */
    private String extractCleanErrorMessage(String errorResponse) {
        return errorResponse.replaceAll("[{}\"]", "").replaceAll("message:", "").trim();
    }

    /**
     * Extracts an error message for booking-related errors.
     */
    private String extractErrorMessage(String response, Long bookingId) {
        if (response.contains("not found") || response.contains("does not exist")) {
            return "Booking ID " + bookingId + " does not exist.";
        } else {
            return "Failed to cancel booking.";
        }
    }

    /**
     * Extracts a message from a JSON response body.
     */
    // Helper method to extract message from response
    private String extractMessage(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.has("message") ? jsonNode.get("message").asText() : "An unexpected error occurred.";
        } catch (Exception e) {
            return response.contains("Booking does not exist") ? "Booking not found." : "Booking conflict occured , please try other time slot";
        }
    }
}