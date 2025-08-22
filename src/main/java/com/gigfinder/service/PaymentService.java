package com.gigfinder.service;

import com.gigfinder.dto.PaymentRequestDTO;
import com.gigfinder.dto.PaymentResponseDTO;
import com.gigfinder.model.Job;
import com.gigfinder.model.Payment;
import com.gigfinder.model.User;
import com.gigfinder.model.enums.PaymentStatus;
import com.gigfinder.repository.JobRepository;
import com.gigfinder.repository.PaymentRepository;
import com.gigfinder.repository.JobAssignmentRepository;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final JobRepository jobRepository;
    private final JobAssignmentRepository jobAssignmentRepository;
    private final UserRepository userRepository;
    
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequest) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Job job = jobRepository.findById(paymentRequest.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Verify the current user is the client of this job
        if (!job.getClient().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to make payment for this job");
        }
        
        // Check if payment already exists for this job
        Optional<Payment> existingPayment = paymentRepository.findByJobId(job.getId());
        if (existingPayment.isPresent()) {
            throw new RuntimeException("Payment already exists for this job");
        }
        
        // Get the assigned worker
        var assignment = jobAssignmentRepository.findByJob(job)
                .orElseThrow(() -> new RuntimeException("No worker assigned to this job"));
        
        Payment payment = Payment.builder()
                .job(job)
                .client(currentUser)
                .worker(assignment.getWorker().getUser())
                .amount(paymentRequest.getAmount())
                .status(PaymentStatus.PENDING)
                .paymentMethod(paymentRequest.getPaymentMethod())
                .transactionId(generateTransactionId())
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        return convertToPaymentResponseDTO(savedPayment);
    }
    
    public PaymentResponseDTO processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Simulate payment processing
        try {
            // In a real application, this would integrate with a payment gateway
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);
            
            // Simulate successful payment
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);
            
            return convertToPaymentResponseDTO(savedPayment);
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }
    
    public List<PaymentResponseDTO> getClientPayments() {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Payment> payments = paymentRepository.findByClient(currentUser);
        return payments.stream()
                .map(this::convertToPaymentResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<PaymentResponseDTO> getWorkerPayments() {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Payment> payments = paymentRepository.findByWorker(currentUser);
        return payments.stream()
                .map(this::convertToPaymentResponseDTO)
                .collect(Collectors.toList());
    }
    
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!payment.getClient().getId().equals(currentUser.getId()) && 
            !payment.getWorker().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to view this payment");
        }
        
        return convertToPaymentResponseDTO(payment);
    }
    
    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    private PaymentResponseDTO convertToPaymentResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setJobId(payment.getJob().getId());
        dto.setClientName(payment.getClient().getName());
        dto.setWorkerName(payment.getWorker().getName());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setFailureReason(payment.getFailureReason());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}
