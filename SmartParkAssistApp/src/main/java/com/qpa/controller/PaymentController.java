package com.qpa.controller;

import com.qpa.entity.Payment;
import com.qpa.entity.SpotBookingInfo;
import com.qpa.repository.SpotBookingInfoRepository;
import com.qpa.service.PayEmailService;
import com.qpa.service.PaymentService;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.qpa.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PayEmailService emailService;

    @Autowired
    private SpotBookingInfoRepository spotBookingInfoRepository;//03/04

    @PostMapping("/process")
    public Payment processPayment(@RequestBody Payment payment) {
        Payment processedPayment = paymentService.processPayment(payment.getBookingId(), payment.getUserEmail(),
                payment.getTotalAmount());
                //03/04
        if (processedPayment != null && "SUCCESS".equals(processedPayment.getPaymentStatus())) {
            // Update the SpotBookingInfo paymentStatus to true
            SpotBookingInfo booking = spotBookingInfoRepository.findById(
                Long.valueOf(processedPayment.getBookingId())
            ).orElse(null);
            if (booking != null) {
                booking.setPaymentStatus(true); // Mark as paid
                spotBookingInfoRepository.save(booking); // Save the updated booking
            }
        }
        //03/04
        try {
            emailService.sendReceipt(processedPayment.getUserEmail(), processedPayment.getOrderId(),
                    processedPayment.getBookingId(), processedPayment.getTotalAmount());
        } catch (MessagingException e) {
            System.out.println("Email failed: " + e.getMessage());
        }
        return processedPayment;
    }

    @GetMapping("/history")
    public List<Payment> getPaymentHistory(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        if (start != null && end != null) {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            return paymentService.getPaymentsBetweenDates(startDate, endDate);
        }
        return paymentService.getAllPayments();
    }

    @GetMapping("/getAllAdminPayments")
    public List<Payment> getAdminPayments(HttpServletRequest request) {
        Long userId = authService.getUserId(request);
        return paymentService.getAllPaymentsByAdmin(userId);
    }

    @GetMapping("/viewByBookingId/{bookingId}")
    public ResponseEntity<Payment> getMethodName(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentByBookingId(bookingId));
    }

}
