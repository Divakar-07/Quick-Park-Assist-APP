package com.qpa.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.entity.SpotBookingInfo;
import com.qpa.exception.InvalidEntityException;
import com.qpa.repository.SpotBookingInfoRepository;
import com.qpa.service.SpotBookingService;

@RestController
@RequestMapping("/api/bookSlot")
public class SpotBookingController {

    @Autowired
    SpotBookingService spotBookingService;

    @Autowired
    private SpotBookingInfoRepository spotBookingRepository;

    @PostMapping("/add/{slotId}/{registrationNumber}")
    public ResponseEntity<SpotBookingInfo> addBooking(
            @PathVariable("slotId") long slotId,
            @PathVariable("registrationNumber") String registrationNumber,
            @RequestBody SpotBookingInfo bookingInfo) throws InvalidEntityException {
        // Basic validation for required fields
        if (bookingInfo.getStartDate() == null || bookingInfo.getEndDate() == null) {
            throw new InvalidEntityException("Start date and end date are required for booking.");
        }
        SpotBookingInfo savedBooking = spotBookingService.addBooking(slotId, registrationNumber, bookingInfo);
        return ResponseEntity.ok(savedBooking);
    }

    @GetMapping("/viewAllBookings")
    public ResponseEntity<List<SpotBookingInfo>> viewAllBookingInfos() {
        List<SpotBookingInfo> bookings = spotBookingService.findAllBookingInfos();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/viewBookingById/{bookingId}")
    public ResponseEntity<SpotBookingInfo> viewBookingById(@PathVariable Long bookingId) throws InvalidEntityException {
        SpotBookingInfo bookingInfo = spotBookingService.findBookingById(bookingId);
        return ResponseEntity.ok(bookingInfo);
    }

    @GetMapping("/viewBookingByVehicleId/{vehicleId}")
    public List<SpotBookingInfo> viewBookingByVehicleId(@PathVariable int vehicleId) throws InvalidEntityException {
        return spotBookingService.getBookingsByVehicleId(vehicleId);
    }

    @GetMapping("/viewBookingBySlotId/{spotId}")
    public List<SpotBookingInfo> getBookingsBySpotId(@PathVariable long spotId) throws InvalidEntityException {
        return spotBookingService.getBookingsBySlotId(spotId);
    }

    @GetMapping("/viewByContactNumber/{contactNumber}")
    public ResponseEntity<List<SpotBookingInfo>> viewBookingsByContactNumber(@PathVariable String contactNumber)
            throws InvalidEntityException {
        List<SpotBookingInfo> bookings = spotBookingService.getBookingsByContactNumber(contactNumber);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/viewBetweenDates/{startDate}/{endDate}")
    public ResponseEntity<List<SpotBookingInfo>> viewBookingsBetweenDates(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
            throws InvalidEntityException {
        List<SpotBookingInfo> bookings = spotBookingService.getBookingsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/getCancelledBookingByContactNumber/{contactNumber}")
    public ResponseEntity<List<SpotBookingInfo>> getCancelledBookings(
            @PathVariable("contactNumber") String contactNumber) throws InvalidEntityException {

        List<SpotBookingInfo> cancelledBookings = spotBookingService.getCancelledBookingsByContactNumber(contactNumber);
        return ResponseEntity.ok(cancelledBookings);

    }

    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable long bookingId) throws InvalidEntityException {
        spotBookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @PutMapping("/update/{bookingId}")
    public ResponseEntity<SpotBookingInfo> updateBooking(@PathVariable long bookingId,
            @RequestBody SpotBookingInfo newBooking) throws InvalidEntityException {
        SpotBookingInfo updatedBooking = spotBookingService.updateBooking(bookingId, newBooking);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/check-update-conflict/{bookingId}")
    public ResponseEntity<String> checkUpdateConflict(@PathVariable long bookingId,
            @RequestBody SpotBookingInfo updatedBooking) {
        try {
            long spotId = updatedBooking.getSpotInfo().getSpotId();
            List<SpotBookingInfo> conflictingBookings = spotBookingRepository.findConflictingBookingsforUpdatedbookings(
                    spotId,
                    updatedBooking.getStartDate(),
                    updatedBooking.getStartTime(),
                    updatedBooking.getEndDate(),
                    updatedBooking.getEndTime(),
                    bookingId);

            if (!conflictingBookings.isEmpty()) {
                SpotBookingInfo conflict = conflictingBookings.get(0);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        "The updated booking conflicts with an existing booking for spot " + spotId + ". " +
                                "Conflict details: Start: " + conflict.getStartDate() + " " + conflict.getStartTime() +
                                ", End: " + conflict.getEndDate() + " " + conflict.getEndTime());
            }
            return ResponseEntity.ok("No conflict");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking conflicts.");
        }
    }

    @GetMapping("/admin/{userId}")
    public ResponseEntity<List<SpotBookingInfo>> getAllAdminBookings(@PathVariable Long userId)
            throws InvalidEntityException {
        return ResponseEntity.ok(spotBookingService.getAllAdminBookings(userId));
    }
}