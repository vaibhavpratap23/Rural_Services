package com.gigfinder.controller;

import com.gigfinder.dto.LoginDTO;
import com.gigfinder.dto.RegisterDTO;
import com.gigfinder.model.ClientProfile;
import com.gigfinder.model.User;
import com.gigfinder.model.WorkerProfile;
import com.gigfinder.model.enums.Role;
import com.gigfinder.model.enums.VerificationStatus;
import com.gigfinder.repository.ClientProfileRepository;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.repository.WorkerProfileRepository;
import com.gigfinder.service.OtpService;
import com.gigfinder.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO dto) {
        try {
            // Check if email already exists
            Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error","Registration failed",
                                     "message","Email already exists: " + dto.getEmail()));
            }

            // 1) Create and save USER
            User user = User.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .phone(dto.getPhone())
                    .passwordHash(passwordEncoder.encode(dto.getPassword()))
                    .role(dto.getRole())  // Use the role from DTO
                    .build();

            User savedUser = userRepository.save(user);

            // 2) Create appropriate profile based on role
            if (dto.getRole() == Role.CLIENT) {
                ClientProfile clientProfile = ClientProfile.builder()
                        .user(savedUser)
                        .address("N/A")
                        .preferredPaymentMethod(null)
                        .locationLat(dto.getLocationLat())
                        .locationLng(dto.getLocationLng())
                        .build();
                clientProfileRepository.save(clientProfile);
            } else if (dto.getRole() == Role.WORKER) {
                VerificationStatus status = VerificationStatus.PENDING;
                if ("BASIC".equals(dto.getVerificationType())) {
                    status = VerificationStatus.PENDING_BASIC;
                } else if ("FULL".equals(dto.getVerificationType())) {
                    status = VerificationStatus.PENDING_FULL;
                }
                
                WorkerProfile workerProfile = WorkerProfile.builder()
                        .user(savedUser)
                        .skills("")
                        .radiusKm(dto.getRadiusKm() != null ? dto.getRadiusKm() : 5)
                        .verificationStatus(status)
                        .locationLat(dto.getLocationLat())
                        .locationLng(dto.getLocationLng())
                        .aadhaarNumber(dto.getAadhaarNumber())
                        .panNumber(dto.getPanNumber())
                        .address(dto.getAddress())
                        .build();
                workerProfileRepository.save(workerProfile);
            }

            // Response
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("name", savedUser.getName());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole().name());
            response.put("message", "User registered successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error","Registration failed","message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        try {
            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + dto.getEmail()));

            if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error","Authentication failed","message","Invalid email or password"));
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            return ResponseEntity.ok(
                    Map.of(
                        "token", token,
                        "user", Map.of("id", user.getId(),"name", user.getName(),"email", user.getEmail(),"role", user.getRole().name()),
                        "message", "Login successful"
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error","Login failed","message", e.getMessage()));
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String otp = otpService.generateOtp(phoneNumber);
            
            return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "otp", otp // In production, don't return OTP in response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send OTP", "message", e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String otp = request.get("otp");
            
            boolean isValid = otpService.verifyOtp(phoneNumber, otp);
            
            if (isValid) {
                return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "OTP verification failed", "message", e.getMessage()));
        }
    }
}
