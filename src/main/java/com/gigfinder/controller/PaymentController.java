package com.gigfinder.controller;

import com.gigfinder.dto.PaymentRequestDTO;
import com.gigfinder.dto.PaymentResponseDTO;
import com.gigfinder.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequest) {
        try {
            PaymentResponseDTO payment = paymentService.createPayment(paymentRequest);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating payment: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/process")
    public ResponseEntity<?> processPayment(@PathVariable Long id) {
        try {
            PaymentResponseDTO payment = paymentService.processPayment(id);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing payment: " + e.getMessage());
        }
    }
    
    @GetMapping("/client")
    public ResponseEntity<?> getClientPayments() {
        try {
            List<PaymentResponseDTO> payments = paymentService.getClientPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching client payments: " + e.getMessage());
        }
    }
    
    @GetMapping("/worker")
    public ResponseEntity<?> getWorkerPayments() {
        try {
            List<PaymentResponseDTO> payments = paymentService.getWorkerPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching worker payments: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            PaymentResponseDTO payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching payment: " + e.getMessage());
        }
    }
}
