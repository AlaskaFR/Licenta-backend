package com.example.licenta_backend2.controller;

import com.example.licenta_backend2.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPayment(@RequestParam BigDecimal amount) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(amount);
            Map<String, String> responseData = Map.of(
                    "clientSecret", paymentIntent.getClientSecret()
            );
            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentIntent> confirmPayment(@RequestParam String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = paymentService.confirmPayment(paymentIntentId);
            return ResponseEntity.ok(paymentIntent);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<PaymentIntent> cancelPayment(@RequestParam String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = paymentService.cancelPayment(paymentIntentId);
            return ResponseEntity.ok(paymentIntent);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
