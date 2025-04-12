package com.qpa.controller;


import com.qpa.entity.PaymentUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

// import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class PaymentUIController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BACKEND_URL = "http://localhost:7212/api/payments";

    @GetMapping("/payment")
public String paymentPage(@RequestParam String bookingId, @RequestParam Double amount, Model model) {
    model.addAttribute("bookingId", bookingId);
    model.addAttribute("totalAmount", amount);
    return "bookings/payment";
}

    @SuppressWarnings("null")//warning removed  01/04
    @PostMapping("/processPayment")
    public String processPayment(
            @RequestParam String bookingId,
            @RequestParam String userEmail,
            @RequestParam Double totalAmount,
            Model model) {
        PaymentUI payment = new PaymentUI(bookingId, userEmail, totalAmount, null, "PENDING");
        PaymentUI response = restTemplate.postForObject(BACKEND_URL + "/process", payment, PaymentUI.class);

        model.addAttribute("orderId", response.getOrderId());
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("userEmail", userEmail);
        return "bookings/success";
    }

    @GetMapping("/history")
    public String getPaymentHistory(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            Model model) {
                if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
                    model.addAttribute("errorMessage", "Please select both 'From' and 'To' dates before filtering.");
                    model.addAttribute("payments", List.of()); // Empty list to prevent errors
                    return "bookings/history"; // Return the template with an error message
                }
            
                String url = BACKEND_URL + "/history?start=" + start + "&end=" + end;
                PaymentUI[] payments = restTemplate.getForObject(url, PaymentUI[].class);
                List<PaymentUI> paymentList = payments != null ? Arrays.asList(payments) : List.of();
                
                model.addAttribute("payments", paymentList);
                return "bookings/history";
    }
}
