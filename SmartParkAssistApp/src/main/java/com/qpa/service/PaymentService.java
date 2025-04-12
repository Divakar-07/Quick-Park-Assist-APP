package com.qpa.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.qpa.dto.SpotResponseDTO;
import com.qpa.entity.Payment;
import com.qpa.exception.InvalidEntityException;
import com.qpa.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Lazy
    @Autowired
    private SpotService spotService;

    
    @Autowired
    private SpotBookingService spotBookingService;

    public Payment processPayment(Long bookingId, String userEmail, Double totalAmount) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        Payment payment = new Payment(bookingId, userEmail, totalAmount, orderId, "SUCCESS");
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsBetweenDates(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return paymentRepository.findPaymentsBetweenDates(startDateTime, endDateTime);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getAllPaymentsByAdmin(Long userId) {
        List<SpotResponseDTO> ownerSpots = spotService.getSpotByOwner(userId);

        return ownerSpots.stream()
                .flatMap(spot -> {
                    try {
                        return spotBookingService.getBookingsBySlotId(spot.getSpotId()).stream();
                    } catch (InvalidEntityException e) {
                        return Stream.empty(); // Return an empty stream if an exception occurs
                    }
                })
                .map(booking -> paymentRepository.findByBookingId(booking.getBookingId())) // Fetch payments once
                .filter(Objects::nonNull) // Ensure we only process non-null payments
                .collect(Collectors.toList());
    }

    public Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

}
