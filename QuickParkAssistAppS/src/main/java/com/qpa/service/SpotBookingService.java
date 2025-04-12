package com.qpa.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.qpa.dto.SpotResponseDTO;
import com.qpa.entity.PriceType;
import com.qpa.entity.Spot;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.SpotStatus;
import com.qpa.entity.Vehicle;
import com.qpa.entity.VehicleType;
import com.qpa.exception.InvalidEntityException;
import com.qpa.repository.SpotBookingInfoRepository;
import com.qpa.repository.SpotRepository;
import com.qpa.repository.VehicleRepository;

import jakarta.transaction.Transactional;

@Service
public class SpotBookingService {

    @Autowired
    private SpotBookingInfoRepository spotBookingRepository;

    @Lazy
    @Autowired
    private SpotService spotService;

    @Autowired
    private SpotRepository spotInfoRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public SpotBookingInfo addBooking(long spotId, String registrationNumber, SpotBookingInfo bookingInfo)
            throws InvalidEntityException {
        // Validate bookingInfo dates and times
        if (bookingInfo.getStartDate() == null || bookingInfo.getStartTime() == null
                || bookingInfo.getEndTime() == null) {
            throw new InvalidEntityException("Start date, start time, and end time must be provided.");
        }
        if (bookingInfo.getEndDate() == null) {
            throw new InvalidEntityException("End date must be provided.");
        }

        // Validate that startDate is on or before endDate
        if (bookingInfo.getStartDate().isAfter(bookingInfo.getEndDate())) {
            throw new InvalidEntityException("Start date (" + bookingInfo.getStartDate() +
                    ") must be on or before end date (" + bookingInfo.getEndDate() + ").");
        }

        // Validate full date-time consistency
        LocalDateTime startDateTime = bookingInfo.getStartDate().atTime(bookingInfo.getStartTime());
        LocalDateTime endDateTime = bookingInfo.getEndDate().atTime(bookingInfo.getEndTime());
        if (!startDateTime.isBefore(endDateTime)) {
            throw new InvalidEntityException("Booking start (" + startDateTime +
                    ") must be before booking end (" + endDateTime + ").");
        }

        // Check for conflicting booking (excluding cancelled/completed)
        List<SpotBookingInfo> conflictingBooking = spotBookingRepository.findConflictingBooking(
                spotId, bookingInfo.getStartDate(), bookingInfo.getStartTime(),
                bookingInfo.getEndDate(), bookingInfo.getEndTime());
        if (!conflictingBooking.isEmpty()) {
            SpotBookingInfo conflict = conflictingBooking.get(0);
            throw new InvalidEntityException(
                    "Spot is already booked from " + conflict.getStartTime() + " to " + conflict.getEndTime() +
                            " on " + conflict.getStartDate());
        }

        // Retrieve SpotInfo
        Spot spot = spotInfoRepository.findById(spotId)
                .orElseThrow(() -> new InvalidEntityException("Spot with ID " + spotId + " does not exist."));

        // Check spot status
        SpotStatus spotStatus = spot.getStatus();
        System.out.println("spot status: " + spotStatus);
        if (spotStatus == null || spotStatus != SpotStatus.AVAILABLE) {
            throw new InvalidEntityException(
                    "Spot with ID " + spotId + " is not available for booking. Current status: " +
                            (spotStatus != null ? spotStatus.name() : "null"));
        }

        // Retrieve Vehicle
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber(registrationNumber);
        if (vehicle == null) {
            throw new InvalidEntityException(
                    "Vehicle with Registration Number " + registrationNumber + " does not exist.");
        }

        // Validate vehicle type
        VehicleType vehicleType = vehicle.getVehicleType();
        if (vehicleType == null) {
            throw new InvalidEntityException(
                    "Vehicle with Registration Number " + registrationNumber + " does not have a valid type.");
        }

        // Set default booking date if not provided
        if (bookingInfo.getBookingDate() == null) {
            bookingInfo.setBookingDate(LocalDate.now());
        }

        // Associate spot and vehicle with booking
        bookingInfo.setSpotInfo(spot);
        bookingInfo.setVehicle(vehicle);

        double totalAmount = 0;

        Duration duration = calculateParkingHours(bookingInfo.getStartDate(), bookingInfo.getStartTime(),
                bookingInfo.getEndDate(), bookingInfo.getEndTime());

        // Get total hours
        long totalHours = duration.toHours();

        // Round off the days
        long roundedDays = Math.round((double) totalHours / 24);

        long roundedHours = Math.round(totalHours);

        if (spot.getPriceType() == PriceType.HOURLY) {
            totalAmount = roundedHours * spot.getPrice();
        } else if (spot.getPriceType() == PriceType.DAILY) {
            totalAmount = roundedDays * spot.getPrice();
        }

        bookingInfo.setTotalAmount(totalAmount);

        // Compare startdate and starttime with current date and current time to set
        // booking status as either confirmed or booked
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Compare startDate with currentDate and startTime with currentTime
        if (bookingInfo.getStartDate().isEqual(currentDate) &&
                (bookingInfo.getStartTime().isBefore(currentTime) || bookingInfo.getStartTime().equals(currentTime))) {
            bookingInfo.setStatus("confirmed");
        } else {
            bookingInfo.setStatus("booked");
        }

        // Update spot status to UNAVAILABLE
        // spot.setStatus(SpotStatus.UNAVAILABLE);
        // spotInfoRepository.save(spot);

        // Save and return the booking
        SpotBookingInfo savedBooking = spotBookingRepository.save(bookingInfo);

        try {
            // Assuming Vehicle has a getUser() method that returns a User object with
            // getEmail()
            String userEmail = savedBooking.getVehicle().getUserObj().getEmail();
            String subject = "Booking Confirmation - Spot ID: " + spotId;
            String body = "Dear User,\n\n" +
                    "Your booking for spot " + spotId + " has been confirmed.\n" +
                    "Booking Details:\n" +
                    "Start: " + savedBooking.getStartDate() + " " + savedBooking.getStartTime() + "\n" +
                    "End: " + savedBooking.getEndDate() + " " + savedBooking.getEndTime() + "\n" +
                    "Vehicle: " + registrationNumber + "\n" +
                    "Thank you for using our service!";
            emailService.sendSimpleMail(userEmail, subject, body);
        } catch (Exception e) {
            // Log the error but don’t throw to avoid rolling back the transaction
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
        return savedBooking;
    }

    // Get all bookings
    public List<SpotBookingInfo> findAllBookingInfos() {
        return spotBookingRepository.findAll();
    }

    // Get booking by ID
    public SpotBookingInfo findBookingById(long bookingId) throws InvalidEntityException {
        return spotBookingRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidEntityException("Booking does not exist with ID: " + bookingId));
    }

    // Get bookings by vehicle ID
    public List<SpotBookingInfo> getBookingsByVehicleId(long vehicleId) throws InvalidEntityException {
        List<SpotBookingInfo> bookings = spotBookingRepository.findByVehicle_VehicleId(vehicleId);
        if (bookings.isEmpty()) {
            throw new InvalidEntityException("No bookings found for vehicle ID: " + vehicleId);
        }
        return bookings;
    }

    // Get bookings by spot ID
    public List<SpotBookingInfo> getBookingsBySlotId(long slotId) throws InvalidEntityException {
        List<SpotBookingInfo> bookings = spotBookingRepository.findBySpotInfo_SpotId(slotId);
        if (bookings.isEmpty()) {
            throw new InvalidEntityException("No bookings found for Slot ID: " + slotId);
        }
        return bookings;
    }

    public List<SpotBookingInfo> getBookingsByContactNumber(String contactNumber) throws InvalidEntityException {
        List<SpotBookingInfo> bookings = spotBookingRepository.findByVehicle_UserObj_ContactNumber(contactNumber);
        if (bookings.isEmpty()) {
            throw new InvalidEntityException("No bookings found for contact number: " + contactNumber);
        }
        return bookings;
    }

    public List<SpotBookingInfo> getBookingsBetweenDates(LocalDate startDate, LocalDate endDate)
            throws InvalidEntityException {
        // Validate date range
        if (startDate == null || endDate == null) {
            throw new InvalidEntityException("Start date and end date must not be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidEntityException("Start date must be before or equal to end date.");
        }

        List<SpotBookingInfo> bookings = spotBookingRepository.findByStartDateBetween(startDate, endDate);
        if (bookings.isEmpty()) {
            throw new InvalidEntityException("No bookings found between " + startDate + " and " + endDate);
        }
        return bookings;
    }

    public List<SpotBookingInfo> getCancelledBookingsByContactNumber(String contactNumber)
            throws InvalidEntityException {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new InvalidEntityException("Contact number cannot be null or empty.");
        }

        List<SpotBookingInfo> cancelledBookings = spotBookingRepository
                .findCancelledBookingsByContactNumber(contactNumber);

        if (cancelledBookings.isEmpty()) {
            throw new InvalidEntityException("No cancelled bookings found for contact number: " + contactNumber);
        }

        return cancelledBookings;
    }

    // Update Booking
    @Transactional
    public SpotBookingInfo updateBooking(long bookingId, SpotBookingInfo newBooking) throws InvalidEntityException {
        // Fetch existing booking
        SpotBookingInfo existingBooking = spotBookingRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidEntityException("Booking not found with ID: " + bookingId));

        // Validate and update Booking Date (cannot be in the past)
        if (newBooking.getBookingDate() != null) {
            if (!newBooking.getBookingDate().isBefore(LocalDate.now())) {
                existingBooking.setBookingDate(newBooking.getBookingDate());
            } else {
                throw new InvalidEntityException("Booking date cannot be in the past.");
            }
        }

        // Validate and update Start Time (if new time is provided)
        if (newBooking.getStartTime() != null) {
            existingBooking.setStartTime(newBooking.getStartTime());
        }

        // Validate and update End Date
        if (newBooking.getEndDate() != null) {
            existingBooking.setEndDate(newBooking.getEndDate());
        }

        // Validate and update End Time
        if (newBooking.getEndTime() != null) {
            existingBooking.setEndTime(newBooking.getEndTime());
        }

        // **Logical Validation: Ensure End Date & Time is after Start Date & Time**
        LocalDate startDate = existingBooking.getStartDate(); // Use getStartDate() instead of getBookingDate()
        LocalTime startTime = existingBooking.getStartTime();
        LocalDate endDate = existingBooking.getEndDate();
        LocalTime endTime = existingBooking.getEndTime();

        if (startDate == null || startTime == null || endDate == null || endTime == null) {
            throw new InvalidEntityException("Start date, start time, end date, and end time must not be null.");
        }

        if (endDate.isBefore(startDate) || (endDate.equals(startDate) && endTime.isBefore(startTime))) {
            throw new InvalidEntityException("End date & time must be after start date & time.");
        }

        // **Conflict Check: Check for overlapping bookings for the same spot**
        long spotId = existingBooking.getSpotInfo().getSpotId(); // Get the spot ID from the existing booking
        List<SpotBookingInfo> conflictingBookings = spotBookingRepository.findConflictingBookingsforUpdatedbookings(
                spotId, startDate, startTime, endDate, endTime, bookingId); // Exclude current bookingId

        if (!conflictingBookings.isEmpty()) {
            SpotBookingInfo conflict = conflictingBookings.get(0); // Take the first conflicting booking for the error
                                                                   // message
            throw new InvalidEntityException(
                    "The updated booking conflicts with an existing booking for spot " + spotId + ". " +
                            "Conflict details: Start: " + conflict.getStartDate() + " " + conflict.getStartTime() +
                            ", End: " + conflict.getEndDate() + " " + conflict.getEndTime());
        }

        // Update booking status if provided
        if (newBooking.getStatus() != null) {
            existingBooking.setStatus(newBooking.getStatus());
        }

        // Save and return the updated booking
        return spotBookingRepository.save(existingBooking);
    }

    // Cancel Booking
    @Transactional
    public void cancelBooking(long bookingId) throws InvalidEntityException {
        // Step 1: Retrieve the booking
        SpotBookingInfo booking = spotBookingRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidEntityException("Booking not found with ID: " + bookingId));

        // Step 2: Check if the startDate is null
        if (booking.getStartDate() == null) {
            throw new InvalidEntityException("Booking with ID " + bookingId + " has no start date.");
        }

        // Step 3: Check if the startDate is before today
        if (booking.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidEntityException(
                    "Cannot cancel a booking that has already started. Booking ID: " + bookingId);
        }

        // Step 4: Retrieve the associated spot
        Spot spot = booking.getSpotInfo();
        if (spot == null) {
            throw new InvalidEntityException("No spot associated with booking ID: " + bookingId);
        }
        try {
            // Retrieve the user's email (adjust based on your entity relationships)
            String userEmail = booking.getVehicle().getUserObj().getEmail(); // Example path, modify as needed
            String subject = "Booking Cancellation - Spot ID: " + spot.getSpotId();
            String body = "Dear User,\n\n" +
                    "Your booking for spot " + spot.getSpotId() + " has been canceled.\n" +
                    "Booking Details:\n" +
                    "Start: " + booking.getStartDate() + " " + booking.getStartTime() + "\n" +
                    "End: " + booking.getEndDate() + " " + booking.getEndTime() + "\n" +
                    "Status: CANCELED\n\n" +
                    "We hope to serve you again!";
            emailService.sendSimpleMail(userEmail, subject, body);
        } catch (Exception e) {
            // Log the error but proceed with cancellation
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }

        // Step 5: Update the spot status to AVAILABLE
        spot.setStatus(SpotStatus.AVAILABLE);
        spotInfoRepository.save(spot);
        booking.setStatus("cancelled");
        // Step 6: Delete only the booking details
        spotBookingRepository.save(booking);

    }

    @Transactional

    @Scheduled(fixedRate = 1 * 60 * 1000) // Runs every 15 minutes (made for 2 min for testing purpose)

    public void updateBookingStatusesToConfirm() {

        LocalDate currentDate = LocalDate.now();

        LocalTime currentTime = LocalTime.now();

        // Confirm bookings that have started

        List<SpotBookingInfo> toConfirm = spotBookingRepository.findBookingsToConfirm(currentDate, currentTime);

        for (SpotBookingInfo booking : toConfirm) {

            booking.setStatus("confirmed");

            Spot spot = booking.getSpotInfo();

            if (spot != null) {

                spot.setStatus(SpotStatus.UNAVAILABLE);

                spotInfoRepository.save(spot);

            }

        }

        spotBookingRepository.saveAll(toConfirm);

    }

    @Transactional

    @Scheduled(fixedRate = 1 * 60 * 1000) // Runs every 15 minutes

    public void updateBookingStatusesToComplete() {

        LocalDate currentDate = LocalDate.now();

        LocalTime currentTime = LocalTime.now();

        // Complete bookings that have ended

        List<SpotBookingInfo> toComplete = spotBookingRepository.findBookingsToComplete(currentDate, currentTime);

        for (SpotBookingInfo booking : toComplete) {

            booking.setStatus("completed");

            Spot spot = booking.getSpotInfo();

            if (spot != null) {

                spot.setStatus(SpotStatus.AVAILABLE);

                spotInfoRepository.save(spot);

            }

        }

        spotBookingRepository.saveAll(toComplete);

    }

    public static Duration calculateParkingHours(LocalDate startDate, LocalTime startTime,

            LocalDate endDate, LocalTime endTime) {

        // Combine date and time into LocalDateTime

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        // Calculate the duration

        Duration duration = Duration.between(startDateTime, endDateTime);

        // Get total hours

        return duration;

    }

    public List<SpotBookingInfo> getAllAdminBookings(Long userId) throws InvalidEntityException {
        List<SpotResponseDTO> ownerSpots = spotService.getSpotByOwner(userId);

        List<SpotBookingInfo> allBookings = new ArrayList<>();

        for (SpotResponseDTO spot : ownerSpots) {
            try {
                allBookings.addAll(getBookingsBySlotId(spot.getSpotId()));
            } catch (InvalidEntityException e) {
            }
        }

        return allBookings;
    }

}